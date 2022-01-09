package org.jeecg.modules.exam.service;

import org.jeecg.modules.exam.entity.Question;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.exam.entity.Question1;

import java.util.List;

/**
 * @Description: 试题表
 * @Author: jeecg-boot
 * @Date:   2019-08-08
 * @Version: V1.0
 */
public interface IQuestionService extends IService<Question> {

    public void removequestions(String examId);
    List<Question> questionlist(String examid);
    public Question setquestion(String examid,Question1 question1);
    int paperGen(String json);
}
