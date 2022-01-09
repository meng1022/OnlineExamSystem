package org.jeecg.modules.exam.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jeecg.modules.exam.entity.Exam;
import org.jeecg.modules.exam.entity.Question;
import org.jeecg.modules.exam.entity.Question1;
import org.jeecg.modules.exam.mapper.QuestionMapper;
import org.jeecg.modules.exam.service.IExamService;
import org.jeecg.modules.exam.service.IQsetService;
import org.jeecg.modules.exam.service.IQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 试题表
 * @Author: jeecg-boot
 * @Date:   2019-08-08
 * @Version: V1.0
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements IQuestionService {

    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private IQsetService qsetService;
    @Autowired
    private IExamService examService;

    @Override
    public void removequestions(String examId){
        questionMapper.removequestions(examId);
        return;
    }

    @Override
    public List<Question> questionlist(String examid){
        return questionMapper.queryQuestionsByExamId(examid);
    }

    @Override
    public Question setquestion(String examid,Question1 question1){
        Question question = new Question();
        question.setExamId(examid);
        String type = question1.getType();
        question.setType(type);
        question.setBigDescribe(type);
        question.setContent(question1.getContent());
        question.setOptionA(question1.getOptionA());
        question.setOptionB(question1.getOptionB());
        question.setOptionC(question1.getOptionC());
        question.setOptionD(question1.getOptionD());
        question.setOptionE(question1.getOptionE());
        question.setImgUrlA(question1.getImgUrlA());
        question.setImgUrlB(question1.getImgUrlB());
        question.setImgUrlC(question1.getImgUrlC());
        question.setImgUrlD(question1.getImgUrlD());
        question.setImgUrlE(question1.getImgUrlE());
        question.setAnswer(question1.getAnswer());
        question.setScore(question1.getScore());
        return question;
    }

    @Override
    public int paperGen(String json){
        int result = 1;//生成成功：1，考试已经开始：2，生成失败：3
        JSONObject jsonObject = JSONObject.parseObject(json);
        String examId = jsonObject.getString("examId");
        JSONArray paper = jsonObject.getJSONArray("paper");
        List<Question> questionList = this.questionlist(examId);
        long curTimeMillis = System.currentTimeMillis();
        Exam exam = examService.getById(examId);
        if(curTimeMillis > (exam.getExamStartTime().getTime() - 1000*60*2) && curTimeMillis < (exam.getExamEndTime().getTime() +1000*60*10)){
            return 2;
        }
        if(questionList.size()>0){
            this.removequestions(examId);
        }
        if(paper!=null&&paper.size()>0){
            for(int i=0;i<paper.size();i++){//遍历大题
                String big = paper.getString(i);
                JSONObject jsonObject1 = JSONObject.parseObject(big);//大题
                String bigDescribe = jsonObject1.getString("bigDescribe");
                JSONArray questionlist = jsonObject1.getJSONArray("paperList");//小题list
                for(int j=0;j<questionlist.size();j++){
                    JSONObject question = (JSONObject)questionlist.get(j);
                    Question question1 = JSONObject.parseObject(question.toJSONString(),Question.class);
                    question1.setBigDescribe(bigDescribe);
                    question1.setExamId(examId);
                    question1.setSortNo(i+1);
                    question1.setNo(i*100+(j+1));
                    try{
                        questionMapper.insert(question1);
                    }catch (Exception e){
                        result = 3;
                    }
                }
            }
            qsetService.genQsetByExamId(examId);
        }
        return result;
    }
}
