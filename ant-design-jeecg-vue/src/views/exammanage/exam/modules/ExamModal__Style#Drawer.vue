<template>
  <a-drawer
      :title="title"
      :width="800"
      placement="right"
      :closable="false"
      @close="close"
      :visible="visible"
  >

    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
      
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="场次">
          <a-input placeholder="请输入场次" v-decorator="['sessionInfo', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="考试名称">
          <a-input placeholder="请输入考试名称" v-decorator="['examName', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="可见开始时间">
          <a-date-picker showTime format='YYYY-MM-DD HH:mm:ss' v-decorator="[ 'visiableStartTime', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="可见结束时间">
          <a-date-picker showTime format='YYYY-MM-DD HH:mm:ss' v-decorator="[ 'visiableEndTime', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="考试开始时间">
          <a-date-picker showTime format='YYYY-MM-DD HH:mm:ss' v-decorator="[ 'examStartTime', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="考试结束时间">
          <a-date-picker showTime format='YYYY-MM-DD HH:mm:ss' v-decorator="[ 'examEndTime', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="验证码">
          <a-input placeholder="请输入验证码" v-decorator="['verificationCode', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="状态">
<!--          <a-input-number v-decorator="[ 'status', {}]" />-->
          <a-select v-model="selected" v-decorator="[ 'status', {}]" placeholder="请输入状态（1：正常，2：冻结）">
            <a-select-option value="1">正常</a-select-option>
            <a-select-option value="2">冻结</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="删除状态">
<!--          <a-input placeholder="请输入删除状态（0，正常，1已删除）" v-decorator="['delflag', {}]" />-->
          <a-select v-model="selected" v-decorator="['delflag', {}]" placeholder="请输入删除状态（0，正常，1已删除）">
            <a-select-option value="0">正常</a-select-option>
            <a-select-option value="1">已删除</a-select-option>
          </a-select>
        </a-form-item>
		
      </a-form>
    </a-spin>
    <a-button type="primary" @click="handleOk">确定</a-button>
    <a-button type="primary" @click="handleCancel">取消</a-button>
  </a-drawer>
</template>

<script>
  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import moment from "moment"

  export default {
    name: "ExamModal",
    data () {
      return {
        title:"操作",
        visible: false,
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },

        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules:{
        },
        url: {
          add: "/exam/exam/add",
          edit: "/exam/exam/edit",
        },
      }
    },
    created () {
      console.log(this.form)
    },
    methods: {
      add () {
        this.edit({});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'sessionInfo','examName','verificationCode','status','delflag'))
		  //时间格式化
          this.form.setFieldsValue({visiableStartTime:this.model.visiableStartTime?moment(this.model.visiableStartTime):null})
          this.form.setFieldsValue({visiableEndTime:this.model.visiableEndTime?moment(this.model.visiableEndTime):null})
          this.form.setFieldsValue({examStartTime:this.model.examStartTime?moment(this.model.examStartTime):null})
          this.form.setFieldsValue({examEndTime:this.model.examEndTime?moment(this.model.examEndTime):null})
        });

      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      handleOk () {
        const that = this;
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true;
            let httpurl = '';
            let method = '';
            if(!this.model.id){
              httpurl+=this.url.add;
              method = 'post';
            }else{
              httpurl+=this.url.edit;
               method = 'put';
            }
            let formData = Object.assign(this.model, values);
            //时间格式化
            formData.visiableStartTime = formData.visiableStartTime?formData.visiableStartTime.format('YYYY-MM-DD HH:mm:ss'):null;
            formData.visiableEndTime = formData.visiableEndTime?formData.visiableEndTime.format('YYYY-MM-DD HH:mm:ss'):null;
            formData.examStartTime = formData.examStartTime?formData.examStartTime.format('YYYY-MM-DD HH:mm:ss'):null;
            formData.examEndTime = formData.examEndTime?formData.examEndTime.format('YYYY-MM-DD HH:mm:ss'):null;
            
            console.log(formData)
            httpAction(httpurl,formData,method).then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                that.$emit('ok');
              }else{
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
              that.close();
            })



          }
        })
      },
      handleCancel () {
        this.close()
      },


    }
  }
</script>

<style lang="less" scoped>
/** Button按钮间距 */
  .ant-btn {
    margin-left: 30px;
    margin-bottom: 30px;
    float: right;
  }
</style>