package org.jeecg.modules.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.exam.entity.Question;
import org.jeecg.modules.exam.entity.Question1;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: 试题表
 * @Author: jeecg-boot
 * @Date:   2019-08-08
 * @Version: V1.0
 */
public interface IQuestionbaseService extends IService<Question1> {

    public List<Question1> selectquestions(String sessionName,String type,int hard, int number,int score);

    List<List<Question1>> designpaper(HttpServletRequest request);
}
