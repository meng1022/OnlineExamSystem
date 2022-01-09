<template>
  <a-drawer
    :title="title"
    :maskClosable="true"
    :width="drawerWidth"
    placement="right"
    :closable="true"
    @close="handleCancel"
    :visible="visible"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">

    <template slot="title">
      <div style="width: 100%;">
        <span>{{ title }}</span>
        <span style="display:inline-block;width:calc(100% - 51px);padding-right:10px;text-align: right">
          <a-button @click="toggleScreen" icon="appstore" style="height:20px;width:20px;border:0px"></a-button>
        </span>
      </div>

    </template>

    <a-spin :spinning="confirmLoading">
      <a-form :form="form">

        <a-form-item label="用户账号" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input placeholder="请输入用户账号" v-decorator="[ 'username', validatorRules.username]" :readOnly="!!model.id"/>
        </a-form-item>

        <template v-if="!model.id">
          <a-form-item label="登陆密码" :labelCol="labelCol" :wrapperCol="wrapperCol" >
            <a-input type="password" placeholder="请输入登陆密码" v-decorator="[ 'password', validatorRules.password]" />
          </a-form-item>

          <a-form-item label="确认密码" :labelCol="labelCol" :wrapperCol="wrapperCol" >
            <a-input type="password" @blur="handleConfirmBlur" placeholder="请重新输入登陆密码" v-decorator="[ 'confirmpassword', validatorRules.confirmpassword]"/>
          </a-form-item>
        </template>

        <a-form-item label="用户名字" :labelCol="labelCol" :wrapperCol="wrapperCol" >
          <a-input placeholder="请输入用户名称" v-decorator="[ 'realname', validatorRules.realname]" />
        </a-form-item>

        <a-form-item label="身份证号" :labelCol="labelCol" :wrapperCol="wrapperCol" >
          <a-input placeholder="请输入身份证号" v-decorator="[ 'idcard', validatorRules.idcard]" />
        </a-form-item>
        <!--
        <template v-show="!roleDisabled">
        <a-form-item label="角色分配" :labelCol="labelCol" :wrapperCol="wrapperCol" >
          <a-select
            mode="multiple"
            style="width: 100%"
            placeholder="请选择用户角色"
            v-model="selectedRole">
            <a-select-option v-for="(role,roleindex) in roleList" :key="roleindex.toString()" :value="role.id">
              {{ role.roleName }}
            </a-select-option>
          </a-select>
        </a-form-item>
        </template>
        <template v-show="!departDisabled">
        <a-form-item   label="部门分配" :labelCol="labelCol" :wrapperCol="wrapperCol" >
          <a-input-search
            placeholder="点击右侧按钮选择部门"
            v-model="checkedDepartNameString"
            disabled
            @search="onSearch">
            <a-button slot="enterButton" icon="search">选择</a-button>
          </a-input-search>
        </a-form-item>
        </template>
        <a-form-item label="头像" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-upload
            listType="picture-card"
            class="avatar-uploader"
            :showUploadList="false"
            :action="uploadAction"
            :data="{'isup':1}"
            :headers="headers"
            :beforeUpload="beforeUpload"
            @change="handleChange"
          >
            <img v-if="picUrl" :src="getAvatarView()" alt="头像" style="height:104px;max-width:300px"/>
            <div v-else>
              <a-icon :type="uploadLoading ? 'loading' : 'plus'" />
              <div class="ant-upload-text">上传</div>
            </div>
          </a-upload>
        </a-form-item>

        <a-form-item label="生日" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-date-picker
            style="width: 100%"
            placeholder="请选择生日"
            v-decorator="['birthday', {initialValue:!model.birthday?null:moment(model.birthday,dateFormat)}]"/>
        </a-form-item>-->

        <a-form-item label="性别" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-select v-decorator="[ 'sex', {}]" placeholder="请选择性别">
            <a-select-option :value="1">男</a-select-option>
            <a-select-option :value="2">女</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="邮箱" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input placeholder="请输入邮箱" v-decorator="[ 'email', validatorRules.email]" />
        </a-form-item>
        <a-form-item label="手机号码" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input placeholder="请输入手机号码" v-decorator="[ 'phone', validatorRules.phone]" />
        </a-form-item>

        <a-form-item label="省" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-select placeholder="请选择省份" v-decorator="[ 'provcode', validatorRules.provcode]" @change="provChange">
            <a-select-option v-for="item in provinceData" :value="item">{{item}}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="市" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-select placeholder="请选择市" v-decorator="[ 'citycode', validatorRules.citycode]">
            <a-select-option v-for="item in cityData" :value="item">{{item}}</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="职称" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input placeholder="请输入职称" v-decorator="[ 'proftitle', validatorRules.proftitle]" />
        </a-form-item>
        <a-form-item label="单位" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input placeholder="请输入单位" v-decorator="[ 'orgname', validatorRules.orgname]" />
        </a-form-item>
        <a-form-item label="单位类别" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-select placeholder="请选择单位类别" v-decorator="[ 'orgtype', validatorRules.orgtype]">
            <a-select-option value="医疗机构">医疗机构</a-select-option>
            <a-select-option value="疾控机构">疾控机构</a-select-option>
            <a-select-option value="科研机构">科研机构</a-select-option>
            <a-select-option value="高等院校">高等院校</a-select-option>
            <a-select-option value="其它">其它</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="工作类别" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-select placeholder="请选择工作类别" v-decorator="[ 'jobtype', validatorRules.jobtype]">
            <a-select-option value="临床">临床</a-select-option>
            <a-select-option value="医技">医技</a-select-option>
            <a-select-option value="科研">科研</a-select-option>
            <a-select-option value="防治">防治</a-select-option>
            <a-select-option value="药房">药房</a-select-option>
            <a-select-option value="行政">行政</a-select-option>
            <a-select-option value="学生">学生</a-select-option>
            <a-select-option value="其他">其他</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="最高学历" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-select placeholder="请选择学历" v-decorator="[ 'edu', validatorRules.edu]">
            <a-select-option value="专科">专科</a-select-option>
            <a-select-option value="本科">本科</a-select-option>
            <a-select-option value="硕士">硕士</a-select-option>
            <a-select-option value="博士">博士</a-select-option>
            <a-select-option value="其他">其他</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-spin>

    <div class="drawer-bootom-button" v-show="!disableSubmit">
      <a-popconfirm title="确定放弃编辑？" @confirm="handleCancel" okText="确定" cancelText="取消">
        <a-button style="margin-right: .8rem">取消</a-button>
      </a-popconfirm>
      <a-button @click="handleSubmit" type="primary" :loading="confirmLoading">提交</a-button>
    </div>
  </a-drawer>
