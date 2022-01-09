package org.jeecg.modules.exam.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.util.QuestUtils;
import org.jeecg.modules.exam.entity.Question;
import org.jeecg.modules.exam.mapper.QuestionMapper;
import org.jeecg.modules.exam.entity.Score;
import org.jeecg.modules.exam.mapper.ScoreMapper;
import org.jeecg.modules.exam.service.IQAsetService;
import org.jeecg.modules.exam.service.IScoreService;
import org.jeecg.modules.exam.vo.ScoreExamUserVo;
import org.jeecg.modules.exam.entity.Submit;
import org.jeecg.modules.exam.mapper.SubmitMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: 成绩表
 * @Author: jeecg-boot
 * @Date:   2019-08-08
 * @Version: V1.0
 */
@Slf4j
@Service
public class ScoreServiceImpl extends ServiceImpl<ScoreMapper, Score> implements IScoreService {
    @Autowired
    private ScoreMapper scoreMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private SubmitMapper submitMapper;
    @Autowired
    private IQAsetService qAsetService;

    @Override
    public boolean grade1ByExamId(String examId){
        scoreMapper.delete(new QueryWrapper<Score>().eq("exam_id", examId));
        //删除该考试原来的成绩
        //List<Submit> submits = submitMapper.getLastSubmitsByExamId(examId);
        List<Submit> submits = submitMapper.selectList(new QueryWrapper<Submit>().eq("exam_id",examId));
        List<Question> questions = questionMapper.selectList(new QueryWrapper<Question>().eq("exam_id",examId));

        Map<String, Map<String, Object>> questionNumberMap = new HashMap<>();
        try{
            for (Submit submit : submits) {
                double iscore = 0.0;
                String result = submit.getResult();
                //convert result in submit into a map
                JSONArray jsonArray = (JSONArray)JSON.parse(result);
                HashMap<String, String> resultMap = new HashMap<>();
                for(int index=0; index< jsonArray.size(); index++){
                    JSONObject jsonObject = (JSONObject) jsonArray.get(index);
                    String no = jsonObject.getString("no");
                    String value = jsonObject.getString("value");
                    resultMap.put(no, value);
                }
                for(Question question: questions ){
                    double questionScore = 0.0;
                    if (question.getScore() != null){
                        questionScore = question.getScore();
                    }
                    //如果question No为空/无此题答案/题目类型为是quest，则进入下一个题目
                    if (question.getNo() == null || !resultMap.containsKey(question.getNo().toString()) ||
                            question.getType().equalsIgnoreCase("quest")||
                            resultMap.get(question.getNo().toString())==null){
                        continue;
                    }

                    //准备生成试卷分析数据，如果eachQuestionMap 或者 questionNumberMap 为空，则插入新的数据
                    Map<String, Object> eachQuestionMap = new HashMap<>();
                    if(questionNumberMap.containsKey(question.getNo().toString())){
                        eachQuestionMap = questionNumberMap.get(question.getNo().toString());
                    }

                    if (!eachQuestionMap.containsKey("rightAnswer")){
                        eachQuestionMap.put("rightAnswer", 0);
                    }
                    if (!eachQuestionMap.containsKey("answer")){
                        eachQuestionMap.put("answer", question.getAnswer());
                    }
                    if (!eachQuestionMap.containsKey("optionNum")){
                        if(question.getOptionC() == null){
                            eachQuestionMap.put("optionNum", "A,B");
                        }else if(question.getOptionD()== null){
                            eachQuestionMap.put("optionNum", "A,B,C");
                        }else if(question.getOptionE()== null){
                            eachQuestionMap.put("optionNum", "A,B,C,D");
                        }else{
                            eachQuestionMap.put("optionNum", "A,B,C,D,E");
                        }
                    }

                    String resultValue = resultMap.get(question.getNo().toString());//所选答案
                    if (question.getType().equalsIgnoreCase("single") || question.getType().equalsIgnoreCase("judge")){
                        //single
                        if (resultValue.equalsIgnoreCase(question.getAnswer())){
                            iscore += questionScore;

                            int count = (int)eachQuestionMap.get("rightAnswer");
                            count += 1;
                            eachQuestionMap.put("rightAnswer", count);
                        }

                        //eachQuestionMap记录的是各个选项选择的次数
                        if (eachQuestionMap.containsKey(resultValue)){
                            int count = (int)eachQuestionMap.get(resultValue);
                            count += 1;
                            eachQuestionMap.put(resultValue, count);
                        } else {
                            eachQuestionMap.put(resultValue, 1);
                        }
                    } else if (question.getType().equalsIgnoreCase("multi"))
                    {
                        //multi
                        String tmp =resultValue.replace("[", "").replace("]", "").replace("\"", "");
                        if(!tmp.isEmpty()) {

                            String[] multiResult = tmp.split(",");

                            String multiAnswer = question.getAnswer().replaceAll(" ", "");

                            //eachQuestionMap记录的是各个选项选择的次数
                            for (String option : multiResult) {
                                if (eachQuestionMap.containsKey(option)) {
                                    int count = (int) eachQuestionMap.get(option);
                                    count += 1;
                                    eachQuestionMap.put(option, count);
                                } else {
                                    eachQuestionMap.put(option, 1);
                                }
                            }
                            if (multiResult.length == multiAnswer.length()) {
                                boolean isRight = true;
                                for (String option : multiResult) {
                                    if (!multiAnswer.toUpperCase().contains(option.toUpperCase())) {
                                        isRight = false;
                                    }
                                }
                                if (isRight) {
                                    iscore += questionScore;

                                    int count = (int) eachQuestionMap.get("rightAnswer");
                                    count += 1;
                                    eachQuestionMap.put("rightAnswer", count);
                                }
                            }
                        }
                    }
                    questionNumberMap.put(question.getNo().toString(), eachQuestionMap);
                }

                Score score = new Score();
                score.setExamId(examId);
                score.setUserId(submit.getUserId());
                score.setScore1(iscore);
                score.setSubmitTime(submit.getCreateTime());
                scoreMapper.insert(score);
            }
        } catch (Exception e){
            return false;
        }

        log.debug("计算分数时记录的各个选项选择次数以及正确答案为：" + questionNumberMap.toString());

        Map<String, Object> qAMap = new HashMap<String,Object>();//判分完毕后生成成绩分析数据
        int submitNumber = submits.size();//如果没有任何试卷提交的时候， 显示分析结果均为0
        if (submitNumber <= 0){
            log.info("该项考试" + examId + "没有任何试卷提交。");
            for (Question question: questions){
                if(question.getType().equalsIgnoreCase("quest")){
                    continue;
                }
                Map<String, Object> demoresult = new HashMap<String,Object>();
                demoresult.put("answer", question.getAnswer());
                demoresult.put("acc", 0);

                String[] optionList = null;//new String[]{"A", "B", "C", "D", "E", "F", "G"};
                if(question.getOptionC()== null){
                    optionList = new String[]{"A", "B"};
                }else if(question.getOptionD()== null){
                    optionList = new String[]{"A", "B", "C"};
                }else if(question.getOptionE()== null){
                    optionList = new String[]{"A", "B", "C", "D"};
                }else{
                    optionList = new String[]{"A", "B", "C", "D", "E"};
                }

                for(String option: optionList){
                    demoresult.put("ratio" + option, 0);
                }
                qAMap.put(question.getNo().toString(), demoresult);
            }
        } else {
            DecimalFormat df = new DecimalFormat("0.000");//格式化小数
            for(String questionNo: questionNumberMap.keySet()){
                Map<String, Object> questionStatus = questionNumberMap.get(questionNo);
                Map<String, Object> demoresult = new HashMap<String,Object>();
                demoresult.put("answer", questionStatus.get("answer"));

                //计算正确率
                float accTmp = Float.parseFloat(questionStatus.get("rightAnswer").toString()) / submitNumber;
                float acc=Float.parseFloat(df.format(accTmp));//返回一个String类型的两位小数
                demoresult.put("acc", acc);

                String[] optionList = questionStatus.get("optionNum").toString().split(",");
                for (String option: optionList){
                    if(questionStatus.containsKey(option)){
                        float ratioTmp = Float.parseFloat(questionStatus.get(option).toString()) / submitNumber;
                        float ratio=Float.parseFloat(df.format(ratioTmp));//返回一个String类型的两位小数
                        demoresult.put("ratio" + option, df.format(ratio));//选择率
                    } else {
                        demoresult.put("ratio" + option, 0);
                    }
                }
                qAMap.put(questionNo,demoresult);
                //考试分析
            }
        }

        log.info("该项考试" + examId + "各个选项选择次数以及正确答案为：" + qAMap.toString());
        boolean success = qAsetService.genQAsetByExamId(examId,qAMap);
        return success;
    }

