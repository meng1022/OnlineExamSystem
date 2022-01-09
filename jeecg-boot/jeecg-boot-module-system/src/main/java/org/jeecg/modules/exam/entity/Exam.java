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
 * @Description: 考试
 * @Author: jeecg-boot
 * @Date:   2019-08-07
 * @Version: V1.0
 */
@Data
@TableName("exam_exam")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="exam_exam对象", description="考试")
public class Exam implements Serializable{
    
	/**主键ID*/
	@TableId(type = IdType.UUID)
    @ApiModelProperty(value = "主键ID")
	private java.lang.String id;
	/**考试名称*/
	@Excel(name = "考试名称", width = 15)
	@ApiModelProperty(value = "考试名称")
	private java.lang.String examName;
	/**场次*/
	@Excel(name = "场次", width = 15)
    @ApiModelProperty(value = "场次")
	private java.lang.String sessionCode;
	/**考试名称*/
	@Excel(name = "场次名称", width = 15)
    @ApiModelProperty(value = "场次名称")
	private java.lang.String sessionName;
	/**可见开始时间*/
	@Excel(name = "可见开始时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "可见开始时间")
	private java.util.Date visiableStartTime;
	/**可见结束时间*/
	@Excel(name = "可见结束时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "可见结束时间")
	private java.util.Date visiableEndTime;
	/**考试开始时间*/
	@Excel(name = "考试开始时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "考试开始时间")
	private java.util.Date examStartTime;
	/**考试结束时间*/
	@Excel(name = "考试结束时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "考试结束时间")
	private java.util.Date examEndTime;
	/**验证码*/
	@Excel(name = "验证码", width = 15)
    @ApiModelProperty(value = "验证码")
	private java.lang.String verificationCode;
	/**考试时长*/
	@Excel(name = "考试时长", width = 15)
	@ApiModelProperty(value = "考试时长")
	private java.lang.Integer examDuration;
	/**序号*/
	@Excel(name = "序号", width = 15)
	@ApiModelProperty(value = "序号")
	private java.lang.Integer sortNo;

	/**允许提交次数*/
	@Excel(name = "允许提交次数", width = 15)
	@ApiModelProperty(value = "允许提交次数")
	private java.lang.Integer submitTimes;

	/**是否启用服务器端提交缓存*/
	@Excel(name = "是否启用(0：不启用  1：启用 ）", width = 15)
	@ApiModelProperty(value = "是否启用服务器端提交缓存")
	private java.lang.Integer submitServerCache;

	/**状态(1：正常  2：冻结 ）*/
	@Excel(name = "状态(1：正常  2：冻结 ）", width = 15)
    @ApiModelProperty(value = "状态(1：正常  2：冻结 ）")
	private java.lang.Integer status;
	/**删除状态（0，正常，1已删除）*/
	@Excel(name = "删除状态（0，正常，1已删除）", width = 15)
    @ApiModelProperty(value = "删除状态（0，正常，1已删除）")
	private java.lang.String delflag;
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
