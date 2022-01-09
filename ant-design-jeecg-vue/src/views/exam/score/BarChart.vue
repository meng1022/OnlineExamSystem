<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
          </a-col>
          <a-col :md="6" :sm="8" >
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
            </span>
          </a-col>

          <a-col :span='12'>
            <div class="card-action">
              <a-button type= "primary" style=" float: right;margin-right: 1.0em;margin-top: 1.0em" @click="ToCommentList(examid,examName,sessionCode)">试题点评</a-button>
              <a-button type= "primary" style=" float: right;margin-right: 1.0em;margin-top: 1.0em" @click="toSubmitList(examid,examName,sessionCode)">考生答卷</a-button>
              <a-button  type= "primary" style=" float: right;margin-right: 1.0em;margin-top: 1.0em" @click="toScoreList(examid,examName,sessionCode)">本场成绩表格</a-button>
            </div>
          </a-col>
          </a-row>
      </a-form>

    </div>

    <bar title="成绩柱状图(按照100*成绩/总分计算)" :dataSource="dataSource" :height="420"/>

  </a-card>
</template>

<script>
  import Bar from '@/components/chart/Bar'
  import {getBarChartData} from '@/api/api'

  export default {
    name: "BarChart",
    components: {
      Bar
    },
    data () {
      return {
        dataSource: [],
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
      this.examid = this.$route.params.examid
      this.examName = this.$route.params.examName
      this.sessionCode = this.$route.params.sessionCode
      getBarChartData(this.examid).then((res)=>{
        if(res.success){
            this.dataSource = res.result
        }else{

        }
      })
  },
    methods: {
      ToCommentList(examid,examName,sessionCode) {
        this.$router.push({ name: 'exammanage-comment', params: {examid: examid,examName:examName,sessionCode:sessionCode}  })
      },
      toSubmitList (examid,examName,sessionCode) {
        this.$router.push({ name: 'exam-submit', params: {examid: examid,examName:examName,sessionCode:sessionCode} })
      },
      toScoreList (examid,examName,sessionCode) {
        this.$router.push({ name: 'exam-score', params: {examid: examid,examName:examName,sessionCode:sessionCode} })
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>