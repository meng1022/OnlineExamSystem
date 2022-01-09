<template>
  <a-modal
    :title="title"
    :width="1200"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">

    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-row :gutter="24">


          <a-col :md="6" :sm="8">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="题型：">
              <a-select v-model="queryParam.type" placeholder="请选择">
                <a-select-option value="single">单选</a-select-option>
                <a-select-option value="multi">多选</a-select-option>
                <a-select-option value="judge">判断</a-select-option>
                <a-select-option value="quest">问答</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="8">
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="难度：">
          <a-select  v-model="queryParam.hard" placeholder="请选择">
            <a-select-option value="0">简单</a-select-option>
            <a-select-option value="1">中等</a-select-option>
            <a-select-option value="2">困难</a-select-option>
          </a-select>
          </a-form-item>
          </a-col>

          <a-col :md="4" :sm="5">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="分值：">
              <a-input-number v-model="queryParam.score"></a-input-number>
            </a-form-item>
          </a-col>
          <a-col>
          <a-button size="middle" type="primary" style="margin-right: 1.9em;" @click="searchQuery">查询</a-button>
          <a-button size="middle" type="reset" style="margin-right: 1.9em;" @click="searchReset1">重置</a-button>
          </a-col>
        </a-row>


<!--          <a-col :md="8" :sm="10">-->
<!--            <a-form-item-->
<!--                    :labelCol="labelCol"-->
<!--                    :wrapperCol="wrapperCol"-->
<!--                    label="知识点标签：">-->
<!--              <a-input placeholder="请输入" v-model="queryParam.description" />-->
<!--            </a-form-item>-->
<!--          </a-col>-->

        <a-row :gutter="24">
          <a-table
                  ref="table"
                  size="middle"
                  bordered
                  rowKey="id"
                  :columns="columns"
                  :dataSource="dataSource"
                  :pagination="ipagination"
                  :loading="loading"
                  :rowSelection="{selectedRowKeys: selectedRowKeys1, onChange: onSelectChange1}"
                  @change="handleTableChange">
          </a-table>

        </a-row>

      </a-form>
    </a-spin>
  </a-modal>


</template>

<script>
  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: "SelectModal",
    mixins:[JeecgListMixin],
    data () {
      return {
        description: '试题表管理页面',
        rowSelection:{},
        dataSource:[],
        selectedRows:[],
        selectedRowKeys1:[],

        index1:0,
        // 表头
        columns: [
          {
            title: '#',
            dataIndex: '',
            key:'rowIndex',
            width:30,
            align:"center",
            customRender:function (t,r,index) {
              return parseInt(index)+1;
            }
          },
          // {
          //   title: '题干',
          //   align:"center",
          //   dataIndex: 'parentContent',
          //   width: 120
          // },
          {
            title: '试题类型',
            align:"center",
            dataIndex: 'type'
          },
          {
            title: '问题描述',
            align:"center",
            dataIndex: 'content',
            width: 120
          },
          // {
          //   title: '试题解析',
          //   align:"center",
          //   dataIndex: 'analysis'
          // },
          // {
          //   title: '知识点标签',
          //   align:"center",
          //   dataIndex: 'description'
          // },
          ,
          {
            title: '标准答案',
            align:"center",
            dataIndex: 'answer'
          },
          {
            title: '分值',
            align:"center",
            dataIndex: 'score'
          },
          {
            title: '难度',
            align:"center",
            dataIndex: 'hard',
            width: 120
          },
          {
            title: '创建时间',
            align:"center",
            dataIndex: 'createTime'
          },
          // {
          //   title: '操作',
          //   dataIndex: 'action',
          //   align:"center",
          //   scopedSlots: { customRender: 'action' },
          //   width: 140
          // }
        ],
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
        confirmLoading2: false,
        form: this.$form.createForm(this),
        validatorRules:{
        },
        url: {
          list:"/exam/questionbase/list",
          add: "/exam/question/addfromquestionbase",
          // edit: "/exam/paper/edit",
          // delete: "/exam/question/delete",
          // deleteBatch: "/exam/question/deleteBatch",
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
        this.selectedRows=[];
        this.selectedRowKeys1=[];
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'no','type','content','optionA','optionB','optionC','optionD','optionE','answer','score','analysis','sortNo',))
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
            // that.confirmLoading = true;
            // let httpurl = '';
            // let method = '';
            // if(!this.model.id){
            //   httpurl+=this.url.add;
            //   method = 'post';
            // }else{
            //   httpurl+=this.url.edit;
            //    method = 'put';
            // }
            that.confirmLoading = false;
            let formData={selectedRows:JSON.parse(JSON.stringify(that.selectedRows))}
            formData.index1=this.index1;
            console.log(formData)
            that.$emit('addPapersinQuestion', formData);
            that.close();
          }
        })
      },
      handleCancel () {
        this.close()
      },
      // initDataSource(){
      //   let httpurl = this.url.list;
      //   let method = 'get';
      //   var that=this;
      //   console.log(this)
      //
      //   httpAction(httpurl,null,method).then((res)=>{
      //     console.log(res)
      //     if(res.success){
      //       this.dataSource2=res.result.records;
      //     }else{
      //       this.$message.warning(res.message);
      //     }
      //   }).finally(() => {
      //     this.confirmLoading = false;
      //     // that.close();
      //   })
      // },
      onSelectChange1 (selectedRowKeys,selectionRows) {
        this.selectedRowKeys1 = selectedRowKeys;
        this.selectedRows = selectionRows;
      }

      }
  }
</script>

<style lang="less" scoped>

</style>
