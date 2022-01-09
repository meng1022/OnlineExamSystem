<template>
  <div class="card-list" ref="content">

    <a-card style="text-align: center" >


<!--      <a-button size="big" type= "primary" style=" float: right;margin-right: 1.0em;margin-top: 1.0em" @click="toAnalysis(examid)">数据分析</a-button>-->
<!--      <a-button size="big" type= "primary" style=" float: right;margin-right: 1.0em;margin-top: 1.0em" @click="toTotalScore(examName)">总成绩</a-button>-->
      <a-button  type= "primary" style=" float: right;margin-right: 1.0em;margin-top: 1.0em" @click="toScoreList(examid,examName,sessionCode)">本场成绩</a-button>
      <a-button  type= "primary" style=" float: right;margin-right: 1.0em;margin-top: 1.0em" @click="toSubmitList(examid,examName,sessionCode)">考生答卷</a-button>
    </a-card>

<!--    <a-card style="text-align: center">-->
<!--      <a-radio-group :value="orderBy" @change="orderByChange">-->
<!--        <a-radio-button value="number">按题号排序</a-radio-button>-->
<!--        <a-radio-button value="ratio">按得分率排序</a-radio-button>-->
<!--      </a-radio-group>-->
<!--    </a-card>-->
    <a-list
      :grid="{gutter: 24}"
      :dataSource="dataSource"
    >
      <a-list-item class="new-list-item" slot="renderItem" slot-scope="item, index">

<!--        <template v-if="item.type === 'desc'">-->
<!--          <a-card>-->
<!--          <a-card-meta>-->
<!--            <div style="margin-bottom: 3px" slot="title">{{ item.title }}</div>-->
<!--            <p>{{ item.content }}</p>-->
<!--            <template v-if="item.imgs != null && (item.imgs.length > 0)">-->
<!--              <a-divider orientation="left">相关图像</a-divider>-->
<!--              <a-list :grid="{gutter: 24, lg: 1, md: 1, sm: 1, xs: 1}" :dataSource="item.imgs">-->
<!--                <a-list-item slot="renderItem" slot-scope="imgitem, imgindex">-->
<!--                  <template>-->
<!--                    <a-card>-->
<!--                      <img-->
<!--                        alt="example"-->
<!--                        :src="imgitem.src"-->
<!--                        slot="cover"-->
<!--                      />-->
<!--                      <a-card-meta>-->
<!--                        <div style="margin-bottom: 3px" slot="title">{{ imgitem.title }}</div>-->
<!--                      </a-card-meta>-->
<!--                    </a-card>-->
<!--                  </template>-->
<!--                </a-list-item>-->
<!--              </a-list>-->
<!--            </template>-->
<!--          </a-card-meta>-->
<!--          </a-card>-->
<!--        </template>-->

        <template v-if="item.type === 'single'||item.type==='judge'">
          <a-card>
            <p>{{ item.title }}</p>
            <p>{{item.no}}.{{ item.content }}</p>
            <div v-if="item.optionA!=null">A.<span v-html="item.optionA"></span><br><img :src="item.imgUrlA"/></div>
            <div v-if="item.optionB!=null">B.<span v-html="item.optionB"></span><br><img :src="item.imgUrlB"/></div>
            <div v-if="item.optionC!=null">C.<span v-html="item.optionC"></span><br><img :src="item.imgUrlC"/></div>
            <div v-if="item.optionD!=null">D.<span v-html="item.optionD"></span><br><img :src="item.imgUrlD"/></div>
            <div v-if="item.optionE!=null">E.<span v-html="item.optionE"></span><br><img :src="item.imgUrlE"/></div>
          </a-card>
        </template>
        <template v-else-if="item.type === 'multi'">
          <a-card>
            <p>{{ item.title }}</p>
            <p>{{item.no}}.{{ item.content }}</p>
            <div v-if="item.optionA!=null">A.<span v-html="item.optionA"></span><br><img :src="item.imgUrlA"/></div>
            <div v-if="item.optionB!=null">B.<span v-html="item.optionB"></span><br><img :src="item.imgUrlB"/></div>
            <div v-if="item.optionC!=null">C.<span v-html="item.optionC"></span><br><img :src="item.imgUrlC"/></div>
            <div v-if="item.optionD!=null">D.<span v-html="item.optionD"></span><br><img :src="item.imgUrlD"/></div>
            <div v-if="item.optionE!=null">E.<span v-html="item.optionE"></span><br><img :src="item.imgUrlE"/></div>
          </a-card>
        </template>
        <template v-else>
          <a-card>
            <p>{{ item.title }}</p>
            <p>{{item.no}}.{{ item.content }}</p>
          </a-card>
        </template>
        <template v-if="item.type === 'single' || item.type === 'judge'">
          <a-card>
            <p style="font-size: 17px;font-weight: 600;line-height: 14px;">标准答案：{{item.answer}}</p>
            <p style="font-size: 17px;font-weight: 600;line-height: 14px;">得分比例：{{item.acc*100 |numFilter}}%</p>
            <p style="font-size: 17px;font-weight: 600;line-height: 14px;">考生答案分析：
              <span v-for="analysis in item.analysis">选择{{analysis.key}}的有{{analysis.ratio*100 | numFilter}}%； </span></p>
          </a-card>
        </template>
        <template v-else-if="item.type === 'multi'">
          <a-card>
            <p style="font-size: 17px;font-weight: 600;line-height: 14px;">标准答案：
              <span v-for="value in item.answer">{{value}}</span></p>
            <p style="font-size: 17px;font-weight: 600;line-height: 14px;">得分比例：{{item.acc*100| numFilter}}%</p>
            <p style="font-size: 17px;font-weight: 600;line-height: 14px;">考生答案分析：
              <span v-for="analysis in item.analysis">选择{{analysis.key}}的有{{analysis.ratio*100 |numFilter}}%； </span></p>
          </a-card>
        </template>
        <template v-else>
          <a-card>
            <p style="font-size: 17px;font-weight: 600;line-height: 14px;">标准答案：
              <span v-for="value in item.answer">{{value}}</span></p>
            <p style="font-size: 17px;font-weight: 600;line-height: 14px;">平均得分：{{item.ave}}</p>
          </a-card>
        </template>
      </a-list-item>
    </a-list>
    <div>
      <a-back-top />
    </div>

  </div>



