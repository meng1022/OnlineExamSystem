<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">
    
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="考试id">
          <a-input disabled placeholder="" v-decorator="['examId', {}]"/>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="组题试题库">
          <a-input disabled placeholder="" v-decorator="['sessionName', {}]"/>
        </a-form-item>

    <a-row>
      <a-col :span="6">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="题型">
              <a-select placeholder="请选择问题类型" v-decorator="['type',validatorRules.type, {}]" >
                <a-select-option value="single">单选题</a-select-option>
                <a-select-option value="multi">多选题</a-select-option>
                <a-select-option value="judge">判断题</a-select-option>
                <a-select-option value="quest">问答题</a-select-option>
              </a-select>
            </a-form-item>
      </a-col>
      <a-col :span="6">
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="数量">
          <a-input-number :min=0 v-decorator="[ 'number', validatorRules.number,{}]"  />
        </a-form-item>
      </a-col>
      <a-col :span="6">
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="难度">
          <a-select v-decorator="[ 'hard', {}]" >
            <a-select-option value="-1">随机</a-select-option>
            <a-select-option value="0">简单</a-select-option>
            <a-select-option value="1">中等</a-select-option>
            <a-select-option value="2">困难</a-select-option>
          </a-select>
        </a-form-item>
      </a-col>
      <a-col :span="6">
      <a-form-item
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        label="分值">
        <a-input-number :min=0 v-decorator="[ 'score', {initialValue:0}]" />
      </a-form-item>
      </a-col>
    </a-row>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { httpAction } from '@/api/manage'
  import {smartGenquestion} from  '@/api/api'
  import pick from 'lodash.pick'
  import ARow from 'ant-design-vue/es/grid/Row'

  export default {
    name: "StrategyModal",
    components: { ARow },
    data () {
      return {
        title:"操作",
        visible: false,
        index1:0,
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 8 },//左侧留白大小
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 10 },//文本框宽度
        },
        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules:{
          type:{rules:[{required:true,message:'试题类型为必选字段',validator:'click'}]},
          number:{rules:[{required:true,type:'number',message:'试题数量为必填字段',validator:'click'}]},
        },
      }
    },
    watch:{

    },
    created () {

    },
    methods: {
      add () {
        console.log(this.$route.params)
        this.edit({examId: this.$route.params.examid,sessionName:this.$route.params.sessionName});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'examId','sessionName','type','number','hard','score'))
        });},
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
            let formData = Object.assign(this.model, values);
            console.log("表单数据：")
            console.log(formData)
          //{sessionName:formData.sessionName,type:formData.type,number:formData.number,hard:formData.hard,score:formData.score}
            smartGenquestion(formData).then((res)=>{
              if(res.success){
                console.log("随机选题结果res.result::::")
                console.log(res.result)
                that.$emit('smartaddQuestions',res.result,this.index1);
                that.$message.success(res.message);
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