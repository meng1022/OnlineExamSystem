package org.jeecg.modules.system.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 注册表单
 *
 * @Author wzl
 * @since  2019-07-24
 */
@ApiModel(value="注册对象", description="注册对象")
public class SysRegisterModel {
	@ApiModelProperty(value = "手机号")
    private String phone;
	@ApiModelProperty(value = "密码")
    private String password;
	@ApiModelProperty(value = "验证码")
    private String captcha;
    @ApiModelProperty(value = "姓名")
    private String realname;
    @ApiModelProperty(value = "身份证号")
    private String idcard;
    @ApiModelProperty(value = "邮箱")
    private String email;
    @ApiModelProperty(value = "省编码")
    private String provcode;
    @ApiModelProperty(value = "市编码")
    private String citycode;
    @ApiModelProperty(value = "机构名称")
    private String orgname;
    @ApiModelProperty(value = "机构类别")
    private String orgtype;
    @ApiModelProperty(value = "工作类别")
    private String jobtype;
    @ApiModelProperty(value = "职称")
    private String proftitle;
    @ApiModelProperty(value = "学历")
    private String edu;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getProftitle() {
        return proftitle;
    }

    public void setProftitle(String proftitle) {
        this.proftitle = proftitle;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProvcode() {
        return provcode;
    }

    public void setProvcode(String provcode) {
        this.provcode = provcode;
    }

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }

    public String getOrgname() {
        return orgname;
    }

    public void setOrgname(String orgname) {
        this.orgname = orgname;
    }

    public String getOrgtype() {
        return orgtype;
    }

    public void setOrgtype(String orgtype) {
        this.orgtype = orgtype;
    }

    public String getJobtype() {
        return jobtype;
    }

    public void setJobtype(String jobtype) {
        this.jobtype = jobtype;
    }

    public String getEdu() {
        return edu;
    }

    public void setEdu(String edu) {
        this.edu = edu;
    }
}