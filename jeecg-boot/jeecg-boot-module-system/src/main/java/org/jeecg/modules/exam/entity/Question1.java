package org.jeecg.modules.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;


@Data
@TableName("exam_question1")
@EqualsAndHashCode(callSuper = false)//?
@Accessors(chain = true)
public class Question1 {//试题库中试题的数据类型

    @TableId(type = IdType.UUID)
    @ApiModelProperty(value = "主键ID")
    private java.lang.String id;
    /**考试场次名字*/
    @Excel(name = "科目名称", width = 15)
    @ApiModelProperty(value = "科目名称")
    private java.lang.String sessionName;
    /**问题类型*/
    @Excel(name = "问题类型(必填，single/multi/judge/quest)", width = 15)
    @ApiModelProperty(value = "问题类型")
    private java.lang.String type;
    /**问题描述*/
    @Excel(name = "问题描述", width = 15)
    @ApiModelProperty(value = "问题描述")
    private java.lang.String content;
    /**选项A*/
    @Excel(name = "选项A", width = 15)
    @ApiModelProperty(value = "选项A")
    private java.lang.String optionA;
    /**选项B*/
    @Excel(name = "选项B", width = 15)
    @ApiModelProperty(value = "选项B")
    private java.lang.String optionB;
    /**选项C*/
    @Excel(name = "选项C", width = 15)
    @ApiModelProperty(value = "选项C")
    private java.lang.String optionC;
    /**选项D*/
    @Excel(name = "选项D", width = 15)
    @ApiModelProperty(value = "选项D")
    private java.lang.String optionD;
    /**选项E*/
    @Excel(name = "选项E", width = 15)
    @ApiModelProperty(value = "选项E")
    private java.lang.String optionE;
    /**图片AURL*/
    @Excel(name = "图片AURL", width = 15)
    @ApiModelProperty(value = "图片AURL")
    private java.lang.String imgUrlA;
    /**图片BURL*/
    @Excel(name = "图片BURL", width = 15)
    @ApiModelProperty(value = "图片BURL")
    private java.lang.String imgUrlB;
    /**图片CURL*/
    @Excel(name = "图片CURL", width = 15)
    @ApiModelProperty(value = "图片CURL")
    private java.lang.String imgUrlC;
    /**图片DURL*/
    @Excel(name = "图片DURL", width = 15)
    @ApiModelProperty(value = "图片DURL")
    private java.lang.String imgUrlD;
    /**图片EURL*/
    @Excel(name = "图片EURL", width = 15)
    @ApiModelProperty(value = "图片EURL")
    private java.lang.String imgUrlE;
    /**试题答案*/
    @Excel(name = "试题答案(必填)", width = 15)
    @ApiModelProperty(value = "试题答案")
    private java.lang.String answer;
    /**试题分数*/
    @Excel(name = "试题分数(必填)", width = 15)
    @ApiModelProperty(value = "试题分数")
    private java.lang.Double score;
    /**难易程度*/
    @Excel(name = "难易程度(0:简单 1:中等 2:困难)", width = 15)
    @ApiModelProperty(value = "难易程度(0:简单 1:中等 2:困难)")
    private java.lang.Integer hard;
    /**创建人*/
    @Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
    /**创建时间*/
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
    /**更新人*/
    @Excel(name = "更新人", width = 15)
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
    /**更新时间*/
    @Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private java.util.Date updateTime;
}
