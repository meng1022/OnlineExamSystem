<template>
  <div class="page-header-index-wide">
    <a-row class="data-group" v-for="item, i in listData">
      <a-card class="box-card" style="margin-bottom: 20px;">
        <div>
          <span style="color: #9e9e9e">{{item.examName}}：{{item.sessionCode}}</span>
        </div>
        <a-row>
          <div style="margin-top:8px;border-top: 1px solid #EAEAEA;padding-top: 10px;"></div>
        </a-row>
        <div class="card-content">
          <a-row type="flex" align="bottom" style="height: 80px;">
            <a-col :span='20'>
              <div class="card-title">
                <div class="layouts">
                  <div class="title">
                    <span>{{item.sessionName}}</span>
                  </div>
                  <div class="tag">
                  </div>
                </div>
              </div>
              <div class="description" style="width: 100%">
                <a-row>
                  <p>考试时间：{{item.examStartTime | formatDate}} ─ {{item.examEndTime | formatDate }}</p>
                </a-row>
              </div>
            </a-col>
            <a-col :span='4'>
              <div class="card-action">
                <a-button :loading="item.loading" :disabled="item.timeFlag != 1" type="primary" style=" float: right;margin-right: 1.9em;margin-top: 1.4em" @click="ToQuestionList(item.id,item.examEndTime,item.verificationCode,i,item.examDuration,item.sessionName)">{{item.text}}</a-button>
              </div>
            </a-col>
          </a-row>
        </div>
      </a-card>
    </a-row>
    <a-modal
      title="本场考试验证码"
      :visible="visible"
      @ok="handleOk"
      :confirmLoading="confirmLoading"
      @cancel="handleCancel"
    >
      请输入验证码：<a-input v-model="code"></a-input>
    </a-modal>
  </div>
</template>

