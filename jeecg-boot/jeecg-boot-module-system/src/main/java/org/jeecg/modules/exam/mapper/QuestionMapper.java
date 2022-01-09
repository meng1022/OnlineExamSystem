package org.jeecg.modules.exam.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.exam.entity.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 试题表
 * @Author: jeecg-boot
 * @Date:   2019-08-08
 * @Version: V1.0
 */
public interface QuestionMapper extends BaseMapper<Question> {

    public List<Question> queryQuestionsByExamId(@Param("examId") String examId);

    public void removequestions(@Param("examId") String examId);

}
