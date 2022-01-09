package org.jeecg.modules.exam.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.exam.entity.Question;
import org.jeecg.modules.exam.entity.Question1;
import org.jeecg.modules.exam.entity.Session;
import org.jeecg.modules.exam.mapper.QuestionbaseMapper;
import org.jeecg.modules.exam.service.IQuestionbaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 试题表
 * @Author: jeecg-boot
 * @Date:   2019-08-08
 * @Version: V1.0
 */
@Service
public class QuestionbaseServiceImpl extends ServiceImpl<QuestionbaseMapper, Question1> implements IQuestionbaseService {

    @Autowired
    private QuestionbaseMapper questionbaseMapper;

    @Override
    public List<Question1> selectquestions(String sessionName, String type, int hard, int number, int score){
        List<Question1> question1List = questionbaseMapper.selectquestions(sessionName,type,hard,number,score);
//        List<Question> questionList = new ArrayList<Question>();
//        for(int i = 0;i<question1List.size();i++){
//            Question1 question1 = question1List.get(i);
//            Question question = new Question();
//            //question.setExamId(examId);
//            question.setType(question1.getType());
//            question.setAnswer(question1.getAnswer());
//            question.setScore(question1.getScore());
//            question.setContent(question1.getContent());
//            question.setOptionA(question1.getOptionA());
//            question.setOptionB(question1.getOptionB());
//            question.setOptionC(question1.getOptionC());
//            question.setOptionD(question1.getOptionD());
//            question.setOptionE(question1.getOptionE());
//            question.setOptionF(question1.getOptionF());
//            question.setOptionG(question1.getOptionG());
//            question.setImgTitleA(question1.getImgTitleA());
//            question.setImgTitleB(question1.getImgTitleB());
//            question.setImgTitleC(question1.getImgTitleC());
//            question.setImgTitleD(question1.getImgTitleD());
//            question.setImgTitleE(question1.getImgTitleE());
//            question.setImgUrlA(question1.getImgUrlA());
//            question.setImgUrlB(question1.getImgUrlB());
//            question.setImgUrlC(question1.getImgUrlC());
//            question.setImgUrlD(question1.getImgUrlD());
//            question.setImgUrlE(question1.getImgUrlE());
//            questionList.add(question);
//        }
        return question1List;
    }

    @Override
    public List<List<Question1>> designpaper(HttpServletRequest request){
        List<List<Question1>> paper = new ArrayList<List<Question1>>();
        List<Question1> question = new ArrayList<Question1>();
        String sessionName = request.getParameter("sessionName");

        String type1 = request.getParameter("type1");
        int number1 = Integer.parseInt(request.getParameter("number1"));
        int score1 = Integer.parseInt(request.getParameter("score1"));
        int hard1=-1;
        if(request.getParameter("hard1")!=null){
            hard1 = Integer.parseInt(request.getParameter("hard1"));
        }
        else;
        question = questionbaseMapper.selectquestions(sessionName,type1,hard1,number1,score1);
        paper.add(question);

        String type2="";
        int number2=0;
        int score2 =0;
        if(request.getParameter("type2")!=null&&request.getParameter("number2")!=null){
            int hard2 = -1;
            type2 = request.getParameter("type2");
            number2 = Integer.parseInt(request.getParameter("number2"));
            score2 = Integer.parseInt(request.getParameter("score2"));
            if(request.getParameter("hard2")!=null){
                hard2 = Integer.parseInt(request.getParameter("hard2"));
            }
            else;
            question = questionbaseMapper.selectquestions(sessionName,type2,hard2,number2,score2);
            paper.add(question);
        }
        else;

        String type3="";
        int number3=0;
        int score3 =0;
        if(request.getParameter("type3")!=null&&request.getParameter("number3")!=null){
            int hard3 = -1;
            type3 = request.getParameter("type3");
            number3 = Integer.parseInt(request.getParameter("number3"));
            score3 = Integer.parseInt(request.getParameter("score3"));
            if(request.getParameter("hard3")!=null){
                hard3 = Integer.parseInt(request.getParameter("hard3"));
            }
            else;
            question = questionbaseMapper.selectquestions(sessionName,type3,hard3,number3,score3);
            paper.add(question);
        }
        else;

        String type4="";
        int number4=0;
        int score4 =0;
        if(request.getParameter("type4")!=null&&request.getParameter("number4")!=null){
            int hard4 = -1;
            type4 = request.getParameter("type4");
            number4 = Integer.parseInt(request.getParameter("number4"));
            score4 = Integer.parseInt(request.getParameter("score4"));
            if(request.getParameter("hard4")!=null){
                hard4 = Integer.parseInt(request.getParameter("hard4"));
            }
            else;
            question = questionbaseMapper.selectquestions(sessionName,type4,hard4,number4,score4);
            paper.add(question);
        }
        else;

        String type5="";
        int number5=0;
        int score5 =0;
        if(request.getParameter("type5")!=null&&request.getParameter("number5")!=null){
            int hard5 = -1;
            type5 = request.getParameter("type5");
            number5 = Integer.parseInt(request.getParameter("number5"));
            score5 = Integer.parseInt(request.getParameter("score5"));
            if(request.getParameter("hard5")!=null){
                hard5 = Integer.parseInt(request.getParameter("hard5"));
            }
            else;
            question = questionbaseMapper.selectquestions(sessionName,type5,hard5,number5,score5);
            paper.add(question);
        }
        else;

        String type6="";
        int number6=0;
        int score6=0;
        if(request.getParameter("type6")!=null&&request.getParameter("number6")!=null){
            int hard6 = -1;
            type6 = request.getParameter("type6");
            number6 = Integer.parseInt(request.getParameter("number6"));
            score6 = Integer.parseInt(request.getParameter("score6"));
            if(request.getParameter("hard6")!=null){
                hard6 = Integer.parseInt(request.getParameter("hard6"));
            }
            else;
            question = questionbaseMapper.selectquestions(sessionName,type6,hard6,number6,score6);
            paper.add(question);
        }
        else;

        return paper;
    }
}
