package org.jeecg.modules.exam.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.exam.entity.Exam;
import org.jeecg.modules.exam.entity.Session;
import org.jeecg.modules.exam.mapper.QuestionMapper;
import org.jeecg.modules.exam.service.ISessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;


/**
* @Description: 考试
* @Author: jeecg-boot
* @Date:   2019-08-07
* @Version: V1.0
 * //
*/
@Slf4j
@Api(tags="考试")
@RestController
@RequestMapping("/exam/session")
public class SessionController {
   @Autowired
   private ISessionService sessionService;
   /**
     * 分页列表查询
    * @param session
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   @AutoLog(value = "场次-分页列表查询")
   @ApiOperation(value="场次-分页列表查询", notes="场次-分页列表查询")
   @GetMapping(value = "/list")
   public Result<JSONObject> queryPageList(Session session,
                                           @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                           @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                           HttpServletRequest req) {
       Result<JSONObject> result = new Result<JSONObject>();
       JSONObject resultobj = new JSONObject();
       if (session == null) {
           Page<Session> page = new Page<Session>(pageNo, pageSize);
           IPage<Session> pageList = sessionService.page(page, new QueryWrapper<Session>().orderBy(true,true, "sort_no"));
           resultobj.put("current",pageList.getCurrent());
           resultobj.put("pages",pageList.getPages());
           resultobj.put("searchCount",pageList.isSearchCount());
           resultobj.put("size",pageList.getSize());
           resultobj.put("total",pageList.getTotal());
           JSONArray records = new JSONArray();
           for(Session isession : pageList.getRecords()){
               JSONObject record = new JSONObject();
               record.put("sessionName",isession.getSessionName());
               records.add(record);
           }
           resultobj.put("records",records);
       }else{
               Page<Session> page = new Page<Session>(pageNo, pageSize);
               QueryWrapper<Session> queryWrapper = QueryGenerator.initQueryWrapper(session, req.getParameterMap());
               IPage<Session> pageList = sessionService.page(page, queryWrapper);
               resultobj.put("current",pageList.getCurrent());
               resultobj.put("pages",pageList.getPages());
               resultobj.put("searchCount",pageList.isSearchCount());
               resultobj.put("size",pageList.getSize());
               resultobj.put("total",pageList.getTotal());
               JSONArray records = new JSONArray();
               for(Session isession : pageList.getRecords()){
                   JSONObject record = new JSONObject();
                   record.put("sessionName",isession.getSessionName());
                   records.add(record);
               }
               resultobj.put("records",records);
           }
       result.setSuccess(true);
       result.setResult(resultobj);
       return result;
   }

    @AutoLog(value = "科目-添加")
    @ApiOperation(value="科目-添加", notes="科目-添加")
    @RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
    @PostMapping(value = "/add")
    public Result<Session> add(@RequestBody Session session) {
        Result<Session> result = new Result<Session>();
        Session session_ =new Session();
        session_.setSessionName(session.getSessionName());
        QueryWrapper<Session> queryWrapper = new QueryWrapper<Session>(session_);
        if(sessionService.count(queryWrapper)==0){//场次名称即科目是否存在
            sessionService.save(session);
            result.success("添加成功！");
            result.setResult(session);
        }
        else {
            result.error500("试题库已存在！");
        }
        return result;
    }

    @AutoLog(value = "科目-通过名称删除")
    @ApiOperation(value="科目-通过名称删除", notes="科目-通过名称删除")
    @RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
    @DeleteMapping(value = "/delete")
    public Result<Session> delete(@RequestParam(name="id",required=true) String sessionName) {
        Result<Session> result = new Result<Session>();
        Session session_ = new Session();
        session_.setSessionName(sessionName);
        QueryWrapper<Session> queryWrapper = new QueryWrapper<Session>(session_);
        if(sessionService.getOne(queryWrapper)==null) {
            result.error500("未找到对应实体");
        }else {
            boolean ok = sessionService.remove(queryWrapper);
            if(ok) {
                result.success("删除成功!");
            }
        }

        return result;
    }

    @AutoLog(value = "科目-批量删除")
    @ApiOperation(value="科目-批量删除", notes="科目-批量删除")
    @RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
    @DeleteMapping(value = "/deleteBatch")
    public Result<Session> deleteBatch(@RequestParam(name="ids",required=true) String sessionNames) {
        Result<Session> result = new Result<Session>();
        if(sessionNames==null || "".equals(sessionNames.trim())) {
            result.error500("参数不识别！");
        }else {
            List<String> sessionnames = Arrays.asList(sessionNames.split(","));
            for(int i = 0;i<sessionnames.size();i++){
                Session session = new Session().setSessionName(sessionnames.get(i));
                QueryWrapper<Session> queryWrapper = new QueryWrapper<Session>(session);
                sessionService.remove(queryWrapper);
            }
            result.success("删除成功!");
        }
        return result;
    }



}
