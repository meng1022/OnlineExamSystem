package org.jeecg.modules.exam.controller;

import java.util.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.exam.entity.*;
import org.jeecg.modules.exam.service.IExamService;
import org.jeecg.modules.exam.service.IQsetService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.exam.service.ISubmitService;
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
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 /**
 * @Description: 试题集合
 * @Author: jeecg-boot
 * @Date:   2019-08-08
 * @Version: V1.0
 */
@Slf4j
@Api(tags="试题集合")
@RestController
@RequestMapping("/exam/qset")
public class QsetController {
	@Autowired
	private IQsetService qsetService;
	@Autowired
	private IExamService examService;
	@Autowired
    private ISubmitService submitService;

	@Autowired
	private RedisUtil redisUtil;

	 @RequestMapping(value = "/getQsetByExamId", method = RequestMethod.GET)
	 public Result<Map> getQsetByExamId(@RequestParam(name="examId", defaultValue="") String  examId) {

		 Result<Map> result = new Result<Map>();
         result.setSuccess(true);
		 Exam exam = (Exam)(redisUtil.get(CommonConstant.PREFIX_EXAM_CACHE + examId));
		 if (oConvertUtils.isEmpty(exam)) {
			 exam = examService.getById(examId);
			 redisUtil.set(CommonConstant.PREFIX_EXAM_CACHE + examId, exam);
			 redisUtil.expire(CommonConstant.PREFIX_EXAM_CACHE + examId, CommonConstant.EXAM_CACHE_EXPIRE_TIME);
		 }
         long curTimeMillis = System.currentTimeMillis();
		 if(exam == null) { //考试不存在
			 result.setSuccess(false);
			 result.setMessage("考试码不存在！");
		 }else if(curTimeMillis < (exam.getExamStartTime().getTime() - 1000*60*6) || curTimeMillis > exam.getExamEndTime().getTime() ) { //当前不再开始时间范围
             result.setSuccess(false);
             result.setMessage("不在考试时间范围内！");
		 }else if( exam.getSubmitTimes() > 0 ) { //有提交次数限制
             LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
             if(sysUser!=null) {
             	/*
                 Map<String, Object> map = new HashMap<>();
                 map.put("exam_id", examId);
                 map.put("user_id", sysUser.getId());
                 Integer submitTimes = submitService.count(new QueryWrapper<Submit>().allEq(map));
                 */

				 Integer submitTimes = (Integer)(redisUtil.get(CommonConstant.PREFIX_SUBMIT_CACHE + examId+"_"+sysUser.getId()));
				 if (oConvertUtils.isEmpty(submitTimes)) {
					 submitTimes = 0;
				 }
				 if(submitTimes >= exam.getSubmitTimes()) {
                     result.setSuccess(false);
                     result.setMessage("答题次数已经超过上限！");
                 }
             }else {
                 result.setSuccess(false);
                 result.setMessage("Token已失效，请重新登录！");
             }
         }
         if(result.isSuccess()) {
			 Qset qset =(Qset)(redisUtil.get(CommonConstant.PREFIX_QSET_CACHE + examId));
			 if (oConvertUtils.isEmpty(qset)) {
				 qset = qsetService.getQsetByExamId(examId);
				 redisUtil.set(CommonConstant.PREFIX_QSET_CACHE + examId, qset);
				 redisUtil.expire(CommonConstant.PREFIX_QSET_CACHE + examId, CommonConstant.EXAM_CACHE_EXPIRE_TIME);
			 }
             Map map = new HashMap();
             map.put("qset", qset.getQset());
             map.put("hashcode", qset.getHashCode());
             map.put("servercache",exam.getSubmitServerCache());
             result.setResult(map);
         }
		 return result;
	 }


	 @RequiresRoles(value = { "admin","DESIGNER" },logical = Logical.OR )
	 @RequestMapping(value = "/genQsetByExamId", method = RequestMethod.GET)
	 public Result<JSONObject> genQsetByExamId(@RequestParam(name="examId", defaultValue="") String  examId) {
		 Result<JSONObject> result = new Result<JSONObject>();
		 boolean success = qsetService.genQsetByExamId(examId);
		 if(success){
		 	redisUtil.del(CommonConstant.PREFIX_QSET_CACHE + examId);
			 result.success("试题集生成成功！");
		 }else{
			 result.error500("试题集生成失败！");
		 }
		 return result;
	 }



	 @ApiOperation(value="设置考试答题开始时间", notes="设置考试答题开始时间")
	 @RequestMapping(value = "/setStartAnswerTime", method = RequestMethod.POST)
	 public Result<JSONObject> setStartAnswerTime(@RequestParam(name="examId") String examId){
		 Result<JSONObject> result = new Result<JSONObject>();
		 Map map=new HashMap();
		 LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
		 long curTimeMillis = System.currentTimeMillis();
		 Object o = redisUtil.get(CommonConstant.START_ANSWER_TIME + examId + sysUser.getId());
		 if(oConvertUtils.isNotEmpty(o)){
			 Long startAnswerTimes = Long.valueOf(String.valueOf(o));
			 result.setSuccess(true);
			 result.setMessage("考生考试时间");
			 map.put("startAnswerTime",startAnswerTimes);
			 String s = JSON.toJSONString(map);
			 JSONObject maps = JSON.parseObject(s);
			 result.setResult(maps);
		 }
		 else{
			 if (sysUser!=null) {
				 boolean set = redisUtil.set(CommonConstant.START_ANSWER_TIME + examId + sysUser.getId(), curTimeMillis);
				 if (set == false) {
					 result.setSuccess(false);
					 result.setMessage("考生考试时间设置失败");
					 return result;
				 }
//            redisUtil.expire(CommonConstant.START_ANSWER_TIME + subjectId + sysUser.getId(), CommonConstant.START_ANSWER_EXPIRE_TIME);
				 result.setSuccess(set);
				 result.setMessage("考生考试时间设置成功");
				 map.put("startAnswerTime",curTimeMillis);
				 String s = JSON.toJSONString(map);
				 JSONObject maps = JSON.parseObject(s);
				 result.setCode(200);
				 result.setResult(maps);
				 return result;
			 }else {
				 result.setSuccess(false);
				 result.setMessage("未获取到考试信息");
			 }
		 }
		 return result;
	 }

