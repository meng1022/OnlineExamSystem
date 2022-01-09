package org.jeecg.modules.exam.service.impl;

import org.jeecg.modules.exam.entity.Exam;
import org.jeecg.modules.exam.mapper.ExamMapper;
import org.jeecg.modules.exam.service.IExamService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 考试
 * @Author: jeecg-boot
 * @Date:   2019-08-07
 * @Version: V1.0
 */
@Service
public class ExamServiceImpl extends ServiceImpl<ExamMapper, Exam> implements IExamService {

}
