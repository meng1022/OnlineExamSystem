package org.jeecg.modules.exam.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.exam.entity.Question;
import org.jeecg.modules.exam.entity.Question1;
import org.jeecg.modules.exam.service.IQuestionbaseService;
import org.jeecg.modules.exam.service.IQuestionService;
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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 /**
 * @Description: 试题表
 * @Author: jeecg-boot
 * @Date:   2019-08-08
 * @Version: V1.0
 */
@Slf4j
@Api(tags="试题表")
@RestController
@RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER" },logical = Logical.OR )
@RequestMapping("/exam/question")
public class QuestionController {
	@Autowired
	private IQuestionService questionService;
	 @Autowired
	 private IQuestionbaseService question1Service;
	/**
	  * 分页列表查询
	 * @param question
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "试题表-分页列表查询")
	@ApiOperation(value="试题表-分页列表查询", notes="试题表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<Question>> queryPageList(Question question,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<Question>> result = new Result<IPage<Question>>();
		QueryWrapper<Question> queryWrapper = QueryGenerator.initQueryWrapper(question, req.getParameterMap());
		Page<Question> page = new Page<Question>(pageNo, pageSize);
		IPage<Question> pageList = questionService.page(page, queryWrapper.orderByAsc("sort_no"));
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}


	 @ApiOperation(value="试卷", notes="试卷")
	 @GetMapping(value ="/queryPaper")
	 public List<Question> queryPaper(@RequestParam(name="examid") String examid){
		 List<Question> paper = questionService.questionlist(examid);
		 return paper;
	 }

	 @ApiOperation(value="生成", notes="生成")
	 @PostMapping(value ="/adds")
	 public Result<Question> adds(@RequestBody String json){
		 Result<Question> result = new Result<Question>();
		 if(questionService.paperGen(json)==1){
		 	result.success("试卷生成成功！");
		 }
		 else if(questionService.paperGen(json)==2){
		 	result.error500("考试已经开放，不允许修改试卷");
		 }
		 else{
		 	result.error500("试卷生成失败！");
		 }
		 return result;
	 }



	/**
	  *   添加
	 * @param question
	 * @return
	 */
	@AutoLog(value = "试题表-添加")
	@ApiOperation(value="试题表-添加", notes="试题表-添加")
	@PostMapping(value = "/add")
	public Result<Question> add(@RequestBody Question question) {
		Result<Question> result = new Result<Question>();
		try {
			questionService.save(question);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}
	
	/**
	  *  编辑
	 * @param question
	 * @return
	 */
	@AutoLog(value = "试题表-编辑")
	@ApiOperation(value="试题表-编辑", notes="试题表-编辑")
	@PutMapping(value = "/edit")
	public Result<Question> edit(@RequestBody Question question) {
		Result<Question> result = new Result<Question>();
		Question questionEntity = questionService.getById(question.getId());
		if(questionEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = questionService.updateById(question);
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
	 @AutoLog(value = "试题表-通过id删除")
	 @ApiOperation(value="试题表-通过id删除", notes="试题表-通过id删除")
	 @DeleteMapping(value = "/delete")
	 public Result<Question> delete(@RequestParam(name="id",required=true) String id) {
		 Result<Question> result = new Result<Question>();
		 Question question = questionService.getById(id);
		 if(question==null) {
			 result.error500("未找到对应实体");
		 }else {
			 boolean ok = questionService.removeById(id);
			 if(ok) {
				 result.success("删除成功!");
			 }
		 }

		 return result;
	 }
	 /**
	  *  批量添加
	  * @param ids
	  * @return
	  */
	 @AutoLog(value = "试题表-批量新增")
	 @ApiOperation(value="试题表-批量新增", notes="试题表-批量新增")
	 @DeleteMapping(value = "/addfromquestionbase")
	 public Result<Question> addBatch(@RequestParam(name="ids",required=true) String ids,
									  @RequestParam(name = "examid",required = true)String examid) {
		 Result<Question> result = new Result<Question>();
		 if(ids==null || "".equals(ids.trim())) {
			 result.error500("参数不识别！");
		 }else {
		 	String[] IDs = ids.split(",");
		 	Question1 question1 = new Question1();
		 	for(int i = 0;i<IDs.length;i++){
		 		question1 = question1Service.getById(IDs[i]);
				Question question = questionService.setquestion(examid,question1);
				questionService.save(question);
			}
		 	result.success("试题添加成功!");
		 }
		 return result;
	 }

	/**
	  *  批量删除
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "试题表-批量删除")
	@ApiOperation(value="试题表-批量删除", notes="试题表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<Question> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<Question> result = new Result<Question>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.questionService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}
	
	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "试题表-通过id查询")
	@ApiOperation(value="试题表-通过id查询", notes="试题表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<Question> queryById(@RequestParam(name="id",required=true) String id) {
		Result<Question> result = new Result<Question>();
		Question question = questionService.getById(id);
		if(question==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(question);
			result.setSuccess(true);
		}
		return result;
	}

	 @RequestMapping(value = "/exportXlsTemplate")
	 public ModelAndView exportXlsTemplate(HttpServletRequest request, HttpServletResponse response) {
		 // Step.1 组装查询条件

		 //Step.2 AutoPoi 导出Excel
		 ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		 List<Question1> pageList = new ArrayList<>();
		 //导出文件名称
		 mv.addObject(NormalExcelConstants.FILE_NAME, "试题库列表");
		 mv.addObject(NormalExcelConstants.CLASS, Question1.class);
		 mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("试题库列表数据", "导出人:Jeecg", "导出信息"));
		 mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
		 return mv;
	 }

  /**
      * 导出excel
   * @param request
   * @param response
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
      // Step.1 组装查询条件
      QueryWrapper<Question> queryWrapper = null;
      try {
          String examId = request.getParameter("examId");
          Question question = new Question();
          if (oConvertUtils.isNotEmpty(examId)) {
          	String exam_id = URLDecoder.decode(examId, "UTF-8");
          	question.setExamId(exam_id);
          	//Question question = JSON.parseObject(deString, Question.class);
          	queryWrapper = QueryGenerator.initQueryWrapper(question, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<Question> pageList = questionService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "试题表列表");
      mv.addObject(NormalExcelConstants.CLASS, Question.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("试题表列表数据", "导出人:Jeecg", "导出信息"));
      mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
      return mv;
  }

	 @RequestMapping(value = "/uploadQuestion", method = RequestMethod.POST)
	 public Result<List<Question1>> uploadQuestion(HttpServletRequest request, HttpServletResponse response) {
		 MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		 Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		 Result<List<Question1>> result = new Result<List<Question1>>();
		 for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			 MultipartFile file = entity.getValue();// 获取上传文件对象
			 ImportParams params = new ImportParams();
			 params.setTitleRows(2);
			 params.setHeadRows(1);
			 params.setNeedSave(true);
			 try {
				 List<Question1> listQuestions = ExcelImportUtil.importExcel(file.getInputStream(), Question1.class, params);
				 result.setResult(listQuestions);
				 result.setSuccess(true);
				 result.setMessage("文件导入成功！数据行数:" + listQuestions.size());
				 return result;
			 } catch (Exception e) {
				 log.error(e.getMessage(),e);
				 result.error500("文件导入失败:"+e.getMessage());
				 return result;
			 } finally {
				 try {
					 file.getInputStream().close();
				 } catch (IOException e) {
					 e.printStackTrace();
				 }
			 }
		 }
		 result.setMessage("文件导入失败！");
		 return result;
	 }

  /**
      * 通过excel导入数据
   *
   * @param request
   * @param response
   * @return
   */
  @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
  public Result<?> importExcel(HttpServletRequest request,
							   HttpServletResponse response) {
  	  String examid = request.getParameter("examid");
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
      for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
          MultipartFile file = entity.getValue();// 获取上传文件对象
          ImportParams params = new ImportParams();
          params.setTitleRows(2);
          params.setHeadRows(1);
          params.setNeedSave(true);
          try {
              List<Question> listQuestions = ExcelImportUtil.importExcel(file.getInputStream(), Question.class, params);
              for (Question questionExcel : listQuestions) {
              	if(questionExcel.getExamId()!=examid){
					return Result.error("试卷信息不匹配！");//导入非本场试卷的题目
				}
              	else{
					questionService.save(questionExcel);
				}
              }
              return Result.ok("文件导入成功！数据行数:" + listQuestions.size());
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
