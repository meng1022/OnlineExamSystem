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
      
<!--        <a-form-item-->
<!--          :labelCol="labelCol"-->
<!--          :wrapperCol="wrapperCol"-->
<!--          label="考试id">-->
<!--          <a-input-->
<!--          disabled placeholder="请输入考试场次id" v-decorator="['examId', {}]" />-->
<!--        </a-form-item>-->
<!--        <a-form-item-->
<!--          :labelCol="labelCol"-->
<!--          :wrapperCol="wrapperCol"-->
<!--          label="试题序号">-->
<!--          <a-input-number v-decorator="[ 'no',validatorRules.no, {}]" />-->
<!--        </a-form-item>-->
<!--        <a-form-item-->
<!--        :labelCol="labelCol"-->
<!--        :wrapperCol="wrapperCol"-->
<!--        label="题目显示顺序号">-->
<!--        <a-input-number v-decorator="[ 'sortNo', {}]" />-->
<!--      </a-form-item>-->
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="问题类型">
          <a-select placeholder="请选择问题类型" v-decorator="['type',validatorRules.type, {}]" @change="parentContentHidden">
            <a-select-option value="single">单选题</a-select-option>
            <a-select-option value="multi">多选题</a-select-option>
            <a-select-option value="quest">问答题</a-select-option>
            <a-select-option value="judge">判断题</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="题目描述">
          <a-input placeholder="请输入题目描述" v-decorator="['content', {}]" />
        </a-form-item>

    <div v-show="!optionhidden">
      <a-row>
        <a-col :md="23" :sm="10">
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="选项A">
            <a-input placeholder="请输入选项A，点击右侧图标上传图片" v-decorator="['optionA', {}]" />
          </a-form-item>
        </a-col>
        <a-col :md="1" :sm="10">
          <a-icon style="position: absolute;right: 100px;top: 14px;"  @click="contentModal('imgUrlA')" type="book" />
        </a-col>
      </a-row>

      <a-row>
        <a-col :md="23" :sm="10">
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="选项B">
            <a-input placeholder="请输入选项B，点击右侧图标上传图片" v-decorator="['optionB', {}]" />
          </a-form-item>
        </a-col>
        <a-col :md="1" :sm="10">
          <a-icon style="position: absolute;right: 100px;top: 14px;"  @click="contentModal('imgUrlB')" type="book" />
        </a-col>
      </a-row>
      <a-row>
        <a-col :md="23" :sm="10">
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="选项C">
            <a-input placeholder="请输入选项C，点击右侧图标上传图片" v-decorator="['optionC', {}]" />
          </a-form-item>
        </a-col>
        <a-col :md="1" :sm="10">
          <a-icon style="position: absolute;right: 100px;top: 14px;"  @click="contentModal('imgUrlC')" type="book" />
        </a-col>
      </a-row>
      <a-row>
        <a-col :md="23" :sm="10">
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="选项D">
            <a-input placeholder="请输入选项D，点击右侧图标上传图片" v-decorator="['optionD', {}]" />
          </a-form-item>
        </a-col>
        <a-col :md="1" :sm="10">
          <a-icon style="position: absolute;right: 100px;top: 14px;"  @click="contentModal('imgUrlD')" type="book" />
        </a-col>
      </a-row>
      <a-row>
        <a-col :md="23" :sm="10">
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="选项E">
            <a-input placeholder="请输入选项E，点击右侧图标上传图片" v-decorator="['optionE', {}]" />
          </a-form-item>
        </a-col>
        <a-col :md="1" :sm="10">
          <a-icon style="position: absolute;right: 100px;top: 14px;"  @click="contentModal('imgUrlE')" type="book" />
        </a-col>
      </a-row>

      <div v-show="false">
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="选项A图片">
          <a-input placeholder="" v-decorator="['imgUrlA', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="选项B图片">
          <a-input placeholder="" v-decorator="['imgUrlB', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="选项C图片">
          <a-input placeholder="" v-decorator="['imgUrlC', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="选项D图片">
          <a-input placeholder="" v-decorator="['imgUrlD', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="选项E图片">
          <a-input placeholder="" v-decorator="['imgUrlE', {}]" />
        </a-form-item>
      </div>
    </div>
        <a-row v-if="!isjudge">
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="试题答案">
            <a-input placeholder="请输入试题答案" v-decorator="['answer',validatorRules.answer, {}]" />
          </a-form-item>
        </a-row>
        <a-row v-if="isjudge">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="答案">
              <a-select v-decorator="['answer',validatorRules.answer, {}]" placeholder="请选择答案" @change="answerChange">
                <a-select-option value="A">正确</a-select-option>
                <a-select-option value="B">错误</a-select-option>
              </a-select>
            </a-form-item>
        </a-row>


        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="试题分数">
          <a-input-number v-decorator="[ 'score',validatorRules.score, {}]" />
        </a-form-item>

      </a-form>

      <content-modal ref="contentForm" @parentFn="parentFn" @ok="modalFormOk"></content-modal>
    </a-spin>
  </a-modal>
