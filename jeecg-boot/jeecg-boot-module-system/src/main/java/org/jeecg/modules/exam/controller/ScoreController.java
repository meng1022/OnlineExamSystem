package org.jeecg.modules.exam.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.exam.entity.Qset;
import org.jeecg.modules.exam.entity.Score;
import org.jeecg.modules.exam.service.IQsetService;
import org.jeecg.modules.exam.vo.ScoreExamUserVo;
import org.jeecg.modules.exam.service.IScoreService;
import org.jeecg.modules.exam.service.IQAsetService;
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
 * @Description: 成绩表
 * @Author: jeecg-boot
 * @Date:   2019-08-08
 * @Version: V1.0
 */
@Slf4j
@Api(tags="成绩表")
@RestController
@RequestMapping("/exam/score")
public class ScoreController {
	@Autowired
	private IScoreService scoreService;
	@Autowired
	private IQsetService qsetService;

	 @RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
	 @RequestMapping(value = "/grade1ByExamId", method = RequestMethod.GET)
	 public Result<Object> grade1ByExamId(@RequestParam(name="examId", defaultValue="") String  examId) {
		 boolean success = scoreService.grade1ByExamId(examId);
		 if(success){
		 	return Result.ok("判卷成功！");
		 }else{
		 	return Result.error("判卷失败！");
		 }
	 }
     @RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
     @RequestMapping(value = "/grade2ByExamId", method = RequestMethod.GET)
     public Result<Object> grade2ByExamId(@RequestParam(name="examId", defaultValue="") String  examId) {
         boolean success = scoreService.grade2ByExamId(examId);
         if(success){
             return Result.ok("判卷成功！");
         }else{
             return Result.error("判卷失败！");
         }
     }


	 /*统计各分数段的人数*/
	 @RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
	 @RequestMapping(value = "/getbarchartdata", method = RequestMethod.GET)
	 public Result<JSONArray> getbarchartdata(@RequestParam(name="examId", defaultValue="") String  examId) {
	 	Result<JSONArray> result = new Result<JSONArray>();
	 	float fullscore = qsetService.getFullScore(examId);
	 	JSONArray jsonArray = scoreService.getdata(examId,fullscore);
	 	result.setResult(jsonArray);
		result.setSuccess(true);
		return result;
	 }


	 /**
	  * 分页列表查询
	 * @param score
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "成绩表-分页列表查询")
	@ApiOperation(value="成绩表-分页列表查询", notes="成绩表-分页列表查询")
	@RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
	@GetMapping(value = "/list")
	public Result<IPage<Score>> queryPageList(Score score,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<Score>> result = new Result<IPage<Score>>();
		QueryWrapper<Score> queryWrapper = QueryGenerator.initQueryWrapper(score, req.getParameterMap());
		Page<Score> page = new Page<Score>(pageNo, pageSize);
		IPage<Score> pageList = scoreService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	 /**
	  * 分页列表查询
	  * @param examId
	  * @param pageNo
	  * @param pageSize
	  * @param req
	  * @return
	  */
	 @AutoLog(value = "按考试id查成绩表-分页列表查询")
	 @ApiOperation(value="按考试id查成绩表-分页列表查询", notes="按考试id查成绩表-分页列表查询")
	 @RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
	 @GetMapping(value = "/queryByExamId")
	 public Result<IPage<ScoreExamUserVo>> queryByExamId(@RequestParam(name="examId", defaultValue="") String  examId,
														 @RequestParam(name="provcode", defaultValue="") String  provcode,
											   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
											   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
											   HttpServletRequest req) {
		 Result<IPage<ScoreExamUserVo>> result = new Result<IPage<ScoreExamUserVo>>();
		 Page<ScoreExamUserVo> page = new Page<ScoreExamUserVo>(pageNo, pageSize);
		 IPage<ScoreExamUserVo> pageList = scoreService.queryByExamId(page, examId, provcode);
		 result.setSuccess(true);
		 result.setResult(pageList);
		 return result;
	 }




