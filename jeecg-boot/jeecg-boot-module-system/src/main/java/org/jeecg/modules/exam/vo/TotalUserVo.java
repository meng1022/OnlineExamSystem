package org.jeecg.modules.exam.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class TotalUserVo implements Serializable {

    @ApiModelProperty(value = "总成绩主键id")
    @TableId(value = "id", type = IdType.UUID)
    private String id;;

    @ApiModelProperty(value = "userid")
    @TableField("user_id")
    private String userId;

    @ApiModelProperty(value = "考试省份")
    @TableField("provcode")
    private String provcode;

    @ApiModelProperty(value = "地市")
    @TableField("citycode")
    private String citycode;

    @ApiModelProperty(value = "姓名")
    @TableField("realname")
    private String realname;

    @ApiModelProperty(value = "单位")
    @TableField("orgname")
    private String orgname;

    @ApiModelProperty(value = "总成绩")
    private Double total;

    @ApiModelProperty(value = "总排名")
    private Integer ranking;


    @ApiModelProperty(value = "完成数量")
    private Integer completeNum;

    @ApiModelProperty(value = "详情")
    @TableField("detail")
    private String detail;

}
