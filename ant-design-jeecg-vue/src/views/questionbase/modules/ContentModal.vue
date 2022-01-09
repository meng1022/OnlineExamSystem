<template>
  <a-modal
    :title="title"
    :width="500"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">

<!--    <a-spin :spinning="confirmLoading">        -->
      <a-form :form="form">

<!--        <div id="iviwUp" v-loading="" element-loading-text="请稍等，图片上传中">-->
<!--            <quill-editor v-model="content" ref="myQuillEditor" :options="editorOption">-->
<!--            </quill-editor>-->
              <a-button size="small" @click="removeImg" icon="delete">移除图片</a-button>
              <a-upload showUploadList="false" :customRequest="customRequest">
<!--              <a-upload showUploadList="false" :headers="token" :action="UploadUrl" @change="handleImportImg">-->
                <a-button size="small" icon="upload"  style="margin:8px">上传图片</a-button>
              </a-upload>

              <a @click="checkImg" >{{content}}</a>

<!--        </div>-->

      </a-form>
<!--    </a-spin>-->
  </a-modal>
</template>

<script>
  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import AFormItem from 'ant-design-vue/es/form/FormItem'
  //import QuillEditor from '@/views/questionbase/QuillEditor'
  //import VueQuillEditor, { Quill } from 'vue-quill-editor'
  //import { ImageDrop } from 'quill-image-drop-module'
  //import {ImageResize} from 'quill-image-resize-module'
  //Quill.register('modules/imageDrop', ImageDrop)
  //Quill.register('modules/imageResize', ImageResize)
  // import Quill from 'quill'
  // import { ImageResize } from '../modules/ImageResize.js'

  export default {
    // props: parentContent,
    name: "ContentModal",
    components: {
      AFormItem,
      //QuillEditor,
    },
    data() {
      return {
        title: "操作",
        visible: false,
        content: '123',
        confirmLoading: false,
        form: this.$form.createForm(this),
        str: '',
        model: {},
        validatorRules: {},
        url: {
          uploadURL:"exam/questionbase/uploadImg",
        },
        // editorOption: { // 富文本框配置
        //   placeholder: '',
        //   theme: 'snow', // or 'bubble'
        //   modules: {
        //     toolbar: {
        //       container: [
        //         ['bold', 'italic', 'underline', 'strike'],
        //         ['blockquote', 'code-block'],
        //         [{ 'header': 1 }, { 'header': 2 }],
        //         [{ 'list': 'ordered' }, { 'list': 'bullet' }],
        //         [{ 'script': 'sub' }, { 'script': 'super' }],
        //         [{ 'indent': '-1' }, { 'indent': '+1' }],
        //         [{ 'direction': 'rtl' }],
        //         [{ 'size': ['small', false, 'large', 'huge'] }],
        //         [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
        //         [{ 'font': [] }],
        //         [{ 'color': [] }, { 'background': [] }],
        //         [{ 'align': [] }],
        //         ['clean'],
        //         ['link', 'image', 'video']
        //       ], // 工具栏
        //       handlers: {
        //       }
        //     },
        //     history: {
        //       delay: 1000,
        //       maxStack: 50,
        //       userOnly: false
        //     },
        //     imageDrop: true,
        //     imageResize: {
        //       displayStyles: {
        //         backgroundColor: 'black',
        //         border: 'none',
        //         color: 'white'
        //       },
        //       modules: [ 'Resize', 'DisplaySize', 'Toolbar' ]
        //     }
        //   }
        // }

      }
    },
    created() {
      //this.setHandlers()
    },
    // computed:{
    //   UploadUrl:function(){
    //     return `${window._CONFIG['domianURL']}/${this.url.uploadURL}`;
    //   },
    //   token:function() {
    //     return {
    //       'Authorization': localStorage.getItem('pro__Access-Token')
    //     }
    //   },
    // },
    methods: {
      add() {
        this.edit({});
      },
      edit(record) {
        console.log("Hello,quillEditor!")
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model, 'ContentModal'))
        });
      },
      removeImg(){
        this.content=''
      },
      checkImg(){
        window.open(this.content)
      },
      onEditorBlur(){
      }, // 失去焦点事件
      close() {
        console.log("Bye,quillEditor!")
        // this.$refs.myQuillEditor.$emit('blur');
        this.$emit('close');
        this.visible = false;
      },
      handleOk() {
        console.log("imgURL："+this.content)
        this.$emit('parentFn', this.content);
        this.close();
      },
      handleCancel() {
        this.close()
      },
      handleImportImg(info){
        if (info.file.status !== 'uploading') {
          console.log(info.file, info.fileList);
        }
        if (info.file.status === 'done') {
          if(info.file.response.success){
            this.$message.success(`${info.file.name} 文件上传成功`);
          } else {
            this.$message.error(`${info.file.name} ${info.file.response.message}.`);
          }
        } else if (info.file.status === 'error') {
          this.$message.error(`文件上传失败: ${info.file.msg} `);
        }
      },

      customRequest (data) {
        const formData = new FormData()
        formData.append('image', data.file)
        console.log(data.file)
        // formData.append('token', 'aiufpaidfupipiu')//随便写一个token示例
        this.saveFile(formData)
      },
      saveFile (formData) {
        this.form.validateFields((err, values) => {
          if (!err) {
            //let quill = this.$refs.myQuillEditor.quill
            httpAction('/exam/questionbase/uploadImg', formData, 'post').then((res) => {
              if(res.success){
                this.content = res.result
                console.log("上传图片："+res.result)
              }
              else{
                this.$error({
                  title:res.message
                })
              }
                //let length = quill.getSelection().index;
                //quill.insertEmbed(length,'image',res.result)
                // 调整光标到最后
                //quill.setSelection(length + 1)
            }).finally(() => {
               this.confirmLoading = false;
               //this.close();
            })
          }
        })
      },
      upload(){
      },
      // setHandlers(){
      //   var that=this;
      //   this.editorOption.modules.toolbar.handlers=
      //   {'image':
      //     function(value) {
      //       if (value) {
      //         document.querySelector('#iviwUp input').click()
      //       } else {
      //         that.quill.format('image', false);
      //       }
      //     }
      //   }
      // }
    },
    mounted() {
    }

  }
</script>

<style lang="less" scoped>

</style>
