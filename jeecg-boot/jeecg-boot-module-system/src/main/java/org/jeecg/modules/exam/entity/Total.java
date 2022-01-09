package org.jeecg.modules.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 总成绩与排名表
 * @Author: jeecg-boot
 * @Date:   2019-08-08
 * @Version: V1.0
 */
@Data
@TableName("exam_total")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="exam_total对象", description="总成绩与排名表")
public class Total {

	/**主键ID*/
	@TableId(type = IdType.UUID)
    @ApiModelProperty(value = "主键ID")
	private String id;
	/**考试主名称*/
	@Excel(name = "exam_name", width = 15)
    @ApiModelProperty(value = "考试主名称")
	private String examName;
	/**用户id*/
	@Excel(name = "用户id", width = 15)
    @ApiModelProperty(value = "用户id")
	private String userId;
	/**总成绩*/
	@Excel(name = "总成绩", width = 15)
    @ApiModelProperty(value = "总成绩")
	private Double total;

	/**总排名*/
	@Excel(name = "总排名dis", width = 15)
	@ApiModelProperty(value = "总排名dis")
	private Integer rank;

	/**总排名*/
	@Excel(name = "总排名", width = 15)
	@ApiModelProperty(value = "总排名")
	private Integer ranking;

	/**完成科目数*/
	@Excel(name = "完成科目数", width = 15)
	@ApiModelProperty(value = "完成科目数")
	private Integer completeNum;

	/**详情*/
	@Excel(name = "详情", width = 15)
	@ApiModelProperty(value = "详情")
	private String detail;

	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
	private String createBy;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;
	/**更新人*/
	@Excel(name = "更新人", width = 15)
    @ApiModelProperty(value = "更新人")
	private String updateBy;
	/**更新时间*/
	@Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
	private Date updateTime;
}
