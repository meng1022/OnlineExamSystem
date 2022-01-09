package org.jeecg.modules.exam.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.exam.entity.Exam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.exam.vo.ScoreExamUserVo;

import java.util.List;

/**
 * @Description: 考试
 * @Author: jeecg-boot
 * @Date:   2019-08-07
 * @Version: V1.0
 */
public interface ExamMapper extends BaseMapper<Exam> {
    Integer getSessionNumByExamName(@Param("examName") String examName);
}
