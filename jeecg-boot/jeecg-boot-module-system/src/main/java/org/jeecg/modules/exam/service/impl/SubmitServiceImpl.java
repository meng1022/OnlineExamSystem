package org.jeecg.modules.exam.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections.list.SetUniqueList;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.exam.entity.*;
import org.jeecg.modules.exam.mapper.QAsetMapper;
import org.jeecg.modules.exam.mapper.SCacheMapper;
import org.jeecg.modules.exam.mapper.ScoreMapper;
import org.jeecg.modules.exam.mapper.SubmitMapper;
import org.jeecg.modules.exam.service.IExamService;
import org.jeecg.modules.exam.service.ISubmitService;
import org.jeecg.modules.exam.vo.SubmitExamUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: 答题结果表
 * @Author: jeecg-boot
 * @Date:   2019-08-08
 * @Version: V1.0
 */
@Service
public class SubmitServiceImpl extends ServiceImpl<SubmitMapper, Submit> implements ISubmitService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SubmitMapper submitMapper;
    @Autowired
    private SCacheMapper sCacheMapper;
    @Autowired
    private ScoreMapper scoreMapper;
    @Autowired
    private QAsetMapper qAsetMapper;

    @Autowired
    private IExamService examService;

    @Override
    public int dump2db(String examId){
        //当前时段是否允许收集答题结果
        //curTimeMillis > (exam.getExamStartTime().getTime() - 1000*60*2) && curTimeMillis < (exam.getExamEndTime().getTime() +1000*60*10)
        long curTimeMillis = System.currentTimeMillis();
        Exam exam = examService.getById(examId);
//        if(exam.getExamStartTime().getTime()-1000*60<curTimeMillis && curTimeMillis<exam.getExamEndTime().getTime()+1000*60*10){
//            return -1;
//        }
        //删除之前的提交
        Map<String, Object> map = new HashMap<>();
        map.put("exam_id", examId);
        map.put("create_by","redis");
        submitMapper.delete(new QueryWrapper<Submit>().allEq(map));
        long listSize = redisUtil.lGetListSize(CommonConstant.PREFIX_SUBMIT_LIST+ examId);
        if(listSize==0){
            return 0;
        }
        int insertCount = 0;
        long remain = listSize;
        long start = 0;
        List<Submit> Allsubmits = new ArrayList<Submit>();
        while(remain>0){
            List<Object> submitList = redisUtil.lGet(CommonConstant.PREFIX_SUBMIT_LIST+ examId,start,start+2000);
            System.out.println("缓存提交list表头"+redisUtil.lGet(CommonConstant.PREFIX_SUBMIT_LIST+ examId,0,-1).toString());
            remain = remain -submitList.size();
            start = start + submitList.size();
            List<Submit> subList = new ArrayList<Submit>();
            for(Object isubmit : submitList){
                JSONObject jsonObject = JSONObject.parseObject(String.valueOf(isubmit));
                Submit submit = new Submit();
                submit.setExamId(examId);
                submit.setUserId(jsonObject.getString("uid"));
                submit.setResult(jsonObject.getString("res"));
                submit.setCreateTime(new Date(jsonObject.getLong("time")));
                submit.setCreateBy("redis");
                subList.add(submit);
                redisUtil.del(CommonConstant.START_ANSWER_TIME + examId + jsonObject.getString("uid"));
                //删除考生答题缓存scache， localstorage？
                if(exam.getSubmitServerCache()==1){
                    SCache sCache = new SCache();
                    sCache.setExamId(examId);
                    sCache.setUserId(jsonObject.getString("uid"));
                    sCacheMapper.delete(new QueryWrapper<SCache>(sCache));
                }

//                if(this.exist(examId,uid)==true){//新建方法
//                    this.update(examId,uid,res,date);//重写方法
//                }
//                else{
//                    this.save(submit);
//                }
                //subList.add(submit);
            }
            this.saveBatch(subList);
            List<Submit> submits = submitMapper.getLastSubmitsByExamId(examId);
            Allsubmits.addAll(submits);
            submitMapper.deleteall(examId);
        }
        redisUtil.del(CommonConstant.PREFIX_SUBMIT_LIST+ examId);
        this.saveBatch(Allsubmits);
        scoreMapper.delete(new QueryWrapper<Score>(new Score().setExamId(examId)));
        qAsetMapper.delete(new QueryWrapper<QAset>(new QAset().setExamId(examId)));
        return  Allsubmits.size();
    }

    @Override
    public IPage<SubmitExamUserVo> queryByExamId(Page<SubmitExamUserVo> page, String examId, String provcode){
        return this.baseMapper.queryByExamId(page,examId,provcode);
    }

}