</template>

<script>

  import {getComment} from '@/api/api'
  import DictItemList from '../../system/DictItemList'
  export default {
    name: "QuestionList",
    components: {
      DictItemList
    },
    data() {
      return {
        loading: true,
        affixVisible: false,
        center: null,
        examid: 'test',
        subid: '1',
        total_count: 0,
        finish_count: 0,
        dataSource:[],
        dataSource1: [],
        username: "",
        endTime: '',
        orderBy: 'number',
        radioStyle: {
          display: 'block',
          marginBottom: '20px',
          whiteSpace: 'normal'
        },
      }
    },
    created() {
      setTimeout(() => {
        this.loading = !this.loading
      }, 1000)
      this.initData()
    },
    filters: {
      numFilter (value) {
        // 截取当前数据到小数点后两位
        let realVal = parseFloat(value).toFixed(1)
        return realVal
      }
    },
    watch :{
      '$route': function (to, from) {
        if (to.name == 'exammanage-comment') {
          this.initData()
        }
      },
      'orderBy': function (value) {
        if (value == 'number') {
          this.dataSource = JSON.parse(JSON.stringify(this.dataSource1))
        } else if (value == 'ratio') {
          this.dataSource = this.sortKey(this.dataSource,'acc')
        }
      },
    },
    methods: {
      initData () {
        let self = this
        this.examid = this.$route.params.examid;
        this.examName = this.$route.params.examName
        this.sessionCode = this.$route.params.sessionCode
        self.dataSource = []
        getComment(self.examid).then((res)=>{
            if (res.success) {
              let data = JSON.parse(res.result.qaset)
              data.forEach(function(item){
                if (item.type != 'desc') {
                  self.dataSource.push(item)
                  self.dataSource1.push(item)
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
      toSubmitList (examid,examName,sessionCode) {
        this.$router.push({ name: 'exam-submit', params: {examid: examid,examName:examName,sessionCode:sessionCode} })
      },
      toScoreList (examid,examName,sessionCode) {
        this.$router.push({ name: 'exam-score', params: {examid: examid,examName:examName,sessionCode:sessionCode} })
      },

    }
  }

</script>

<style lang="scss">
  .card-list .ant-card {
    font-size: 17px;
    font-weight: 500;
  }
  .card-list .ant-list {
    font-size: 17px;
  }
  .card-list .card-avatar {
    width: 48px;
    height: 48px;
    border-radius: 48px;
  }
  .card-list .ant-list-item {
    margin-bottom: 0px;
  }
  .card-list .ant-card-actions {
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
  .card-list .ant-radio-wrapper {
    margin-left: 17px;
  }

  .card-list .ant-radio-wrapper-checked {
    font-weight: 700;
    color: #1890ff;
  }

  .card-list .ant-checkbox-wrapper-checked {
    font-weight: 700;
    color: #1890ff;
  }

  .card-list .ant-checkbox-wrapper {
    margin-left: 17px;
  }

  .card-list .new-btn {
    background-color: #fff;
    border-radius: 2px;
    width: 100%;
    height: 188px;
  }

  .card-list .meta-content {
    position: relative;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    // height: 64px;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
  }

</style>

