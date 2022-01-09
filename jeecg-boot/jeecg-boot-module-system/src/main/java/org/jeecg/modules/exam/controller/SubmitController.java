package org.jeecg.modules.exam.controller;

import java.text.SimpleDateFormat;
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
import org.docx4j.wml.R;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.exam.entity.Exam;
import org.jeecg.modules.exam.entity.Question;
import org.jeecg.modules.exam.entity.Submit;
import org.jeecg.modules.exam.service.IExamService;
import org.jeecg.modules.exam.service.IQuestionService;
import org.jeecg.modules.exam.service.ISubmitService;
import org.jeecg.modules.exam.vo.SubmitExamUserVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

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
 * @Description: 答题结果表
 * @Author: jeecg-boot
 * @Date:   2019-08-08
 * @Version: V1.0
 */
@Slf4j
@Api(tags="答题结果表")
@RestController
@RequestMapping("/exam/submit")
public class SubmitController {
	@Autowired
	private ISubmitService submitService;
	 @Autowired
	 private IExamService examService;
	 @Autowired
	 private IQuestionService questionService;
	 @Autowired
	 private RedisUtil redisUtil;
	/*
	* 接收提交答卷
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
         long curTimeMillis = System.currentTimeMillis();
		 LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
		 if(sysUser!=null){
			 Exam exam = (Exam)(redisUtil.get(CommonConstant.PREFIX_EXAM_CACHE + examId));
			 if (oConvertUtils.isEmpty(exam)) {
				 exam = examService.getById(examId);
				 redisUtil.set(CommonConstant.PREFIX_EXAM_CACHE + examId, exam);
				 redisUtil.expire(CommonConstant.PREFIX_EXAM_CACHE + examId, CommonConstant.EXAM_CACHE_EXPIRE_TIME);
			 }

			 if(exam == null) { //考试不存在
				 return Result.error("考试码不存在!");
			 }else if(curTimeMillis < (exam.getExamStartTime().getTime() - 1000*60*2) || curTimeMillis > exam.getExamEndTime().getTime() +1000*60*10) { //当前不在开始时间范围
				 return Result.error("不在考试时间范围内!");
			 }else if( exam.getSubmitTimes() > 0 ) { //有提交次数限制
/*				 Map<String, Object> querymap = new HashMap<>();
				 querymap.put("exam_id", examId);
				 querymap.put("user_id", sysUser.getId());
				 Integer submitTimes = submitService.count(new QueryWrapper<Submit>().allEq(querymap));
				 */
				 Integer submitTimes = (Integer)(redisUtil.get(CommonConstant.PREFIX_SUBMIT_CACHE + examId+"_"+sysUser.getId()));
				 if (oConvertUtils.isEmpty(submitTimes)) {
					 submitTimes = 0;
					 redisUtil.set(CommonConstant.PREFIX_SUBMIT_CACHE + examId+"_"+sysUser.getId(), 0);
				 }
				 if(submitTimes >= exam.getSubmitTimes()) {
					 return Result.error("答题次数已经超过上限!");
				 }
			 }
