<template>
  <div class="question-list" ref="content">
    <a-affix :offsetTop="10" :style="{ position: 'absolute', top: '122px', right: '12px', width: '100px'}" @change="affixChange">
      <a-card v-show="affixVisible" style="width: 120px;z-index: 2000;filter:alpha(Opacity=80);-moz-opacity:0.8;opacity: 0.8;line-height: 10px;text-align: center;">
        <p>{{finish_count}}/{{total_count}}</p>
        <p><span :class="{aevasive1:isevasive}">{{countdown_value}}</span></p>
        <a-button size="small" type="primary" @click="submitQuestion">交卷</a-button>
      </a-card>
    </a-affix>
    <a-card style="text-align: center">
      <span style="font-size: 18px;font-weight: 600;margin-right: 25px;">答题进度：{{finish_count}}/{{total_count}}</span>
      <span v-if="countdown_value" style="font-size: 18px;font-weight: 600;margin-right: 25px;">
        考试倒计时：<span :class="{aevasive:isevasive}">{{countdown_value}}</span>
      </span>
      <a-button type="primary" @click="submitQuestion">交卷</a-button>
    </a-card>

    <a-list
      :grid="{gutter: 24}"
      :dataSource="dataSource"
    >
      <a-list-item class="new-list-item" slot="renderItem" slot-scope="item, index">
        <a-card>
          <p style="margin-bottom: 3px;font-weight: 600;" v-html="item.getBigDescribe"></p>
        </a-card>
        <a-list :grid="{gutter: 24, lg: 1, md: 1, sm: 1, xs: 1}" :dataSource="item.paperList">
          <a-list-item slot="renderItem" slot-scope="questionitem,smallindex">
            <a-card>
              <p>{{questionitem.smallIndex+1}}、<span v-html="questionitem.content"></span></p>
              <div v-if="questionitem.optionA!=null">
                <a-checkbox-group size="large"  @change="selectchange" v-model="questionitem.value" v-if="questionitem.type=='multi'">
                  <a-checkbox :style="radioStyle" v-if="questionitem.optionA!=null" value="A"><span v-html="questionitem.optionA"></span><div v-if="questionitem.imgUrlA!=null"><br><img :src="questionitem.imgUrlA"/></div></a-checkbox>
                  <a-checkbox :style="radioStyle" v-if="questionitem.optionB!=null" value="B"><span v-html="questionitem.optionB"></span><div v-if="questionitem.imgUrlB!=null"><br><img :src="questionitem.imgUrlB"/></div></a-checkbox>
                  <a-checkbox :style="radioStyle" v-if="questionitem.optionC!=null" value="C"><span v-html="questionitem.optionC"></span><div v-if="questionitem.imgUrlC!=null"><br><img :src="questionitem.imgUrlC"/></div></a-checkbox>
                  <a-checkbox :style="radioStyle" v-if="questionitem.optionD!=null" value="D"><span v-html="questionitem.optionD"></span><div v-if="questionitem.imgUrlD!=null"><br><img :src="questionitem.imgUrlD"/></div></a-checkbox>
                  <a-checkbox :style="radioStyle" v-if="questionitem.optionE!=null" value="E"><span v-html="questionitem.optionE"></span><div v-if="questionitem.imgUrlE!=null"><br><img :src="questionitem.imgUrlE"/></div></a-checkbox>
                </a-checkbox-group>
                <a-radio-group size="large"  @change="selectchange" v-model="questionitem.value" v-else>
                  <a-radio :style="radioStyle" v-if="questionitem.optionA!=null" value="A"><span v-html="questionitem.optionA"></span><div v-if="questionitem.imgUrlA!=null"><br><img :src="questionitem.imgUrlA"/></div></a-radio>
                  <a-radio :style="radioStyle" v-if="questionitem.optionB!=null" value="B"><span v-html="questionitem.optionB"></span><div v-if="questionitem.imgUrlB!=null"><br><img :src="questionitem.imgUrlB"/></div></a-radio>
                  <a-radio :style="radioStyle" v-if="questionitem.optionC!=null" value="C"><span v-html="questionitem.optionC"></span><div v-if="questionitem.imgUrlC!=null"><br><img :src="questionitem.imgUrlC"/></div></a-radio>
                  <a-radio :style="radioStyle" v-if="questionitem.optionD!=null" value="D"><span v-html="questionitem.optionD"></span><div v-if="questionitem.imgUrlD!=null"><br><img :src="questionitem.imgUrlD"/></div></a-radio>
                  <a-radio :style="radioStyle" v-if="questionitem.optionE!=null" value="E"><span v-html="questionitem.optionE"></span><div v-if="questionitem.imgUrlE!=null"><br><img :src="questionitem.imgUrlE"/></div></a-radio>
                </a-radio-group>
              </div>
              <div v-if="questionitem.type=='quest'">
                <a-textarea :autosize="{ minRows: 2, maxRows: 6 }" placeholder="解答"  @change="selectchange" v-model="questionitem.value" />
              </div>
            </a-card>
          </a-list-item>
        </a-list>
      </a-list-item>
    </a-list>

    <a-card style="text-align: center">
      <span style="font-size: 18px;font-weight: 600;margin-right: 25px;">答题进度：{{finish_count}}/{{total_count}}</span>
      <span v-if="countdown_value" style="font-size: 18px;font-weight: 600;margin-right: 25px;">考试倒计时：
        <span :class="{aevasive:isevasive}">{{countdown_value}}</span>
      </span>
      <a-button type="primary" @click="submitQuestion">交卷</a-button>
    </a-card>
    <a-modal
      title=""
      v-model="modalvisible" 
      :footer="null"
      :maskClosable="false"
      :closable="false"
      width="300px"
    >
    <div style="margin: 10px">
      <a-spin tip="正在交卷..." size="small">
        <div class="spin-content">
        </div>
      </a-spin>
    </div>
    </a-modal>
    <div>
      <a-back-top />
    </div>
  </div>
