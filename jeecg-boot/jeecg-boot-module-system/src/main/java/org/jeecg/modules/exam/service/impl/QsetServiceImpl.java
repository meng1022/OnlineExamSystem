package org.jeecg.modules.exam.service.impl;

import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.exam.entity.Qset;
import org.jeecg.modules.exam.entity.Question;
import org.jeecg.modules.exam.mapper.QsetMapper;
import org.jeecg.modules.exam.service.IQsetService;
import org.jeecg.modules.exam.mapper.QuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
/**
 * @Description: 试题集合
 * @Author: jeecg-boot
 * @Date:   2019-08-08
 * @Version: V1.0
 */
@Service
public class QsetServiceImpl extends ServiceImpl<QsetMapper, Qset> implements IQsetService {
    @Autowired
    private QsetMapper qsetMapper;
    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public Qset getQsetByExamId(String examId) {
        return qsetMapper.getQsetByExamId(examId);
    }

    @Override
    public boolean genQsetByExamId(String examId){
        List<Question> qlist = questionMapper.queryQuestionsByExamId(examId);
        JSONArray list = new JSONArray();
        LinkedHashMap<String,List<Question>> map = qlist.stream().collect(Collectors.groupingBy(Question::getBigDescribe,LinkedHashMap::new,Collectors.toList()));
        Set<String> strings = map.keySet();//关键字集合，bigdescribe
        for(String bigDescribe:strings){
            JSONObject big = new JSONObject();
            big.put("getBigDescribe",bigDescribe);
            List<Question> questionList = map.get(bigDescribe);
            for(int i = 0;i<questionList.size();i++){
                Question question = questionList.get(i);
                big.put("sortNo",question.getSortNo());//?
                big.put("paperList",questionList);
            }
            list.add(big);
        }

        Qset qset = qsetMapper.getQsetByExamId(examId);
        if(qset!=null){
            qsetMapper.DelbyExamId(examId);
        }
        Qset qset1 = new Qset();
        qset1.setExamId(examId);
        qset1.setQset(list.toJSONString());
        qset1.setHashCode(String.valueOf(list.toJSONString().hashCode()));
        qsetMapper.insert(qset1);
//        for(int i = 0;i < qlist.size();i++){
//            Question temp = qlist.get(i);
//            JSONObject map = new JSONObject();
//            map.put("type", temp.getType());
//            map.put("title",temp.getTitle());
//            map.put("content",temp.getContent());
//            map.put("no",temp.getNo());
//            map.put("score",temp.getScore());
//            if(temp.getType().equals("multi")){
//                map.put("value",new JSONArray());
//            }else{
//                map.put("value","");
//            }
//            if(!temp.getType().equals("desc")){
//                JSONArray options = new JSONArray();
//                if(oConvertUtils.isNotEmpty(temp.getOptionA())){
//                    JSONObject option = new JSONObject();
//                    option.put("key","A");
//                    option.put("desc",temp.getOptionA());
//                    options.add(option);
//                }
//                if(oConvertUtils.isNotEmpty(temp.getOptionB())){
//                    JSONObject option = new JSONObject();
//                    option.put("key","B");
//                    option.put("desc",temp.getOptionB());
//                    options.add(option);
//                }
//                if(oConvertUtils.isNotEmpty(temp.getOptionC())){
//                    JSONObject option = new JSONObject();
//                    option.put("key","C");
//                    option.put("desc",temp.getOptionC());
//                    options.add(option);
//                }
//                if(oConvertUtils.isNotEmpty(temp.getOptionD())){
//                    JSONObject option = new JSONObject();
//                    option.put("key","D");
//                    option.put("desc",temp.getOptionD());
//                    options.add(option);
//                }
//                if(oConvertUtils.isNotEmpty(temp.getOptionE())){
//                    JSONObject option = new JSONObject();
//                    option.put("key","E");
//                    option.put("desc",temp.getOptionE());
//                    options.add(option);
//                }
//                if(oConvertUtils.isNotEmpty(temp.getOptionF())){
//                    JSONObject option = new JSONObject();
//                    option.put("key","F");
//                    option.put("desc",temp.getOptionF());
//                    options.add(option);
//                }
//                if(oConvertUtils.isNotEmpty(temp.getOptionG())){
//                    JSONObject option = new JSONObject();
//                    option.put("key","G");
//                    option.put("desc",temp.getOptionG());
//                    options.add(option);
//                }
//                map.put("options",options);
//
//            }
//            if(temp.getType().equals("img") || temp.getType().equals("desc") || temp.getType().equals("multi"))
//            {
//                JSONArray imgs = new JSONArray();
//                if(oConvertUtils.isNotEmpty(temp.getImgUrlA())  ){
//                    JSONObject img = new JSONObject();
//                    img.put("title",temp.getImgTitleA());
//                    img.put("src",temp.getImgUrlA());
//                    imgs.add(img);
//                }
//                if(oConvertUtils.isNotEmpty(temp.getImgUrlB())){
//                    JSONObject img = new JSONObject();
//                    img.put("title",temp.getImgTitleB());
//                    img.put("src",temp.getImgUrlB());
//                    imgs.add(img);
//                }
//                if(oConvertUtils.isNotEmpty(temp.getImgUrlC())){
//                JSONObject img = new JSONObject();
//                img.put("title",temp.getImgTitleC());
//                img.put("src",temp.getImgUrlC());
//                imgs.add(img);
//                }
//                if(oConvertUtils.isNotEmpty(temp.getImgUrlD())){
//                    JSONObject img = new JSONObject();
//                    img.put("title",temp.getImgTitleD());
//                    img.put("src",temp.getImgUrlD());
//                    imgs.add(img);
//                }
//                if(oConvertUtils.isNotEmpty(temp.getImgUrlE())){
//                    JSONObject img = new JSONObject();
//                    img.put("title",temp.getImgTitleE());
//                    img.put("src",temp.getImgUrlE());
//                    imgs.add(img);
//                }
//                map.put("imgs",imgs);
//            }
//            list.add(map);
//
//        }
//
//        Qset qset = qsetMapper.getQsetByExamId(examId);
//        if(qset == null){
//            Qset qsetnew = new Qset();
//            qsetnew.setExamId(examId);
//            qsetnew.setQset(list.toJSONString());
//            qsetnew.setHashCode(String.valueOf(list.toJSONString().hashCode()));
//            qsetMapper.insert(qsetnew);
//        }else{
//            qsetMapper.updateQSet(examId,list.toJSONString(),String.valueOf(list.toJSONString().hashCode()));
//        }
        return true;
    }

    public float getFullScore(String examId){
        float fullscore = 0;
        Qset qset = this.getQsetByExamId(examId);
        String qsetstring = qset.getQset().toString();
        JSONArray jsonArray = JSONArray.parseArray(qsetstring);
        for(int i =0;i<jsonArray.size();i++){
           JSONObject jsonObject = (JSONObject) jsonArray.get(i);
           JSONArray paperList = (JSONArray)jsonObject.get("paperList");
           for(int j=0;j<paperList.size();j++){
               if(paperList.getJSONObject(j).get("score")!=null){
                   String score = paperList.getJSONObject(j).get("score").toString();
                   fullscore += Float.parseFloat(score);
               }
               else;
           }
        }
        return fullscore;
    }

}