//          Submit submit1 = new Submit();
//		 	submit1.setExamId(examId);
//		 	submit1.setUserId(sysUser.getId());
//		 	submit1.setResult(result);
//		 	submitService.save(submit1);

            JSONObject submit = new JSONObject();
            submit.put("uid",sysUser.getId());
            submit.put("res",result);
            submit.put("time",curTimeMillis);
            //先自增，比先插入要好。
			redisUtil.incr(CommonConstant.PREFIX_SUBMIT_CACHE + examId+"_"+sysUser.getId(),1);//答题次数增加1
            redisUtil.lSet(CommonConstant.PREFIX_SUBMIT_LIST+ examId,submit.toJSONString());
			return Result.ok("提交成功！");
		 }else{
			 return Result.error("未授权，请先登陆后再答题！");
		 }
	 }


     @RequestMapping(value = "/dump2db", method = RequestMethod.GET)
     @RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
     @ApiOperation("答案入口")
     public Result<Object> dump2db(@RequestParam(name="examId", defaultValue="") String  examId) {
	     int itemCount = submitService.dump2db(examId);
		 if (itemCount == -1)
		 	return Result.error("考试尚未结束，当前无法查看考试结果！");
		 else if(itemCount==0){
		 	return Result.error("没有考生交卷或答卷已经入库完毕！");
		 }
		 else
			 return Result.ok("提交成功！" + "条数：" + String.valueOf(itemCount));
	 }


     /**
	  * 分页列表查询
	 * @param submit
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "答题结果表-分页列表查询")
	@ApiOperation(value="答题结果表-分页列表查询", notes="答题结果表-分页列表查询")
	@GetMapping(value = "/list")
	@RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
	public Result<IPage<Submit>> queryPageList(Submit submit,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<Submit>> result = new Result<IPage<Submit>>();
		QueryWrapper<Submit> queryWrapper = QueryGenerator.initQueryWrapper(submit, req.getParameterMap());
		Page<Submit> page = new Page<Submit>(pageNo, pageSize);
		IPage<Submit> pageList = submitService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	 /**
	  * 分页列表查询
	  * @param examId
	  * @param provcode
	  * @param pageNo
	  * @param pageSize
	  * @param req
	  * @return
	  */
	 @AutoLog(value = "按考试id查询答题结果表-分页列表查询")
	 @ApiOperation(value="按考试id查询答题结果表-分页列表查询", notes="按考试id查询答题结果表-分页列表查询")
	 @RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
	 @GetMapping(value = "/queryByExamId")
	 public Result<IPage<SubmitExamUserVo>> queryByExamId(@RequestParam(name="examId", defaultValue="") String  examId,
														  @RequestParam(name="provcode", defaultValue="") String  provcode,
														  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
												@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
												HttpServletRequest req) {
		 Result<IPage<SubmitExamUserVo>> result = new Result<IPage<SubmitExamUserVo>>();
		 Page<SubmitExamUserVo> page = new Page<SubmitExamUserVo>(pageNo, pageSize);
		 IPage<SubmitExamUserVo> pageList = submitService.queryByExamId(page, examId, provcode);
		 result.setSuccess(true);
		 Question question = new Question().setExamId(examId);
		 question.setType("quest");
		 QueryWrapper<Question> queryWrapper = new QueryWrapper<Question>(question);
		 int questnum = questionService.count(queryWrapper);
		 result.setMessage(String.valueOf(questnum));//问答题的数量
		 result.setResult(pageList);
		 return result;
	 }

	/**
	  *   添加
	 * @param submit
	 * @return
	 */
	@AutoLog(value = "答题结果表-添加")
	@ApiOperation(value="答题结果表-添加", notes="答题结果表-添加")
	@RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
	@PostMapping(value = "/add")
	public Result<Submit> add(@RequestBody Submit submit) {
		Result<Submit> result = new Result<Submit>();
		try {
			submitService.save(submit);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}
	
	/**
	  *  编辑
	 * @param submit
	 * @return
	 */
	@AutoLog(value = "答题结果表-编辑")
	@ApiOperation(value="答题结果表-编辑", notes="答题结果表-编辑")
	@RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
	@PutMapping(value = "/edit")
	public Result<Submit> edit(@RequestBody Submit submit) {
		Result<Submit> result = new Result<Submit>();
		Submit submitEntity = submitService.getById(submit.getId());
		if(submitEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = submitService.updateById(submit);
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
	@AutoLog(value = "答题结果表-通过id删除")
	@ApiOperation(value="答题结果表-通过id删除", notes="答题结果表-通过id删除")
	@RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
	@DeleteMapping(value = "/delete")
	public Result<Submit> delete(@RequestParam(name="id",required=true) String id) {
		Result<Submit> result = new Result<Submit>();
		Submit submit = submitService.getById(id);
		if(submit==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = submitService.removeById(id);
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
	@AutoLog(value = "答题结果表-批量删除")
	@ApiOperation(value="答题结果表-批量删除", notes="答题结果表-批量删除")
	@RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
	@DeleteMapping(value = "/deleteBatch")
	public Result<Submit> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<Submit> result = new Result<Submit>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.submitService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}
	
	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "答题结果表-通过id查询")
	@ApiOperation(value="答题结果表-通过id查询", notes="答题结果表-通过id查询")
	@GetMapping(value = "/queryById")
	@RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
	public Result<Submit> queryById(@RequestParam(name="id",required=true) String id) {
		Result<Submit> result = new Result<Submit>();
		Submit submit = submitService.getById(id);
		if(submit==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(submit);
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
  @RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
      // Step.1 组装查询条件
      QueryWrapper<Submit> queryWrapper = null;
      try {
		  String examId = request.getParameter("examId");
		  Submit submit = new Submit();
          if (oConvertUtils.isNotEmpty(examId)) {
              String exam_id = URLDecoder.decode(examId, "UTF-8");
              submit.setExamId(exam_id);
              queryWrapper = QueryGenerator.initQueryWrapper(submit, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<Submit> pageList = submitService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "答题结果表列表");
      mv.addObject(NormalExcelConstants.CLASS, Submit.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("答题结果表列表数据", "导出人:Jeecg", "导出信息"));
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
              List<Submit> listSubmits = ExcelImportUtil.importExcel(file.getInputStream(), Submit.class, params);
              for (Submit submitExcel : listSubmits) {
                  submitService.save(submitExcel);
              }
              return Result.ok("文件导入成功！数据行数:" + listSubmits.size());
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
