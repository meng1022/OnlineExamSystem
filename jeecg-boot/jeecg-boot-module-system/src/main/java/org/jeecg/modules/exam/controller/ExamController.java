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
 * @Description: ??????
 * @Author: jeecg-boot
 * @Date:   2019-08-07
 * @Version: V1.0
  * //
 */
@Slf4j
@Api(tags="??????")
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
	  * ??????????????????
	 * @param exam
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "??????-??????????????????")
	@ApiOperation(value="??????-??????????????????", notes="??????-??????????????????")
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
	  *   ??????
	 * @param exam
	 * @return
	 */
	@AutoLog(value = "??????-??????")
	@ApiOperation(value="??????-??????", notes="??????-??????")
	@RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
	@PostMapping(value = "/add")
	public Result<Exam> add(@RequestBody Exam exam) {
		Result<Exam> result = new Result<Exam>();
		try {
			examService.save(exam);
			Session session =new Session();
			session.setSessionName(exam.getSessionName());
			QueryWrapper<Session> queryWrapper = new QueryWrapper<Session>(session);
			if(sessionService.count(queryWrapper)==0)//???????????????????????????????????????????????????????????????
				sessionService.save(session);
			else;
			result.success("???????????????");
		} catch (Exception e) {
			//log.error(e.getMessage(),e);
			result.error500("????????????");
		}
        redisUtil.del(CommonConstant.PREFIX_EXAM_CACHE + "LIST");
        redisUtil.del(CommonConstant.PREFIX_EXAM_CACHE + exam.getId());
		return result;
	}
	
	/**
	  *  ??????
	 * @param exam
	 * @return
	 */
	@AutoLog(value = "??????-??????")
	@ApiOperation(value="??????-??????", notes="??????-??????")
	@RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
	@PutMapping(value = "/edit")
	public Result<Exam> edit(@RequestBody Exam exam) {
		Result<Exam> result = new Result<Exam>();
		long curTimeMillis = System.currentTimeMillis();
		Exam examEntity = examService.getById(exam.getId());
		if(examEntity==null) {
			result.error500("?????????????????????");
		}else {
//			if(curTimeMillis > (exam.getExamStartTime().getTime() - 1000*60*2) && curTimeMillis < (exam.getExamEndTime().getTime() +1000*60*10)){
//				result.error500("?????????????????????????????????????????????");
//				//???????????????2????????????????????????10???????????????????????????????????????????????????
//			}
//			else{
				boolean ok = examService.updateById(exam);
				//TODO ??????false???????????????
				if(ok) {
					redisUtil.del(CommonConstant.PREFIX_EXAM_CACHE + "LIST");
					redisUtil.del(CommonConstant.PREFIX_EXAM_CACHE + exam.getId());
					result.success("????????????!");
				}
			//}
		}
		return result;
	}


	/**
	  *   ??????id??????
	 * @param id
	 * @return
	 */
	@AutoLog(value = "??????-??????id??????")
	@ApiOperation(value="??????-??????id??????", notes="??????-??????id??????")
	@RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
	@DeleteMapping(value = "/delete")
	public Result<Exam> delete(@RequestParam(name="id",required=true) String id) {
		Result<Exam> result = new Result<Exam>();
		Exam exam = examService.getById(id);
		if(exam==null) {
			result.error500("?????????????????????");
		}else {
			boolean ok = examService.removeById(id);
			if(ok) {
				result.success("????????????!");
			}
		}
        redisUtil.del(CommonConstant.PREFIX_EXAM_CACHE + "LIST");
        redisUtil.del(CommonConstant.PREFIX_EXAM_CACHE + exam.getId());
		
		return result;
	}
	
	/**
	  *  ????????????
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "??????-????????????")
	@ApiOperation(value="??????-????????????", notes="??????-????????????")
	@RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
	@DeleteMapping(value = "/deleteBatch")
	public Result<Exam> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<Exam> result = new Result<Exam>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("??????????????????");
		}else {
			this.examService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("????????????!");
		}
        redisUtil.del(CommonConstant.PREFIX_EXAM_CACHE + "LIST");
		return result;
	}
	
	/**
	  * ??????id??????
	 * @param id
	 * @return
	 */
	@AutoLog(value = "??????-??????id??????")
	@ApiOperation(value="??????-??????id??????", notes="??????-??????id??????")
	@RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
	@GetMapping(value = "/queryById")
	public Result<Exam> queryById(@RequestParam(name="id",required=true) String id) {
		Result<Exam> result = new Result<Exam>();
		Exam exam = examService.getById(id);
		if(exam==null) {
			result.error500("?????????????????????");
		}else {
			result.setResult(exam);
			result.setSuccess(true);
		}
		return result;
	}

  /**
      * ??????excel
   *
   * @param request
   * @param response
   */
  @RequestMapping(value = "/exportXls")
  @RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
  public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
      // Step.1 ??????????????????
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

      //Step.2 AutoPoi ??????Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<Exam> pageList = examService.list(queryWrapper);
      //??????????????????
      mv.addObject(NormalExcelConstants.FILE_NAME, "????????????");
      mv.addObject(NormalExcelConstants.CLASS, Exam.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("??????????????????", "?????????:Jeecg", "????????????"));
      mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
      return mv;
  }

  /**
      * ??????excel????????????
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
          MultipartFile file = entity.getValue();// ????????????????????????
          ImportParams params = new ImportParams();
          params.setTitleRows(2);
          params.setHeadRows(1);
          params.setNeedSave(true);
          try {
              List<Exam> listExams = ExcelImportUtil.importExcel(file.getInputStream(), Exam.class, params);
              for (Exam examExcel : listExams) {
                  examService.save(examExcel);
              }
              return Result.ok("?????????????????????????????????:" + listExams.size());
          } catch (Exception e) {
              log.error(e.getMessage(),e);
              return Result.error("??????????????????:"+e.getMessage());
          } finally {
              try {
                  file.getInputStream().close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      }
      return Result.ok("?????????????????????");
  }

	 @RequestMapping(value = "/genCertification", method = RequestMethod.GET)
	 public Result<Object> genCertification(@RequestParam(name="userId", defaultValue="") String  userId) {
		 boolean success = true;
		 try {
			 WordprocessingMLPackage mlPackage = WordprocessingMLPackage.load(new File("certification1.docx"));

			 File file = new File( upLoadPath + File.separator + userId  + ".pdf");
             Mapper fontMapper = new IdentityPlusMapper();
//             fontMapper.put("??????", PhysicalFonts.get("LiSu"));
//             fontMapper.put("??????", PhysicalFonts.get("SimSun"));
//             fontMapper.put("????????????", PhysicalFonts.get("Microsoft Yahei"));
//             fontMapper.put("??????", PhysicalFonts.get("SimHei"));
//             fontMapper.put("??????", PhysicalFonts.get("KaiTi"));
//             fontMapper.put("?????????", PhysicalFonts.get("NSimSun"));
             fontMapper.put("????????????", PhysicalFonts.get("STXingkai"));
//             fontMapper.put("????????????", PhysicalFonts.get("STFangsong"));
//             fontMapper.put("??????", PhysicalFonts.get("FangSong"));
//             fontMapper.put("??????", PhysicalFonts.get("YouYuan"));
//             fontMapper.put("????????????", PhysicalFonts.get("STSong"));
//             fontMapper.put("????????????", PhysicalFonts.get("STZhongsong"));
//             fontMapper.put("??????", PhysicalFonts.get("SimSun"));
//             fontMapper.put("?????? Light", PhysicalFonts.get("SimSun"));
//             fontMapper.put("????????????", PhysicalFonts.get("STHupo"));
//             fontMapper.put("????????????", PhysicalFonts.get("STLiti"));
//             fontMapper.put("????????????", PhysicalFonts.get("STXinwei"));
//             fontMapper.put("????????????", PhysicalFonts.get("STCaiyun"));
//             fontMapper.put("????????????", PhysicalFonts.get("FZYaoti"));
//             fontMapper.put("????????????", PhysicalFonts.get("FZShuTi"));
//             fontMapper.put("????????????", PhysicalFonts.get("STXihei"));
//             fontMapper.put("????????????",PhysicalFonts.get("simsun-extB"));
//             fontMapper.put("??????_GB2312",PhysicalFonts.get("FangSong_GB2312"));
//             fontMapper.put("????????????",PhysicalFonts.get("SimSun"));
//             //????????????????????????????????????????????????????????????
//             PhysicalFonts.put("PMingLiU", PhysicalFonts.get("SimSun"));
//             PhysicalFonts.put("????????????", PhysicalFonts.get("SimSun"));
             mlPackage.setFontMapper(fontMapper);

             FOSettings foSettings = Docx4J.createFOSettings();
             foSettings.setWmlPackage(mlPackage);


			 MainDocumentPart documentPart = mlPackage.getMainDocumentPart();

			 SysUser user = sysUserService.getById(userId);
			 //???????????????map
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
			 return Result.ok("????????????pdf??????");
		 }else{
			 return Result.error("????????????pdf??????");
		 }
	 }

}