</template>



<script>
  import {postQuestionList, postCacheQuestionList, getCacheQuestionList} from '@/api/api'
  export default {
    name: "QuestionList",
    components: {
    },
    data() {
      return {
        loading: true,
        affixVisible: false,
        modalvisible: false,
        center: null,
        subid: '1',
        total_count: 0,
        finish_count: 0,
        countdown_value: '',
        dataSource:[],
        isevasive:false,
        count: null,
        submit: null,
        username: "",
        hashcode: "",
        endTime: '',
        result: {},
        radioStyle: {
          display: 'block',
          marginBottom: '20px',
          whiteSpace: 'normal'
        },
        timediff: 0,
        jieshu:null,
        startTime:null,
        startAnswerTime:null,
      }
    },
    created() {
      setTimeout(() => {
        this.loading = !this.loading
      }, 1000)
      this.initData()
    },
    mounted() {
        document.addEventListener('visibilitychange',this.diffTime)
    },
    beforeDestroy(){
        document.removeEventListener('visibilitychange', this.diffTime)
    },
    beforeRouteLeave(to, form, next) {
      clearInterval(this.count)
      this.count = null
      clearInterval(this.submit)
      this.submit = null
      next()
    },
    watch :{
      '$route': function (to, from) {
        if (to.name == 'questionlist') {
          this.initData()
        }
      }
    },
    methods: {
      initData () {
        let self = this
        //初始化数据
        this.countdown_value = ''
        this.timediff = 0
        this.affixVisible = false
        this.dataSource = []
        this.username = JSON.parse(window.localStorage.pro__Login_Username).value
        this.subid = this.$route.params.subid;
        this.startTime =  (new Date()).getTime();
        this.startAnswerTime = this.$moment(this.$route.params.startAnswerTime)//从首页传入的参数

        // console.log("this.$route.params：")
        // console.log(this.$route.params)
        if (this.$route.params.hasOwnProperty('result')) {
          this.result = this.$route.params.result
        } else {
          this.$router.push({name: 'dashboard-exercise'})
          return
        }
        var data =JSON.parse(self.result.qset)
        data = self.sortindex(data)
        let name = this.username + '_' + this.subid + '_' + this.result.hashcode + '_result'
        //如果localstorage中有此次考试的缓存则本地获取，如果没有则远程获取。
        if (window.localStorage.getItem(name)) {
          console.log("localstorage中有此次考试的缓存🐧")
          var data = JSON.parse(window.localStorage.getItem(name))
          data = self.sortindex(data)
          self.dataSource = data
          self.selectchange()
          self.setTotalCount()
          self.SubmitResult()
        } else if (this.result.servercache) {
          console.log("场内考生获取Cache服务器缓存🐧")
          getCacheQuestionList(self.subid).then((res)=>{
            if (res.success) {
              console.log("考试启用缓存，且获取到缓存结果🐧")
              var data = JSON.parse(res.result.result.qset)//res?self?
              data = self.sortindex(data)
              self.dataSource = data
              self.selectchange()
              self.setTotalCount()
              self.SubmitResult()
            } else {
              //若服务器无缓存结果则获取试题
              console.log("无缓存 获取Qset试题🐧")
              var data = JSON.parse(self.result.qset)
              self.dataSource = self.sortindex(data)
              self.selectchange()
              self.setTotalCount()
              self.SubmitResult()
            }
          }).catch(function(e){
            self.$confirm({
              title: '服务器缓存获取失败，请检查网络！',
              okText: '重新获取缓存',
              cancelText: '获取新试题',
              onOk () {
                self.initData()
              },
              onCancel () {
                //若服务器无缓存结果则获取试题
                console.log("无缓存 获取Qset试题🐧")
                var data = JSON.parse(self.result.qset)
                data = self.sortindex(data)
                self.dataSource = data
                self.selectchange()
                self.setTotalCount()
                self.SubmitResult()
              }
            })
          })
        } else {
          //场外考生本地无缓存则直接获取试题
          console.log("考试未启用缓存 直接获取试题🐧")
          self.dataSource = data
          self.selectchange()
          self.setTotalCount()
          self.SubmitResult()
        }
        //根据考试结束时间开启计时器
        this.endTime = this.$route.params.end
        let now = this.$moment()
        let today = this.$moment(this.$route.params.result.timestamp)
        let diff = this.$route.params.diff //场外考生获得一个正负60秒之内的随机差值
        this.timediff = now.diff(today,'second') + diff
        let second = this.$moment(this.endTime).diff(today,'second') + diff
        this.jieshu = this.$moment(this.startAnswerTime).add(this.$route.params.examDuration, 'minutes')
        let dura=this.$moment(this.jieshu).diff(today,'second') + diff
        if(second>dura){
          second = dura
        }
        this.countdown(second)//倒计时
      },
      sortindex(data){
        var bigIndex = 0
        for(var i=0;i<data.length;i++){
          var smallIndex = 0;
          for(var j=0;j<data[i].paperList.length;j++){
            var question = data[i].paperList[j];//小题
            question.smallIndex = j;
            question.bigIndex = i;
            data[i].paperList[j] = question
          }
        }
        // console.log("所有题目：")
        // console.log(data)
        return data
      },
      diffTime() {
        if (document.visibilityState =='visible') {
          let now = this.$moment()
          let second = this.$moment(this.endTime).diff(now,'second') + this.timediff
          let dura=this.$moment(this.jieshu).diff(now,'second') + this.timediff
          if(second>dura){
            second = dura
          }
          this.countdown(second)
        }
      },
      SubmitResult () {
        let self = this
        //如果此计时器正在运行，则停止它。
        if (this.submit != null) {
          clearInterval(this.submit)
          this.submit = null
        }
        console.log(self.result)
        console.log("self.result.servercache:"+self.result.servercache)
        //启动周期性提交结果
        let data = {examId:self.subid,startTime:this.startTime,result: JSON.stringify(self.dataSource)}
        this.submit = setInterval(function () {
          let now = self.$moment()
          let second = self.$moment(self.endTime).diff(now,'second') + self.timediff
          let dura=self.$moment(self.jieshu).diff(now,'second') + self.timediff
          if(second>dura){
            second = dura
          }
          self.countdown(second)
          if (self.result.servercache) {
            postCacheQuestionList(JSON.stringify(data)).then((res)=>{
              if (res.success) {
                console.log('周期性提交成功 success！')
              }
            })
          }
        },parseInt(Math.random()*(1000*60*2+1)+1000*60*3,10))
      },
      setTotalCount () {//总题数
        let self = this
        self.total_count = 0
        this.dataSource.forEach(function(dati) {
          dati.paperList.forEach(function(xiaoti){
            self.total_count +=  1

          })
        })
      },
      renderItem (item, index) {
        console.log(item,index)
      },
      countdown (time) {
        let self = this
        //如果此计时器正在运行，则停止它。
        if (this.count != null) {
          clearInterval(this.count)
          this.count = null
        }
        //启动计时器
        this.count = setInterval(function(){
          time=time-1;
          var minute=parseInt(time/60);
          var second=parseInt(time%60);
          if (second < 10) {
            second = '0' + second
          }
          if (minute <= 59 && minute >= 0) {
            self.countdown_value = minute + ': ' + second
          }
          if (time <= 0) {
            clearInterval(self.count);
            self.questionPost()
          }
          if(minute <= 9 && !self.isevasive){
            self.isevasive=true;
          }
        },1000);
      },
      selectchange (val) {
        let self = this
        this.finish_count = 0//已经回答的题目
        this.dataSource.forEach(function(dati) {
          dati.paperList.forEach(function(xiaoti){
            if(xiaoti.value&&xiaoti.value.length!=0){
              self.finish_count ++//已经作答
            }
          })
        })

        let name = this.username + '_' + this.subid + '_' + this.result.hashcode + '_result'
        window.localStorage.setItem(name,JSON.stringify(this.dataSource))
        //答题缓存
      },
      affixChange (affix) {
        this.affixVisible = affix
      },
      submitQuestion () {
        let self = this
        let now = this.$moment()
        //let dura =self.$moment(self.startAnswerTime).diff(now,'second') + self.timediff - self.params.diff

        if (this.finish_count != this.total_count) {
          // let str = new Array
          // self.dataSource.forEach(function(dati){
          //   dati.paperList.forEach(function(xiaoti){
          //     if(xiaoti.no&&(xiaoti.value==''||xiaoti.value==undefined)){
          //       str += ' '+xiaoti.bigIndex+'('+ xiaoti.smallIndex +')'
          //     }
          //   })
          // })
          this.$confirm({
            title: '还有题目未完成，请确认是否交卷？',
            // content: str,
            // cancelText: '确定',
            // okText: '取消',
            onOk () {
              self.questionPost()
            }
          });
        }else{
          self.$confirm({
            title: '请确认是否交卷',
            onOk () {
              self.questionPost()
            }
          });
        }
      },
      questionPost () {
        clearInterval(this.submit)
        console.log("已停止周期性提交答题结果到scache")
        console.log("考生交卷submit")
        let self = this
        self.modalvisible = true
        let result = []
        self.dataSource.forEach(function(dati) {
          dati.paperList.forEach(function(xiaoti){
            if(xiaoti.no){
              console.log("no："+xiaoti.no+"value："+xiaoti.value)
              result.push({no:xiaoti.no,value:xiaoti.value})
            }
          })
        })
        let data = {examId:self.subid,result:JSON.stringify(result)}
        postQuestionList(JSON.stringify(data)).then((res)=>{
          if (res.success) {
            self.modalvisible = false
            self.$success({
              title: '交卷成功！'
            })
            let name = this.username + '_' + this.subid + '_' + this.result.hashcode + '_result'
            //let name = this.username + '_' + this.subid + '_result'
            console.log("删除本地localstorage")
            window.localStorage.removeItem(name)
            this.$router.push({ path: '/dashboard/exercise'})
          } else {
            self.modalvisible = false
            this.$error({
              title: res.message,
              okText: '重新交卷',
              cancelText:'取消',
              onOk () {
                self.questionPost()
              }
            })
          }
        }).catch(function(){
          self.modalvisible = false
          self.$error({
            title: '交卷失败，请检查网络！',
            okText: '重新交卷',
            cancelText:'取消',
            onOk () {
              self.questionPost()
            }
          })
        })
      }
    }
  }

