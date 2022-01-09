package org.jeecg.modules.exam.vo;
import lombok.Data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.List;

@Data
public class ScoreExamUserVo implements Serializable {

    @ApiModelProperty(value = "成绩主键id")
    @TableId(value = "id", type = IdType.UUID)
    private java.lang.String id;;

    @ApiModelProperty(value = "考试外键id")
    @TableField("exam_id")
    private java.lang.String examId;

    @ApiModelProperty(value = "用户外键id")
    @TableField("user_id")
    private java.lang.String userId;

    @ApiModelProperty(value = "客观题成绩")
    private Double score1;

    @ApiModelProperty(value = "问答题成绩")
    private Double score2;

    @ApiModelProperty(value = "考试成绩")
    private Double score;

    @ApiModelProperty(value = "考试名称")
    private java.lang.String examName;

    @ApiModelProperty(value = "场次名称")
    private java.lang.String sessionName;

    @ApiModelProperty(value = "考生姓名")
    private java.lang.String realname;

    @ApiModelProperty(value = "省")
    private java.lang.String provcode;

    @ApiModelProperty(value = "市")
    private java.lang.String citycode;

    @ApiModelProperty(value = "提交时间")
    private java.util.Date submitTime;
}
