package org.jeecg.modules.system.controller;

        import java.util.*;

        import javax.servlet.http.HttpServletRequest;
        import javax.servlet.http.HttpServletResponse;

        import org.apache.shiro.SecurityUtils;
        import org.apache.shiro.subject.Subject;
        import org.jeecg.common.api.vo.Result;
        import org.jeecg.common.constant.CommonConstant;
        import org.jeecg.common.system.api.ISysBaseAPI;
        import org.jeecg.common.system.util.JwtUtil;
        import org.jeecg.common.util.*;
        import org.jeecg.modules.system.entity.SysDepart;
        import org.jeecg.modules.system.entity.SysUser;
        import org.jeecg.modules.system.model.SysRegisterModel;
        import org.jeecg.modules.system.model.SysResetModel;
        import org.jeecg.modules.system.model.SysSmsModel;
        import org.jeecg.modules.system.service.ISysDepartService;
        import org.jeecg.modules.system.service.ISysLogService;
        import org.jeecg.modules.system.service.ISysUserService;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.web.bind.annotation.GetMapping;
        import org.springframework.web.bind.annotation.RequestBody;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RequestMethod;
        import org.springframework.web.bind.annotation.RestController;

        import com.alibaba.fastjson.JSONObject;

        import io.swagger.annotations.Api;
        import io.swagger.annotations.ApiOperation;
        import lombok.extern.slf4j.Slf4j;


        import com.aliyuncs.CommonRequest;
        import com.aliyuncs.CommonResponse;
        import com.aliyuncs.DefaultAcsClient;
        import com.aliyuncs.IAcsClient;
        import com.aliyuncs.exceptions.ClientException;
        import com.aliyuncs.exceptions.ServerException;
        import com.aliyuncs.http.MethodType;
        import com.aliyuncs.profile.DefaultProfile;

/**
 * @Author wzl
 * @since 2019-07-24
 */
@RestController
@RequestMapping("/sys")
@Api(tags="用户注册")
@Slf4j
public class RegisterController {
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ISysBaseAPI sysBaseAPI;
    @Autowired
    private ISysLogService logService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ISysDepartService sysDepartService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ApiOperation("注册接口")
    public Result<JSONObject> register(@RequestBody SysRegisterModel sysRegisterModel) {
        Result<JSONObject> result = new Result<JSONObject>();
        String username = sysRegisterModel.getPhone();
        SysUser sysUser = sysUserService.getUserByName(username);
        if(sysUser!=null) {
            result.error500("该手机号已注册");
            sysBaseAPI.addLog("注册失败，手机号:"+username+"已存在！", CommonConstant.LOG_TYPE_3, null);
            return result;
        }else {
            //手机验证码校验
            String captcha = sysRegisterModel.getCaptcha();
            String cacheCaptcha = String.valueOf(redisUtil.get(CommonConstant.PREFIX_SMS_CAPTCHA + username));
            if (oConvertUtils.isEmpty(cacheCaptcha)) {
                result.error500("手机验证码已失效");
                return result;
            }
            if (!captcha.equals(cacheCaptcha)) {
                result.error500("手机验证码错误");
                return result;
            }
            //生成token
          //  String token = JwtUtil.sign(username, syspassword);
         //   redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
            //设置超时时间
          //  redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME / 1000);

            try {
                SysUser user = new SysUser();
                user.setUsername(username);
                user.setPhone(username);
                user.setPassword(sysRegisterModel.getPassword());
                user.setRealname(sysRegisterModel.getRealname());
                user.setIdcard(sysRegisterModel.getIdcard());
                user.setEmail(sysRegisterModel.getEmail());
                user.setProvcode(sysRegisterModel.getProvcode());
                user.setCitycode(sysRegisterModel.getCitycode());
                user.setOrgname(sysRegisterModel.getOrgname());
                user.setOrgtype(sysRegisterModel.getOrgtype());
                user.setJobtype(sysRegisterModel.getJobtype());
                user.setProftitle(sysRegisterModel.getProftitle());
                user.setEdu(sysRegisterModel.getEdu());
                user.setOrgCode("A01A06");

                Map<String,String> idcardMap = IDCardUtil.getBirAgeSex(sysRegisterModel.getIdcard());
                if(idcardMap.containsKey("birthday")){
                    user.setBirthday(DateUtils.str2Date(idcardMap.get("birthday"),DateUtils.date_sdf));
                }
                if(idcardMap.containsKey("sexCode")){
                    if(idcardMap.get("sexCode").equals("M")){
                        user.setSex(1);
                    }else{
                        user.setSex(2);
                    }
                    user.setBirthday(DateUtils.str2Date(idcardMap.get("birthday"),DateUtils.date_sdf));
                }


                user.setCreateTime(new Date());//设置创建时间
                String salt = oConvertUtils.randomGen(8);
                user.setSalt(salt);
                String passwordEncode = PasswordUtil.encrypt(user.getUsername(), user.getPassword(), salt);
                user.setPassword(passwordEncode);
                user.setStatus(1);
                user.setDelFlag("0");
                sysUserService.addUserWithRoleAndDepart(user, "1","1");
                result.success("添加成功！");
                sysBaseAPI.addLog("用户名: " + username + ",注册成功！", CommonConstant.LOG_TYPE_3, null);

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                result.error500("操作失败");
                sysBaseAPI.addLog("用户名: " + username + ",注册失败！", CommonConstant.LOG_TYPE_3, null);

            }
            return result;
        }

    }