</script>

<style lang="scss">
  .aevasive{
    font-size: 24px;
    color: red;
  }
  .aevasive1{
    font-size: 20px;
    color: red;
  }
  .question-list .ant-card {
    font-size: 15px;
    font-weight: 500;
  }
  .question-list .ant-list {
    font-size: 15px;
  }
  .question-list .card-avatar {
    width: 48px;
    height: 48px;
    border-radius: 48px;
  }
  .question-list .ant-list-item {
    margin-bottom: 0px;
  }
  .question-list .ant-card-actions {
    background: #f7f9fa;
    li {

      color: rgba(0, 0, 0, 0.45);
      width: 50%;

      &:not(:last-child) {
        border-right: 1px solid #e8e8e8;
      }

      a {
        color: rgba(0, 0, 0, .45);
        line-height: 22px;
        display: inline-block;
        width: 100%;
        &:hover {
          color: #1890ff;
        }
      }
    }
    li>:first-child {
      float: left;
      margin-left: 10px;
    }
    span {
    }
  }
  .question-list .ant-radio-wrapper {
    margin-left: 15px;
  }

  .question-list .ant-checkbox-wrapper {
    margin-left: 15px;
  }

  .question-list .new-btn {
    background-color: #fff;
    border-radius: 2px;
    width: 100%;
    height: 188px;
  }

  .question-list .meta-content {
    position: relative;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    // height: 64px;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
  }

  .question-list .ant-card-head-title {
    white-space: break-all;
  }

  /*场外引用此样式，场内不引用*/
  .question-list .ant-radio-wrapper-checked {
    font-weight: 700;
    color: #1890ff;
  }

  .question-list .ant-checkbox-wrapper-checked {
    font-weight: 700;
    color: #1890ff;
  }/**/
</style>