	/**
	  *   添加
	 * @param score
	 * @return
	 */
	@AutoLog(value = "成绩表-添加")
	@ApiOperation(value="成绩表-添加", notes="成绩表-添加")
	@RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
	@PostMapping(value = "/add")
	public Result<Score> add(@RequestBody Score score) {
		Result<Score> result = new Result<Score>();
		try {
			scoreService.save(score);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}
	
	/**
	  *  编辑
	 * @param score
	 * @return
	 */
	@AutoLog(value = "成绩表-编辑")
	@ApiOperation(value="成绩表-编辑", notes="成绩表-编辑")
	@RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
	@PutMapping(value = "/edit")
	public Result<Score> edit(@RequestBody Score score) {
		Result<Score> result = new Result<Score>();
		Score scoreEntity = scoreService.getById(score.getId());
		if(scoreEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = scoreService.updateById(score);
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
	@AutoLog(value = "成绩表-通过id删除")
	@ApiOperation(value="成绩表-通过id删除", notes="成绩表-通过id删除")
	@RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
	@DeleteMapping(value = "/delete")
	public Result<Score> delete(@RequestParam(name="id",required=true) String id) {
		Result<Score> result = new Result<Score>();
		Score score = scoreService.getById(id);
		if(score==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = scoreService.removeById(id);
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
	@AutoLog(value = "成绩表-批量删除")
	@ApiOperation(value="成绩表-批量删除", notes="成绩表-批量删除")
	@RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
	@DeleteMapping(value = "/deleteBatch")
	public Result<Score> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<Score> result = new Result<Score>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.scoreService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}

	 @AutoLog(value = "按考生id查问答题分数")
	 @ApiOperation(value="按考生id查问答题分数", notes="按考生id查问答题分数")
	 @RequiresRoles(value = { "admin","EXAMADMIN","OFFICER" },logical = Logical.OR )
	 @GetMapping(value = "/getscore2")
	 public double getscore2(Score score) {
		 QueryWrapper<Score> queryWrapper = new QueryWrapper<Score>(score);
		 double score2 = scoreService.getOne(queryWrapper).getScore2();
		 return score2;
	 }

	 @AutoLog(value = "录入问答题分数")
	 @ApiOperation(value="录入问答题分数", notes="录入问答题分数")
	 @RequiresRoles(value = { "admin","EXAMADMIN","OFFICER" },logical = Logical.OR )
	 @PostMapping(value = "/postscore2")
	 public Result<?> postscore2(@RequestBody Score score) {
		Score score1 = new Score();
		score1.setExamId(score.getExamId());
		score1.setUserId(score.getUserId());
		QueryWrapper<Score> queryWrapper = new QueryWrapper<Score>(score1);
		Score score2 = scoreService.getOne(queryWrapper);
		score2.setScore2(score.getScore2());
		scoreService.updateById(score2);
	 	return Result.ok("问答题分数录入成功！");
	 }

	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "成绩表-通过id查询")
	@ApiOperation(value="成绩表-通过id查询", notes="成绩表-通过id查询")
	@RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
	@GetMapping(value = "/queryById")
	public Result<Score> queryById(@RequestParam(name="id",required=true) String id) {
		Result<Score> result = new Result<Score>();
		Score score = scoreService.getById(id);
		if(score==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(score);
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
      QueryWrapper<Score> queryWrapper = null;
      try {
          String examId = request.getParameter("examId");
          Score score = new Score();
          if (oConvertUtils.isNotEmpty(examId)) {
			  String exam_id = URLDecoder.decode(examId, "UTF-8");
			  score.setExamId(exam_id);
              queryWrapper = QueryGenerator.initQueryWrapper(score, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<Score> pageList = scoreService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "成绩表列表");
      mv.addObject(NormalExcelConstants.CLASS, Score.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("成绩表列表数据", "导出人:Jeecg", "导出信息"));
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
              List<Score> listScores = ExcelImportUtil.importExcel(file.getInputStream(), Score.class, params);
              for (Score scoreExcel : listScores) {
                  scoreService.save(scoreExcel);
              }
              return Result.ok("文件导入成功！数据行数:" + listScores.size());
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
