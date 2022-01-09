package org.jeecg.modules.exam.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.exam.entity.Qset;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 试题集合
 * @Author: jeecg-boot
 * @Date:   2019-08-08
 * @Version: V1.0
 */
public interface QsetMapper extends BaseMapper<Qset> {
    /**
     * 通过考试号查询试题信息
     * @param examId
     * @return
     */
    public Qset getQsetByExamId(@Param("examId") String examId);

    /**
     * 根据examId设置QSet
     * @param examId
     * @param qSet
     */
    void updateQSet(@Param("examId") String examId,@Param("qSet") String qSet,@Param("hashCode") String hashCode);

    void DelbyExamId(@Param("examId") String examId);
}
