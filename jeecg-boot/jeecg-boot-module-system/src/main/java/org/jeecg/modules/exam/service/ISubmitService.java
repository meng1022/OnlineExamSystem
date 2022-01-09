package org.jeecg.modules.exam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.exam.entity.Submit;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.exam.vo.SubmitExamUserVo;

/**
 * @Description: 答题结果表
 * @Author: jeecg-boot
 * @Date:   2019-08-08
 * @Version: V1.0
 */
public interface ISubmitService extends IService<Submit> {
   public int dump2db(String examId);
   public IPage<SubmitExamUserVo> queryByExamId(Page<SubmitExamUserVo> page, String examId, String provcode);
}
