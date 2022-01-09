<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="6" :sm="8" v-show=false>
            <a-form-item label="场次名称">
              <a-input disabled placeholder="" v-model="queryParam.sessionName"></a-input>
            </a-form-item>
          </a-col>
            <a-col :md="4" :sm="8">
                  <a-form-item
                    :labelCol="labelCol"
                    :wrapperCol="wrapperCol"
                    label="题型：">
                    <a-select v-model="queryParam.type" placeholder="请选择题型">
                      <a-select-option value="single">单选</a-select-option>
                      <a-select-option value="multi">多选</a-select-option>
                      <a-select-option value="judge">判断</a-select-option>
                      <a-select-option value="quest">问答</a-select-option>
                    </a-select>
                  </a-form-item>
            </a-col>
          <a-col :md="4" :sm="8">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="难度：">
              <a-select  v-model="queryParam.hard" placeholder="请选择难度">
                <a-select-option value="0">简单</a-select-option>
                <a-select-option value="1">中等</a-select-option>
                <a-select-option value="2">困难</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="8">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="分值：">
              <a-input-number v-model="queryParam.score" placeholder=""></a-input-number>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="6" >
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset1" icon="reload" style="margin-left: 8px">重置</a-button>
<!--              <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">-->
                <a-button @click="openUploadModal" type="primary" icon="import" style="margin-left: 8px">导入试题</a-button>
<!--              </a-upload>-->
              <a-button type="primary" icon="download" style="margin-left: 8px" @click="handleExportXls(queryParam.sessionName+'试题库')">导出</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange">

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical" />
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
        </span>

      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <question-modal ref="modalForm" @ok="modalFormOk" @uploadok="uploadok"></question-modal>
    <upload-modal ref="uploadForm" @ok="modalFormOk"></upload-modal>
  </a-card>
</template>

<script>
  import QuestionModal from './modules/QuestionModal'
  import UploadModal from './modules/UploadModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: "QuestionList",
    mixins:[JeecgListMixin],
    components: {
      QuestionModal,
      UploadModal,
    },
    data () {
      return {
        description: '试题库',
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
          {
            title: '问题类型',
            align:"center",
            dataIndex: 'type'
          },
          {
            title: '问题描述',
            align:"center",
            dataIndex: 'content',
            width: 120
          },
          {
            title: '选项A',
            align:"center",
            dataIndex: 'optionA',
            width: 120
          },
          {
            title: '选项B',
            align:"center",
            dataIndex: 'optionB',
            width: 120
          },
          {
            title: '选项C',
            align:"center",
            dataIndex: 'optionC',
            width: 120
          },
          {
            title: '选项D',
            align:"center",
            dataIndex: 'optionD',
            width: 120
          },
          {
            title: '选项E',
            align:"center",
            dataIndex: 'optionE',
            width: 120
          },
          {
            title: '答案',
            align:"center",
            dataIndex: 'answer',
            width: 120
          },
          {
            title: '分值',
            align:"center",
            dataIndex: 'score',
            width: 120
          },
          {
            title: '难度(0简单,1中等,2困难)',
            align:"center",
            dataIndex: 'hard',
            width: 120
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
            width: 140
          }
        ],
        url: {
          list: "/exam/questionbase/list",
          delete: "/exam/questionbase/delete",
          deleteBatch: "/exam/questionbase/deleteBatch",
          exportXlsUrl: "/exam/questionbase/exportXls",
          importExcelUrl: "/exam/questionbase/importExcel",
        },
      }
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      },
      },
    created () {
      console.log(this.queryParam.sessionName)
    },
    methods: {
      openUploadModal:function() {
        this.$refs.uploadForm.add(),
        this.$refs.uploadForm.title="导入试题"
      },
      uploadok:function(){
        this.loadData()
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>