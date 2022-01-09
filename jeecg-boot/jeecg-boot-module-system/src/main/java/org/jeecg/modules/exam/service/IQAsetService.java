package org.jeecg.modules.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.exam.entity.QAset;

import java.util.Map;

/**
 * @Description: 试题集合
 * @Author: jeecg-boot
 * @Date:   2019-08-08
 * @Version: V1.0
 */
public interface IQAsetService extends IService<QAset> {

    public QAset getQAsetByExamId(String examId);
    public boolean genQAsetByExamId(String examId, Map<String,Object> qAMap);
}