    @Override
    public boolean grade2ByExamId(String examId){
        List<Submit> submits = submitMapper.selectList(new QueryWrapper<Submit>().eq("exam_id",examId));
        List<Question> questions = questionMapper.selectList(new QueryWrapper<Question>().eq("exam_id",examId));

        Map<String, Map<String, Object>> questionNumberMap = new HashMap<>();
        try{
            for (Submit submit : submits) {
                double iscore = 0.0;
                String result = submit.getResult();
                JSONArray jsonArray = (JSONArray)JSON.parse(result);
                HashMap<String, String> resultMap = new HashMap<>();
                for(int index=0; index< jsonArray.size(); index++){
                    JSONObject jsonObject = (JSONObject) jsonArray.get(index);
                    String no = jsonObject.getString("no");
                    String value = jsonObject.getString("value");
                    resultMap.put(no, value);
                }
                double score2=0.0;
                for(Question question: questions ){
                    double questionScore = 0.0;
                    if (question.getScore() != null){
                        questionScore = question.getScore();
                    }
                    //如果question No为空/无此题答案/题目类型不是quest，则进入下一个题目
                    if (question.getNo() != null && resultMap.containsKey(question.getNo().toString()) &&
                            question.getType().equalsIgnoreCase("quest")&&
                            resultMap.get(question.getNo().toString())!=null){
                        String resultValue = resultMap.get(question.getNo().toString());//所选答案
                        double similarity = QuestUtils.getSimilarity(question.getAnswer(),resultValue);
                        iscore = questionScore*similarity;
                        score2+=iscore;

                        Map<String,Object> eachQuestMap = new HashMap<String,Object>();
                        if(questionNumberMap.containsKey(question.getNo().toString())){
                            eachQuestMap = questionNumberMap.get(question.getNo().toString());
                        }
                        if(!eachQuestMap.containsKey("totalscore2")){
                            eachQuestMap.put("totalscore2",iscore);
                        }else{
                            double totalscore2_ = (double)eachQuestMap.get("totalscore2");
                            totalscore2_+=iscore;
                            eachQuestMap.put("totalscore2",totalscore2_);
                        }
                        questionNumberMap.put(question.getNo().toString(),eachQuestMap);
                    }
                    else;
                }
                Score score0 = new Score();
                score0.setExamId(examId);
                score0.setUserId(submit.getUserId());
                QueryWrapper<Score> queryWrapper = new QueryWrapper<Score>(score0);
                Score score = scoreMapper.selectOne(queryWrapper);
                score.setScore2(score2);
                scoreMapper.updateById(score);//? insert
                //scoreMapper.insert(score);
            }
        } catch (Exception e){
            return false;
        }
        int subsize = submits.size();
        DecimalFormat df = new DecimalFormat("#.00");
        Map<String,Object>qMap = new HashMap<String, Object>();
        Map<String,Object>demoresult = new HashMap<String, Object>();
        for(String questionNo:questionNumberMap.keySet()){
            double totalscore2 = (double)questionNumberMap.get(questionNo).get("totalscore2");
            String a = df.format(totalscore2/subsize);
            double ave = Double.valueOf(a);
            demoresult.put("ave",ave);
            qMap.put(questionNo,demoresult);
        }//平均分分析结果，先不保存到qaset里

        boolean success = qAsetService.genQAsetByExamId(examId,qMap);
        return success;
    }