<script>
  import { getExamList, getQuestionList,setStartAnswerTime} from '@/api/api'
  import moment from 'moment'
  export default {
    name: "Prictice",
    components: {
    },
    data() {
      return {
        timeDate:new Date(),
        loading: true,
        center: null,
        index: null,
        listData: [],
        alertFlag: 0,
        timeFlag: 0,
        visible: false,
        confirmLoading: false,
        code: '',
        listCode: '',
        listId: '',
        listEndTime: '',
        setTimeArr: [],
        times:'',
        closeTime:'',
        diff: 0,
        timediff: 0,
        examDuration:10,
        sessionName:null
      }
    },
    beforeRouteLeave(to, form, next) {
      for (let i in this.setTimeArr) {
        clearTimeout(this.setTimeArr[i])
        clearInterval(this.setTimeArr[i])
      }
      this.setTimeArr = []
      next()
    },
    mounted() {
      document.addEventListener('visibilitychange',this.diffTime)
    },
    beforeDestroy(){
      document.removeEventListener('visibilitychange', this.diffTime)
    },
    created() {
      if (this.$store.state.diff != null) {
        this.diff = this.$store.state.diff
      } else {
        this.$store.state.diff = parseInt(this.$isInfield ? 0 : (Math.random()*120 - 60))//-60 ~ +60
        this.diff = this.$store.state.diff
      }
      this.initData()
      setTimeout(() => {
        this.loading = !this.loading
      }, 1000)
    },
    filters:{
      formatDate:function (val) {
        return moment(val).format('MM/DD HH:mm')
      }
    },
    watch :{
      '$route': function (to, from) {
        if (to.name == 'dashboard-exercise') {
          this.initData()
        }
      }
    },
    methods: {
      initData () {
        let self = this
        getExamList(null).then((res)=>{
            if(res.success){
              self.listData = res.result.records//listData是一堆考试信息
              console.log("this.moment："+this.$moment())
              for(var i=0;i<self.listData.length;){
                console.log(self.listData.length)
                var end = new Date(self.listData[i].examEndTime.toString())
                console.log("exam.endTime："+end.getTime())
                if(this.$moment()>end.getTime()){
                  self.listData.splice(i,1)
                }else{
                  i++
                }
              }
              for (let i in self.listData) {
                self.$set(self.listData[i],'timeFlag',0)
                self.$set(self.listData[i],'text','进入')
                self.$set(self.listData[i],'count',null)
                self.$set(self.listData[i],'questionList',[])
                self.$set(self.listData[i],'loading',false)

              }
              self.clock(res.timestamp)
            } else {
              self.$error({
                title: res.message,
                okText: '重新获取',
                onOk () {
                  self.initData()
                }
              })
            }
        }).catch(function(){
            self.$error({
              title: '系统繁忙，请稍后重试!',
              okText: '刷新',
              onOk () {
                self.initData()
              }
            })
        })
      },
      diffTime() {
        if (document.visibilityState =='visible') {
          this.initData()
        }
      },
      countdown (list, time) {
        let self = this
        //如果此计时器正在运行，则停止它。
        if (list.count != null) {
          clearInterval(list.count)
          list.count = null
        }
        //启动计时器
        self.setTimeArr.push(list.count = setInterval(function(){
          time=time-1;
          var minute=parseInt(time/60);
          var second=parseInt(time%60);
          if (second < 10) {
            second = '0' + second
          }
          if (minute <= 19 && minute >= 0) {
            list.text = minute + ': ' + second
          }
          if (time <= 0) {
            clearInterval(list.count);
            list.timeFlag = 1
            list.text = '进入'
          }
        },1000));
      },
      clock(timestamp){
        let self = this
        //重新计算前先清除之前所有的计时器
        for (let i in this.setTimeArr) {
          clearTimeout(this.setTimeArr[i])
        }
        this.setTimeArr = []

        var now = this.$moment()
        var today=this.$moment(timestamp)
        this.timediff = now.diff(today)
        for (let i in self.listData) {
          var startTime = this.$moment(self.listData[i].examStartTime)
          var stopTime = this.$moment(self.listData[i].examEndTime)
          var shengyu = startTime.diff(today)//距离考试开始的时间
          var duration = startTime.diff(stopTime)//考试时长examStartTime-examEndTime
          var one_day = 24 * 60 * 60 * 1000;
          var l = shengyu - duration//距离结束的时间
          if (shengyu >= one_day) {//距考试开始还有一天以上
            self.setTimeArr.push(setTimeout(function() {
                         window.location.reload();
                        }, one_day - 20*60*1000));
          } else if (shengyu < one_day && shengyu > 0 && l >= one_day) {  //距考试开始在一天以内，距考试结束仍有一天以上
            self.listData[i].timeFlag = 0
            self.countdown(self.listData[i],startTime.diff(today,'seconds') + self.diff)
            self.setTimeArr.push(setTimeout(function() {
                         window.location.reload();
                        }, one_day));

          } else if (shengyu < one_day && shengyu > 0 && l < one_day) {//距考试开始在一天以内，且距考试结束也在一天以内
            self.listData[i].timeFlag = 0
            self.countdown(self.listData[i],startTime.diff(today,'seconds') + self.diff)
            self.setTimeArr.push(setTimeout(() => {self.listData[i].timeFlag = 2},l))
          } else if (shengyu <= 0 && shengyu >= duration && l >= one_day){//考试已开始但未结束，且距结束时间超过一天
            self.listData[i].timeFlag = 1
            self.setTimeArr.push(setTimeout(function() {
                         window.location.reload();
                        }, one_day));
          } else if (shengyu <= 0 && shengyu >= duration && l < one_day) {//考试已开始但未结束，且距结束时间再一天以内
            self.listData[i].timeFlag = 1
            self.setTimeArr.push(setTimeout(() => {self.listData[i].timeFlag = 2},l))
          } else {
            self.listData[i].timeFlag = 2
          }

          //console.log("practice.vue,shengyu:"+shengyu)
          //距离考试开始5分钟内，随机一个时间获取考试列表
          if (shengyu < one_day && shengyu > 5*60*1000) {
            //getQsetbyExamId
            self.setTimeArr.push(setTimeout(() => {getQuestionList(self.listData[i].id).then((res) =>{
              if (res.success) {
                self.listData[i].questionList = res.result//.qset//res
              }
            })},shengyu - (Math.random()*(5*60*1000))))
          } else if (shengyu <= 5*60*1000 && shengyu > 0) {
            self.setTimeArr.push(setTimeout(() => {getQuestionList(self.listData[i].id).then((res) =>{
              if (res.success) {
                self.listData[i].questionList = res.result//.qset//res
              }
            })},Math.random()*shengyu))
          }
        }
      },
      //item.id,item.examEndTime,item.verificationCode,i,item.examDuration,item.sessionName
      ToQuestionList(id,examEndTime,code,j,examDuration,sessionName) {
        let self = this
        if(self.listData){
          console.log("practice.vue,listData:")
          console.log(self.listData)
        }
        if (code) {//需要验证码验证码
          this.visible = true
          this.code = ''
          this.listId = id//examid
          this.listEndTime = examEndTime
          this.listCode = code
          this.index = j//exam-index
          this.examDuration = examDuration
          this.sessionName = sessionName
        } else if (self.listData[j].questionList != "") {
          console.log("不需要验证码+考试开始前就停在首页")
          console.log("Practice.vue:listData["+j+"].questionlist:")
          console.log(self.listData[j].questionList)
          //获取当前时间戳毫秒
          let now = this.$moment().valueOf()
          //self.listData[j].questionList.timestamp = now + this.timediff
          // if(self.listData[j].questionList.result.startAnswerTime){
          //   this.$router.push({ name: 'questionlist',
          //     params:{
          //       diff:self.diff,
          //       subid: id,
          //       end: examEndTime,
          //       result: self.listData[j].questionList,
          //       examDuration:examDuration,
          //       sessionName:sessionName,
          //       startAnswerTime:self.listData[j].questionList.result.startAnswerTime,
          //   }
          //   })
          // }else{
           // var startAnswerTime = self.$moment().format('YYYY-MM-DD HH:mm')
            var data = {examId: id }
            setStartAnswerTime(data).then((res1) => {
              self.$router.push({
                name: 'questionlist',
                params: {
                  diff: self.diff,
                  subid: id,
                  end: examEndTime,
                  result: self.listData[j].questionList,
                  examDuration: examDuration,
                  sessionName: sessionName,
                  startAnswerTime: res1.result.startAnswerTime//startAnswerTime
                }
              })
            })
          //}
        } else {
          console.log("考试中途才点击进入")
          self.listData[j].loading = true
          self.listData[j].text = '加载中…'
          getQuestionList(id).then((res)=>{
            if (res.success) {
            //   if(res.result.startAnswerTime){
            //   self.$router.push({ name: 'questionlist',
            //                       params:{
            //                         diff:self.diff,
            //                         subid: id,
            //                         end: examEndTime,
            //                         result: res.result.qset,
            //                         examDuration:examDuration,
            //                         sessionName:sessionName,
            //                         startAnswerTime:res.result.startAnswerTime}//budui
            //   })
            // } else {
                var startAnswerTime = self.$moment().format('YYYY-MM-DD HH:mm')
                var data = { examId: id }
                setStartAnswerTime(data).then((res1) => {
                  self.$router.push({
                    name: 'questionlist',
                    params: {
                      diff: self.diff,
                      subid: id,
                      end: examEndTime,
                      result: res.result,
                      examDuration: examDuration,
                      sessionName: sessionName,
                      startAnswerTime: res1.result.startAnswerTime//startAnswerTime
                    }
                  })
                })
              //}
            }else{
              self.$error({
                title: res.message
              })
            }
            self.listData[j].loading = false
            self.listData[j].text = '进入'
          }).catch(function(){
            self.$error({
              title: '系统繁忙，请稍后重试！'
            })
            self.listData[j].loading = false
            self.listData[j].text = '进入'
          })
        }
      },
      /*
      notOpen (i) {
        if (i == 1) {
          this.$warning({
            title: '考试尚未开始！'
          });
        } else {
          this.$warning({
            title: '考试已经结束！'
          });
        }
      },*/
      handleOk (e) {
        let self = this
        if (this.code == this.listCode) {
          if (self.listData[self.index].questionList != '') {
            //获取当前时间戳毫秒
            console.log("需要验证码+考试开始前就停在首页")
            console.log("Practice.vue:listData["+self.index+"].questionlist:")
            console.log(self.listData[self.index].questionList)
            let now = this.$moment().valueOf()
           //  self.listData[self.index].questionList.timestamp = now + this.timediff
           //  if(self.listData[self.index].questionList.result.startAnswerTime){
           //    this.$router.push({ name: 'questionlist',
           //                      params:{
           //                        diff:self.diff,
           //                        subid: self.listId,//examid
           //                        end: self.listEndTime,
           //                        result: self.listData[self.index].questionList,
           //                        examDuration:self.examDuration,
           //                        sessionName:self.sessionName,
           //                        startAnswerTime:self.listData[self.index].questionList.result.startAnswerTime} })
           //  }
           // else {
           // var startAnswerTime=self.$moment().format('YYYY-MM-DD HH:mm')
            var data = { examId: self.listId }
            setStartAnswerTime(data).then((res1) => {
              if (res1.success) {
                this.$router.push({
                  name: 'questionlist',
                  params: {
                    diff: self.diff,
                    subid: self.listId,
                    end: self.listEndTime,
                    result: self.listData[self.index].questionList,
                    examDuration: self.examDuration,
                    sessionName: self.sessionName,
                    startAnswerTime: res1.result.startAnswerTime
                  }
                })
              }else{
                self.$error({
                  title: res1.message
                })
              }
            })
            // self.listData[self.index].loading = true
            // self.listData[self.index].text = '加载中'
            // getQuestionList(this.listId).then((res)=>{
            //   if (res.success) {
            //     self.$router.push({ name: 'questionlist', params:{diff:self.diff,subid: self.listId,end: self.listEndTime,result: res }})
            //   } else {
            //     self.$error({
            //       title: res.message
            //     })
            //   }
            //   self.listData[self.index].loading = false
            //   self.listData[self.index].text = '进入'
            // }).catch(function(){
            //   self.$error({
            //     title: '系统繁忙，请稍后重试！'
            //   })
            //   self.listData[self.index].loading = false
            //   self.listData[self.index].text = '进入'
            // })
            //
          } else{
            console.log("考试中途才点击进入")
            self.listData[self.index].loading = true
            self.listData[self.index].text = '加载中…'
            getQuestionList(self.listId).then((res)=>{
              if (res.success) {
                var data = { examId: self.listId }
                setStartAnswerTime(data).then((res1) => {
                  self.$router.push({
                    name: 'questionlist',
                    params: {
                      diff: self.diff,
                      subid: self.listId,
                      end: self.listEndTime,
                      result: res.result,
                      examDuration: self.examDuration,
                      sessionName: self.sessionName,
                      startAnswerTime: res1.result.startAnswerTime
                    }
                  })
                })
              }else{
                self.$error({
                  title: res.message
                })
              }
              self.listData[self.index].loading = false
              self.listData[self.index].text = '进入'
            }).catch(function(){
              self.$error({
                title: '系统繁忙，请稍后重试！'
              })
              self.listData[self.index].loading = false
              self.listData[self.index].text = '进入'
            })
          }
          this.visible = false
        }
        else {
          this.$error({
            title: '验证码不正确！',
          });
          this.code = ''
          this.visible = false
        }
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