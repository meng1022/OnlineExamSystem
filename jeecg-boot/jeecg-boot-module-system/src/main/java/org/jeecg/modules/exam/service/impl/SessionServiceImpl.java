package org.jeecg.modules.exam.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.exam.entity.Session;
import org.jeecg.modules.exam.mapper.SessionMapper;
import org.jeecg.modules.exam.service.ISessionService;
import org.springframework.stereotype.Service;

/**
 * @Description: 场次
 * @Author: jeecg-boot
 * @Date:   2019-08-07
 * @Version: V1.0
 */
@Service
public class SessionServiceImpl extends ServiceImpl<SessionMapper, Session> implements ISessionService {

}