</template>

<script>
  import pick from 'lodash.pick'
  import ARow from 'ant-design-vue/es/grid/Row'
  import ContentModal from '@/views/questionbase/modules/ContentModal'

  export default {
    name: "QuestionModal",
    components: {
      ARow,
      ContentModal,
    },
    data () {
      return {
        title:"操作",
        bigDescribe:'',
        visible: false,
        answerchange:"",
        isjudge:false,//是否是判断题
        optionhidden:false,//是否需要隐藏选项
        answer:'',
        model: {},
        index:-1,//,
        index1:-1,//操作为add时，index1为-1
        no:0,
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
          // no:{rules:[{required:true,type:'number',message:'试题序号为必填字段',validator:'click'}]},
          type:{rules:[{required:true,message:'试题类型为必选字段',validator:'click'}]},
          answer:{rules:[{required:true,message:'答案为必填字段',validator:'click'}]},
          score:{rules:[{required:true,type:'number',message:'分数为必填字段',validator:'click'}]},
        },
        url: {
          add: "/exam/question/add",
          edit: "/exam/question/edit",
        },
      }
    },
    created () {

    },
    methods: {
      parentContentHidden(val){
        if(val==="quest"){
          this.optionhidden=true;
        }else{
          this.optionhidden=false;
        }
        if(val==="judge"){
          this.isjudge=true;
          this.optionhidden=true;
        }else{
          this.isjudge=false;
        }
      },
      answerChange(val){
        if(val=="A"){
          this.answerchange ="A"
        }
        else if(val=="B"){
          this.answerchange ="B"
        }
      },
      add () {
        console.log(this.$route.params)
        console.log("this.bigdescribe:"+this.bigDescribe)
        console.log("this.no:"+this.no)
        this.edit({bigDescribe: this.bigDescribe,examId:this.$route.params.examid,no:this.no});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        console.log("model:")
        console.log(this.model)
        this.parentContentHidden(this.model.type);
        this.answerChange(this.model.answer);
        this.model.sortNo="";
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'id','examId','no','type','title','content','optionA','optionB','optionC','optionD','optionE','imgUrlA','imgUrlB','imgUrlC','imgUrlD','imgUrlE','answer','score','sortNo'))
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

            if(this.isjudge){
              formData.answer = this.answerchange
              formData.optionA="正确"
              formData.optionB="错误"
            }
            //console.log(formData)
            var even={index:this.index,formData:formData}//index?
            if(that.index1!=-1){
              //操作为编辑
              even.index1 = that.index1;
              that.$emit("editPapersinQuestionOne",even)
            }else{
              that.$emit('addPapersinQuestionOne', even);
            }
            that.index1=-1
            that.index=-1
            that.confirmLoading = false;
            that.close();

            // httpAction(httpurl,formData,method).then((res)=>{
            //   if(res.success){
            //     var even={index:this.index,formData:formData}//index?
            //     if(that.index1!=-1){
            //       //操作为编辑
            //       even.index1 = that.index1;
            //       that.$emit("editPapersinQuestionOne",even)
            //     }else{
            //       that.$emit('addPapersinQuestionOne', even);
            //     }
            //     that.index1=-1
            //     that.index=-1
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
      parentFn(imgURL) {
        console.log("QuestionModal.parentFn-->"+this.editorName+"："+imgURL)
        this.form.setFieldsValue({
          [this.editorName]: imgURL,
        });

      },
      contentModal: function (e) {
        this.$refs.contentForm.add();
        // 获取某个元素的值
        var content=this.form.getFieldValue(e)
        console.log("option content："+content)
        //把当前要调用文本框的name给他赋值
        this.editorName=e;
        this.$refs.contentForm.content= content
        this.$refs.contentForm.title = "选项图片";
      },

    }
  }
</script>

<style lang="less" scoped>

</style>