package org.jeecg.modules.exam.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.exam.entity.SCache;
import org.jeecg.modules.exam.entity.Submit;
import org.jeecg.modules.exam.service.ISCacheService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @Description: 答题结果表
* @Author: jeecg-boot
* @Date:   2019-08-08
* @Version: V1.0
*/
@Slf4j
@Api(tags="答题结果缓存表")
@RestController
@RequestMapping("/exam/scache")
public class SCacheController {
   @Autowired
   private ISCacheService scacheService;

   /*
   * 接收提交答卷缓存
   *
   * @return
   */
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    @ApiOperation("提交答卷接口")
    public Result<Object> submit(@RequestBody Map<String, String> map) {
        String examId = "";
        String result = "";
        if(map.containsKey("examId")){
            examId = map.get("examId").toString();
        }
        if(map.containsKey("result")){
            result = map.get("result").toString();
        }
        LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
        if(sysUser!=null){
            SCache scache = new SCache();
            scache.setExamId(examId);
            scache.setUserId(sysUser.getId());
            scache.setResult(result);
            scacheService.save(scache);
           return Result.ok("提交成功！");
        }else{
            return Result.error("未授权，请先登陆后再提交！");
        }
    }

    @RequestMapping(value = "/getSCacheByExamId", method = RequestMethod.GET)
    public Result<Map> getSCacheByExamId(@RequestParam(name="examId", defaultValue="") String  examId) {
        Result<Map> result = new Result<Map>();
        LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
        if(sysUser!=null){
            Map<String, Object> map = new HashMap<>();
            map.put("exam_id", examId);
            map.put("user_id", sysUser.getId());
            SCache scache = scacheService.getOne(new QueryWrapper<SCache>().allEq(map).orderByDesc("create_time"));
            if(scache != null) {
                result.setSuccess(true);
                result.setMessage("获取成功");
                Map resultmap = new HashMap();
                resultmap.put("result", scache.getResult());
                result.setResult(resultmap);
            }else{
                result.setSuccess(false);
                result.setMessage("不存在缓存结果！");
            }
        }else{
            result.setSuccess(false);
            result.setMessage("未授权，请先登陆后再答题！");
        }

        return result;
    }


}
