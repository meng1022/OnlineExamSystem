/******************************************
 * My Login
 *
 * Bootstrap 4 Login Page
 *
 * @author          Muhamad Nauval Azhar
 * @uri 			https://nauval.in
 * @copyright       Copyright (c) 2018 Muhamad Nauval Azhar
 * @license         My Login is licensed under the MIT license.
 * @github          https://github.com/nauvalazhar/my-login
 * @version         1.2.0
 *
 * Help me to keep this project alive
 * https://www.buymeacoffee.com/mhdnauvalazhar
 *
 ******************************************/

'use strict';

$(function() {

	// author badge :)
//	var author = '<div style="position: fixed;bottom: 0;right: 20px;background-color: #fff;box-shadow: 0 4px 8px rgba(0,0,0,.05);border-radius: 3px 3px 0 0;font-size: 12px;padding: 5px 10px;">By <a href="https://twitter.com/mhdnauvalazhar">@mhdnauvalazhar</a> &nbsp;&bull;&nbsp; <a href="https://www.buymeacoffee.com/mhdnauvalazhar">Buy me a Coffee</a></div>';
//	$("body").append(author);

	$("input[type='password'][data-eye]").each(function(i) {
		var $this = $(this),
			id = 'eye-password-' + i,
			el = $('#' + id);

		$this.wrap($("<div/>", {
			style: 'position:relative',
			id: id
		}));

		$this.css({
			paddingRight: 60
		});
		$this.after($("<div/>", {
			html: '显示',
			class: 'btn btn-primary btn-sm',
			id: 'passeye-toggle-'+i,
		}).css({
				position: 'absolute',
				right: 10,
				top: ($this.outerHeight() / 2) - 12,
				padding: '2px 7px',
				fontSize: 12,
				cursor: 'pointer',
		}));

		$this.after($("<input/>", {
			type: 'hidden',
			id: 'passeye-' + i
		}));

		var invalid_feedback = $this.parent().parent().find('.invalid-feedback');

		if(invalid_feedback.length) {
			$this.after(invalid_feedback.clone());
		}

		$this.on("keyup paste", function() {
			$("#passeye-"+i).val($(this).val());
		});
		$("#passeye-toggle-"+i).on("click", function() {
			if($this.hasClass("show")) {
				$this.attr('type', 'password');
				$this.removeClass("show");
				$(this).removeClass("btn-outline-primary");
			}else{
				$this.attr('type', 'text');
				$this.val($("#passeye-"+i).val());
				$this.addClass("show");
				$(this).addClass("btn-outline-primary");
			}
		});
	});


	//报名页面发送验证码
    $('#capnumber').click(function(){
        $(this).attr("disabled",true);
		var vercode	 = 0;
		var time = 60;
		var flag = true;   //设置点击标记，防止60内再次点击生效
		var phone = $('#phone').val();
        if(flag){
            var timer = setInterval(function () {
                if(time == 60 && flag){
                    flag = false;
                    let payload = {
				    	phone: phone,
				    	smstype: 1
				    }
                    $.ajax({
                      type: 'POST',
                      url: '/web-api/sys/sms',
                      data: JSON.stringify(payload),
                      contentType: "application/json",
                      success: function(res) {
                        if (res.message == "手机验证码发送成功") {
                            $('#alertText').html(res.message);
                            $('#myModal').modal('show')
                        } else {
                            $('#alertText').html(res.message)
                            $('#myModal').modal('show')
                            flag = true;
                            time = 60;
                            clearInterval(timer);
                            $("#capnumber").removeAttr("disabled");
                        }
                      }
                    });
                } else if(time == 0){
                    $("#capnumber").removeAttr("disabled");
                    $("#capnumber").val("获取验证码");
                    clearInterval(timer);
                    time = 60;
                    flag = true;
                }else {
                    $("#capnumber").val(time + " s 重新发送");
                    time--;
                }
            },1000);
		}

	});

	$(".my-login-validation").submit(function() {
        var form = $(this);
        var bootstrapValidator = form.data('bootstrapValidator');
        event.preventDefault();
        event.stopPropagation();
        if (bootstrapValidator.isValid()) {
            event.preventDefault();
            event.stopPropagation();
            $('#phone_value').html(document.getElementById('phone').value)
            $('#realname_value').html(document.getElementById('realname').value)
            $('#idcard_value').html(document.getElementById('idcard').value)
            $('#email_value').html(document.getElementById('email').value)
            $('#provcity_value').html(document.getElementById('province').value + ' ' + document.getElementById('city').value)
            $('#orgname_value').html(document.getElementById('orgname').value)
            $('#orgtype_value').html(document.getElementById('orgtype').value)
            $('#jobtype_value').html(document.getElementById('id_select').value)
            $('#edu_value').html(document.getElementById('edu').value)
            $('#proftitle_value').html(document.getElementById('proftitle').value)
            $('#myTest').modal('show')
        } else {
            $('#myTest2').modal('show')
        }
    })

    $('#myTest-yes').click(function(){
        let payload = {
            phone: document.getElementById('phone').value,
            password: document.getElementById('password1').value,
            captcha: document.getElementById('captcha').value,
            realname: document.getElementById('realname').value,
            idcard: document.getElementById('idcard').value,
            email: document.getElementById('email').value,
            provcode: document.getElementById('province').value,
            citycode: document.getElementById('city').value,
            orgname: document.getElementById('orgname').value,
            orgtype: document.getElementById('orgtype').value,
            jobtype: document.getElementById('id_select').value,
            edu: document.getElementById('edu').value,
            proftitle: document.getElementById('proftitle').value
        }
        $.ajax({
          type: 'POST',
          url: '/web-api/sys/register',
          data: JSON.stringify(payload),
          contentType: "application/json",
          success: function(res) {
            $('#alertText').html(res.message)
            $('#myModal').modal('show')
            $('#myModal').on('hidden.bs.modal', function () {
                if (res.success == true && res.message == "添加成功！") {
                    window.location.href = '/'
                } else {
                    return
                }
            })
          }
        });
    })

    $(".my-login-validation").bootstrapValidator({
        message: 'This value is not valid',
        feedbackIcons: {
　　　　　　　valid: 'glyphicon glyphicon-ok',
　　　　　　　invalid: 'glyphicon glyphicon-remove',
　　　　　　　validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
            phone: {
                message: '手机号验证失效',
                validators: {
                    notEmpty: {
                        message: '手机号不能为空'
                    },
                    phone: {
                        message: '请输入正确的手机号',
                        country:'CN'
                    }
                }
            },
            captcha: {
                message: '验证码不正确',
                validators: {
                    notEmpty: {
                        message: '请输入验证码'
                    }
                }
            },
            password1: {
                message: '密码验证失效',
                validators: {
                    callback: {
                        callback: function(value, validator) {
                           // 检查密码长度
                            if (value.length < 6) {
                                return {
                                    valid: false,
                                    message: '密码必须超过6位'
                                }
                            }
                            return {valid: true}
                        }
                    },
                    identical: {
                        field: 'password2',
                        message: '   '
                    }
                }
            },
            password2: {
                message: '密码输入不正确',
                validators: {
                    notEmpty: {
                        message: '请确认密码'
                    },
                    identical: {
                        field: 'password1',
                        message: '两次密码不相同'
                    }
                }
            },
            realname: {
                message: '姓名验证失效',
                validators: {
                    notEmpty: {
                        message: '姓名不能为空'
                    },
                    regexp: { //正则表达式
                        regexp: /[\u4e00-\u9fa5]{2,}/,
                        message: '姓名必须为中文，且长度不小于2位'
                    },
                }
            },
            idcard: {
                validators: {
                    notEmpty : {
                        message : '身份证号码不能为空！'
                    },
                    regexp: {
                        regexp: /^(^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$)|(^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])((\d{4})|\d{3}[Xx])$)$/,
                        message: '身份证号码格式不正确，为15位和18位身份证号码！'
                    },
                    callback: {/*自定义，可以在这里与其他输入项联动校验*/
                        message: '身份证号码无效！',
                        callback:function(value, validator,$field){
                            //15位和18位身份证号码的正则表达式
                            var regIdCard = /^(^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$)|(^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])((\d{4})|\d{3}[Xx])$)$/;
                            //如果通过该验证，说明身份证格式正确，但准确性还需计算
                            var idCard = value;
                            if (regIdCard.test(idCard)) {
                                if (idCard.length == 18) {
                                    var idCardWi = new Array(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2); //将前17位加权因子保存在数组里
                                    var idCardY = new Array(1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2); //这是除以11后，可能产生的11位余数、验证码，也保存成数组
                                    var idCardWiSum = 0; //用来保存前17位各自乖以加权因子后的总和
                                    for (var i = 0; i < 17; i++) {
                                        idCardWiSum += idCard.substring(i, i + 1) * idCardWi[i];
                                    }
                                    var idCardMod = idCardWiSum % 11;//计算出校验码所在数组的位置
                                    var idCardLast = idCard.substring(17);//得到最后一位身份证号码
                                    //如果等于2，则说明校验码是10，身份证号码最后一位应该是X
                                    if (idCardMod == 2) {
                                        if (idCardLast == "X" || idCardLast == "x") {
                                            return true;
                                            //alert("恭喜通过验证啦！");
                                        } else {
                                            return false;
                                            //alert("身份证号码错误！");
                                        }
                                    } else {
                                        //用计算出的验证码与最后一位身份证号码匹配，如果一致，说明通过，否则是无效的身份证号码
                                        if (idCardLast == idCardY[idCardMod]) {
                                            //alert("恭喜通过验证啦！");
                                            return true;
                                        } else {
                                            return false;
                                            //alert("身份证号码错误！");
                                        }
                                    }
                                }
                            } else {
                                //alert("身份证格式不正确!");
                                return false;
                            }
                        }
                    }
                }
            },
            email: {
                validators: {
                    // notEmpty: {
                    //     message: '电子邮箱不能为空'
                    // },
                    emailAddress: {
                        message: '邮箱地址格式有误'
                    }
                }
            },
            province: {
                validators: {
                    notEmpty: {
                        message: '请选择所在省市'
                    }
                }
            },
            orgname: {
                validators: {
                    notEmpty: {
                        message: '请填写所在单位(全称)'
                    }
                }
            },
            orgtype: {
                validators: {
                    notEmpty: {
                        message: '请选择单位类别'
                    }
                }
            },
            jobtype: {
                validators: {
                    notEmpty: {
                        message: '请选择工作类别'
                    }
                }
            },
            edu: {
                validators: {
                    notEmpty: {
                        message: '请选择最高学历'
                    }
                }
            },
            proftitle: {
                validators: {
                    notEmpty: {
                        message: '请选择职称'
                    }
                }
            }
        }
    })


    //密码重置页面发送验证码
    $('#capnumber1').click(function(){
        $(this).attr("disabled",true);
        var vercode  = 0;
        var time = 60;
        var flag = true;   //设置点击标记，防止60内再次点击生效
        var phone = $('#phone1').val();
        if(flag){
            var timer = setInterval(function () {
                if(time == 60 && flag){
                    flag = false;
                    let payload = {
                        phone: phone,
                        smstype: 2
                    }
                    $.ajax({
                      type: 'POST',
                      url: '/web-api/sys/sms',
                      data: JSON.stringify(payload),
                      contentType: "application/json",
                      success: function(res) {
                        if (res.message == "手机验证码发送成功") {
                          $('#alertText').html(res.message)
                          $('#myModal').modal('show')
                        } else {
                            $('#alertText').html(res.message)
                            $('#myModal').modal('show')
                            flag = true;
                            time = 60;
                            clearInterval(timer);
                            $("#capnumber1").removeAttr("disabled");
                        }
                      }
                    });
                } else if(time == 0){
                    $("#capnumber1").removeAttr("disabled");
                    $("#capnumber1").val("获取验证码");
                    clearInterval(timer);
                    time = 60;
                    flag = true;
                }else {
                    $("#capnumber1").val(time + " s 重新发送");
                    time--;
                }
            },1000);
        }

    });

    $(".my-forgot-validation").submit(function() {
        var form = $(this);
        var bootstrapValidator = form.data('bootstrapValidator');
        event.preventDefault();
        event.stopPropagation();
        if (bootstrapValidator.isValid()) {
            event.preventDefault();
            event.stopPropagation();
            let payload = {
                phone: document.getElementById('phone1').value,
                password: document.getElementById('password11').value,
                captcha: document.getElementById('captcha1').value

            }
            $('#ReTest').modal('show')
        } else {
            $('#ReTest2').modal('show')
        }
    })

    $('#ReTest-yes').click(function(){
        let payload = {
            phone: document.getElementById('phone1').value,
            password: document.getElementById('password11').value,
            captcha: document.getElementById('captcha1').value

        }
        $.ajax({
            type: 'POST',
            url: '/web-api/sys/reset',
            data: JSON.stringify(payload),
            contentType: "application/json",
            success: function(res) {
                $('#alertText').html(res.message)
                $('#myModal').modal('show')
                $('#ReTest').modal('hide')
                $('#myModal').on('hidden.bs.modal', function () {
                    if (res.success == true && res.message == "密码修改完成！") {
                        window.location.href = '/'
                    } else {
                        return
                    }
                })
            }
        });
    })



    // $('#Rest-modal').click(function () {
    //     var form = $(this);
    //     var bootstrapValidator = form.data('bootstrapValidator');
    //     event.preventDefault();
    //     event.stopPropagation();
    //     $('#ReTest').modal('show')
    //     $('#ReTest').on('shown.bs.modal', function () {
    //         $(".my-forgot-validation").submit(function () {
    //             var form = $(this);
    //             var bootstrapValidator = form.data('bootstrapValidator');
    //             event.preventDefault();
    //             event.stopPropagation();
    //             if (bootstrapValidator.isValid()) {
    //                 event.preventDefault();
    //                 event.stopPropagation();
    //                 let payload = {
    //                     phone: document.getElementById('phone1').value,
    //                     password: document.getElementById('password11').value,
    //                     captcha: document.getElementById('captcha1').value
    //
    //                 }
    //                 $.ajax({
    //                     type: 'POST',
    //                     url: '/web-api/sys/reset',
    //                     data: JSON.stringify(payload),
    //                     contentType: "application/json",
    //                     success: function (res) {
    //                         if ( res.message !="密码修改完成！" ){
    //                             $('#ReTest').modal('hide')
    //                         }
    //                         $('#alertText').html(res.message)
    //                         $('#myModal').modal('show')
    //                         if (res.success == true && res.message == "密码修改完成！") {
    //                             window.location.href = '/'
    //                         } else {
    //                             return
    //                         }
    //                     }
    //                 });
    //             } else {
    //                 $('#ReTest2').modal('show')
    //                 $('#ReTest').modal('hide')
    //             }
    //
    //         })
    //
    //     })
    // })



    $(".my-forgot-validation").bootstrapValidator({
        message: 'This value is not valid',
        feedbackIcons: {
　　　　　　　valid: 'glyphicon glyphicon-ok',
　　　　　　　invalid: 'glyphicon glyphicon-remove',
　　　　　　　validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
            phone: {
                message: '手机号验证失效',
                validators: {
                    notEmpty: {
                        message: '手机号不能为空'
                    },
                    phone: {
                        message: '请输入正确的手机号',
                        country:'CN'
                    }
                }
            },
            captcha: {
                message: '验证码不正确',
                validators: {
                    notEmpty: {
                        message: '请输入验证码'
                    }
                }
            },
            password1: {
                message: '密码验证失效',
                validators: {
                    callback: {
                        callback: function(value, validator) {
                           // 检查密码长度
                            if (value.length < 6) {
                                return {
                                    valid: false,
                                    message: '密码必须超过6位'
                                }
                            }
                            return {valid: true}
                        }
                    },
                    identical: {
                        field: 'password2',
                        message: '   '
                    }
                }
            },
            password2: {
                message: '密码输入不正确',
                validators: {
                    notEmpty: {
                        message: '请确认密码'
                    },
                    identical: {
                        field: 'password1',
                        message: '两次密码不相同'
                    }
                }
            }
        }
    })


});

function selectOnChangeFunc() {
    document.getElementById('id_select').value = document.getElementById('id_select').options[document.getElementById('id_select').selectedIndex].value;
}


