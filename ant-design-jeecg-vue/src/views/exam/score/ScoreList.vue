<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8" v-show="false">
            <a-form-item label="考试场次id">
              <a-input disabled placeholder="请输入考试场次id" v-model="queryParam.examId"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="所在省份">
              <a-select v-model="queryParam.provcode" placeholder="请选择省份查询">
                <a-select-option value="">请选择省份查询</a-select-option>
                <a-select-option v-for="province in provinceData" :key="province">{{province}}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8" >
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset2" icon="reload" style="margin-left: 8px">重置</a-button>
            </span>
          </a-col>

          <a-col :span='12'>
            <div class="card-action">
              <a-button type= "primary" style=" float: right;margin-right: 1.0em;margin-top: 1.0em" @click="ToCommentList(queryParam.examId,examName,sessionCode)">试题点评</a-button>
<!--              <a-button type= "primary" style=" float: right;margin-right: 1.0em;margin-top: 1.0em" @click="toAnalysis(queryParam.examId)">数据分析</a-button>-->
<!--              <a-button type= "primary" style=" float: right;margin-right: 1.0em;margin-top: 1.0em" @click="toTotalScore(queryParam.examName)">总成绩</a-button>-->
              <a-button type= "primary" style=" float: right;margin-right: 1.0em;margin-top: 1.0em" @click="toSubmitList(queryParam.examId,examName,sessionCode)">考生答卷</a-button>
              <a-button type= "primary" style=" float: right;margin-right: 1.0em;margin-top: 1.0em" @click="ToBarChart(queryParam.examId,examName,sessionCode)">成绩柱状图</a-button>
            </div>
          </a-col>
          </a-row>
      </a-form>

    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button type="primary" icon="download" @click="handleExportXls(examName+sessionCode+'成绩表')">导出</a-button>
     <!-- <a-upload  name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
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


      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        @change="handleTableChange">

        <span slot="action" slot-scope="text, record">
          <a>
            <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
              <a>删除</a>
            </a-popconfirm>
          </a>
          <!--
          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        -->
        </span>

      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <score-modal ref="modalForm" @ok="modalFormOk"></score-modal>
  </a-card>
</template>

<script>
  import ScoreModal from './modules/ScoreModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import {getLoginfo} from '@/api/api'
  import {getExamList} from '@/api/api'

  export default {
    name: "ScoreList",
    mixins:[JeecgListMixin],
    components: {
      ScoreModal
    },
    data () {
      return {
        description: '成绩表管理页面',
        provinceData: ["北京", "上海", "重庆", "安徽", "福建", "甘肃", "广东", "广西", "贵州", "海南", "河北", "黑龙江", "河南", "香港", "湖北", "湖南", "江苏", "江西", "吉林", "辽宁", "澳门", "内蒙古", "宁夏", "青海", "山东","山西", "陕西", "四川", "台湾", "天津", "新疆", "西藏", "云南", "浙江", "海外" ],
        // 表头
        listData: [],
        columns: [
          {
            title: '#',
            dataIndex: '',
            key:'rowIndex',
            width:60,
            align:"center",
            customRender:function (t,r,index) {
              return parseInt(index)+1;
            }
           },
       {
            title: '考卷名称',
            align:"center",
            dataIndex: 'sessionName'
           },
       {
            title: '考生姓名',
            align:"center",
            dataIndex: 'realname'
           },
       {
            title: '所在省份',
            align:"center",
            dataIndex: 'provcode'
           },
       // {
       //      title: '所在城市',
       //      align:"center",
       //      dataIndex: 'citycode'
       //     },
		   {
            title: '客观题分数',
            align:"center",
            dataIndex: 'score1'
           },
          {
            title:'问答题分数',
            align:"center",
            dataIndex:'score2'
          },
          {
            title:'总分',
            align:"center",
            dataIndex:'score'
          },
          {
            title: '提交时间',
            align: "center",
            width: 150,
            dataIndex: 'submitTime',
            sorter: true
          },
        ],
		url: {
          list: "/exam/score/queryByExamId",
          delete: "/exam/score/delete",
          deleteBatch: "/exam/score/deleteBatch",
          exportXlsUrl: "/exam/score/exportXls",
          importExcelUrl: "/exam/score/importExcel",
       },
    }
  },
  computed: {
    importExcelUrl: function(){
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
    }
  },
  watch :{
    '$route': function (to, from) {
      if (to.name == 'exam-score') {
        this.queryParam.examId = this.$route.params.examid
        this.loadData()
      }
    }
  },
  created () {
      this.examName = this.$route.params.examName
      this.sessionCode = this.$route.params.sessionCode
  },
    methods: {
      ToCommentList(examid,examName,sessionCode) {
        this.$router.push({ name: 'exammanage-comment', params: {examid: examid,examName:examName,sessionCode:sessionCode}  })
      },
      toSubmitList (examid,examName,sessionCode) {
        this.$router.push({ name: 'exam-submit', params: {examid: examid,examName:examName,sessionCode:sessionCode} })
      },
      ToBarChart(examid,examName,sessionCode){
        this.$router.push({name:'examscore-barchart',params:{examid: examid,examName:examName,sessionCode:sessionCode}})
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>