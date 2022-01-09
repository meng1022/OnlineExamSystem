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
          label="科目名称">
          <a-input placeholder="请输入试题库名称" v-decorator="['sessionName', {}]"/>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import ARow from 'ant-design-vue/es/grid/Row'

  export default {
    name: "QuestionbaseModal",
    components: { ARow },
    data () {
      return {
        title:"操作",
        visible: false,
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
        },
        totalscore:"",
        url: {
          add: "/exam/session/add",
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
        this.edit({sessionName:this.$route.params.sessionName});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'sessionName'))
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
            let httpurl = this.url.add;
            let method = 'post';
            let formData = Object.assign(this.model, values);
            console.log("表单数据")
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

</style>