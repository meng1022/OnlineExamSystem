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
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
            </span>
          </a-col>
          <a-col :span='12'>
            <div class="card-action">
<!--              <a-button size="big" type= "primary" style=" float: right;margin-right: 1.0em;margin-top: 1.0em" @click="ToQuestionList(queryParam.examName)">试题点评</a-button>-->
<!--              <a-button size="big" type= "primary" style=" float: right;margin-right: 1.0em;margin-top: 1.0em" @click="toAnalysis(queryParam.examName)">数据分析</a-button>-->
<!--              <a-button size="big" type= "primary" style=" float: right;margin-right: 1.0em;margin-top: 1.0em" @click="toScoreList(queryParam.examName)">本场成绩</a-button>-->
<!--              <a-button size="big" type= "primary" style=" float: right;margin-right: 1.0em;margin-top: 1.0em" @click="toExamList(queryParam.examName)">考生答卷</a-button>-->
            </div>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button type="primary" icon="download" @click="handleExportXls('成绩表')">导出</a-button>
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
  import { filterObj } from '@/utils/util';

  export default {
    name: "totalScoreList",
    mixins:[JeecgListMixin],
    components: {
      ScoreModal
    },
    data () {
      return {
        description: '总成绩表',
        provinceData: ["北京", "上海", "重庆", "安徽", "福建", "甘肃", "广东", "广西", "贵州", "海南", "河北", "黑龙江", "河南", "香港", "湖北", "湖南", "江苏", "江西", "吉林", "辽宁", "澳门", "内蒙古", "宁夏", "青海", "山东","山西", "陕西", "四川", "台湾", "天津", "新疆", "西藏", "云南", "浙江", "海外" ],
        // 表头
        columns: [
       {
            title: '省份',
            align:"center",
            dataIndex: 'provcode'
           },
       {
            title: '姓名',
            align:"center",
            dataIndex: 'realname'
           },
       {
            title: '单位',
            align:"center",
            dataIndex: 'orgname'
           },
       {
            title: '基础知识',
            align:"center",
            children: [{
              title: '分数',
              align:"center",
              dataIndex: 'score1'
            },{
              title: '排名',
              align:"center",
              dataIndex: 'rank1'
            }]
          },
       {
            title: '影像诊断',
            align:"center",
            children: [{
              title: '分数',
              align:"center",
              dataIndex: 'score2'
            },{
              title: '排名',
              align:"center",
              dataIndex: 'rank2'
            }]
          },
       {
            title: '病例分析',
            align:"center",
            children: [{
              title: '分数',
              align:"center",
              dataIndex: 'score3'
            },{
              title: '排名',
              align:"center",
              dataIndex: 'rank3'
            }]
          },
       {
            title: '总分',
            align:"center",
            children: [{
              title: '分数',
              align:"center",
              dataIndex: 'total'
            },{
              title: '排名',
              align:"center",
              dataIndex: 'rank'
            }]
          },
        ],
		url: {
          list: "/exam/total/queryPageListByExamName",
          delete: "/exam/total/delete",
          deleteBatch: "/exam/total/deleteBatch",
          exportXlsUrl: "/exam/total/exportXls",
          importExcelUrl: "/exam/total/importExcel",
       },
    }
  },
  computed: {
    importExcelUrl: function(){
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
    }
  },
  watch :{
  },
  created () {
  },
    methods: {
      getQueryParams() {
        //获取查询条件
        let sqp = {}
        if(this.superQueryParams){
          sqp['superQueryParams']=encodeURI(this.superQueryParams)
        }
        var param = Object.assign(sqp, this.queryParam, this.isorter ,this.filters);
        param.field = this.getQueryField();
        param.pageNo = this.ipagination.current;
        param.pageSize = this.ipagination.pageSize;
        param.examName = this.$route.params.examName
        return filterObj(param);
      },
      getQueryField() {
        //TODO 字段权限控制
        var str = "id,";
        this.columns.forEach(function (value) {
          if (value.dataIndex) {
            str += "," + value.dataIndex;
          } else {
            value.children.forEach(function (item){
              str += "," + item.dataIndex;
            })
          }
        });
        return str;
      },
      ToQuestionList(examid) {
        this.$router.push({ name: 'exammanage-comment', params: {examid: examid}  })
      },
      toExamList (examid) {
        this.$router.push({ name: 'exam-submit', params: {examid: examid} })
      },
      toScoreList (examid) {
        this.$router.push({ name: 'exam-score', params: {examid: examid} })
      },
      toAnalysis (examid) {
        this.$router.push({ name: 'Analysis', params: {examid: examid} })
      },
      toTotalScore (examName) {
        this.$router.push({ name: 'exam-totalscore', params: {examName: examName} })
      },

    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>