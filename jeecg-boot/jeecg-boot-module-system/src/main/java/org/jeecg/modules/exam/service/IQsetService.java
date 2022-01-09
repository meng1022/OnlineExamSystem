package org.jeecg.modules.exam.service;

import org.jeecg.modules.exam.entity.Qset;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 试题集合
 * @Author: jeecg-boot
 * @Date:   2019-08-08
 * @Version: V1.0
 */
public interface IQsetService extends IService<Qset> {

    public Qset getQsetByExamId(String examId);
    public boolean genQsetByExamId(String examId);
    public float getFullScore(String examId);
}
