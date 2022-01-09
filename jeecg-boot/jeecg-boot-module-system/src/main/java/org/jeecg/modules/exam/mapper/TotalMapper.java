package org.jeecg.modules.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.exam.entity.Total;
import org.jeecg.modules.exam.vo.TotalUserVo;

import java.util.List;

/**
 * @Description: 总成绩与排名表
 * @Author: jeecg-boot
 * @Date:   2019-08-08
 * @Version: V1.0
 */
public interface TotalMapper extends BaseMapper<Total> {
 IPage<TotalUserVo> queryPageListByExamName(Page<TotalUserVo> page, @Param("examName") String examName, @Param("provcode") String provcode);
 List<TotalUserVo> queryListByExamName(@Param("examName") String examName, @Param("provcode") String provcode);
 List<TotalUserVo> queryListWithRank(@Param("examName") String examName, @Param("start") Integer start, @Param("end") Integer end);

}
