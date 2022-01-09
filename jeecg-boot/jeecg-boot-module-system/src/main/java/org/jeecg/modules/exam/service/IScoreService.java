package org.jeecg.modules.exam.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.exam.entity.Score;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.exam.vo.ScoreExamUserVo;

/**
 * @Description: 成绩表
 * @Author: jeecg-boot
 * @Date:   2019-08-08
 * @Version: V1.0
 */
public interface IScoreService extends IService<Score> {
    public boolean grade1ByExamId(String examId);
    public boolean grade2ByExamId(String examId);
    public IPage<ScoreExamUserVo> queryByExamId(Page<ScoreExamUserVo> page, String examId, String provcode);
//    public IPage<ScoreExamUserVo> queryByExamName(Page<ScoreExamUserVo> page, String examName);
    public JSONArray getdata(String examId, float fullscore);
}
