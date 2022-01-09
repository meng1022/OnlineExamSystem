<template>
  <div class="page-header-index-wide">
    <a-row class="data-group" v-for="item in listData">
      <a-card class="box-card" style="margin-bottom: 20px;">
        <div>
          <span style="color: #9e9e9e">{{item.examName}}：{{item.sessionCode}}</span>
        </div>
        <a-row>
          <div style="margin-top:8px;border-top: 1px solid #EAEAEA;padding-top: 10px;"></div>
        </a-row>
        <div class="card-content">
          <a-row type="flex" align="bottom" style="height: 80px;">
            <a-col :span='12'>
              <div class="card-title">
                <div class="layouts">
                  <div class="title">
                    <span>{{item.sessionName}}</span>
                  </div>
                  <!--                  <a href="/" style="margin-left: 20px;font-size: 16px;margin-top: 10px;">考试说明</a>-->
                  <div class="tag">
                  </div>
                </div>
              </div>
              <div class="description" style="width: 100%">
                <a-row>
                  <p v-if="item.timeFlag === 0">考试开始时间：{{item.examStartTime}}</p>
                  <p v-else-if="item.timeFlag === 1">考试开始时间：开放中</p>
                  <p v-else-if="item.timeFlag === 2">考试开始时间：已结束</p>
                </a-row>
              </div>
            </a-col>
            <a-col :span='12'>
              <div class="card-action">
<!--                <a-button size="small" type= "primary" style=" float: right;margin-right: 1.0em;margin-top: 1.0em" @click="ToQuestionList(item.id,item.examName,item.sessionCode)">试题点评</a-button>-->
                <!--<a-button size="small" type= "primary" style=" float: right;margin-right: 1.0em;margin-top: 1.0em" @click="toAnalysis(item.id)">数据分析</a-button>-->
<!--                <a-button size="small" type= "primary" style=" float: right;margin-right: 1.0em;margin-top: 1.0em" @click="toTotalScore(item.examName)">总成绩</a-button>-->
<!--                <a-button size="small" type= "primary" style=" float: right;margin-right: 1.0em;margin-top: 1.0em" @click="toScoreList(item.id,item.examName,item.sessionCode)">本场成绩</a-button>-->
                <a-button size="middle" type= "primary" style=" float: right;margin-right: 1.0em;margin-top: 1.0em" @click="toSubmitList(item.id,item.examName,item.sessionCode)">查看</a-button>
              </div>
            </a-col>
          </a-row>
        </div>
      </a-card>
    </a-row>
    <!--
    <a-row class="data-group">
      <a-card class="box-card" style="margin-bottom: 20px;">
        <div><span style="color: #9e9e9e">第二场</span></div>
        <a-row>
          <div style="margin-top:8px;border-top: 1px solid #EAEAEA;padding-top: 10px;"></div>
        </a-row>
        <div class="card-content">
          <a-row type="flex" align="bottom" style="height: 80px;">
            <a-col :span='20'>
              <div class="card-title">
                <div class="layouts">
                  <div class="title">
                    <span>影像阅片</span>
                  </div>
                  <div class="tag">
                  </div>
                </div>
              </div>
              <div class="description" style="width: 75%">
                <a-row>
                  <p>考试开始时间：开放中</p>
                </a-row>
              </div>
            </a-col>
            <a-col :span='4'>
              <div class="card-action">
                <a-button style=" float: right;margin-right: 1.9em;margin-top: 1.4em" @click="ToQuestionList(2)">进入</a-button>
              </div>
            </a-col>
          </a-row>
        </div>
      </a-card>
    </a-row>
    <a-row class="data-group">
      <a-card class="box-card" style="margin-bottom: 20px;">
        <div><span style="color: #9e9e9e">第三场</span></div>
        <a-row>
          <div style="margin-top:8px;border-top: 1px solid #EAEAEA;padding-top: 10px;"></div>
        </a-row>
        <div class="card-content">
          <a-row type="flex" align="bottom" style="height: 80px;">
            <a-col :span='20'>
              <div class="card-title">
                <div class="layouts">
                  <div class="title">
                    <span>病例分析</span>
                  </div>
                  <div class="tag">
                  </div>
                </div>
              </div>
              <div class="description" style="width: 75%">
                <a-row>
                  <p>考试开始时间：开放中</p>
                </a-row>
              </div>
            </a-col>
            <a-col :span='4'>
              <div class="card-action">
                <a-button style=" float: right;margin-right: 1.9em;margin-top: 1.4em" @click="ToQuestionList(3)">进入</a-button>
              </div>
            </a-col>
          </a-row>
        </div>
      </a-card>
    </a-row>
    -->
  </div>
