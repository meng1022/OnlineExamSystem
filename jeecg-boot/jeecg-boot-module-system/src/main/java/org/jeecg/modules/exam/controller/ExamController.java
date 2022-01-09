package org.jeecg.modules.exam.controller;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.openpackaging.io.SaveToZipFile;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.exam.entity.Exam;
import org.jeecg.modules.exam.entity.Session;
import org.jeecg.modules.exam.service.IExamService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.exam.service.ISessionService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

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
@RequestMapping("/exam/exam")
public class ExamController {
    @Value("${jeecg.path.upload}")
    private String upLoadPath;

	@Autowired
	private IExamService examService;

	@Autowired
	private ISysUserService sysUserService;

	 @Autowired
	 private ISessionService sessionService;

	 @Autowired
	 private RedisUtil redisUtil;
	/**
	  * 分页列表查询
	 * @param exam
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "考试-分页列表查询")
	@ApiOperation(value="考试-分页列表查询", notes="考试-分页列表查询")
	@GetMapping(value = "/list")
	public Result<JSONObject> queryPageList(Exam exam,
											@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
											@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
											HttpServletRequest req) {
		Result<JSONObject> result = new Result<JSONObject>();
		JSONObject resultobj = new JSONObject();
		String exam_list_str = String.valueOf(redisUtil.get(CommonConstant.PREFIX_EXAM_CACHE + "LIST"));
		if (oConvertUtils.isEmpty(exam_list_str)) {
			//QueryWrapper<Exam> queryWrapper = QueryGenerator.initQueryWrapper(exam, req.getParameterMap());
			Page<Exam> page = new Page<Exam>(pageNo, pageSize);
			IPage<Exam> pageList = examService.page(page, new QueryWrapper<Exam>().orderByAsc("sort_no"));
			resultobj.put("current",pageList.getCurrent());
			resultobj.put("pages",pageList.getPages());
			resultobj.put("searchCount",pageList.isSearchCount());
			resultobj.put("size",pageList.getSize());
			resultobj.put("total",pageList.getTotal());
			JSONArray records = new JSONArray();
			for(Exam iexam : pageList.getRecords()){
				JSONObject record = new JSONObject();
				record.put("id",iexam.getId());
				record.put("examDuration",iexam.getExamDuration());
				record.put("examEndTime",iexam.getExamEndTime());
				record.put("examName",iexam.getExamName());
				record.put("examStartTime",iexam.getExamStartTime());
				record.put("sessionCode",iexam.getSessionCode());
				record.put("sessionName",iexam.getSessionName());
				record.put("sortNo",iexam.getSortNo());
				record.put("status",iexam.getStatus());
				record.put("submitServerCache",iexam.getSubmitServerCache());
				record.put("submitTimes",iexam.getSubmitTimes());
				record.put("verificationCode",iexam.getVerificationCode());
				record.put("visiableEndTime",iexam.getVisiableEndTime());
				record.put("visiableStartTime",iexam.getVisiableStartTime());
				records.add(record);
			}
			resultobj.put("records",records);
			redisUtil.set(CommonConstant.PREFIX_EXAM_CACHE + "LIST", records.toJSONString());
			redisUtil.expire(CommonConstant.PREFIX_EXAM_CACHE + "LIST", CommonConstant.EXAM_CACHE_EXPIRE_TIME);
		}else{
				Page<Exam> page = new Page<Exam>(pageNo, pageSize);
				QueryWrapper<Exam> queryWrapper = QueryGenerator.initQueryWrapper(exam, req.getParameterMap());
				IPage<Exam> pageList = examService.page(page, queryWrapper.orderByAsc("sort_no"));
	//			JSONArray records = JSONArray.parseArray(exam_list_str);
	//			resultobj.put("records",records);
				resultobj.put("current",pageList.getCurrent());
				resultobj.put("pages",pageList.getPages());
				resultobj.put("searchCount",pageList.isSearchCount());
				resultobj.put("size",pageList.getSize());
				resultobj.put("total",pageList.getTotal());
				JSONArray records = new JSONArray();
				for(Exam iexam : pageList.getRecords()){
					JSONObject record = new JSONObject();
					record.put("id", iexam.getId());
					record.put("examDuration", iexam.getExamDuration());
					record.put("examEndTime", iexam.getExamEndTime());
					record.put("examName", iexam.getExamName());
					record.put("examStartTime", iexam.getExamStartTime());
					record.put("sessionCode", iexam.getSessionCode());
					record.put("sessionName", iexam.getSessionName());
					record.put("sortNo", iexam.getSortNo());
					record.put("status", iexam.getStatus());
					record.put("submitServerCache", iexam.getSubmitServerCache());
					record.put("submitTimes", iexam.getSubmitTimes());
					record.put("verificationCode", iexam.getVerificationCode());
					record.put("visiableEndTime", iexam.getVisiableEndTime());
					record.put("visiableStartTime", iexam.getVisiableStartTime());
					records.add(record);

				}
				resultobj.put("records",records);
			}
		result.setSuccess(true);
		result.setResult(resultobj);
		return result;
	}

	/**
	  *   添加
	 * @param exam
	 * @return
	 */
	@AutoLog(value = "考试-添加")
	@ApiOperation(value="考试-添加", notes="考试-添加")
	@RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
	@PostMapping(value = "/add")
	public Result<Exam> add(@RequestBody Exam exam) {
		Result<Exam> result = new Result<Exam>();
		try {
			examService.save(exam);
			Session session =new Session();
			session.setSessionName(exam.getSessionName());
			QueryWrapper<Session> queryWrapper = new QueryWrapper<Session>(session);
			if(sessionService.count(queryWrapper)==0)//场次名称即科目是否存在，是否需要增加试题库
				sessionService.save(session);
			else;
			result.success("添加成功！");
		} catch (Exception e) {
			//log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
        redisUtil.del(CommonConstant.PREFIX_EXAM_CACHE + "LIST");
        redisUtil.del(CommonConstant.PREFIX_EXAM_CACHE + exam.getId());
		return result;
	}
	
	/**
	  *  编辑
	 * @param exam
	 * @return
	 */
	@AutoLog(value = "考试-编辑")
	@ApiOperation(value="考试-编辑", notes="考试-编辑")
	@RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
	@PutMapping(value = "/edit")
	public Result<Exam> edit(@RequestBody Exam exam) {
		Result<Exam> result = new Result<Exam>();
		long curTimeMillis = System.currentTimeMillis();
		Exam examEntity = examService.getById(exam.getId());
		if(examEntity==null) {
			result.error500("未找到对应实体");
		}else {
//			if(curTimeMillis > (exam.getExamStartTime().getTime() - 1000*60*2) && curTimeMillis < (exam.getExamEndTime().getTime() +1000*60*10)){
//				result.error500("当前时段内不允许修改考试信息！");
//				//考试开始前2分钟和考试结束后10分钟内不允许修改考试信息和试卷内容
//			}
//			else{
				boolean ok = examService.updateById(exam);
				//TODO 返回false说明什么？
				if(ok) {
					redisUtil.del(CommonConstant.PREFIX_EXAM_CACHE + "LIST");
					redisUtil.del(CommonConstant.PREFIX_EXAM_CACHE + exam.getId());
					result.success("修改成功!");
				}
			//}
		}
		return result;
	}


	/**
	  *   通过id删除
	 * @param id
	 * @return
	 */
	@AutoLog(value = "考试-通过id删除")
	@ApiOperation(value="考试-通过id删除", notes="考试-通过id删除")
	@RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
	@DeleteMapping(value = "/delete")
	public Result<Exam> delete(@RequestParam(name="id",required=true) String id) {
		Result<Exam> result = new Result<Exam>();
		Exam exam = examService.getById(id);
		if(exam==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = examService.removeById(id);
			if(ok) {
				result.success("删除成功!");
			}
		}
        redisUtil.del(CommonConstant.PREFIX_EXAM_CACHE + "LIST");
        redisUtil.del(CommonConstant.PREFIX_EXAM_CACHE + exam.getId());
		
		return result;
	}
	
	/**
	  *  批量删除
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "考试-批量删除")
	@ApiOperation(value="考试-批量删除", notes="考试-批量删除")
	@RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
	@DeleteMapping(value = "/deleteBatch")
	public Result<Exam> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<Exam> result = new Result<Exam>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.examService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
        redisUtil.del(CommonConstant.PREFIX_EXAM_CACHE + "LIST");
		return result;
	}
	
	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "考试-通过id查询")
	@ApiOperation(value="考试-通过id查询", notes="考试-通过id查询")
	@RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
	@GetMapping(value = "/queryById")
	public Result<Exam> queryById(@RequestParam(name="id",required=true) String id) {
		Result<Exam> result = new Result<Exam>();
		Exam exam = examService.getById(id);
		if(exam==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(exam);
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
      QueryWrapper<Exam> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              Exam exam = JSON.parseObject(deString, Exam.class);
              queryWrapper = QueryGenerator.initQueryWrapper(exam, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<Exam> pageList = examService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "考试列表");
      mv.addObject(NormalExcelConstants.CLASS, Exam.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("考试列表数据", "导出人:Jeecg", "导出信息"));
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
              List<Exam> listExams = ExcelImportUtil.importExcel(file.getInputStream(), Exam.class, params);
              for (Exam examExcel : listExams) {
                  examService.save(examExcel);
              }
              return Result.ok("文件导入成功！数据行数:" + listExams.size());
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

	 @RequestMapping(value = "/genCertification", method = RequestMethod.GET)
	 public Result<Object> genCertification(@RequestParam(name="userId", defaultValue="") String  userId) {
		 boolean success = true;
		 try {
			 WordprocessingMLPackage mlPackage = WordprocessingMLPackage.load(new File("certification1.docx"));

			 File file = new File( upLoadPath + File.separator + userId  + ".pdf");
             Mapper fontMapper = new IdentityPlusMapper();
//             fontMapper.put("隶书", PhysicalFonts.get("LiSu"));
//             fontMapper.put("宋体", PhysicalFonts.get("SimSun"));
//             fontMapper.put("微软雅黑", PhysicalFonts.get("Microsoft Yahei"));
//             fontMapper.put("黑体", PhysicalFonts.get("SimHei"));
//             fontMapper.put("楷体", PhysicalFonts.get("KaiTi"));
//             fontMapper.put("新宋体", PhysicalFonts.get("NSimSun"));
             fontMapper.put("华文行楷", PhysicalFonts.get("STXingkai"));
//             fontMapper.put("华文仿宋", PhysicalFonts.get("STFangsong"));
//             fontMapper.put("仿宋", PhysicalFonts.get("FangSong"));
//             fontMapper.put("幼圆", PhysicalFonts.get("YouYuan"));
//             fontMapper.put("华文宋体", PhysicalFonts.get("STSong"));
//             fontMapper.put("华文中宋", PhysicalFonts.get("STZhongsong"));
//             fontMapper.put("等线", PhysicalFonts.get("SimSun"));
//             fontMapper.put("等线 Light", PhysicalFonts.get("SimSun"));
//             fontMapper.put("华文琥珀", PhysicalFonts.get("STHupo"));
//             fontMapper.put("华文隶书", PhysicalFonts.get("STLiti"));
//             fontMapper.put("华文新魏", PhysicalFonts.get("STXinwei"));
//             fontMapper.put("华文彩云", PhysicalFonts.get("STCaiyun"));
//             fontMapper.put("方正姚体", PhysicalFonts.get("FZYaoti"));
//             fontMapper.put("方正舒体", PhysicalFonts.get("FZShuTi"));
//             fontMapper.put("华文细黑", PhysicalFonts.get("STXihei"));
//             fontMapper.put("宋体扩展",PhysicalFonts.get("simsun-extB"));
//             fontMapper.put("仿宋_GB2312",PhysicalFonts.get("FangSong_GB2312"));
//             fontMapper.put("新細明體",PhysicalFonts.get("SimSun"));
//             //解决宋体（正文）和宋体（标题）的乱码问题
//             PhysicalFonts.put("PMingLiU", PhysicalFonts.get("SimSun"));
//             PhysicalFonts.put("新細明體", PhysicalFonts.get("SimSun"));
             mlPackage.setFontMapper(fontMapper);

             FOSettings foSettings = Docx4J.createFOSettings();
             foSettings.setWmlPackage(mlPackage);


			 MainDocumentPart documentPart = mlPackage.getMainDocumentPart();

			 SysUser user = sysUserService.getById(userId);
			 //需要替换的map
			 HashMap<String, String> mappings = new HashMap<String, String>();
			 mappings.put("name", user.getRealname());

			 documentPart.variableReplace(mappings);
			 OutputStream os = new java.io.FileOutputStream(file);

//			 Docx4J.toFO(foSettings,os, Docx4J.FLAG_EXPORT_PREFER_XSL);
			 Docx4J.toPDF(mlPackage, os);

			 os.flush();
			 os.close();

		 } catch (Exception e){
			 //log.error(e.getMessage());
			 success = false;
		 }

		 if(success){
			 return Result.ok("证书转成pdf成功");
		 }else{
			 return Result.error("证书转成pdf失败");
		 }
	 }

}
