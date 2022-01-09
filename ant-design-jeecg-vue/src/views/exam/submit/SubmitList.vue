<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8" v-show=false>
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
          <!--
        <template v-if="toggleSearchStatus">
        <a-col :md="6" :sm="8">
            <a-form-item label="答题结果">
              <a-input placeholder="请输入答题结果" v-model="queryParam.result"></a-input>
            </a-form-item>
          </a-col>
        </template>
      -->
          <a-col :md="6" :sm="8" >
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset2" icon="reload" style="margin-left: 8px">重置</a-button>
            </span>
          </a-col>

          <a-col :span='12'>
            <div class="card-action">
              <a-button type= "primary" style=" float: right;margin-right: 1.0em;margin-top: 1.0em" @click="ToQuestionList(queryParam.examId,examName,sessionCode)">试题点评</a-button>
<!--              <a-button type= "primary" style=" float: right;margin-right: 1.0em;margin-top: 1.0em" @click="toAnalysis(queryParam.examId)">数据分析</a-button>-->
<!--              <a-button type= "primary" style=" float: right;margin-right: 1.0em;margin-top: 1.0em" @click="toTotalScore(queryParam.examName)">总成绩</a-button>-->
              <a-button type= "primary" style=" float: right;margin-right: 1.0em;margin-top: 1.0em" @click="toScoreList(queryParam.examId,examName,sessionCode)">本场成绩</a-button>
            </div>
          </a-col>

        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button type="primary"  icon="download" style="margin-left: 8px" @click="handleExportXls(examName+sessionCode+'答题结果表')">导出</a-button>
      <!--<a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primrt">导入</a-button>
      </a-upload>-->
      <a-button type="primary" style="margin-left: 8px" @click="dump2db" v-show="disableFlag">答卷入库</a-button>
      <a-button type="primary" style="margin-left: 8px" @click="getScore1" v-show="disableFlag">客观题一键判卷</a-button>
      <a-button type="primary" style="margin-left: 8px" @click="getScore2" v-show="disableFlag">问答题智能判卷</a-button>
      <br><br><p>请先判客观题，再判问答题</p>
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

        <span slot="result" slot-scope="result">
          <span v-for="item in result">{{item.no}}.{{item.value}}; </span>
        </span>
<!--        <span slot="action" slot-scope="text, record">-->
<!--            <a @click="openQuestModal(record.userId)">手动批阅问答题</a>-->
<!--          &lt;!&ndash;-->
<!--          <a @click="handleEdit(record)">编辑</a>-->

<!--          <a-divider type="vertical" />-->
<!--          <a-dropdown>-->
<!--            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>-->
<!--            <a-menu slot="overlay">-->
<!--              <a-menu-item>-->
<!--                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">-->
<!--                  <a>删除</a>-->
<!--                </a-popconfirm>-->
<!--              </a-menu-item>-->
<!--            </a-menu>-->
<!--          </a-dropdown>-->
<!--        &ndash;&gt;-->
<!--        </span>-->

      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <submit-modal ref="modalForm" @ok="modalFormOk"></submit-modal>
    <quest-modal ref="QuestForm" @ok="modalFormOk"></quest-modal>
  </a-card>
</template>

<script>
  import SubmitModal from './modules/SubmitModal'
  import QuestModal from './modules/QuestModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import {getScore1,getScore2,dump2db} from '@/api/api'
  import { getAction } from '@/api/manage'

  export default {
    name: "SubmitList",
    mixins:[JeecgListMixin],
    components: {
      QuestModal,
      SubmitModal,
    },
    data () {
      return {
        description: '答题结果表管理页面',
        disableFlag: true,
        provinceData: ["北京", "上海", "重庆", "安徽", "福建", "甘肃", "广东", "广西", "贵州", "海南", "河北", "黑龙江", "河南", "香港", "湖北", "湖南", "江苏", "江西", "吉林", "辽宁", "澳门", "内蒙古", "宁夏", "青海", "山东","山西", "陕西", "四川", "台湾", "天津", "新疆", "西藏", "云南", "浙江", "海外" ],
        hasquest:"",
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
            title: '答题结果',
            align:"center",
            dataIndex: 'result',
            scopedSlots: { customRender: 'result' },
            width: 400
           },
          // {
          //   title:'操作',
          //   align:"center",
          //   dataIndex:'action',
          //   scopedSlots: { customRender: 'action' },
          // },
          {
            title: '提交时间',
            align: "center",
            width: 150,
            dataIndex: 'createTime',
            sorter: true
          },
        ],
		url: {
          list: "/exam/submit/queryByExamId",
          delete: "/exam/submit/delete",
          deleteBatch: "/exam/submit/deleteBatch",
          exportXlsUrl: "/exam/submit/exportXls",
          importExcelUrl: "/exam/submit/importExcel",
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
      if (to.name == 'exam-submit') {
        this.queryParam.examId = this.$route.params.examid
        if (this.$route.params.examid) {
          this.disableFlag = true
        } else {
          this.disableFlag = false
        }
        this.loadData()
      }
    }
  },
  created () {

    if (this.$route.params.examid) {
      this.disableFlag = true
      this.examName = this.$route.params.examName
      this.sessionCode = this.$route.params.sessionCode
    } else {
      this.disableFlag = false
    }
  },
    methods: {
      getScore1 () {
        getScore1(this.queryParam.examId).then((res)=>{
          if (res.success) {
            this.$success({
              title: '客观题判卷成功！'
            })
          } else {
            this.$error({
              title: res.message
            })
          }
        })
      },
      getScore2 () {
        getScore2(this.queryParam.examId).then((res)=>{
          if (res.success) {
            this.$success({
              title: '问答题判卷成功！'
            })
          } else {
            this.$error({
              title: '错误！'
            })
          }
        })
      },
      dump2db () {
        dump2db(this.queryParam.examId).then((res)=>{
          if (res.success) {
            this.$success({
              title: res.message
            })
            this.loadData()
          } else {
            this.$error({
              title: res.message
            })
          }
        })
      },
      initDictConfig(){
        let self = this
        this.dataSource.map(function(item){
          if (self.isJsonString(item.result)) {
            item.result = JSON.parse(item.result)
          }
          return item
        })
      },
      isJsonString(str) {
          try {
              if (typeof JSON.parse(str) == "object") {
                  return true;
              }
          } catch(e) {
          }
          return false;
      },
      openQuestModal(userId){
        if(this.hasquest=="0"){
          this.$message.error("本考试无问答题，无法批阅！")
        }else{
          this.$refs.QuestForm.userId=userId
          this.$refs.QuestForm.examId=this.$route.params.examid
          let httpurl="/exam/score/getscore2"
          let formData = {userId:userId,examId:this.$route.params.examid}
          getAction(httpurl,formData).then((res)=>{
            this.$refs.QuestForm.edit({score2:res})
          })
          this.$refs.QuestForm.title='录入问答题总分'
        }
      },
      ToQuestionList(examid,examName,sessionCode) {
        this.$router.push({ name: 'exammanage-comment', params: {examid: examid,examName:examName,sessionCode:sessionCode}  })
      },
      toScoreList (examid,examName,sessionCode) {
        console.log(examName+","+sessionCode)
        this.$router.push({ name: 'exam-score', params: {examid: examid,examName:examName,sessionCode:sessionCode} })
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>