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
          label="考试场次id">
          <a-input placeholder="请输入考试场次id" v-decorator="['examId', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="试题序号">
          <a-input v-decorator="[ 'no', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="问题类型">
          <a-input placeholder="请输入问题类型" v-decorator="['type', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="问题标题">
          <a-input placeholder="请输入问题标题" v-decorator="['title', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="问题描述">
          <a-input placeholder="请输入问题描述" v-decorator="['content', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="选项A">
          <a-input placeholder="请输入选项A" v-decorator="['optionA', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="选项B">
          <a-input placeholder="请输入选项B" v-decorator="['optionB', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="选项C">
          <a-input placeholder="请输入选项C" v-decorator="['optionC', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="选项D">
          <a-input placeholder="请输入选项D" v-decorator="['optionD', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="选项E">
          <a-input placeholder="请输入选项E" v-decorator="['optionE', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="选项F">
          <a-input placeholder="请输入选项F" v-decorator="['optionF', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="选项G">
          <a-input placeholder="请输入选项G" v-decorator="['optionG', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="图片A标题">
          <a-input placeholder="请输入图片A标题" v-decorator="['imgTitleA', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="图片AURL">
          <a-input placeholder="请输入图片AURL" v-decorator="['imgUrlA', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="图片B标题">
          <a-input placeholder="请输入图片B标题" v-decorator="['imgTitleB', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="图片BURL">
          <a-input placeholder="请输入图片BURL" v-decorator="['imgUrlB', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="图片C标题">
          <a-input placeholder="请输入图片C标题" v-decorator="['imgTitleC', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="图片CURL">
          <a-input placeholder="请输入图片CURL" v-decorator="['imgUrlC', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="图片D标题">
          <a-input placeholder="请输入图片D标题" v-decorator="['imgTitleD', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="图片DURL">
          <a-input placeholder="请输入图片DURL" v-decorator="['imgUrlD', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="图片E标题">
          <a-input placeholder="请输入图片E标题" v-decorator="['imgTitleE', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="图片EURL">
          <a-input placeholder="请输入图片EURL" v-decorator="['imgUrlE', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="试题答案">
          <a-input placeholder="请输入试题答案" v-decorator="['answer', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="试题分数">
          <a-input-number v-decorator="[ 'score', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="排序序号">
          <a-input-number v-decorator="[ 'sortNo', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="状态(1：正常  2：冻结 ）">
<!--          <a-input-number v-decorator="[ 'status', {}]" />-->
          <a-select v-model="selected" v-decorator="[ 'status', {}]" placeholder="请输入状态（1：正常，2：冻结）">
            <a-select-option value="1">正常</a-select-option>
            <a-select-option value="2">冻结</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="删除状态（0，正常，1已删除）">
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
    name: "QuestionModal",
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
          add: "/question/question/add",
          edit: "/question/question/edit",
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
          this.form.setFieldsValue(pick(this.model,'examId','no','type','title','content','optionA','optionB','optionC','optionD','optionE','optionF','optionG','imgTitleA','imgUrlA','imgTitleB','imgUrlB','imgTitleC','imgUrlC','imgTitleD','imgUrlD','imgTitleE','imgUrlE','answer','score','sortNo','status','delflag'))
		  //时间格式化
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