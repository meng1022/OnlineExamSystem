<template>
  <a-modal
    :title="title"
    :width="600"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">

    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-row>
          <a-col :md="24" :sm="10">
            <a-form-item
                    :labelCol="labelCol"
                    :wrapperCol="wrapperCol"
                    label="模板下载">
              <a-button type="primary" icon="download" @click="handleExportTemplate('试卷导入试题模板')">下载Excel模板</a-button>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :md="24" :sm="10">
            <a-form-item
                    :labelCol="labelCol"
                    :wrapperCol="wrapperCol"
                    label="导入试题">
              <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :customRequest="customRequest">
                <a-button type="primary" icon="import" >上传Excel文件</a-button>
              </a-upload>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { httpAction,downFile } from '@/api/manage'

  import pick from 'lodash.pick'

  // import VueQuillEditor, { Quill } from 'vue-quill-editor'
  // import { ImageDrop } from 'quill-image-drop-module'
  // import ImageResize from 'quill-image-resize-module'
  // Quill.register('modules/imageDrop', ImageDrop)
  // Quill.register('modules/imageResize', ImageResize)
  // import Quill from 'quill'
  // import { ImageResize } from '../modules/ImageResize.js'
  // Quill.register('modules/imageResize', ImageResize)

  export default {
    // props: parentContent,
    name: "UploadModal",
    components: {},
    data() {
      return {
        title: "操作",
        visible: false,
        confirmLoading: false,
        form: this.$form.createForm(this),
        index:0,
        model: {},
        questionlist:[],
        validatorRules: {},
        url: {
          // add: "/exam/paper/add",
          // edit: "/exam/paper/edit",
          exportXlsUrl: "exam/question/exportXlsTemplate",

        },
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        formData:null,


      }
    },
    created() {
      //this.initCustomRequest();
      // this.setHandlers()
    },
    methods: {
      add() {
        this.edit({});
      },
      edit(record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model, 'ContentModal'))
          //时间格式化
        });

      },
      onEditorBlur(){
      }, // 失去焦点事件
      close() {
        this.$emit('close');
        this.visible = false;
      },
      handleOk() {
        const that = this;
        // 触发表单验证
        let formData ={selectedRows:this.questionlist}
        formData.index1=this.index;
        that.$emit('addPapersinQuestion', formData);
        that.$emit('ok');
        // this.$emit('parentFn', this.content);
        this.close();
      },
      handleCancel() {
        this.close()
      },
      customRequest(data){
        console.log(data.file)
        const formData = new FormData()
        formData.append('file',data.file)
          httpAction("/exam/question/uploadQuestion",formData,'post').then((res)=>{
            if(res.success){
              this.questionlist = res.result
              console.log("Excel文件questionlist：")
              console.log(this.questionlist)
              this.$message.success(res.message)
            }else{
              this.$error({
                title:res.message
              })
            }
          })
      },
     // initCustomRequest(){
     //    var that=this;
     //   this.customRequest=async ({ action, file, onSuccess, onError, onProgress }) => {
     //     const base64 = await new Promise(resolve => {
     //       const fileReader = new FileReader();
     //       fileReader.readAsDataURL(file);
     //       fileReader.onload = () => {
     //         resolve(fileReader.result);
     //       };
     //     });
     //     const formData = new FormData();
     //     formData.append(file.name, file);
     //     this.formData=formData;
     //     onSuccess(null,file);
     //     return {
     //       abort() {
     //         alert('image uploader aborted');
     //       },
     //     };
     //   }
      // 下载模板

      handleExportTemplate:function(fileName){
        if(!fileName || typeof fileName != "string"){
          fileName = "导出文件"
        }
        let param = {...this.queryParam};
        if(this.selectedRowKeys && this.selectedRowKeys.length>0){
          param['selections'] = this.selectedRowKeys.join(",")
        }
        console.log("导出参数",param)
        downFile(this.url.exportXlsUrl,param).then((data)=>{
          if (!data) {
            this.$message.warning("文件下载失败")
            return
          }
          if (typeof window.navigator.msSaveBlob !== 'undefined') {
            window.navigator.msSaveBlob(new Blob([data]), fileName+'.xls')
          }else{
            let url = window.URL.createObjectURL(new Blob([data]))
            let link = document.createElement('a')
            link.style.display = 'none'
            link.href = url
            link.setAttribute('download', fileName+'.xls')
            document.body.appendChild(link)
            link.click()
            document.body.removeChild(link); //下载完成移除元素
            window.URL.revokeObjectURL(url); //释放掉blob对象
          }
        })
      },

     },
    }



</script>

<style lang="less" scoped>

</style>
