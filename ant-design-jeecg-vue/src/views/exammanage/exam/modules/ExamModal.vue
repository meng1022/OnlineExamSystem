<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    okText="确定"
    @cancel="handleCancel"
    cancelText="关闭">
    
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="考试名称">
          <a-input placeholder="请输入考试名称" v-decorator="['examName',validatorRules.examInfo, {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="场次">
          <a-input placeholder="请输入场次" v-decorator="['sessionCode',validatorRules.examInfo, {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="科目">
          <a-input placeholder="请输入科目名称" v-decorator="['sessionName',validatorRules.examInfo, {}]" />
        </a-form-item>
<!--        <a-form-item-->
<!--          :labelCol="labelCol"-->
<!--          :wrapperCol="wrapperCol"-->
<!--          label="可见开始时间">-->
<!--          <a-date-picker showTime format='YYYY-MM-DD HH:mm:ss' v-decorator="[ 'visiableStartTime',validatorRules.TimeInfo, {}]" />-->
<!--        </a-form-item>-->
<!--        <a-form-item-->
<!--          :labelCol="labelCol"-->
<!--          :wrapperCol="wrapperCol"-->
<!--          label="可见结束时间">-->
<!--          <a-date-picker showTime format='YYYY-MM-DD HH:mm:ss' v-decorator="[ 'visiableEndTime',validatorRules.TimeInfo, {}]" />-->
<!--        </a-form-item>-->
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="考试开始时间">
          <a-date-picker showTime format='YYYY-MM-DD HH:mm:ss' v-decorator="[ 'examStartTime',validatorRules.TimeInfo, {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="考试结束时间">
          <a-date-picker showTime format='YYYY-MM-DD HH:mm:ss' v-decorator="[ 'examEndTime',validatorRules.TimeInfo, {}]" />
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
          label="考试时长">
          <a-input-number v-decorator="[ 'examDuration',validatorRules.examDuration, {}]" />
        </a-form-item>
<!--        <a-form-item-->
<!--          :labelCol="labelCol"-->
<!--          :wrapperCol="wrapperCol"-->
<!--          label="序号">-->
<!--          <a-input-number v-decorator="[ 'sortNo', {}]" />-->
<!--        </a-form-item>-->
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="提交次数（0为不限制）">
          <a-input-number v-decorator="[ 'submitTimes', {initialValue:0}]" />
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="是否启用服务器结果缓存">
          <a-select v-decorator="[ 'submitServerCache', {}]" placeholder="请输入是否启用服务器结果缓存（0：不启用，1：启用）">
            <a-select-option value="0">不启用</a-select-option>
            <a-select-option value="1">启用</a-select-option>
          </a-select>
        </a-form-item>

<!--        <a-form-item-->
<!--          :labelCol="labelCol"-->
<!--          :wrapperCol="wrapperCol"-->
<!--          label="状态">-->
<!--&lt;!&ndash;          <a-input-number v-decorator="[ 'status', {}]" />&ndash;&gt;-->
<!--          <a-select v-decorator="[ 'status', {}]" placeholder="请输入状态（1：正常，2：冻结）">-->
<!--            <a-select-option value="1">正常</a-select-option>-->
<!--            <a-select-option value="2">冻结</a-select-option>-->
<!--          </a-select>-->
<!--        </a-form-item>-->
<!--        <a-form-item-->
<!--          :labelCol="labelCol"-->
<!--          :wrapperCol="wrapperCol"-->
<!--          label="删除状态">-->
<!--&lt;!&ndash;          <a-input placeholder="请输入删除状态（0，正常，1已删除）" v-decorator="['delflag', {}]" />&ndash;&gt;-->
<!--          <a-select v-decorator="['delflag', {}]" placeholder="请输入删除状态（0，正常，1已删除）">-->
<!--            <a-select-option value="0">正常</a-select-option>-->
<!--            <a-select-option value="1">已删除</a-select-option>-->
<!--          </a-select>-->
<!--        </a-form-item>-->
		
      </a-form>
    </a-spin>
  </a-modal>
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
          examInfo:{rules:[{required:true,message:'必填字段',validator:'click'}]},
          IimeInfo:{rules:[{required:true,message:'必填字段',validator:'click'}]},
          examDuration:{rules:[{required:true,type:'number',message:'考试时长为必填字段',validator:'click'}]},
        },
        url: {
          add: "/exam/exam/add",
          edit: "/exam/exam/edit",
        },
      }
    },
    created () {
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
          this.form.setFieldsValue(pick(this.model,'examName','sessionCode','sessionName','verificationCode','examDuration','sortNo','submitTimes','submitServerCache','status','delflag'))
		  //时间格式化
          this.form.setFieldsValue({submitServerCache:this.model.submitServerCache === 0?"0":"1"})
          this.form.setFieldsValue({delflag:this.model.delflag === 1?"1":"0"})
          this.form.setFieldsValue({status:this.model.status === 2?"2":"1"})
          // this.form.setFieldsValue({visiableStartTime:this.model.visiableStartTime?moment(this.model.visiableStartTime):null})
          // this.form.setFieldsValue({visiableEndTime:this.model.visiableEndTime?moment(this.model.visiableEndTime):null})
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
            // formData.visiableStartTime = formData.visiableStartTime?formData.visiableStartTime.format('YYYY-MM-DD HH:mm:ss'):null;
            // formData.visiableEndTime = formData.visiableEndTime?formData.visiableEndTime.format('YYYY-MM-DD HH:mm:ss'):null;
            formData.examStartTime = formData.examStartTime?formData.examStartTime.format('YYYY-MM-DD HH:mm:ss'):null;
            formData.examEndTime = formData.examEndTime?formData.examEndTime.format('YYYY-MM-DD HH:mm:ss'):null;
            
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

</style>