</template>

<script>
  import pick from 'lodash.pick'
  import moment from 'moment'
  import Vue from 'vue'
  import { getAction, httpAction,putAction } from '@/api/manage'
  import {addUser,editUser,checkUsername } from '@/api/api'

  export default {
    name: "RoleModal",
    data () {
      var checkCard = (rule, value, callback) => {
          if (value !== "") {
              var card = value;
              if (isCardNo(card) === false) { //校验长度，类型
                  callback(new Error("您输入的身份证号码不正确，请重新输入"));
              } else if (checkProvince(card) === false) { //检查省份
                  callback(new Error("您输入的身份证号码不正确，请重新输入"));
              } else if (checkParity(card) === false) { //检验位的检测
                  callback(new Error("您的身份证校验位不正确,请重新输入"));
              } else {
                  checkBirthday(card);
                  callback();
              }
              // callback();
          } else {
              callback(new Error("选项不能为空"));
          }
      };
      var isCardNo = function(card) {
          //身份证号码为15位或者18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字或字符X
          var reg = /(^\d{15}$)|(^\d{17}(\d|X)$)/;
          if (reg.test(card) === false) {
              return false;
          }
          return true;
      }; 
      return {
        departDisabled: false, //是否是我的部门调用该页面
        deptId: '',
        roleDisabled: false, //是否是角色维护调用该页面
        roleId: '',
        modalWidth:800,
        drawerWidth:700,
        modaltoggleFlag:true,
        confirmDirty: false,
        selectedDepartKeys:[], //保存用户选择部门id
        checkedDepartKeys:[],
        checkedDepartNames:[], // 保存部门的名称 =>title
        checkedDepartNameString:"", // 保存部门的名称 =>title
        userId:"", //保存用户id
        disableSubmit:false,
        userDepartModel:{userId:'',departIdList:[]}, // 保存SysUserDepart的用户部门中间表数据需要的对象
        dateFormat:"YYYY-MM-DD",
        provinceData: ["北京", "上海", "重庆", "安徽", "福建", "甘肃", "广东", "广西", "贵州", "海南", "河北", "黑龙江", "河南", "香港", "湖北", "湖南", "江苏", "江西", "吉林", "辽宁", "澳门", "内蒙古", "宁夏", "青海", "山东","山西", "陕西", "四川", "台湾", "天津", "新疆", "西藏", "云南", "浙江", "海外" ],
        cityData:[],
        validatorRules:{
          username:{
            rules: [{
              required: true, message: '请输入用户账号!'
            },{
              validator: this.validateUsername,
            }]
          },
          password:{
            rules: [{
              required: true, message: '请输入密码!',
            }, {
              validator: this.validateToNextPassword,
            }],
          },
          confirmpassword:{
            rules: [{
              required: true, message: '请重新输入登陆密码!',
            }, {
              validator: this.compareToFirstPassword,
            }],
          },
          realname:{rules: [{ required: true, message: '请输入用户名称!' }]},
          idcard:{rules: [{validator: checkCard}]},
          phone:{rules: [{validator: this.validatePhone}]},
          email:{rules: [{required: true, type: 'email', message: '请输入正确格式的电子邮箱!',}]},
          roles:{},
          //  sex:{initialValue:((!this.model.sex)?"": (this.model.sex+""))}
          proftitle:{rules:[{required: true, message: '请输入职称!'}]},
          orgname:{rules:[{required: true, message: '请输入单位!'}]},
          orgtype:{rules:[{required: true, message: '请选择单位类别!'}]},
          jobtype:{rules:[{required: true, message: '请选择工作类别!'}]},
          edu:{rules:[{required: true, message: '请选择学历!'}]},
          provcode:{rules:[{required: true, message: '请输入正确的省份!'}]},
          citycode:{rules:[{required: true, message: '请输入正确的市!'}]},

        },
        title:"操作",
        visible: false,
        model: {},
        roleList:[],
        selectedRole:[],
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        uploadLoading:false,
        confirmLoading: false,
        form:this.$form.createForm(this),
        picUrl: "",
        url: {
          addUDepartIds:"/sys/user/addUDepartIds", // 引入为用户添加部门信息需要的url
          editUDepartIds:"/sys/user/editUDepartIds", // 引入为用户更新部门信息需要的url
          fileUpload: window._CONFIG['domianURL']+"/sys/common/upload",
          imgerver: window._CONFIG['domianURL']+"/sys/common/view",
          userWithDepart: "/sys/user/userDepartList", // 引入为指定用户查看部门信息需要的url
          userId:"/sys/user/generateUserId", // 引入生成添加用户情况下的url
          syncUserByUserName:"/process/extActProcess/doSyncUserByUserName",//同步用户到工作流
        },
      }
    },
    created () {
    },
    computed:{
      uploadAction:function () {
        return this.url.fileUpload;
      }
    },
    methods: {
      //窗口最大化切换
      toggleScreen(){
        if(this.modaltoggleFlag){
          this.modalWidth = window.innerWidth;
        }else{
          this.modalWidth = 800;
        }
        this.modaltoggleFlag = !this.modaltoggleFlag;
      },
      refresh () {
          this.selectedDepartKeys=[]
          this.checkedDepartKeys=[]
          this.checkedDepartNames=[]
         this.checkedDepartNameString = ""
          this.userId=""
      },
      add () {
        this.picUrl = "";
        this.refresh()
        this.edit({activitiSync:'1'});
      },
      edit (record) {
        this.resetScreenSize(); // 调用此方法,根据屏幕宽度自适应调整抽屉的宽度
        let that = this;
        that.checkedDepartNameString = "";
        that.form.resetFields();
        if(record.hasOwnProperty("id")){
          this.picUrl = "Has no pic url yet";
        }
        that.userId = record.id;
        that.visible = true;
        that.model = Object.assign({}, record);
        console.log(this.model)
        that.$nextTick(() => {
          that.form.setFieldsValue(pick(this.model,'username','sex','realname','email','phone','citycode','orgname','proftitle','provcode','idcard','jobtype','orgtype','edu'))
        });
        // 调用查询用户对应的部门信息的方法
        that.checkedDepartKeys = [];
        that.loadCheckedDeparts();
        that.provChange(that.model.provcode)
      },
      //
      loadCheckedDeparts(){
        let that = this;
        if(!that.userId){return}
        getAction(that.url.userWithDepart,{userId:that.userId}).then((res)=>{
          that.checkedDepartNames = [];
          if(res.success){
            for (let i = 0; i < res.result.length; i++) {
              that.checkedDepartNames.push(res.result[i].title);
              this.checkedDepartNameString = this.checkedDepartNames.join(",");
              that.checkedDepartKeys.push(res.result[i].key);
            }
          }else{
            console.log(res.message+"?");
          }
        })
      },
      close () {
        this.$emit('close');
        this.visible = false;
        this.disableSubmit = false;
        this.selectedRole = [];
        this.userDepartModel = {};
        this.checkedDepartNames = [];
      },
      moment,
      handleSubmit () {
        if(this.departDisabled===true){
          this.handleSubmitDepart();
          return
        }else if(this.roleDisabled===true){
          this.handleSubmitRole()
          return
        }
        const that = this;
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true;
            let avatar = that.model.avatar;
            if(!values.birthday){
              values.birthday = '';
            }else{
              values.birthday = values.birthday.format(this.dateFormat);
            }
            console.log(this.model)
            let formData = Object.assign(this.model, values);
            formData.avatar = avatar;
            formData.selectedroles = this.selectedRole.length>0?this.selectedRole.join(","):'';



            that.addDepartsToUser(that,formData); // 调用根据当前用户添加部门信息的方法
            let obj;
            if(!this.model.id){
              formData.id = this.userId;
              obj=addUser(formData);
              console.log("1")
            }else{
              obj=editUser(formData);
              console.log("2")
            }
            obj.then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                that.$emit('ok');
              }else{
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
              that.checkedDepartNames = [];
              that.close();
            })

          }
        })
      },
      // 提交表单（我的部门调用)
      handleSubmitDepart(){
        const that = this;
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true;
            let avatar = that.model.avatar;
            if (!values.birthday) {
              values.birthday = '';
            } else {
              values.birthday = values.birthday.format(this.dateFormat);
            }
            let formData = Object.assign(this.model, values);
            formData.avatar = avatar;
            formData.selectedroles = this.roleId;
            //生成userId
            getAction(this.url.userId).then((res) => {
              if (res.success) {
                this.userId = res.result;
                formData.id = that.userId;
                this.addDepartsToUser1(that, formData); // 调用根据当前用户添加部门信息的方法
                addUser(formData).then((res) => {
                  if (res.success) {
                    that.$message.success(res.message);
                    that.$emit('ok');
                  } else {
                    that.$message.warning(res.message);
                  }
                }).finally(() => {
                  that.confirmLoading = false;
                  that.checkedDepartNames = [];
                  that.close();
                })
              }
            })
          }
        })
      },
      //提交表单（角色维护调用）
      handleSubmitRole(){
        const that = this;
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true;
            let avatar = that.model.avatar;
            if(!values.birthday){
              values.birthday = '';
            }else{
              values.birthday = values.birthday.format(this.dateFormat);
            }
            let formData = Object.assign(this.model, values);
            formData.avatar = avatar;
            formData.selectedroles = this.selectedRole.length>0?this.selectedRole.join(","):'';
            if(this.roleId!=''){
              formData.selectedroles = this.roleId;
            }
            that.addDepartsToUser(that,formData); // 调用根据当前用户添加部门信息的方法
            let obj;
            if(!this.model.id){
              formData.id = this.userId;
              obj=addUser(formData);
            }else{
              obj=editUser(formData);
            }
            obj.then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                that.$emit('ok');
              }else{
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
              that.checkedDepartNames = [];
              that.close();
            })

          }
        })
      },
      handleCancel () {
        this.close()
      },
      // 根据当前用户添加部门信息的方法
      addDepartsToUser(that,formData){
        let httpurl = '';
        let method = '';
        if(this.userDepartModel.userId != formData.id){
          httpurl+=this.url.addUDepartIds;
          method = 'post';
        }else{
          httpurl+=this.url.editUDepartIds;
          method = 'put';
        }
        httpAction(httpurl,this.userDepartModel,method).then((res)=>{
          if(res.success){
            this.userDepartModel = {};
          }else{
            that.$message.warning(res.message);
          }
        })
      },
      // 根据当前用户添加部门信息的方法（我的部门调用）
      addDepartsToUser1: function() {
        let httpurl = ''
        let method = ''
        httpurl += this.url.addUDepartIds
        method = 'post';
        let departIdList = [];
        departIdList.push(this.deptId);
        httpAction(httpurl, { userId: this.userId, departIdList: departIdList }, method).then((res) => {
          if (res.success) {
            this.userId = ''
            this.deptId = ''
            this.departIdList = []
          } else {
            this.$message.warning(res.message)
          }
        })
      },
      validateToNextPassword  (rule, value, callback) {
        const form = this.form;
        const confirmpassword=form.getFieldValue('confirmpassword');
        if (value && confirmpassword && value !== confirmpassword) {
          callback('两次输入的密码不一样！');
        }
        if (value && this.confirmDirty) {
          form.validateFields(['confirm'], { force: true })
        }
        callback();
      },
      compareToFirstPassword  (rule, value, callback) {
        const form = this.form;
        if (value && value !== form.getFieldValue('password')) {
          callback('两次输入的密码不一样！');
        } else {
          callback()
        }
      },
      validatePhone(rule, value, callback){
        if(!value || new RegExp(/^1[3|4|5|7|8][0-9]\d{8}$/).test(value)){
          callback();
        }else{
          callback("请输入正确格式的手机号码!");
        }
      },
      validateUsername(rule, value, callback){
        var params = {
          id:this.model.id,
          username:value
        };
        checkUsername(params).then((res)=>{
          if(res.success){
            callback();
          }else{
            callback("用户账号已存在！");
          }
        });
      },
      handleConfirmBlur  (e) {
        const value = e.target.value
        this.confirmDirty = this.confirmDirty || !!value
      },

      normFile  (e) {
        if (Array.isArray(e)) {
          return e
        }
        return e && e.fileList
      },
      beforeUpload: function(file){
        var fileType = file.type;
        if(fileType.indexOf('image')<0){
          this.$message.warning('请上传图片');
          return false;
        }
        //TODO 验证文件大小
      },
      handleChange (info) {
        this.picUrl = "";
        if (info.file.status === 'uploading') {
          this.uploadLoading = true
          return
        }
        if (info.file.status === 'done') {
          var response = info.file.response;
          this.uploadLoading = false;
          if(response.success){
            this.model.avatar = response.message;
            this.picUrl = "Has no pic url yet";
          }else{
            this.$message.warning(response.message);
          }
        }
      },
      getAvatarView(){
        return this.url.imgerver +"/"+ this.model.avatar;
      },
      // 根据屏幕变化,设置抽屉尺寸
      resetScreenSize(){
        let screenWidth = document.body.clientWidth;
        if(screenWidth < 500){
          this.drawerWidth = screenWidth;
        }else{
          this.drawerWidth = 700;
        }
      },
      provChange (val) {
        switch (val) { 
        case "安徽" : 
            this.cityData = new Array( 
            "合肥", 
            "安庆", 
            "蚌埠", 
            "亳州", 
            "巢湖", 
            "滁州", 
            "阜阳", 
            "贵池", 
            "淮北", 
            "淮化", 
            "淮南", 
            "黄山", 
            "九华山", 
            "六安", 
            "马鞍山", 
            "宿州", 
            "铜陵", 
            "屯溪", 
            "芜湖", 
            "宣城"); 
             break; 
        case "北京" : 
            this.cityData = new Array( 
            "东城", 
            "西城", 
            "崇文", 
            "宣武", 
            "朝阳", 
            "丰台", 
            "石景山", 
            "海淀", 
            "门头沟", 
            "房山", 
            "通州", 
            "顺义", 
            "昌平", 
            "大兴", 
            "平谷", 
            "怀柔", 
            "密云", 
            "延庆"); 
            break; 
        case "重庆" : 
            this.cityData = new Array( 
            "万州", 
            "涪陵", 
            "渝中", 
            "大渡口", 
            "江北", 
            "沙坪坝", 
            "九龙坡", 
            "南岸", 
            "北碚", 
            "万盛", 
            "双挢", 
            "渝北", 
            "巴南", 
            "黔江", 
            "长寿", 
            "綦江", 
            "潼南", 
            "铜梁", 
            "大足", 
            "荣昌", 
            "壁山", 
            "梁平", 
            "城口", 
            "丰都", 
            "垫江", 
            "武隆", 
            "忠县", 
            "开县", 
            "云阳", 
            "奉节", 
            "巫山", 
            "巫溪", 
            "石柱", 
            "秀山", 
            "酉阳", 
            "彭水", 
            "江津", 
            "合川", 
            "永川", 
            "南川"); 
            break; 
        case "福建" : 
            this.cityData = new Array( 
            "福州", 
            "福安", 
            "龙岩", 
            "南平", 
            "宁德", 
            "莆田", 
            "泉州", 
            "三明", 
            "邵武", 
            "石狮", 
            "永安", 
            "武夷山", 
            "厦门", 
            "漳州"); 
             break; 
        case "甘肃" : 
            this.cityData = new Array( 
            "兰州", 
            "白银", 
            "定西", 
            "敦煌", 
            "甘南", 
            "金昌", 
            "酒泉", 
            "临夏", 
            "平凉", 
            "天水", 
            "武都", 
            "武威", 
            "西峰", 
            "张掖"); 
            break; 
        case "广东" : 
            this.cityData = new Array( 
            "广州", 
            "潮阳", 
            "潮州", 
            "澄海", 
            "东莞", 
            "佛山", 
            "河源", 
            "惠州", 
            "江门", 
            "揭阳", 
            "开平", 
            "茂名", 
            "梅州", 
            "清远", 
            "汕头", 
            "汕尾", 
            "韶关", 
            "深圳", 
            "顺德", 
            "阳江", 
            "英德", 
            "云浮", 
            "增城", 
            "湛江", 
            "肇庆", 
            "中山", 
            "珠海"); 
            break; 
        case "广西" : 
            this.cityData = new Array( 
            "南宁", 
            "百色", 
            "北海", 
            "桂林", 
            "防城港", 
            "河池", 
            "贺州", 
            "柳州", 
            "钦州", 
            "梧州", 
            "玉林"); 
            break; 
        case "贵州" : 
            this.cityData = new Array( 
            "贵阳", 
            "安顺", 
            "毕节", 
            "都匀", 
            "凯里", 
            "六盘水", 
            "铜仁", 
            "兴义", 
            "玉屏", 
            "遵义"); 
            break; 
        case "海南" : 
            this.cityData = new Array( 
            "海口", 
            "儋县", 
            "陵水", 
            "琼海", 
            "三亚", 
            "通什", 
            "万宁"); 
            break; 
        case "河北" : 
            this.cityData = new Array( 
            "石家庄", 
            "保定", 
            "北戴河", 
            "沧州", 
            "承德", 
            "丰润", 
            "邯郸", 
            "衡水", 
            "廊坊", 
            "南戴河", 
            "秦皇岛", 
            "唐山", 
            "新城", 
            "邢台", 
            "张家口"); 
            break; 
        case "黑龙江" : 
            this.cityData = new Array( 
            "哈尔滨", 
            "北安", 
            "大庆", 
            "大兴安岭", 
            "鹤岗", 
            "黑河", 
            "佳木斯", 
            "鸡西", 
            "牡丹江", 
            "齐齐哈尔", 
            "七台河", 
            "双鸭山", 
            "绥化", 
            "伊春"); 
            break; 
        case "河南" : 
            this.cityData = new Array( 
            "郑州", 
            "安阳", 
            "鹤壁", 
            "潢川", 
            "焦作", 
            "济源", 
            "开封", 
            "漯河", 
            "洛阳", 
            "南阳", 
            "平顶山", 
            "濮阳", 
            "三门峡", 
            "商丘", 
            "新乡", 
            "信阳", 
            "许昌", 
            "周口", 
            "驻马店"); 
            break; 
        case "香港" : 
            this.cityData = new Array( 
            "香港", 
            "九龙", 
            "新界"); 
            break; 
        case "湖北" : 
            this.cityData = new Array( 
            "武汉", 
            "恩施", 
            "鄂州", 
            "黄冈", 
            "黄石", 
            "荆门", 
            "荆州", 
            "潜江", 
            "十堰", 
            "随州", 
            "武穴", 
            "仙桃", 
            "咸宁", 
            "襄阳", 
            "襄樊", 
            "孝感", 
            "宜昌"); 
            break; 
        case "湖南" : 
            this.cityData = new Array( 
            "长沙", 
            "常德", 
            "郴州", 
            "衡阳", 
            "怀化", 
            "吉首", 
            "娄底", 
            "邵阳", 
            "湘潭", 
            "益阳", 
            "岳阳", 
            "永州", 
            "张家界", 
            "株洲"); 
            break; 
        case "江苏" : 
            this.cityData = new Array( 
            "南京", 
            "常熟", 
            "常州", 
            "海门", 
            "淮安", 
            "江都", 
            "江阴", 
            "昆山", 
            "连云港", 
            "南通", 
            "启东", 
            "沭阳", 
            "宿迁", 
            "苏州", 
            "太仓", 
            "泰州", 
            "同里", 
            "无锡", 
            "徐州", 
            "盐城", 
            "扬州", 
            "宜兴", 
            "仪征", 
            "张家港", 
            "镇江", 
            "周庄"); 
            break; 
        case "江西" : 
            this.cityData = new Array( 
            "南昌", 
            "抚州", 
            "赣州", 
            "吉安", 
            "景德镇", 
            "井冈山", 
            "九江", 
            "庐山", 
            "萍乡", 
            "上饶", 
            "新余", 
            "宜春", 
            "鹰潭"); 
            break; 
        case "吉林" : 
            this.cityData = new Array( 
            "长春", 
            "白城", 
            "白山", 
            "珲春", 
            "辽源", 
            "梅河", 
            "吉林", 
            "四平", 
            "松原", 
            "通化", 
            "延吉"); 
            break; 
        case "辽宁" : 
            this.cityData = new Array( 
            "沈阳", 
            "鞍山", 
            "本溪", 
            "朝阳", 
            "大连", 
            "丹东", 
            "抚顺", 
            "阜新", 
            "葫芦岛", 
            "锦州", 
            "辽阳", 
            "盘锦", 
            "铁岭", 
            "营口"); 
            break; 
        case "澳门" : 
            this.cityData = new Array( 
            "澳门"); 
            break; 
        case "内蒙古" : 
            this.cityData = new Array( 
            "呼和浩特", 
            "阿拉善盟", 
            "包头", 
            "赤峰", 
            "东胜", 
            "海拉尔", 
            "集宁", 
            "临河", 
            "通辽", 
            "乌海", 
            "乌兰浩特", 
            "锡林浩特"); 
            break; 
        case "宁夏" : 
            this.cityData = new Array( 
            "银川", 
            "固源", 
            "石嘴山", 
            "吴忠"); 
            break; 
        case "青海" : 
            this.cityData = new Array( 
            "西宁", 
            "德令哈", 
            "格尔木", 
            "共和", 
            "海东", 
            "海晏", 
            "玛沁", 
            "同仁", 
            "玉树"); 
            break; 
        case "山东" : 
            this.cityData = new Array( 
            "济南", 
            "滨州", 
            "兖州", 
            "德州", 
            "东营", 
            "菏泽", 
            "济宁", 
            "莱芜", 
            "聊城", 
            "临沂", 
            "蓬莱", 
            "青岛", 
            "曲阜", 
            "日照", 
            "泰安", 
            "潍坊", 
            "威海", 
            "烟台", 
            "枣庄", 
            "淄博"); 
            break; 
        case "上海" : 
            this.cityData = new Array( 
            "崇明", 
            "黄浦", 
            "卢湾", 
            "徐汇", 
            "长宁", 
            "静安", 
            "普陀", 
            "闸北", 
            "虹口", 
            "杨浦", 
            "闵行", 
            "宝山", 
            "嘉定", 
            "浦东", 
            "金山", 
            "松江", 
            "青浦", 
            "南汇", 
            "奉贤"); 
            break; 
        case "山西" : 
            this.cityData = new Array( 
            "太原", 
            "长治", 
            "大同", 
            "候马", 
            "晋城", 
            "离石", 
            "临汾", 
            "宁武", 
            "朔州", 
            "忻州", 
            "阳泉", 
            "榆次", 
            "运城"); 
            break; 
        case "陕西" : 
            this.cityData = new Array( 
            "西安", 
            "安康", 
            "宝鸡", 
            "汉中", 
            "渭南", 
            "商州", 
            "绥德", 
            "铜川", 
            "咸阳", 
            "延安", 
            "榆林"); 
            break; 
        case "四川" : 
            this.cityData = new Array( 
            "成都", 
            "巴中", 
            "达川", 
            "德阳", 
            "都江堰", 
            "峨眉山", 
            "涪陵", 
            "广安", 
            "广元", 
            "九寨沟", 
            "康定", 
            "乐山", 
            "泸州", 
            "马尔康", 
            "绵阳", 
            "眉山", 
            "南充", 
            "内江", 
            "攀枝花", 
            "遂宁", 
            "汶川", 
            "西昌", 
            "雅安", 
            "宜宾", 
            "自贡", 
            "资阳"); 
            break; 
        case "台湾" : 
            this.cityData = new Array( 
            "台北", 
            "基隆", 
            "台南", 
            "台中", 
            "高雄", 
            "屏东", 
            "南投", 
            "云林", 
            "新竹", 
            "彰化", 
            "苗栗", 
            "嘉义", 
            "花莲", 
            "桃园", 
            "宜兰", 
            "台东", 
            "金门", 
            "马祖", 
            "澎湖"); 
            break; 
        case "天津" : 
            this.cityData = new Array( 
            "天津", 
            "和平", 
            "东丽", 
            "河东", 
            "西青", 
            "河西", 
            "津南", 
            "南开", 
            "北辰", 
            "河北", 
            "武清", 
            "红挢", 
            "塘沽", 
            "汉沽", 
            "大港", 
            "宁河", 
            "静海", 
            "宝坻", 
            "蓟县"); 
            break; 
        case "新疆" : 
            this.cityData = new Array( 
            "乌鲁木齐", 
            "阿克苏", 
            "阿勒泰", 
            "阿图什", 
            "博乐", 
            "昌吉", 
            "东山", 
            "哈密", 
            "和田", 
            "喀什", 
            "克拉玛依", 
            "库车", 
            "库尔勒", 
            "奎屯", 
            "石河子", 
            "塔城", 
            "吐鲁番", 
            "伊宁"); 
            break; 
        case "西藏" : 
            this.cityData = new Array( 
            "拉萨", 
            "阿里", 
            "昌都", 
            "林芝", 
            "那曲", 
            "日喀则", 
            "山南"); 
            break; 
        case "云南" : 
            this.cityData = new Array( 
            "昆明", 
            "大理", 
            "保山", 
            "楚雄", 
            "大理", 
            "东川", 
            "个旧", 
            "景洪", 
            "开远", 
            "临沧", 
            "丽江", 
            "六库", 
            "潞西", 
            "曲靖", 
            "思茅", 
            "文山", 
            "西双版纳", 
            "玉溪", 
            "中甸", 
            "昭通"); 
            break; 
        case "浙江" : 
            this.cityData = new Array( 
            "杭州", 
            "安吉", 
            "慈溪", 
            "定海", 
            "奉化", 
            "海盐", 
            "黄岩", 
            "湖州", 
            "嘉兴", 
            "金华", 
            "临安", 
            "临海", 
            "丽水", 
            "宁波", 
            "瓯海", 
            "平湖", 
            "千岛湖", 
            "衢州", 
            "江山", 
            "瑞安", 
            "绍兴", 
            "嵊州", 
            "台州", 
            "温岭", 
            "温州", 
            "余姚", 
            "舟山"); 
            break; 
        case "海外" : 
            this.cityData = new Array( 
            "欧洲", 
            "北美", 
            "南美", 
            "亚洲", 
            "非洲", 
            "大洋洲"); 
            break; 
        default: 
            this.cityData = new Array(""); 
            break; 
        }
      }
    }
  }
</script>

<style scoped>
  .avatar-uploader > .ant-upload {
    width:104px;
    height:104px;
  }
  .ant-upload-select-picture-card i {
    font-size: 49px;
    color: #999;
  }

  .ant-upload-select-picture-card .ant-upload-text {
    margin-top: 8px;
    color: #666;
  }

  .ant-table-tbody .ant-table-row td{
    padding-top:10px;
    padding-bottom:10px;
  }

  .drawer-bootom-button {
    position: absolute;
    bottom: -8px;
    width: 100%;
    border-top: 1px solid #e8e8e8;
    padding: 10px 16px;
    text-align: right;
    left: 0;
    background: #fff;
    border-radius: 0 0 2px 2px;
  }
</style>