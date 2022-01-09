package org.jeecg.modules.exam.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 试题表
 * @Author: jeecg-boot
 * @Date:   2019-08-08
 * @Version: V1.0
 */
@Data
@TableName("exam_question")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="exam_question对象", description="试题表")
public class Question {
	/**主键ID*/
	@TableId(type = IdType.UUID)
    @ApiModelProperty(value = "主键ID")
	private java.lang.String id;
	/**考试场次id*/
	@Excel(name = "考试场次id", width = 15)
    @ApiModelProperty(value = "考试场次id")
	private java.lang.String examId;
	/**试题序号*/
	@Excel(name = "试题序号", width = 15)
    @ApiModelProperty(value = "试题序号")
	private java.lang.Integer no;
	/**问题类型*/
	@Excel(name = "问题类型", width = 15)
    @ApiModelProperty(value = "问题类型")
	private java.lang.String type;
	/**大题描述*/
	@Excel(name = "大题描述", width = 20)
	@ApiModelProperty(value = "大题描述")
	private  java.lang.String  bigDescribe;
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
	@Excel(name = "试题答案", width = 15)
    @ApiModelProperty(value = "试题答案")
	private java.lang.String answer;
	/**试题分数*/
	@Excel(name = "试题分数", width = 15)
    @ApiModelProperty(value = "试题分数")
	private java.lang.Double score;
	/**难易程度*/
	@Excel(name = "难易程度(0:简单 1:中等 2:困难)", width = 15)
	@ApiModelProperty(value = "难易程度(0:简单 1:中等 2:困难)")
	private java.lang.Integer hard;
	/**排序序号*/
	@Excel(name = "排序序号", width = 15)
    @ApiModelProperty(value = "排序序号")
	private java.lang.Integer sortNo;
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
