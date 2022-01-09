package org.jeecg.modules.exam.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itextpdf.text.BaseColor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.exam.entity.Exam;
import org.jeecg.modules.exam.entity.Score;
import org.jeecg.modules.exam.entity.Total;
import org.jeecg.modules.exam.service.IExamService;
import org.jeecg.modules.exam.vo.TotalUserALLVo;
import org.jeecg.modules.exam.vo.TotalUserVo;
import org.jeecg.modules.exam.service.ITotalService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.system.util.ContentStyle;
import org.jeecg.modules.system.util.PDFUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
* @Description: 总成绩与排名表
* @Author: jeecg-boot
* @Date:   2019-08-08
* @Version: V1.0
*/
@Slf4j
@Api(tags="总成绩与排名表")
@RestController
//@RequiresRoles(value = { "admin","EXAMADMIN" },logical = Logical.OR )
@RequestMapping("/exam/total")
public class TotalController {
    @Value("${jeecg.path.upload}")
    private String upLoadPath;

   @Autowired
   private ITotalService totalService;
    @Autowired
    private IExamService examService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private RedisUtil redisUtil;

    @RequestMapping(value = "/calcByExamName", method = RequestMethod.GET)
    public Result<Object> calcByExamName(@RequestParam(name="examName", defaultValue="") String  examName, @RequestParam(name="key", defaultValue="") String  key) {
        if(!key.equals("Hkang123!")) {
            return Result.error("key error！");
        }
        boolean success = totalService.calcByExamName(examName);
        if(success){
            return Result.ok("计算总分及排名成功！");
        }else{
            return Result.error("计算失败！");
        }
    }

    @RequestMapping(value = "/setTotalQueryPara", method = RequestMethod.GET)
    public Result<Object> setTotalQueryPara(@RequestParam(name="examName", defaultValue="") String  examName, @RequestParam(name="time", defaultValue="") String  time,@RequestParam(name="max", defaultValue="") String  max, @RequestParam(name="key", defaultValue="") String  key) {
        if(examName.equals("")||time.equals("")||max.equals("")||!key.equals("Hkang123!")) {
            return Result.error("para or key error！");
        }
        redisUtil.set("DEFAULT_EXMA_NAME", examName);
        redisUtil.set("DEFAULT_TOTAL_QUERY_TIME", time);
        redisUtil.set("DEFAULT_TOTAL_QUERY_MAX", max);
        return Result.ok("设置成功！"+examName+"_"+time+"_"+max);
    }

