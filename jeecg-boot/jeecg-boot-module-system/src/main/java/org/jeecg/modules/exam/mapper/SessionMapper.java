package org.jeecg.modules.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.exam.entity.Exam;
import org.jeecg.modules.exam.entity.Session;

/**
 * @Description: 考试
 * @Author: jeecg-boot
 * @Date:   2019-08-07
 * @Version: V1.0
 */
public interface SessionMapper extends BaseMapper<Session> {
    //Integer getSessionNumByExamName(@Param("examName") String examName);
}
