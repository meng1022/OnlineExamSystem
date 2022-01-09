<template>
  <a-card :bordered="false">
  <!-- 查询区域 -->
  <div class="table-page-search-wrapper">
    <a-form layout="inline">
      <a-row :gutter="24">
        <a-col :md="6" :sm="8">
          <a-form-item label="科目名称">
            <a-input placeholder="请输入科目名称" v-model="queryParam.sessionName"></a-input>
          </a-form-item>
        </a-col>
        <a-col :md="6" :sm="8" >
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
<!--              <a-button type="primary" icon="download" style="margin-left: 8px" @click="handleExportXls('试题库')">导出</a-button>-->
<!--              <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">-->
<!--                <a-button type="primary" icon="import" style="margin-left: 8px">导入</a-button>-->
<!--              </a-upload>-->
            </span>
        </a-col>

      </a-row>
    </a-form>
  </div>

    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('试题库')">导出</a-button>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown>
    </div>

    <div>
      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="sessionName"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange">

        <span slot="action" slot-scope="text, record">
          <a @click="handleQuestion(record.sessionName)">进入试题库</a>
          <a-divider type="vertical" />
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.sessionName)">
            <a>删除</a>
          </a-popconfirm>
        </span>

      </a-table>
    </div>
    <questionbase-modal ref="modalForm" @ok="modalFormOk"></questionbase-modal>

  </a-card>
</template>

<script>
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import QuestionbaseModal from './modules/QuestionbaseModal'
  export default {
    name: "SessionList",
    mixins:[JeecgListMixin],
    components: {
      QuestionbaseModal,
    },
    data() {
      return {
        description:'试题库管理页面',
        columns:[
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
            title: '科目名称',
            align:"center",
            dataIndex: 'sessionName'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
        queryParam: {
          sessionName:"",
        },
        url:{
          list:"exam/session/list",
          delete:"exam/session/delete",
          deleteBatch:"/exam/session/deleteBatch",
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
    methods: {
      handleQuestion: function(sessionName){
        console.log("欢迎进入"+sessionName+"试题库")
        this.$router.push({name:'questionbasemanage-questions',params:{sessionName:sessionName}})
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>
