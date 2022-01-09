package org.jeecg.modules.exam.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.exam.entity.QAset;
import org.jeecg.modules.exam.entity.Qset;
import org.jeecg.modules.exam.entity.Question;
import org.jeecg.modules.exam.mapper.QAsetMapper;
import org.jeecg.modules.exam.mapper.QuestionMapper;
import org.jeecg.modules.exam.service.IQAsetService;
import org.jeecg.modules.exam.service.IQsetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Description: 试题集合
 * @Author: jeecg-boot
 * @Date:   2019-08-08
 * @Version: V1.0
 */
@Slf4j
@Service
public class QAsetServiceImpl extends ServiceImpl<QAsetMapper, QAset> implements IQAsetService {
    @Autowired
    private QAsetMapper qAsetMapper;
    @Autowired
    private IQsetService qsetService;
    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public QAset getQAsetByExamId(String examId) {
        return qAsetMapper.getQAsetByExamId(examId);
    }

    @Override
    public boolean genQAsetByExamId(String examId, Map<String,Object> qAMap){

        //获取试题集
        Qset qset = qsetService.getQsetByExamId(examId);
        //qlist是试题列表
        JSONArray qlist = JSONArray.parseArray(String.valueOf(qset.getQset()));
        //qalist用来存储增加成绩分析字段的试题列表
        JSONArray qalist = new JSONArray();
        for(int i = 0;i < qlist.size();i++){
            JSONObject map1 = (JSONObject) qlist.get(i);
            JSONArray paperList = (JSONArray)map1.get("paperList");
            for(int j=0;j<paperList.size();j++){
                JSONObject map = (JSONObject)paperList.get(j);
                //获取qAMap中针对当前题号的正确率等分析结果
                if(!map.get("type").equals("quest")) {
                    Map<String,Object> qaobj = (Map<String,Object>)qAMap.get(map.get("no").toString());
                    if(qaobj == null){
                        //当前题号在qAMap中不存在时
                        map.put("acc",null);
                        JSONArray analysis = new JSONArray();
                        map.put("analysis",analysis);
                    }else{
                        //当前题号在qAMap中存在时
//                        if(map.get("type").equals("multi")){
//                            //后续需要升级程
//                            map.put("value",qaobj.get("answer"));
//                        }else{
//                            map.put("value",qaobj.get("answer"));
//                        }
                        map.put("acc",qaobj.get("acc"));
                        JSONArray analysis = new JSONArray();
                        if(qaobj.get("ratioA") != null){
                            JSONObject ana = new JSONObject();
                            ana.put("key","A");
                            ana.put("ratio",qaobj.get("ratioA"));
                            analysis.add(ana);
                        }
                        if(qaobj.get("ratioB") != null){
                            JSONObject ana = new JSONObject();
                            ana.put("key","B");
                            ana.put("ratio",qaobj.get("ratioB"));
                            analysis.add(ana);
                        }
                        if(qaobj.get("ratioC") != null){
                            JSONObject ana = new JSONObject();
                            ana.put("key","C");
                            ana.put("ratio",qaobj.get("ratioC"));
                            analysis.add(ana);
                        }
                        if(qaobj.get("ratioD") != null){
                            JSONObject ana = new JSONObject();
                            ana.put("key","D");
                            ana.put("ratio",qaobj.get("ratioD"));
                            analysis.add(ana);
                        }
                        if(qaobj.get("ratioE") != null){
                            JSONObject ana = new JSONObject();
                            ana.put("key","E");
                            ana.put("ratio",qaobj.get("ratioE"));
                            analysis.add(ana);
                        }
                        map.put("analysis",analysis);
                    }
                    qalist.add(map);
                }
//                else{
//                    Map<String,Object> qaobj = (Map<String,Object>)qAMap.get(map.get("no").toString());
//                    if(qaobj == null){
//                        //当前题号在qAMap中不存在时
//                        map.put("ave",0);
//                        JSONArray analysis = new JSONArray();
//                        map.put("analysis",analysis);
//                    }else{
//                        JSONObject analysis = new JSONObject();
//                        analysis.put("ave",qaobj.get("ave"));
//                        map.put("analysis",analysis);
//                    }
//
                else{
                    boolean flag=true;
                    if(qAMap.get(map.get("no").toString())!=null){
                        Map<String,Object> qaobj = (Map<String,Object>)qAMap.get(map.get("no").toString());
                        qalist=JSONArray.parseArray(qAsetMapper.getQAsetByExamId(examId).getQaset().toString());
                        for(int k =0;k<qalist.size();k++){
                            JSONObject jsonObject = (JSONObject) qalist.get(k);
                            int no1 = (int) jsonObject.get("no");
                            int no2 = (int) map.get("no");
                            if(no1==no2){
                                //qaset里已经有该题的ave分析结果
                                if(qaobj.get("ave")==null){
                                    map.put("ave","尚未批阅问答题");
                                }else{
                                    map.put("ave",qaobj.get("ave"));
                                }
                                qalist.remove(k);
                                flag = false;
                            }
                        }
                        if(flag==true){
                            //qaset里没有该题的ave分析结果
                            if(qaobj.get("ave")==null){
                                map.put("ave","尚未批阅问答题");
                            }else{
                                map.put("ave",qaobj.get("ave"));
                            }
                        }
                        qalist.add(map);
                        qAsetMapper.updateQASet(examId,qalist.toJSONString());
                    }
                    else;
                }
            }
        }

        QAset qAset = qAsetMapper.getQAsetByExamId(examId);
        if(qAset == null){
            QAset qAsetnew = new QAset();
            qAsetnew.setExamId(examId);
            qAsetnew.setQaset(qalist.toJSONString());
            qAsetMapper.insert(qAsetnew);
        }else{
            qAsetMapper.updateQASet(examId,qalist.toJSONString());
            log.info(qalist.toJSONString());
        }
        return true;
    }

}