    @Override
    public IPage<ScoreExamUserVo> queryByExamId(Page<ScoreExamUserVo> page, String examId, String provcode){
        scoreMapper.setScore();
        return this.baseMapper.queryByExamId(page,examId,provcode);
    }

    @Override
    public JSONArray getdata(String examId,float fullscore){
        double score_60 = fullscore*0.6;
        double score_80 = fullscore*0.8;
        double score_90 = fullscore*0.9;
        int []data = new int[4];
        data[0] = scoreMapper.getdata1(examId,0,score_60);
        data[1] = scoreMapper.getdata1(examId,score_60,score_80);
        data[2] = scoreMapper.getdata1(examId,score_80,score_90);
        data[3] = scoreMapper.getdata2(examId,score_90,fullscore);
        JSONArray jsonArray = new JSONArray();
        JSONObject jb = new JSONObject();
        jb.put("x","不合格,[0,60)");
        jb.put("y",data[0]);
        jsonArray.add(jb);
        JSONObject jb1 = new JSONObject();
        jb1.put("x","合格,[60,80)");
        jb1.put("y",data[1]);
        jsonArray.add(jb1);
        JSONObject jb2 = new JSONObject();
        jb2.put("x","良好,[80,90)");
        jb2.put("y",data[2]);
        jsonArray.add(jb2);
        JSONObject jb3 = new JSONObject();
        jb3.put("x","优秀,[90,100]");
        jb3.put("y",data[3]);
        jsonArray.add(jb3);
        return jsonArray;
    }

}
