<template>
  <div class="result-list" ref="content">
    <a-card>
      <div class="tablediv">
          <!--<table cellspacing="0" cellpadding="0">-->
          <table cellspacing="0" cellpadding="0" class="tablestyle">
          <caption style="margin-bottom: 10px;caption-side: top;text-align: center;color: rgba(0, 0, 0, 0.85)">{{scoreData.title}}</caption>
              <tbody>
                <tr v-for="n in scoreData.list">
                  <td style="width: 100px;">{{n.key}}</td>
                  <td>{{n.value}}</td>
                  <td v-if="n.type === 'exam'" style="width: 100px;">
                    <a v-if="!n.flag" @click="examAnswer(n)">详情</a>
                    <a v-else @click="answerRetract(n)">收起</a>
                  </td>
                  <td v-else-if="n.link" style="width: 100px;"><a v-bind="{href:n.link}" target="_blank">下载地址</a></td>
                  <td v-else style="width: 100px;"></td>
                </tr>
              </tbody>
          </table>
      </div>
    </a-card>
    <template v-if="questionVisible">
      <a-card style="text-align: center">
        <p style="font-weight: 600">{{exam}}</p> 
        <a-radio-group :value="orderBy" @change="orderByChange">
          <a-radio-button value="all">所有题目</a-radio-button>
          <a-radio-button value="error">只看错题</a-radio-button>
        </a-radio-group>
      </a-card>
      <a-list
        :grid="{gutter: 24}"
        :dataSource="dataSource"
      >
        <a-list-item class="new-list-item" slot="renderItem" slot-scope="item, index">
          <template v-if="item.type === 'desc'">
            <a-card>
              <p style="margin-bottom: 3px;font-weight: 600;">{{ item.title }}</p>
              <p>{{ item.content }}</p>
              <template v-if="item.hasOwnProperty('imgs') && (item.imgs.length > 0)">
                <a-divider orientation="left">相关图像</a-divider>
                <a-list :grid="{gutter: 24, lg: 1, md: 1, sm: 1, xs: 1}" :dataSource="item.imgs">
                  <a-list-item slot="renderItem" slot-scope="imgitem, imgindex">
                    <template>
                      <a-card>
                        <img
                          alt="example"
                          :src="imgitem.src"
                          slot="cover"
                        />
                        <a-card-meta>
                          <div style="margin-bottom: 3px" slot="title">{{ imgitem.title }}</div>
                        </a-card-meta>
                      </a-card>
                    </template>
                  </a-list-item>
                </a-list>
              </template>
            </a-card>
          </template>
          <template v-else-if="item.type === 'single'">
            <a-card>
              <p>{{ item.title }}</p>
              <p>（{{item.no}}）{{ item.content }}</p>
              <p v-for="i in item.options">{{i.desc}}</p>
            </a-card>
          </template>
          <template v-else-if="item.type === 'multi'">
            <a-card>
              <p>{{ item.title }}</p>
              <p>（{{item.no}}）{{ item.content }}</p>
              <template v-if="item.hasOwnProperty('imgs') && (item.imgs.length > 0)">
                <a-list :grid="{gutter: 24, lg: 1, md: 1, sm: 1, xs: 1}" :dataSource="item.imgs">
                  <a-list-item slot="renderItem" slot-scope="imgitem, imgindex">
                    <template>
                      <a-card>
                        <img
                          alt="example"
                          :src="imgitem.src"
                          slot="cover"
                        />
                        <a-card-meta>
                          <div style="margin-bottom: 3px" slot="title">{{ imgitem.title }}</div>
                        </a-card-meta>
                      </a-card>
                    </template>
                  </a-list-item>
                </a-list>
              </template>
              <p v-for="i in item.options">{{i.desc}}</p>
            </a-card>
          </template>
          <template v-else-if="item.type === 'img'">
            <a-card>
              <p>{{ item.title }}</p>
              <p>（{{item.no}}）{{ item.content }}</p>
              <a-list :grid="{gutter: 24, lg: 2, md: 1, sm: 1, xs: 1}" :dataSource="item.imgs">
                <a-list-item slot="renderItem" slot-scope="imgitem, imgindex">
                  <template>
                    <a-card>
                      <img
                        alt="example"
                        :src="imgitem.src"
                        slot="cover"
                      />
                      <a-card-meta>
                      <div style="margin-bottom: 3px" slot="title">{{ imgitem.title }}</div>
                      </a-card-meta>
                    </a-card>
                  </template>
                </a-list-item>
              </a-list>
              <a-divider orientation="left">选项</a-divider>
              <p v-for="i in item.options">{{i.desc}}</p>
            </a-card>
          </template>
          <template v-else>
            <a-card>
              <p>（{{ item.title }}</p>
              <p>{{item.no}}）{{ item.content }}</p>
              <p v-for="i in item.options">{{i.desc}}</p>
            </a-card>
          </template>
          <template v-if="item.type != 'desc' && item.type != 'multi'">
            <a-card>
              <p style="font-size: 15px;font-weight: 600;">标准答案：{{item.value}}</p>
              <p style="font-size: 15px;font-weight: 600;" v-if="item.value === item.answer">你的答案：{{item.answer}}</p>
              <p style="font-size: 15px;font-weight: 600;color: red" v-else="item.value === item.answer">你的答案：{{item.answer}}</p>
            </a-card>
          </template>
          <template v-if="item.type === 'multi'">
            <a-card>
              <p style="font-size: 15px;font-weight: 600;">标准答案：
                <span v-for="value in item.value">{{value}}</span></p>
              <p style="font-size: 15px;font-weight: 600;" v-if="item.value === item.answer">你的答案：
                <span v-for="answer in item.answer">{{answer}}</span></p>
              <p style="font-size: 15px;font-weight: 600;color: red" v-else>你的答案：
                <span v-for="answer in item.answer">{{answer}}</span></p>
            </a-card>
          </template>
        </a-list-item>
      </a-list>
    </template>
    <div>
      <a-back-top />
    </div>

  </div>



