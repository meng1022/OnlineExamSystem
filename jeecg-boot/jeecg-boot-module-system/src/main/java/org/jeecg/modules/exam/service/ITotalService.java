package org.jeecg.modules.exam.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.exam.entity.Total;
import org.jeecg.modules.exam.vo.TotalUserVo;

import java.util.List;

/**
 * @Description: 总成绩与排名表
 * @Author: jeecg-boot
 * @Date:   2019-08-08
 * @Version: V1.0
 */
public interface ITotalService extends IService<Total> {
    public boolean calcByExamName(String examName);
    public IPage<TotalUserVo> queryPageListByExamName(Page<TotalUserVo> page, String examName, String provcode);
    public List<TotalUserVo> queryListByExamName(String examName, String provcode);
    public List<TotalUserVo> queryListWithRank(String examName, Integer start, Integer end);
}
