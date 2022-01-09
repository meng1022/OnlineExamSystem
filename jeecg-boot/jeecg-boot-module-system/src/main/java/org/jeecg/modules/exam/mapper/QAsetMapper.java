package org.jeecg.modules.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.exam.entity.QAset;

/**
 * @Description: 试题集合
 * @Author: jeecg-boot
 * @Date:   2019-08-08
 * @Version: V1.0
 */
public interface QAsetMapper extends BaseMapper<QAset> {
    /**
     * 通过考试号查询试题信息
     * @param examId
     * @return
     */
    public QAset getQAsetByExamId(@Param("examId") String examId);

    /**
     * 根据examId设置QASet
     * @param examId
     * @param qASet
     */
    void updateQASet(@Param("examId") String examId, @Param("qASet") String qASet);


}