</template>

<script>

  import {getOwnQuestion,getOwnScore} from '@/api/api'
  export default {
    name: "QuestionList",
    components: {
    },
    data() {
      return {
        loading: true,
        affixVisible: false,
        questionVisible: false,
        center: null,
        examid: 'test',
        exam: '',
        subid: '1',
        total_count: 0,
        finish_count: 0,
        dataSource:[],
        dataSource1: [],
        username: "",
        endTime: '',
        orderBy: 'all',
        radioStyle: {
          display: 'block',
          marginBottom: '20px',
          whiteSpace: 'normal'
        },
        scoreData: {}
      }
    },
    created() {
      setTimeout(() => {
        this.loading = !this.loading
      }, 1000)
      this.initData()
    },
    watch :{
      '$route': function (to, from) {
        if (to.name == 'exammanage-comment') {
          this.initData()
        }
      },
      'orderBy': function (value) {
        if (value == 'all') {
          this.dataSource = JSON.parse(JSON.stringify(this.dataSource1))

        } else if (value == 'error') {
          let data = []
          this.dataSource.forEach(function(item){
            if (item.value != item.answer) {
              data.push(item)
            }
          })
          this.dataSource = data
        }
      }
    },
    methods: {
      initData () {
        let self = this
        getOwnScore('模拟练习').then(function(res){
          if (res.success) {
            self.scoreData = res.result
            self.scoreData.list.map(function(item){
            if (item.type == 'exam') {
              self.$set(item,'flag',false)
            }
          })
          }
        })
          
      },
      renderItem (item, index) {
        console.log(item,index)
      },
      orderByChange (e) {
        this.orderBy = e.target.value
      },
      sortKey(array,key){
        return array.sort(function(a,b){
         var x = a[key];
         var y = b[key];
         return ((x<y)?-1:(x>y)?1:0)
        })
      },
      examAnswer (n) {
        let self = this
        self.scoreData.list.forEach(function(item){
          if (item.examid) {
            item.flag = false
          }
        })
        n.flag = !n.flag
        self.questionVisible = true
        self.dataSource = []
        self.dataSource1 = []
        self.exam = n.key
        getOwnQuestion(n.examid).then((res)=>{
            if (res.success) {
              let data = JSON.parse(res.result.qaset)
              data.forEach(function(item){
                if (item.type == 'multi') {
                  let answer = JSON.parse(item.answer)
                  answer.sort()
                  item.answer = ""
                  answer.forEach(function(i){
                    item.answer += i
                  })
                }
                if (item.answer === undefined) {
                  item.answer = ""
                }
                self.dataSource.push(item)
                //显示全部
                self.dataSource1.push(item)
              })
              self.orderBy = 'all'
            }
        })
      },
      answerRetract (n) {
        n.flag = !n.flag
        this.questionVisible = false
        this.dataSource = []
        this.dataSource1 = []
        this.exam = ""
      }
    }
  }

</script>

<style lang="scss">
  .result-list .ant-card {
    font-size: 15px;
    font-weight: 500;
  }
  .result-list .ant-list {
    font-size: 15px;
  }
  .result-list .card-avatar {
    width: 48px;
    height: 48px;
    border-radius: 48px;
  }
  .result-list .ant-list-item {
    margin-bottom: 0px;
  }
  .result-list .ant-card-actions {
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
  .result-list .ant-radio-wrapper {
    margin-left: 17px;
  }

  .result-list .ant-radio-wrapper-checked {
    font-weight: 700;
    color: #1890ff;
  }

  .result-list .ant-checkbox-wrapper-checked {
    font-weight: 700;
    color: #1890ff;
  }
  
  .result-list .ant-checkbox-wrapper {
    margin-left: 17px;
  }

  .result-list .new-btn {
    background-color: #fff;
    border-radius: 2px;
    width: 100%;
    height: 188px;
  }

  .result-list .meta-content {
    position: relative;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    // height: 64px;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
  }

  .tablestyle {
      /*width: 1100px;*/
      border: 1px solid #9E9E9E;
      font-size: 15px;
      table-layout: fixed;
      margin-bottom: 10px;
  }

  .tablestyle tbody tr td:first-child {
      color: rgba(0, 0, 0, 0.85);
      background: #F5F5F5
  }

  .tablestyle tr td {
      text-align: center;
      border-right: 1px solid #9E9E9E;
      border-bottom: 1px solid #9E9E9E;
      height: 30px;
      color: rgba(0, 0, 0, 0.65)
  }

  .tablestyle {
    width: 100%;
    max-width: 600px;
    min-width: 300px;
    margin: 0 auto;
  }

  .tablediv {
      width: 100%;
      overflow: auto;
  }

</style>

