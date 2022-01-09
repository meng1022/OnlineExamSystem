<template>
  <div class="edit_container">
    <!-- 新增时输入 -->
    <quill-editor
      v-model="content"
      ref="myQuillEditor"
      :options="editorOption"
      @blur="onEditorBlur($event)" @focus="onEditorFocus($event)"
      @change="onEditorChange($event)">
    </quill-editor>
    <!-- 从数据库读取展示 -->
    <div v-html="str" class="ql-editor">
      {{str}}
    </div>
  </div>
</template>
<script>
  import { quillEditor } from "vue-quill-editor"; //调用编辑器
  import 'quill/dist/quill.core.css';
  import 'quill/dist/quill.snow.css';
  import 'quill/dist/quill.bubble.css';
  export default {
    name:"QuillEditor",
    components: {
      quillEditor
    },
    data() {
      return {
        content: '',
        str: '',
        editorOption: {}
      }
    },
    methods: {
      onEditorReady(editor) { // 准备编辑器

      },
      onEditorBlur(){}, // 失去焦点事件
      onEditorFocus(){}, // 获得焦点事件
      onEditorChange(){
        console.log(this.content)
        this.$emit('customRequest', this.content);
      }, // 内容改变事件
      // 转码
      escapeStringHTML(str) {
        str = str.replace(/</g,'<');
        str = str.replace(/>/g,'>');
        return str;
      }
    },
    computed: {
      editor() {
        return this.$refs.myQuillEditor.quill;
      },
    },
    mounted() {
      let content = ''; // 请求后台返回的内容字符串
      this.str = this.escapeStringHTML(content);
    }
  }
</script>