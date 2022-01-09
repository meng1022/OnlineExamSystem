package org.jeecg.modules.system.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 注册表单
 *
 * @Author wzl
 * @since  2019-07-24
 */
@ApiModel(value="发送短信对象", description="发送短信对象")
public class SysSmsModel {
	@ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "短信类型")
    private String smstype;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSmstype() {
        return smstype;
    }

    public void setSmstype(String smstype) {
        this.smstype = smstype;
    }


}