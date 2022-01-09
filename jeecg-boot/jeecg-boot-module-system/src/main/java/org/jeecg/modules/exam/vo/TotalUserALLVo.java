package org.jeecg.modules.exam.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

@Data
public class TotalUserALLVo implements Serializable {

    @ApiModelProperty(value = "总成绩主键id")
    @TableId(value = "id", type = IdType.UUID)
    private String id;;

    @Excel(name = "考试省份", width = 15)
    @ApiModelProperty(value = "省份")
    @TableField("provcode")
    private String provcode;

    @Excel(name = "地市", width = 15)
    @ApiModelProperty(value = "地市")
    private String citycode;


    @Excel(name = "姓名", width = 15)
    @ApiModelProperty(value = "姓名")
    @TableField("realname")
    private String realname;

    @Excel(name = "单位", width = 15)
    @ApiModelProperty(value = "单位")
    @TableField("orgname")
    private String orgname;

    @Excel(name = "总成绩", width = 15)
    @ApiModelProperty(value = "总成绩")
    private Double total;

    @Excel(name = "总排名", width = 15)
    @ApiModelProperty(value = "总排名")
    private Integer ranking;

    @Excel(name = "基础知识成绩", width = 15)
    @ApiModelProperty(value = "基础知识成绩")
    private Double score1;

    @Excel(name = "基础知识排名", width = 15)
    @ApiModelProperty(value = "基础知识排名")
    private Integer rank1;

    @Excel(name = "影像诊断成绩", width = 15)
    @ApiModelProperty(value = "影像诊断成绩")
    private Double score2;

    @Excel(name = "影像诊断排名", width = 15)
    @ApiModelProperty(value = "影像诊断排名")
    private Integer rank2;

    @Excel(name = "病例分析成绩", width = 15)
    @ApiModelProperty(value = "病例分析成绩")
    private Double score3;

    @Excel(name = "病例分析排名", width = 15)
    @ApiModelProperty(value = "病例分析排名")
    private Integer rank3;

    @Excel(name = "完成场数", width = 15)
    @ApiModelProperty(value = "完成数量")
    private Integer completeNum;


}