</template>

<script>
  import {getLoginfo} from '@/api/api'
  import {getExamList} from '@/api/api'

  export default {
    name: "QuestionList",

    components: {
    },
    data() {
      return {
        loading: true,
        center: null,
        listData: [],
        alertFlag: 0,
        timeFlag: 0,
        visible: false,
        confirmLoading: false,
        code: '',
        listCode: '',
        listId: '',
        listEndTime: '',
      }
    },
    created() {
      this.initData()
      setTimeout(() => {
        this.loading = !this.loading
      }, 1000)
    },
    watch :{
    },
    methods: {
      initData () {
        let self = this
        getExamList(null).then((res)=>{
            if(res.success){
              self.listData = res.result.records
              for (let i in self.listData) {
                self.listData[i].timeFlag = 0
              }
              self.clock()
            }
        })
      },
      initLogInfo () {
        getLoginfo(null).then((res)=>{
          if(res.success){
            this.loginfo = res.result;
          }
        })
      },

      clock(){
        let self = this
        var today=this.$moment()
        for (let i in self.listData) {
          var startTime = this.$moment(self.listData[i].examStartTime)
          var stopTime = this.$moment(self.listData[i].examEndTime)
          var shenyu = startTime.diff(today)
          var duration = startTime.diff(stopTime)
          var one_day = 24 * 60 * 60 * 1000;
          var l = shenyu - duration
          if (shenyu >= one_day) {
            setTimeout(function() {
             window.location.reload();
            }, one_day);
          } else if (shenyu < one_day && shenyu > 0 && l >= one_day) {
            self.listData[i].timeFlag = 0
            setTimeout(() => {self.listData[i].timeFlag = 1},shenyu);
            setTimeout(function() {
             window.location.reload();
            }, one_day);

          } else if (shenyu < one_day && shenyu > 0 && l < one_day) {
            self.listData[i].timeFlag = 0
            setTimeout(() => {self.listData[i].timeFlag = 1},shenyu);
            setTimeout(() => {self.listData[i].timeFlag = 2},l)
          } else if (shenyu <= 0 && shenyu >= duration && l >= one_day){
            self.listData[i].timeFlag = 1
            setTimeout(function() {
             window.location.reload();
            }, one_day);
          } else if (shenyu <= 0 && shenyu >= duration && l < one_day) {
            self.listData[i].timeFlag = 1
            setTimeout(() => {self.listData[i].timeFlag = 2},l)
          } else {
            self.listData[i].timeFlag = 2
          }
        }
          //shengyuD=parseInt(shenyu/(60*60*24*1000)),//转换为天
          //D=parseInt(shenyu)-parseInt(shengyuD*60*60*24*1000),//除去天的毫秒数
          //shengyuH=parseInt(D/(60*60*1000)),//除去天的毫秒数转换成小时
          //H=D-shengyuH*60*60*1000,//除去天、小时的毫秒数
          //shengyuM=parseInt(H/(60*1000)),//除去天的毫秒数转换成分钟
          //M=H-shengyuM*60*1000,//除去天、小时、分的毫秒数
          //S=parseInt((shenyu-shengyuD*60*60*24*1000-shengyuH*60*60*1000-shengyuM*60*1000)/1000);//除去天、小时、分的毫秒数转化为秒
        //this.time = shengyuD+"天"+shengyuH+"小时"+shengyuM+"分"+S+"秒"
        // setTimeout("clock()",500);
        // setTimeout(this.clock,500);
      },
      ToQuestionList(examid,examName,sessionCode) {
        this.$router.push({ name: 'exammanage-comment', params: {examid: examid,examName:examName,sessionCode:sessionCode}})
      },
      toSubmitList (examid,examName,sessionCode) {
        this.$router.push({ name: 'exam-submit', params: {examid: examid,examName:examName,sessionCode:sessionCode}})
      },
      toScoreList (examid,examName,sessionCode) {
        this.$router.push({ name: 'exam-score', params: {examid: examid,examName:examName,sessionCode:sessionCode}})
      },
      // toAnalysis (examid) {
      //   this.$router.push({ name: 'Analysis', params: {examid: examid}})
      // },
      toTotalScore (examName) {
        console.log("欢迎进入"+examName+"试题库")
        this.$router.push({ name: 'exam-totalscore', params: {examName: examName} })
      },
      notOpen () {
          this.$warning({
            title: '成绩分析还没做好，敬请期待！'
          });
      },
      handleCancel(e) {
        this.visible = false
      },
    }
  }