	 /**
	  * 分页列表查询
	 * @param qset
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "试题集合-分页列表查询")
	@ApiOperation(value="试题集合-分页列表查询", notes="试题集合-分页列表查询")
	@RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
	@GetMapping(value = "/list")
	public Result<IPage<Qset>> queryPageList(Qset qset,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<Qset>> result = new Result<IPage<Qset>>();
		QueryWrapper<Qset> queryWrapper = QueryGenerator.initQueryWrapper(qset, req.getParameterMap());
		Page<Qset> page = new Page<Qset>(pageNo, pageSize);
		IPage<Qset> pageList = qsetService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}



	/**
	  *   添加
	 * @param qset
	 * @return
	 */
	@AutoLog(value = "试题集合-添加")
	@ApiOperation(value="试题集合-添加", notes="试题集合-添加")
	@PostMapping(value = "/add")
	@RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
	public Result<Qset> add(@RequestBody Qset qset) {
		Result<Qset> result = new Result<Qset>();
		try {
			qsetService.save(qset);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}
	
	/**
	  *  编辑
	 * @param qset
	 * @return
	 */
	@AutoLog(value = "试题集合-编辑")
	@ApiOperation(value="试题集合-编辑", notes="试题集合-编辑")
	@RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
	@PutMapping(value = "/edit")
	public Result<Qset> edit(@RequestBody Qset qset) {
		Result<Qset> result = new Result<Qset>();
		Qset qsetEntity = qsetService.getById(qset.getId());
		if(qsetEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = qsetService.updateById(qset);
			//TODO 返回false说明什么？
			if(ok) {
				result.success("修改成功!");
			}
		}
		
		return result;
	}
	
	/**
	  *   通过id删除
	 * @param id
	 * @return
	 */
	@AutoLog(value = "试题集合-通过id删除")
	@ApiOperation(value="试题集合-通过id删除", notes="试题集合-通过id删除")
	@RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
	@DeleteMapping(value = "/delete")
	public Result<Qset> delete(@RequestParam(name="id",required=true) String id) {
		Result<Qset> result = new Result<Qset>();
		Qset qset = qsetService.getById(id);
		if(qset==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = qsetService.removeById(id);
			if(ok) {
				result.success("删除成功!");
			}
		}
		
		return result;
	}
	
	/**
	  *  批量删除
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "试题集合-批量删除")
	@ApiOperation(value="试题集合-批量删除", notes="试题集合-批量删除")
	@RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
	@DeleteMapping(value = "/deleteBatch")
	public Result<Qset> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<Qset> result = new Result<Qset>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.qsetService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}
	
	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "试题集合-通过id查询")
	@ApiOperation(value="试题集合-通过id查询", notes="试题集合-通过id查询")
	@RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
	@GetMapping(value = "/queryById")
	public Result<Qset> queryById(@RequestParam(name="id",required=true) String id) {
		Result<Qset> result = new Result<Qset>();
		Qset qset = qsetService.getById(id);
		if(qset==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(qset);
			result.setSuccess(true);
		}
		return result;
	}


  /**
      * 导出excel
   *
   * @param request
   * @param response
   */
  @RequestMapping(value = "/exportXls")
  @RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
  public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
      // Step.1 组装查询条件
      QueryWrapper<Qset> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              Qset qset = JSON.parseObject(deString, Qset.class);
              queryWrapper = QueryGenerator.initQueryWrapper(qset, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<Qset> pageList = qsetService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "试题集合列表");
      mv.addObject(NormalExcelConstants.CLASS, Qset.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("试题集合列表数据", "导出人:Jeecg", "导出信息"));
      mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
      return mv;
  }

  /**
      * 通过excel导入数据
   *
   * @param request
   * @param response
   * @return
   */
  @RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
  @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
  public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
      for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
          MultipartFile file = entity.getValue();// 获取上传文件对象
          ImportParams params = new ImportParams();
          params.setTitleRows(2);
          params.setHeadRows(1);
          params.setNeedSave(true);
          try {
              List<Qset> listQsets = ExcelImportUtil.importExcel(file.getInputStream(), Qset.class, params);
              for (Qset qsetExcel : listQsets) {
                  qsetService.save(qsetExcel);
              }
              return Result.ok("文件导入成功！数据行数:" + listQsets.size());
          } catch (Exception e) {
              log.error(e.getMessage(),e);
              return Result.error("文件导入失败:"+e.getMessage());
          } finally {
              try {
                  file.getInputStream().close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      }
      return Result.ok("文件导入失败！");
  }

}
