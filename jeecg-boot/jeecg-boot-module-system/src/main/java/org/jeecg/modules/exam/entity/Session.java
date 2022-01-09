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

import java.io.Serializable;


/**
 * @Description: 场次名称
 * @Author: jeecg-boot
 * @Date:   2019-08-07
 * @Version: V1.0
 */
@Data
@TableName("exam_session")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="exam_session对象", description="场次名称")
public class Session implements Serializable{

	/**场次名称*/
	@Excel(name = "场次名称", width = 15)
    @ApiModelProperty(value = "场次名称")
	private String sessionName;
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

}
