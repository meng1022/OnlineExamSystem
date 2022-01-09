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
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.exam.entity.QAset;
import org.jeecg.modules.exam.entity.Submit;
import org.jeecg.modules.exam.service.IQAsetService;
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
* @Description: 试题集合
* @Author: jeecg-boot
* @Date:   2019-08-08
* @Version: V1.0
*/
@Slf4j
@Api(tags="试题集合")
@RestController
@RequestMapping("/exam/qaset")
public class QAsetController {
   @Autowired
   private IQAsetService qAsetService;

    @Autowired
    private ISubmitService submitService;
    @RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
    @RequestMapping(value = "/getQAsetByExamId", method = RequestMethod.GET)
    public Result<Map> getQAsetByExamId(@RequestParam(name="examId", defaultValue="") String  examId) {
        Result<Map> result = new Result<Map>();
        QAset qAset = qAsetService.getQAsetByExamId(examId);
        Map map = new HashMap();
        map.put("qaset",qAset.getQaset());
        result.setSuccess(true);
        result.setResult(map);
        return result;
    }

    @RequestMapping(value = "/getOwnQAsetByExamId", method = RequestMethod.GET)
    public Result<Map> getOwnQAsetByExamId(@RequestParam(name="examId", defaultValue="") String  examId) {
        Result<Map> result = new Result<Map>();
        QAset qAset = qAsetService.getQAsetByExamId(examId);
        LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
        Map<String, Object> querymap = new HashMap<>();
        querymap.put("exam_id", examId);
        querymap.put("user_id", sysUser.getId());
        Map map = new HashMap();
        Submit submit = submitService.getOne(new QueryWrapper<Submit>().allEq(querymap).orderByDesc("create_time"));
        if(submit != null) {
            //convert result in submit into a map
            JSONArray jsonArray = (JSONArray)JSON.parse(submit.getResult());
            HashMap<String, String> resultMap = new HashMap<>();
            for(int index=0; index< jsonArray.size(); index++){
                JSONObject jsonObject = jsonArray.getJSONObject(index);
                String no = jsonObject.getString("no");
                String value = jsonObject.getString("value");
                resultMap.put(no, value);
            }

            JSONArray qalistsrc = JSONArray.parseArray(qAset.getQaset().toString());
            JSONArray qalistdst = new JSONArray();

            for (int i = 0; i < qalistsrc.size(); i++) {
                JSONObject qa = qalistsrc.getJSONObject(i);
                if (qa.containsKey("no")) {
                        String no = qa.getString("no");
                        if(resultMap.containsKey(no)){
                            qa.put("answer",resultMap.get(no));
                        }else{
                            qa.put("answer","");
                        }
                }
                qalistdst.add(qa);
            }

            map.put("qaset",qalistdst.toJSONString());
        }else{
            map.put("qaset", qAset.getQaset());
        }
        result.setSuccess(true);
        result.setResult(map);
        return result;
    }

   /**
     * 分页列表查询
    * @param qaset
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   @RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
   @AutoLog(value = "试题集合-分页列表查询")
   @ApiOperation(value="试题集合-分页列表查询", notes="试题集合-分页列表查询")
   @GetMapping(value = "/list")
   public Result<IPage<QAset>> queryPageList(QAset qaset,
                                     @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                     @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                     HttpServletRequest req) {
       Result<IPage<QAset>> result = new Result<IPage<QAset>>();
       QueryWrapper<QAset> queryWrapper = QueryGenerator.initQueryWrapper(qaset, req.getParameterMap());
       Page<QAset> page = new Page<QAset>(pageNo, pageSize);
       IPage<QAset> pageList = qAsetService.page(page, queryWrapper);
       result.setSuccess(true);
       result.setResult(pageList);
       return result;
   }

   /**
     *   添加
    * @param qaset
    * @return
    */
   @AutoLog(value = "试题集合-添加")
   @ApiOperation(value="试题集合-添加", notes="试题集合-添加")
   @RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
   @PostMapping(value = "/add")
   public Result<QAset> add(@RequestBody QAset qaset) {
       Result<QAset> result = new Result<QAset>();
       try {
           qAsetService.save(qaset);
           result.success("添加成功！");
       } catch (Exception e) {
           log.error(e.getMessage(),e);
           result.error500("操作失败");
       }
       return result;
   }

   /**
     *  编辑
    * @param qaset
    * @return
    */
   @AutoLog(value = "试题集合-编辑")
   @ApiOperation(value="试题集合-编辑", notes="试题集合-编辑")
   @RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
   @PutMapping(value = "/edit")
   public Result<QAset> edit(@RequestBody QAset qaset) {
       Result<QAset> result = new Result<QAset>();
       QAset qasetEntity = qAsetService.getById(qaset.getId());
       if(qasetEntity==null) {
           result.error500("未找到对应实体");
       }else {
           boolean ok = qAsetService.updateById(qaset);
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
   public Result<QAset> delete(@RequestParam(name="id",required=true) String id) {
       Result<QAset> result = new Result<QAset>();
       QAset qaset = qAsetService.getById(id);
       if(qaset==null) {
           result.error500("未找到对应实体");
       }else {
           boolean ok = qAsetService.removeById(id);
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
   public Result<QAset> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
       Result<QAset> result = new Result<QAset>();
       if(ids==null || "".equals(ids.trim())) {
           result.error500("参数不识别！");
       }else {
           this.qAsetService.removeByIds(Arrays.asList(ids.split(",")));
           result.success("删除成功!");
       }
       return result;
   }

   /**
     * 通过id查询
    * @param id
    * @return
    */
   @RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
   @AutoLog(value = "试题集合-通过id查询")
   @ApiOperation(value="试题集合-通过id查询", notes="试题集合-通过id查询")
   @GetMapping(value = "/queryById")
   public Result<QAset> queryById(@RequestParam(name="id",required=true) String id) {
       Result<QAset> result = new Result<QAset>();
       QAset qaset = qAsetService.getById(id);
       if(qaset==null) {
           result.error500("未找到对应实体");
       }else {
           result.setResult(qaset);
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
     QueryWrapper<QAset> queryWrapper = null;
     try {
         String paramsStr = request.getParameter("paramsStr");
         if (oConvertUtils.isNotEmpty(paramsStr)) {
             String deString = URLDecoder.decode(paramsStr, "UTF-8");
             QAset qaset = JSON.parseObject(deString, QAset.class);
             queryWrapper = QueryGenerator.initQueryWrapper(qaset, request.getParameterMap());
         }
     } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
     }

     //Step.2 AutoPoi 导出Excel
     ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
     List<QAset> pageList = qAsetService.list(queryWrapper);
     //导出文件名称
     mv.addObject(NormalExcelConstants.FILE_NAME, "试题集合列表");
     mv.addObject(NormalExcelConstants.CLASS, QAset.class);
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
             List<QAset> listQAsets = ExcelImportUtil.importExcel(file.getInputStream(), QAset.class, params);
             for (QAset qasetExcel : listQAsets) {
                 qAsetService.save(qasetExcel);
             }
             return Result.ok("文件导入成功！数据行数:" + listQAsets.size());
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