</script>

<style lang="scss" scoped>

  .data-group {

    .card-content {
      display: block;
      position: relative;
    }
    .card-title {
      align-items: center;
      display: flex;
      flex-wrap: wrap;
    }
    .card-text {
      width: 100%;
      padding: 0px;
    }
    .layouts {
      flex-direction: row;
      display: flex;
      flex: 1 1 auto;
      flex-wrap: nowrap;
      min-width: 0;
      align-items: center;
      .title {
        display: flex;
        align-items: flex-start;
        white-space: nowrap;
        font-size: 1.47em;
        //font-size: 28px;
        font-weight: 1000;
        line-height: 1;
        letter-spacing: .03em!important;
        .access-level {
          font-size: 10px;
          font-weight: 500;
          text-transform: uppercase;
          margin-left: 8px!important;
          color: #9e9e9e;
          caret-color: #9e9e9e!important;
        }
      }

      .tag {
        display: flex;
        flex: 1 1 auto;
        flex-wrap: wrap;
        min-width: 0;
        justify-content: flex-end;
      }
      .a-form-item {
        margin-right: 40px;
      }
    }
    .description {
      width: 100%;
      margin-top: 20px;

      p {
        //font-size: 16px;
        font-size: 0.6em;
        margin-bottom: 0px;
      }
      ul li {
        font-size: 14px;
        color: #9e9e9d;
      }
    }
    .card-data-icon {
      float: left;
      font-size: 24px;
      color: #A4D3EE;
      margin-right: 12px;
      font-family: var(--font-primary)!important;
    }

    .card-data-col{
      margin-bottom: 32px;
    }
    .card-data {
      height: 108px;
      cursor: pointer;
      font-size: 12px;
      position: relative;
      overflow: hidden;
      color: #666;
      background: #fff;
      box-shadow: 4px 4px 40px rgba(0, 0, 0, .05);
      border-color: rgba(0, 0, 0, .05);
      &:hover {
        .card-data-icon-wrapper {
          color: #fff;
        }
        .icon-doc {
          background: #A4D3EE;
        }
        .a-icon-arrow-right {
          color: #A4D3EE;
        }
      }
      .icon-doc {
        color: #A4D3EE;
      }
      .card-data-icon-wrapper {
        float: left;
        margin: 14px 0 0 14px;
        padding: 16px;
        transition: all 0.38s ease-out;
        border-radius: 6px;
      }
    }
  }
  .ant-form label {
    line-height: 20px!important;
    padding-bottom: 0px!important;
    color: #9e9e9d;
    font-size: 15px;
  }
  .ant-form-item {
    color: #555555;
    font-size: 15px;
  }
</style>