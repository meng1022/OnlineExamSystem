package org.jeecg.modules.exam.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.common.util.qiniu.QiNiuUtil;
import org.jeecg.modules.exam.entity.Question;
import org.jeecg.modules.exam.entity.Question1;
import org.jeecg.modules.exam.entity.Session;
import org.jeecg.modules.exam.service.IQuestionbaseService;
import org.jeecg.modules.exam.service.IQuestionService;
import org.jeecg.modules.exam.service.ISessionService;
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
import java.util.*;

/**
* @Description: 试题库
* @Author: jeecg-boot
* @Date:   2019-08-08
* @Version: V1.0
*/
@Slf4j
@Api(tags="试题库")
@RestController
@RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER" },logical = Logical.OR )
@RequestMapping("/exam/questionbase")
public class QuestionbaseController {
   @Autowired
   private IQuestionbaseService questionbaseService;
   @Autowired
   private IQuestionService questionService;
   @Autowired
   private ISessionService sessionService;

    /**
     * 试题库分页列表查询
     * @param question1
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "试题库-分页列表查询")
    @ApiOperation(value="试题库-分页列表查询", notes="试题库-分页列表查询")
    @RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
    @GetMapping(value = "/list")
    public Result<IPage<Question1>> queryPageList(Question1 question1,
                                             @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                             @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                             HttpServletRequest req) {
        Result<IPage<Question1>> result = new Result<IPage<Question1>>();
        QueryWrapper<Question1> queryWrapper = QueryGenerator.initQueryWrapper(question1, req.getParameterMap());
        Page<Question1> page = new Page<Question1>(pageNo, pageSize);
        IPage<Question1> pageList = questionbaseService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     *   添加
     * @param question1
     * @return
     */
    @AutoLog(value = "试题表-添加")
    @ApiOperation(value="试题表-添加", notes="试题表-添加")
    @PostMapping(value = "/add")
    public Result<Question1> add(@RequestBody Question1 question1) {
        Result<Question1> result = new Result<Question1>();
        try {
            questionbaseService.save(question1);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            result.error500("操作失败");
        }
        return result;
    }
    /**
     *  编辑
     * @param question1
     * @return
     */
    @AutoLog(value = "试题库-编辑")
    @ApiOperation(value="试题库-编辑", notes="试题库-编辑")
    @PutMapping(value = "/edit")
    public Result<Question1> edit(@RequestBody Question1 question1) {
        Result<Question1> result = new Result<Question1>();
        Question1 questionEntity = questionbaseService.getById(question1.getId());
        if(questionEntity==null) {
            result.error500("未找到对应实体");
        }else {
            boolean ok = questionbaseService.updateById(question1);
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
    @AutoLog(value = "试题库-通过id删除")
    @ApiOperation(value="试题库-通过id删除", notes="试题库-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<Question1> delete(@RequestParam(name="id",required=true) String id) {
        Result<Question1> result = new Result<Question1>();
        Question1 question1 = questionbaseService.getById(id);
        if(question1==null) {
            result.error500("未找到对应实体");
        }else {
            boolean ok = questionbaseService.removeById(id);
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
    @AutoLog(value = "试题库-批量删除")
    @ApiOperation(value="试题库-批量删除", notes="试题库-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<Question1> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
        Result<Question1> result = new Result<Question1>();
        if(ids==null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        }else {
            this.questionbaseService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }

    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
        // Step.1 组装查询条件
        QueryWrapper<Question1> queryWrapper = null;
        try {
            String sessionName = request.getParameter("sessionName");
            Question1 question1 = new Question1();
            if (oConvertUtils.isNotEmpty(sessionName)) {
                String session_name = URLDecoder.decode(sessionName, "UTF-8");
                question1.setSessionName(session_name);
                queryWrapper = QueryGenerator.initQueryWrapper(question1, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<Question1> pageList = questionbaseService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "试题库列表");
        mv.addObject(NormalExcelConstants.CLASS, Question1.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("试题库列表数据", "导出人:Jeecg", "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    @RequestMapping(value="/uploadImg", method= RequestMethod.POST)
    public Result<Object> uploadImg(@RequestParam MultipartFile image, HttpServletRequest request) {
        Result result = new Result();
        if (image.isEmpty()) {
            result.setCode(400);
            result.setMessage("文件为空，请重新上传");
            return result;
        }
        try {
            byte[] bytes = image.getBytes();
            String imageName = UUID.randomUUID().toString();

            QiNiuUtil qiniuUtil = new QiNiuUtil();
            try {
                //使用base64方式上传到七牛云
                String url = qiniuUtil.put64image(bytes, imageName);
                result.setCode(200);
                result.setMessage("文件上传成功");
                result.setResult(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        } catch (IOException e) {
            result.setCode(500);
            result.setMessage("文件上传发生异常！");
            return result;
        }
    }



    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
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
                List<Question1> listQuestions = ExcelImportUtil.importExcel(file.getInputStream(), Question1.class, params);
                for (Question1 questionExcel : listQuestions) {
                    questionbaseService.save(questionExcel);
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

    /**
     *   智能组卷
     */
    @AutoLog(value = "智能组卷-大题")
    @ApiOperation(value="智能组卷-大题", notes="智能组卷-大题")
    @RequestMapping(value = "/designquestion",method=RequestMethod.GET)
    public Result<List<Question1>> designquestion(HttpServletRequest request) {
        Result<List<Question1>> result = new Result<List<Question1>>();
        String sessionName = request.getParameter("sessionName");
        String type = request.getParameter("type");
        int number = Integer.parseInt(request.getParameter("number"));
        int score = Integer.parseInt(request.getParameter("score"));
        int hard = Integer.parseInt(request.getParameter("hard"));
        Session session = new Session().setSessionName(sessionName);
        if(sessionService.count(new QueryWrapper<Session>(session))==0){
            result.error500("试题库不存在！");
            return result;
        }
        List<Question1> question1List = new ArrayList<Question1>();
        question1List = questionbaseService.selectquestions(sessionName,type,hard,number,score);
        if(question1List.size()<number){
            result.error500("试题库中满足组卷策略的题目数量不足！");
            return result;
        }
//            if(types[i]!=null&&numbers[i]!=0&&scores[i]!=0)
//            {
//                log.info(examId+","+sessionName+","+types[i]+","+numbers[i]+","+scores[i]);
//                questions = questionbaseService.selectquestions(sessionName,types[i],numbers[i],scores[i]);
//                if(questions.size()==numbers[i])
//                {
//                    Questions.addAll(questions);
//                }
//                else
//                {
//                    result1.error500("试题库中分值为"+scores[i]+"分的"+types[i]+"型题目数量不满足要求");
//                    return result1;
//                }
//            }
//            else;


//        for(int i = 0;i<Questions.size();i++){
//            Question1 question1 = Questions.get(i);
//            Question question = new Question();
//            question.setExamId(examId);
//            question.setType(question1.getType());
//            question.setContent(question1.getContent());
//            question.setOptionA(question1.getOptionA());
//            question.setOptionB(question1.getOptionB());
//            question.setOptionC(question1.getOptionC());
//            question.setOptionD(question1.getOptionD());
//            question.setOptionE(question1.getOptionE());
//            question.setOptionF(question1.getOptionF());
//            question.setOptionG(question1.getOptionG());
//            question.setImgTitleA(question1.getImgTitleA());
//            question.setImgTitleB(question1.getImgTitleB());
//            question.setImgTitleC(question1.getImgTitleC());
//            question.setImgTitleD(question1.getImgTitleD());
//            question.setImgTitleE(question1.getImgTitleE());
//            question.setImgUrlA(question1.getImgUrlA());
//            question.setImgUrlB(question1.getImgUrlB());
//            question.setImgUrlC(question1.getImgUrlC());
//            question.setImgUrlD(question1.getImgUrlD());
//            question.setImgUrlE(question1.getImgUrlE());
//            question.setAnswer(question1.getAnswer());
//            question.setScore(question1.getScore());
//            questionService.save(question);
//        }
        result.setResult(question1List);
        return result;
    }


    @AutoLog(value = "智能组卷-试卷")
    @ApiOperation(value="智能组卷-试卷", notes="智能组卷-试卷")
    @RequestMapping(value = "/designpaper",method=RequestMethod.GET)
    public Result< List<List<Question1>> > designpaper(HttpServletRequest request) {
        Result<List<List<Question1>>> result = new Result<List<List<Question1>>>();
//        String sessionName = request.getParameter("sessionName");
//        String type = request.getParameter("type");
//        int number = Integer.parseInt(request.getParameter("number"));
//        int score = Integer.parseInt(request.getParameter("score"));
//        int hard = Integer.parseInt(request.getParameter("hard"));
//        Session session = new Session().setSessionName(sessionName);
//        if(sessionService.count(new QueryWrapper<Session>(session))==0){
//            result.error500("试题库不存在！");
//            return result;
//        }
//        List<Question1> question1List = new ArrayList<Question1>();
//        question1List = questionbaseService.selectquestions(sessionName,type,hard,number,score);
//        if(question1List.size()<number){
//            result.error500("试题库中满足组卷策略的题目数量不足！");
//            return result;
//        }
        List<List<Question1>> paper = questionbaseService.designpaper(request);
        result.setResult(paper);
        return result;
    }
}
