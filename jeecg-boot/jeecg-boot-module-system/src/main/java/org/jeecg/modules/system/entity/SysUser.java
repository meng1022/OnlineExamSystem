package org.jeecg.modules.system.entity;

import java.util.Date;

import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.UUID)
    private String id;

    /**
     * 登录账号
     */
    @Excel(name = "登录账号", width = 15)
    private String username;

    /**
     * 真实姓名
     */
    @Excel(name = "真实姓名", width = 15)
    private String realname;

    /**
     * 密码
     */
    private String password;

    /**
     * md5密码盐
     */
    private String salt;

    /**
     * 头像
     */

    private String avatar;

    /**
     * 生日
     */
    @Excel(name = "出生日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    /**
     * 性别（1：男 2：女）
     */
    @Excel(name = "性别", width = 15,dicCode="sex")
    @Dict(dicCode = "sex")
    private Integer sex;

    /**
     * 电子邮件
     */
    //@Excel(name = "电子邮件", width = 15)
    private String email;

    /**
     * 电话@Excel(name = "电话", width = 15)
     */

    private String phone;

    /**
     * 部门code
     */
    private String orgCode;

    /**
     * 身份证号码
     */
    //@Excel(name = "身份证号码", width = 15)
    private String idcard;

    /**
     * 省行政编码
     */
    @Excel(name = "省份", width = 15)
    private String provcode;

    /**
     * 市行政编码
     */
    @Excel(name = "市/区", width = 15)
    private String citycode;

    /**
     * 机构名称
     */
    @Excel(name = "单位", width = 15)
    private String orgname;

    /**
     * 机构类型
     */
    @Excel(name = "单位类型", width = 15)
    private String orgtype;

    /**
     * 工作类型
     */
    @Excel(name = "工作类型", width = 15)
    private String jobtype;

    /**
     * 职称
     */
    @Excel(name = "职称", width = 15)
    private String proftitle;

    /**
     * 学历
     */
    @Excel(name = "学历", width = 15)
    private String edu;
    /**
     * 状态(1：正常  2：冻结 ）
     */
    //@Excel(name = "状态", width = 15,dicCode="user_status")
    @Dict(dicCode = "user_status")
    private Integer status;

    /**
     * 删除状态（0，正常，1已删除）
     */
    //@Excel(name = "删除状态", width = 15,dicCode="del_flag")
    private String delFlag;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 同步工作流引擎1同步0不同步
     */
    private String activitiSync;


}
