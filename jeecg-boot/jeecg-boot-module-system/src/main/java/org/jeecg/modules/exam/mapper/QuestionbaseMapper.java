package org.jeecg.modules.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.exam.entity.Question;
import org.jeecg.modules.exam.entity.Question1;

import java.util.List;

/**
 * @Description: 试题表
 * @Author: jeecg-boot
 * @Date:   2019-08-08
 * @Version: V1.0
 */
public interface QuestionbaseMapper extends BaseMapper<Question1> {

    public List<Question1> queryQuestionsBySessionName(@org.springframework.data.repository.query.Param("sessionName") String sessionName);

    public List<Question1> selectquestions(@Param("sessionName") String sessionName, @Param("type")String type,@Param("hard")Integer hard,@Param("number")Integer number, @Param("score")Integer score);


}
