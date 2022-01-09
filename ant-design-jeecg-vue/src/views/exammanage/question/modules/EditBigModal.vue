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
          <a-input placeholder="" v-decorator="['bigDescribe', {}]" />
        </a-form-item>

      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import moment from "moment"

  export default {
    name: "EditBigModal",
    components:{
    },
    data () {
      return {
        title:"操作",
        visible: false,
        model: {},
        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules:{
        },
        index:0
        // url: {
        //   add: "/exam/paper/add",
        //   edit: "/exam/paper/edit",
        // },
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
          this.form.setFieldsValue(pick(this.model,'bigDescribe'))
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
            console.log(values);
            that.confirmLoading = false;
            console.log(that.confirmLoading)
            let formData = Object.assign(this.model, values);
             formData.index=this.index;
             console.log(formData)
            this.$emit('setBigDescribe', formData);
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
