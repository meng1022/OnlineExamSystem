<template>
  <div class="question-list" ref="content">
    <a-card style="text-align: center">
        <a-button type="primary" @click="handleQuestion(examid,sessionName,examName,sessionCode)">返回</a-button>
    </a-card>
    <a-list
      :grid="{gutter: 24}"
      :dataSource="dataSource"
    >
      <a-list-item class="new-list-item" slot="renderItem" slot-scope="item, index">
          <a-card>
            <p style="margin-bottom: 3px;font-weight: 600;" v-html="item.bigDescribe"></p>
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
                <a-textarea :autosize="{ minRows: 2, maxRows: 6 }" placeholder="解答"  v-model="questionitem.value" />
              </div>
            </a-card>
          </a-list-item>
        </a-list>

      </a-list-item>
    </a-list>

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
        console.log("预览！")
        let self = this
        // //初始化数据
        // this.countdown_value = ''
        // this.timediff = 0
        // this.affixVisible = false
        // this.dataSource = []
        this.examid = this.$route.params.examid
        this.examName = this.$route.params.examName
        this.sessionName = this.$route.params.sessionName
        this.sessionCode = this.$route.params.sessionCode
        console.log(this.$route.params)
        if(window.localStorage.getItem('result'+this.examid)!=null){
          this.result = JSON.parse(window.localStorage.getItem('result'+this.examid))
        } else {
          this.$router.push({ name: 'dashboard-exercise' })
          return
        }
        var data = this.result.data;//所有的counter
        if(!data){
          return;
        }
        var bigIndex = 0//大题序号
        for(var i=0;i<data.length;i++){
          var smallIndex = 0;  //小题序号
          for(var j=0;j<data[i].paperList.length;j++){
            var question = data[i].paperList[j];//小题
            question.smallIndex = j;
            question.bigIndex = i;
            data[i].paperList[j] = question
          }
        }
        console.log("所有题目：")
        console.log(data)
        self.dataSource = data
        // this.selectchange()
      },
      // renderItem (item, index) {
      //   console.log(item,index)
      // },
      // selectchange () {
      //   let self = this
      //   this.finish_count = 0
      //   this.dataSource.forEach(function(item) {
      //     if (item.value && item.value.length != 0) {
      //       self.finish_count += 1
      //     }
      //   })
      // },
      handleQuestion: function(examId,sessionName,examName,sessionCode){
        this.$router.push({ name: 'exammanage-question1', params: {examid:examId,counter:this.dataSource,sessionName:sessionName,examName:examName,sessionCode:sessionCode}})
      },
      // affixChange (affix) {
      //   this.affixVisible = affix
      // },
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

