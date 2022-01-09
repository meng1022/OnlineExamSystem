package org.jeecg.modules.exam.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SubmitExamUserVo implements Serializable {

    @ApiModelProperty(value = "答案提交主键id")
    @TableId(value = "id", type = IdType.UUID)
    private String id;;

    @ApiModelProperty(value = "考试外键id")
    @TableField("exam_id")
    private String examId;

    @ApiModelProperty(value = "用户外键id")
    @TableField("user_id")
    private String userId;

    @ApiModelProperty(value = "考生答案")
    private String result;

    @ApiModelProperty(value = "场次名称")
    private String sessionName;

    @ApiModelProperty(value = "考生姓名")
    private String realname;

    @ApiModelProperty(value = "省")
    private String provcode;

    @ApiModelProperty(value = "市")
    private String citycode;

    @ApiModelProperty(value = "提交时间")
    private java.util.Date createTime;
}
