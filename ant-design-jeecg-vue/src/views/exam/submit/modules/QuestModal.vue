<template>
  <a-modal
    :title="title"
    :width="500"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">

    <a-spin :spinning="confirmLoading">
      <a-form :form="form">

        <a-form-item>
          <a-input placeholder="请输入问答题分数" v-decorator="['score2', {}]" />
        </a-form-item>

      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import {httpAction} from '@/api/manage'
  import pick from 'lodash.pick'

  export default {
    name: "QuestModal",
    components:{
    },
    data () {
      return {
        title:"",
        visible: false,
        userId:'',
        examId:'',
        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules:{
        },
        url:{
          postscore2:"/exam/score/postscore2"
        },
      }
    },
    created () {
    },
    methods: {
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({},record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'score2'))
        });
      },

      close () {
        this.$emit('close');
        this.visible = false;
      },
      handleOk () {
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            console.log(values);
            this.confirmLoading = false;
            let formData = Object.assign(this.model, values);
            formData.userId = this.userId
            formData.examId= this.examId
            let httpurl=this.url.postscore2
            let method='post'
            httpAction(httpurl,formData,method).then((res)=>{
              if(res.success){
                this.$message.success(res.message);
                this.$emit('ok')
              }else{
                this.$message.warning(res.message);
              }
            })
            this.close();

            // let httpurl = '';
            // let method = '';
            // if(!this.model.id){
            //   httpurl+=this.url.add;
            //   method = 'post';
            // }else{
            //   httpurl+=this.url.edit;
            //    method = 'put';
            // }
            // let formData = Object.assign(this.model, values);
            // //时间格式化
            //
            // console.log(formData)
            // httpAction(httpurl,formData,method).then((res)=>{
            //   if(res.success){
            //     that.$message.success(res.message);
            //     that.$emit('ok');
            //   }else{
            //     that.$message.warning(res.message);
            //   }
            // }).finally(() => {
            //   that.confirmLoading = false;
            //   that.close();
            // })
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