    @RequestMapping(value = "/reset", method = RequestMethod.POST)
    @ApiOperation("密码找回接口")
    public Result<JSONObject> reset(@RequestBody SysResetModel sysResetModel) {
        Result<JSONObject> result = new Result<JSONObject>();
        String username = sysResetModel.getPhone();
        SysUser sysUser = sysUserService.getUserByName(username);
        if(sysUser ==null) {
            result.error500("该手机号未注册");
            sysBaseAPI.addLog("重置密码失败，手机号:"+username+"不存在！", CommonConstant.LOG_TYPE_4, null);
            return result;
        }else {
            //手机验证码校验
            String captcha = sysResetModel.getCaptcha();
            String cacheCaptcha = String.valueOf(redisUtil.get(CommonConstant.PREFIX_SMS_CAPTCHA + username));
            if (oConvertUtils.isEmpty(cacheCaptcha)) {
                result.error500("手机验证码已失效");
                return result;
            }
            if (!captcha.equals(cacheCaptcha)) {
                result.error500("手机验证码错误");
                return result;
            }

            try {
                String password = sysResetModel.getPassword();
                String salt = oConvertUtils.randomGen(8);
                sysUser.setSalt(salt);
                String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(), password, salt);
                sysUser.setPassword(passwordEncode);
                sysUserService.updateById(sysUser);
                result.success("密码修改完成！");
                sysBaseAPI.addLog("用户名: " + username + ",重置密码成功！", CommonConstant.LOG_TYPE_4, null);

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                result.error500("操作失败");
                sysBaseAPI.addLog("用户名: " + username + ",重置密码失败！", CommonConstant.LOG_TYPE_4, null);

            }
            return result;
        }

    }

    @RequestMapping(value = "/sms", method = RequestMethod.POST)
    @ApiOperation("短信接口")
    public Result<JSONObject> sms(@RequestBody SysSmsModel sysSmsModel) {
        Result<JSONObject> result = new Result<JSONObject>();
        String phone = sysSmsModel.getPhone();
        String smstype = sysSmsModel.getSmstype();

        if(phone.length() != 11) {
            result.error500("手机号码错误");
            return result;
        }else if(!smstype.equals("1") &&  !smstype.equals("2")){
            result.error500("验证码类型设置错误");
            return result;
        }else{
            //检查手机号是否存在
            SysUser sysUser = sysUserService.getUserByName(phone);
            if(sysUser!=null && smstype.equals("1")) {
                result.error500("该手机号已注册");
                return result;
            }else  if(sysUser ==null && smstype.equals("2")){
                result.error500("该手机号未注册");
                return result;
            }
             //手机验证码校验
            String cacheSmsLimit = String.valueOf(redisUtil.get(CommonConstant.PREFIX_SMS_LIMIT + phone));
            int smsCount = 0;
            if (oConvertUtils.isNotEmpty(cacheSmsLimit))
                smsCount = oConvertUtils.getInt(cacheSmsLimit);
                if (smsCount >= CommonConstant.SMS_LIMIT) {
                result.error500("手机验证码发送次数超出限制，请稍后再试");
                return result;
            }
            smsCount++;
            redisUtil.set(CommonConstant.PREFIX_SMS_LIMIT + phone, smsCount);
            redisUtil.expire(CommonConstant.PREFIX_SMS_LIMIT + phone, CommonConstant.SMS_LIMIT_EXPIRE_TIME);

            //生成验证码
            String captcha = oConvertUtils.randomNumGen(6);
            redisUtil.set(CommonConstant.PREFIX_SMS_CAPTCHA + phone, captcha);
            //设置超时时间
            redisUtil.expire(CommonConstant.PREFIX_SMS_CAPTCHA + phone, CommonConstant.SMS_CAPTCHA_EXPIRE_TIME );

            DefaultProfile profile = DefaultProfile.getProfile("default", "LTAIoczv8NyaDE17", "78kNKLnteaQU6zKM4bnBXd9bMZSd9b");
            IAcsClient client = new DefaultAcsClient(profile);

            CommonRequest request = new CommonRequest();
            request.setMethod(MethodType.POST);
            request.setDomain("dysmsapi.aliyuncs.com");
            request.setVersion("2017-05-25");
            request.setAction("SendSms");
            request.putQueryParameter("RegionId", "default");
            request.putQueryParameter("SignName", "临床诊疗技能竞赛系统");
            if(smstype.equals("1")){
                request.putQueryParameter("TemplateCode", "SMS_128025033");
            }else if(smstype.equals("2")) {
                request.putQueryParameter("TemplateCode", "SMS_128025032");
            }
            request.putQueryParameter("PhoneNumbers", phone);
            JSONObject templateParam = new JSONObject();
            templateParam.put("code",captcha);
            request.putQueryParameter("TemplateParam", templateParam.toJSONString());

            try {
                CommonResponse response = client.getCommonResponse(request);
                System.out.println(response.getData());
                result.success("手机验证码发送成功");
            } catch (ServerException e) {
                e.printStackTrace();
                result.error500("手机验证码发送失败");
            } catch (ClientException e) {
                e.printStackTrace();
                result.error500("手机验证码发送失败");
            }


            return result;
        }

    }
}

