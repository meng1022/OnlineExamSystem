package org.jeecg.modules.exam.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.exam.entity.Score;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.exam.vo.ScoreExamUserVo;

import java.util.List;

/**
 * @Description: 成绩表
 * @Author: jeecg-boot
 * @Date:   2019-08-08
 * @Version: V1.0
 */
public interface ScoreMapper extends BaseMapper<Score> {
    IPage<ScoreExamUserVo> queryByExamId(Page<ScoreExamUserVo> page, @Param("examId") String examId, @Param("provcode") String provcode);
    List<ScoreExamUserVo> queryByExamName(@Param("examName") String examName);
    void setScore();
    Integer getdata1(@Param("examId") String examId,@Param("score1")double score1,@Param("score2")double score2);
    Integer getdata2(@Param("examId") String examId,@Param("score1")double score1,@Param("score2")double score2);

}
