package org.jeecg.modules.exam.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.exam.entity.SCache;
import org.jeecg.modules.exam.mapper.SCacheMapper;
import org.jeecg.modules.exam.service.ISCacheService;
import org.jeecg.modules.exam.vo.SubmitExamUserVo;
import org.springframework.stereotype.Service;

/**
 * @Description: 答题结果缓存表
 * @Author: jeecg-boot
 * @Date:   2019-08-08
 * @Version: V1.0
 */
@Service
public class SCacheServiceImpl extends ServiceImpl<SCacheMapper, SCache> implements ISCacheService {
}
