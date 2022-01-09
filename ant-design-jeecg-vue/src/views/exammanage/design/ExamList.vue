<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="考试名称">
              <a-input placeholder="请输入考试名称" v-model="queryParam.examName"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="场次">
              <a-input placeholder="请输入场次" v-model="queryParam.sessionCode" ></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="科目">
              <a-input placeholder="请输入科目名称" v-model="queryParam.sessionName"></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="8" >
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <!--<a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
              </a>-->
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button type="primary" icon="download" @click="handleExportXls('考试')">导出</a-button>
      <!--<a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>-->
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
        :data-source="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange">

        <span slot="delflag" slot-scope="delflag">
          <p v-if="delflag == 0">正常</p>
          <p v-else>已删除</p>
        </span>

        <span slot="status" slot-scope="status">
          <p v-if="status == 1">正常</p>
          <p v-else>冻结</p>
        </span>

        <span slot="action" slot-scope="text, record">
          <a @click="handleQuestion(record.id,record.sessionName,record.examName,record.sessionCode,record.examStartTime,record.examEndTime)">设计试卷</a>
        </span>

      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <exam-modal ref="modalForm" @ok="modalFormOk"></exam-modal>
  </a-card>
</template>

<script>
  import ExamModal from './modules/ExamModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import {getPreview} from '@/api/api'
  import { getAction } from '@api/manage'
  export default {
    name: "ExamList",
    mixins:[JeecgListMixin],
    components: {
      ExamModal
    },
    data () {
      return {
        description: '考试管理页面',
        // 表头
        // dataSource:[],
        // selectedRowKeys: [],
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
            title: '考试名称',
            align:"center",
            dataIndex: 'examName'
          },
          {
            title: '场次',
            align:"center",
            dataIndex: 'sessionCode'
          },
          {
            title: '科目',
            align:"center",
            dataIndex: 'sessionName'
          },
          {
            title: '考试开始时间',
            align:"center",
            dataIndex: 'examStartTime'
          },
          {
            title: '考试结束时间',
            align:"center",
            dataIndex: 'examEndTime'
          },
          {
            title: '验证码',
            align:"center",
            dataIndex: 'verificationCode'
          },
          {
            title: '考试时长',
            align:"center",
            dataIndex: 'examDuration'
          },
          {
            title: '提交次数',
            align:"center",
            dataIndex: 'submitTimes'
          },
          {
            title: '服务器缓存',
            align:"center",
            dataIndex: 'submitServerCache'
          },
          // {
          //   title: '状态',
          //   align:"center",
          //   dataIndex: 'status',
          //   scopedSlots: { customRender: 'status' },
          // },
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
        url: {
          list: "/exam/exam/list",
          delete: "/exam/exam/delete",
          deleteBatch: "/exam/exam/deleteBatch",
          exportXlsUrl: "/exam/exam/exportXls",
          importExcelUrl: "/exam/exam/importExcel",
        },
      }
    },

    created(){
      console.log("dataSource in this page:",this.dataSource)
    },

    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      }
    },

    methods: {
      handleQuestion: function(examId,sessionName,examName,sessionCode,starttime,endtime){
        var start = new Date(starttime.toString())
        var end = new Date(endtime.toString())
        console.log(this.$moment()+","+start.getTime()+","+end.getTime())
        if(this.$moment()<start.getTime()-2*60*1000||this.$moment()>end.getTime()+10*60*1000){
          this.$router.push({ name: 'exammanage-question1', params: {examid:examId,sessionName:sessionName,examName:examName,sessionCode:sessionCode}})
          //  this.$refs.modalUserRole.show(roleId);
        }
        else{
          //this.$message.warning("考试即将或已经开始，不允许修改试卷");
          this.$router.push({ name: 'exammanage-question1', params: {examid:examId,sessionName:sessionName,examName:examName,sessionCode:sessionCode}})
        }
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>