    @RequestMapping(value = "/genCert", method = RequestMethod.GET)
    public Result<Object> genCert(@RequestParam(name="key", defaultValue="") String  key, @RequestParam(name="from", defaultValue="") String  from, @RequestParam(name="to", defaultValue="") String  to) {
        if(from.equals("")||to.equals("")||!key.equals("Hkang123!")) {
            return Result.error("para or key error！");
        }
        List<TotalUserVo> totalUserVoList = totalService.queryListWithRank(String.valueOf(redisUtil.get("DEFAULT_EXMA_NAME")),Integer.valueOf(from),Integer.valueOf(to));
        PDFUtil pdfUtil = new PDFUtil();
        ContentStyle style = new ContentStyle();
        style.setTTFPath(upLoadPath+"/"+"STKAITI.TTF");
        style.setFontSize(22);
        style.setBaseColor(new BaseColor(0, 0, 0));
        style.setStyle(Font.BOLD);
        int count =0;
        for(TotalUserVo totalUserVo :totalUserVoList){
            try {
                String imagePath = "";
                if(totalUserVo.getRanking()<=100) {
                    imagePath = upLoadPath + "/" + "cert1.jpg";
                }else if(totalUserVo.getRanking()<99999){
                    imagePath = upLoadPath + "/" + "cert2.jpg";
                }else{
                    continue;
                }
                String pdfFilePath = upLoadPath +"/"+totalUserVo.getUserId()+ ".pdf";
                File file =new File(pdfFilePath);
                if(file.exists()){
                    file.delete();
                }
                float draw_x = 0;
                int namelength = 3;
                if(oConvertUtils.isNotEmpty(totalUserVo.getRealname())){
                   namelength = totalUserVo.getRealname().length();
                }

                if(namelength<=2){
                    draw_x =160;
                }else if(namelength <=3){
                    draw_x =135;
                }else if(namelength <=4){
                    draw_x =125;
                }else if(namelength <=5){
                    draw_x =110;
                }else{
                    draw_x =100;
                }
                pdfUtil.openDocumnet(pdfFilePath)
                    .addImage(imagePath, 15, 0)
                    //.addDateContent(330, 462, null)
                    .addContent(totalUserVo.getRealname(), draw_x, 365, style)
                    .close();
                count ++;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return Result.ok("pdf文件已生成，数量："+String.valueOf(count));
    }

    @RequestMapping(value = "/getTotalQueryPara", method = RequestMethod.GET)
    public Result<Object> getTotalQueryPara(@RequestParam(name="key", defaultValue="") String  key) {
        if(!key.equals("Hkang123!")) {
            return Result.error("para or key error！");
        }
        String examName =  String.valueOf(redisUtil.get("DEFAULT_EXMA_NAME"));
        String time =  String.valueOf(redisUtil.get("DEFAULT_TOTAL_QUERY_TIME"));
        String max =  String.valueOf(redisUtil.get("DEFAULT_TOTAL_QUERY_MAX"));
        return Result.ok("当前参数为："+examName+"_"+time+"_"+max);
    }

    @RequestMapping(value = "/getOwnTotalByExamName", method = RequestMethod.GET)
    public Result<JSONObject> getOwnTotalByExamName() {
        //本阶段设置examName为模拟练习
        //examName = "模拟练习";
        //Map<String, Object> map = new HashMap<>();
        //map.put("exam_name", examName);
        //List<Exam> exams = examService.list(new QueryWrapper<Exam>().allEq(map).orderByAsc("sort_no"));
        Result<JSONObject> result = new Result<JSONObject>();
        LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
        SysUser curUser = sysUserService.getById(sysUser.getId());
        JSONObject resobj = new JSONObject();
        resobj.put("title","2019年全国结核病临床诊疗技能竞赛成绩单");
        JSONArray list = new JSONArray();
        JSONObject temp1 = new JSONObject();
        temp1.put("type","desc");
        temp1.put("key","姓名");
        temp1.put("value",sysUser.getRealname());
        list.add(temp1);
        JSONObject temp2 = new JSONObject();
        temp2.put("type","desc");
        temp2.put("key","省份");
        temp2.put("value",curUser.getProvcode());
        list.add(temp2);
        JSONObject temp3 = new JSONObject();
        temp3.put("type","desc");
        temp3.put("key","所在单位");
        temp3.put("value",curUser.getOrgname());
        list.add(temp3);
        String timestr =  String.valueOf(redisUtil.get("DEFAULT_TOTAL_QUERY_TIME"));
        boolean disallowflag = false;
        if(oConvertUtils.isEmpty(timestr)){
            disallowflag = true;
        }else{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date;
            try {
                date = sdf.parse(timestr);
                long curTimeMillis = System.currentTimeMillis();
                if(curTimeMillis < date.getTime()){
                    disallowflag = true;
                }
            } catch (Exception e) {
                date = null;
                disallowflag = true;
            }
        }
        if(disallowflag) {
            JSONObject temp4 = new JSONObject();
            temp4.put("type", "desc");
            temp4.put("key", "总成绩");
            temp4.put("value", "成绩暂未发布，请稍后再试！");
            list.add(temp4);
            resobj.put("list", list);
            result.setSuccess(true);
            result.setResult(resobj);
            return result;
        }
        Object examList = redisUtil.get("DEFAULT_EXMA_LIST");
        String examName = String.valueOf(redisUtil.get("DEFAULT_EXMA_NAME"));
        if(oConvertUtils.isEmpty(examList)){
            Map<String, Object> map = new HashMap<>();
            map.put("exam_name", examName);
            List<Exam> templist = examService.list(new QueryWrapper<Exam>().allEq(map).orderByAsc("sort_no"));
            examList = templist;
            redisUtil.set("DEFAULT_EXMA_LIST",templist);
            redisUtil.expire("DEFAULT_EXMA_LIST", 600);
        }
        List<Exam> exams = (List<Exam>)examList;

        Map<String, Object> map2 = new HashMap<>();
        map2.put("exam_name", examName);
        map2.put("user_id", sysUser.getId());
        Total total = totalService.getOne(new QueryWrapper<Total>().allEq(map2));
        if(total != null)
        {
            JSONObject temp4 = new JSONObject();
            temp4.put("type","desc");
            temp4.put("key","总成绩");
            if(oConvertUtils.isEmpty(total.getRanking())){
                temp4.put("value",total.getTotal());
            }else if(total.getRanking() == 999999) {
                temp4.put("value",total.getTotal());
            }else{
                    temp4.put("value",total.getTotal()+" 排名："+total.getRanking()+"/"+redisUtil.get("DEFAULT_TOTAL_QUERY_MAX"));
            }

            list.add(temp4);

            JSONObject details = JSONObject.parseObject(total.getDetail());

            for(int j=0; j<exams.size() && j<3 &&details != null;j++)
            {
                if(j == 0){
                    if(details.containsKey(exams.get(0).getId())){
                        JSONObject detail_0 = details.getJSONObject(exams.get(0).getId());
                        JSONObject temp5 = new JSONObject();
                        temp5.put("type","exam");
                        temp5.put("key","基础知识");
                        temp5.put("value",detail_0.get("score"));
                        temp5.put("examid",exams.get(0).getId());
                        list.add(temp5);

;                    }else{
                        JSONObject temp5 = new JSONObject();
                        temp5.put("type","desc");
                        temp5.put("key","基础知识");
                        temp5.put("value","未提交");
                        list.add(temp5);

                    }
                }
                if(j == 1){
                    if(details.containsKey(exams.get(1).getId())){
                        JSONObject detail_1 = details.getJSONObject(exams.get(1).getId());
                        JSONObject temp6 = new JSONObject();
                        temp6.put("type","exam");
                        temp6.put("key","影像诊断");
                        temp6.put("value",detail_1.get("score"));
                        temp6.put("examid",exams.get(1).getId());
                        list.add(temp6);
                    }else{
                        JSONObject temp6 = new JSONObject();
                        temp6.put("type","desc");
                        temp6.put("key","影像诊断");
                        temp6.put("value","未提交");
                        list.add(temp6);
                     }
                }
                if(j == 2){
                    if(details.containsKey(exams.get(2).getId())){
                        JSONObject detail_2 = details.getJSONObject(exams.get(2).getId());
                        JSONObject temp7 = new JSONObject();
                        temp7.put("type","exam");
                        temp7.put("key","病例分析");
                        temp7.put("value",detail_2.get("score"));
                        temp7.put("examid",exams.get(2).getId());
                        list.add(temp7);
                    }else{
                        JSONObject temp7 = new JSONObject();
                        temp7.put("type","desc");
                        temp7.put("key","病例分析");
                        temp7.put("value","未提交");
                        list.add(temp7);
                    }
                }
            }
            if(oConvertUtils.isNotEmpty(total.getRanking())){
                if(total.getRanking()<=100){
                    //优胜奖
                    JSONObject temp8 = new JSONObject();
                    temp8.put("type","desc");
                    temp8.put("key","证书");
                    temp8.put("value","个人优胜奖");
                    File file = new File( upLoadPath + File.separator + sysUser.getId()  + ".pdf");
                    if(file.exists()) {
                        temp8.put("link", "/cert/" + sysUser.getId() + ".pdf");
                    }
                    list.add(temp8);

                    if(!file.exists()){
                        JSONObject temp9 = new JSONObject();
                        temp9.put("type","desc");
                        temp9.put("key","说明");
                        temp9.put("value","证书正在审核制作中，请您1-2个工作日后再来查询！");
                        list.add(temp9);
                    }


                }else if(total.getRanking()>100 && total.getRanking()<= Integer.parseInt(redisUtil.get("DEFAULT_TOTAL_QUERY_MAX").toString())){
                    //参与奖
                    JSONObject temp8 = new JSONObject();
                    temp8.put("type","desc");
                    temp8.put("key","证书");
                    temp8.put("value","个人参与奖");
                    File file = new File( upLoadPath + File.separator + sysUser.getId()  + ".pdf");
                    if(file.exists()) {
                        temp8.put("link", "/cert/" + sysUser.getId() + ".pdf");
                    }
                    list.add(temp8);
                    if(!file.exists()){
                        JSONObject temp9 = new JSONObject();
                        temp9.put("type","desc");
                        temp9.put("key","说明");
                        temp9.put("value","证书正在审核制作中，请您1-2个工作日后再来查询！");
                        list.add(temp9);
                    }
                }
            }
        }else{
            JSONObject temp8 = new JSONObject();
            temp8.put("type","desc");
            temp8.put("key","总成绩");
            temp8.put("value","很遗憾，系统未能在考试时间内接收到您的答卷！");
            list.add(temp8);
        }
        resobj.put("list",list);
        result.setSuccess(true);
        result.setResult(resobj);
        return result;
    }

    /**
     * 分页列表查询
     * @param examName
     * @param pageNo
     * @param pageSize
     * @param provcode
     * @return
     */
    @AutoLog(value = "成绩表-分页列表查询")
    @ApiOperation(value="成绩表-分页列表查询", notes="成绩表-分页列表查询")
    @RequiresRoles(value = { "admin","EXAMADMIN","DESIGNER","OFFICER" },logical = Logical.OR )
    @GetMapping(value = "/queryPageListByExamName")
    public Result<JSONObject> queryPageListByExamName(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                      @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                      @RequestParam(name="examName", defaultValue="") String examName,
                                                      @RequestParam(name="provcode", defaultValue="") String provcode
                                             ) {
        Map<String, Object> map = new HashMap<>();
        map.put("exam_name", examName);
        List<Exam> exams = examService.list(new QueryWrapper<Exam>().allEq(map).orderByAsc("sort_no"));

        Result<JSONObject> result = new Result<JSONObject>();
        Page<TotalUserVo> page = new Page<TotalUserVo>(pageNo, pageSize);
        IPage<TotalUserVo> pageList = totalService.queryPageListByExamName(page, examName,provcode);
        JSONObject resultobj = new JSONObject();
        resultobj.put("current",pageList.getCurrent());
        resultobj.put("pages",pageList.getPages());
        resultobj.put("searchCount",pageList.isSearchCount());
        resultobj.put("size",pageList.getSize());
        resultobj.put("total",pageList.getTotal());
        JSONArray records = new JSONArray();
        List<TotalUserVo> pagelist = pageList.getRecords();
        for(int i=0; i<pagelist.size();i++){
            JSONObject record = new JSONObject();
            TotalUserVo curTotalUserVo = pagelist.get(i);
            record.put("id",curTotalUserVo.getId());
            record.put("orgname",curTotalUserVo.getOrgname());
            record.put("provcode",curTotalUserVo.getProvcode());
            record.put("citycode",curTotalUserVo.getCitycode());
            record.put("realname",curTotalUserVo.getRealname());
            //record.put("rank",curTotalUserVo.getRank());
            record.put("rank",curTotalUserVo.getRanking());
            record.put("total",curTotalUserVo.getTotal());
            record.put("completenum",curTotalUserVo.getCompleteNum());
            JSONObject details = JSONObject.parseObject(curTotalUserVo.getDetail());
            for(int j=0; j<exams.size() && j<3 && details!=null;j++)
            {
                if(j == 0){
                    if(details.containsKey(exams.get(0).getId())){
                        JSONObject detail_0 = details.getJSONObject(exams.get(0).getId());
                        record.put("score1",detail_0.get("score"));
                        record.put("rank1",detail_0.get("rank"));
                    }else{
                        record.put("score1","");
                        record.put("rank1","");
                    }
                }
                if(j == 1){
                    if(details.containsKey(exams.get(1).getId())){
                        JSONObject detail_1 = details.getJSONObject(exams.get(1).getId());
                        record.put("score2",detail_1.get("score"));
                        record.put("rank2",detail_1.get("rank"));
                    }else{
                        record.put("score2","");
                        record.put("rank2","");
                    }
                }
                if(j == 2){
                    if(details.containsKey(exams.get(2).getId())){
                        JSONObject detail_2 = details.getJSONObject(exams.get(2).getId());
                        record.put("score3",detail_2.get("score"));
                        record.put("rank3",detail_2.get("rank"));
                    }else{
                        record.put("score3","");
                        record.put("rank3","");
                    }
                }
            }

            records.add(record);
        }
        resultobj.put("records",records);
        result.setSuccess(true);
        result.setResult(resultobj);
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
    public ModelAndView exportXls(@RequestParam(name="provcode", defaultValue="") String provcode,HttpServletRequest request, HttpServletResponse response) {

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        String examName = String.valueOf(redisUtil.get("DEFAULT_EXMA_NAME"));
        Map<String, Object> map = new HashMap<>();
        map.put("exam_name", examName);
        List<Exam> exams = examService.list(new QueryWrapper<Exam>().allEq(map).orderByAsc("sort_no"));
        List<TotalUserVo> pageList = totalService.queryListByExamName(examName,provcode);
        List<TotalUserALLVo> allList = new ArrayList<TotalUserALLVo>();
        for(TotalUserVo curTotalUserVo:pageList){
            TotalUserALLVo userALLVo = new TotalUserALLVo();
            userALLVo.setId(curTotalUserVo.getId());
            userALLVo.setOrgname(curTotalUserVo.getOrgname());
            userALLVo.setProvcode(curTotalUserVo.getProvcode());
            userALLVo.setCitycode(curTotalUserVo.getCitycode());
            userALLVo.setRealname(curTotalUserVo.getRealname());
            userALLVo.setRanking(curTotalUserVo.getRanking());
            userALLVo.setTotal(curTotalUserVo.getTotal());
            userALLVo.setCompleteNum(curTotalUserVo.getCompleteNum());
            JSONObject details = JSONObject.parseObject(curTotalUserVo.getDetail());
            for(int j=0; j<exams.size() && j<3 && details!=null;j++)
            {
                if(j == 0){
                    if(details.containsKey(exams.get(0).getId())){
                        JSONObject detail_0 = details.getJSONObject(exams.get(0).getId());
                        userALLVo.setScore1(Double.parseDouble(String.valueOf(detail_0.get("score"))));
                        userALLVo.setRank1(Integer.parseInt(String.valueOf(detail_0.get("rank"))));
                    }else{
                    }
                }
                if(j == 1){
                    if(details.containsKey(exams.get(1).getId())){
                        JSONObject detail_1 = details.getJSONObject(exams.get(1).getId());
                        userALLVo.setScore2(Double.parseDouble(String.valueOf(detail_1.get("score"))));
                        userALLVo.setRank2(Integer.parseInt(String.valueOf(detail_1.get("rank"))));
                    }else{
                    }
                }
                if(j == 2){
                    if(details.containsKey(exams.get(2).getId())){
                        JSONObject detail_2 = details.getJSONObject(exams.get(2).getId());
                        userALLVo.setScore3(Double.parseDouble(String.valueOf(detail_2.get("score"))));
                        userALLVo.setRank3(Integer.parseInt(String.valueOf(detail_2.get("rank"))));
                    }else{
                    }
                }
            }
            allList.add(userALLVo);
        }
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "成绩表列表");
        mv.addObject(NormalExcelConstants.CLASS, TotalUserALLVo.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("成绩表列表数据", "导出人:", "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, allList);
        return mv;
    }
}


