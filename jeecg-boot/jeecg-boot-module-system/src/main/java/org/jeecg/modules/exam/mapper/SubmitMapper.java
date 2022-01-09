package org.jeecg.modules.exam.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.exam.entity.Submit;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.exam.vo.SubmitExamUserVo;

/**
 * @Description: 答题结果表
 * @Author: jeecg-boot
 * @Date:   2019-08-08
 * @Version: V1.0
 */
public interface SubmitMapper extends BaseMapper<Submit> {

    /**
     *  查询每场考试每位考生最后的提交
     * @param examId
     * @return
     */
    List<Submit> getLastSubmitsByExamId(@Param("examId") String examId);
    IPage<SubmitExamUserVo> queryByExamId(Page<SubmitExamUserVo> page, @Param("examId") String examId, @Param("provcode") String provcode);
    void deleteall(@Param("examId")String examId);
}
