<<template>
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
        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="addBig" type="primary" icon="plus">创建试卷大题</a-button>
      <a-button @click="smartPaperGeneration" type="primary" icon="plus">智能组卷</a-button>
      <a-button @click="previewPaper(examid)" type="primary" icon="eye">预览试卷</a-button>
      <a-button @click="handleGenerate(examid)" type="primary" icon="check">确定生成试卷</a-button>
      <a-button @click="handleExportXls(examName+sessionCode+'试题表')" type="primary" icon="download">导出本场试卷</a-button>
<!--      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">-->
<!--        <a-button type="primary" icon="import">导入试卷</a-button>-->
<!--      </a-upload>-->
    </div>

    <div v-for="(d,index1) in counter" :key="index1">
      <div class="big-question">
        <div class="headerH" style="border:1px solid gainsboro;display: flex" >
          <div style="width: 50%">
            <p style="display: inline-block;margin: 10px">{{d.bigDescribe}}（<span style="color: red">{{d.paperList.length}}</span>道试题，共<span style="color: red">{{reversedMessage[index1]}}</span>分）</p>
            <a-button size="small" @click="editBigModal(d,index1)" type="primary" style="margin:8px;background-color: white;color: #7e7e7c;border:1px solid #7e7e7c;" > 编辑大题信息 </a-button>
          </div>
          <div style="width:50%;text-align: right">
            <a-button size="small" @click="delBig(index1)" type="primary" style="margin:8px;background-color: white;color: #7e7e7c;border:1px solid #7e7e7c;" > 删除大题 </a-button>
            <a-button size="small" @click="openUploadModal(index1)" type="primary" style="margin:8px;background-color: white;color: #7e7e7c;border:1px solid #7e7e7c;" > 导入试题 </a-button>
            <a-button size="small" @click="_handleAdd(d.bigDescribe,index1)" type="primary" style="margin:8px;background-color: white;color: #7e7e7c;border:1px solid #7e7e7c;" > 新建试题 </a-button>
            <a-button size="small" @click="selectQuestion(d,index1)" type="primary" style="margin:8px;background-color: white;color: #7e7e7c;border:1px solid #7e7e7c;" > 从试题库选择 </a-button>
            <a-button size="small" @click="smartQuestionGeneration(index1)" type="primary" style="margin:8px;background-color: white; color: #7e7e7c;border:1px solid #7e7e7c;">智能组卷</a-button>
          </div>
        </div>

      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="index"
        :columns="columns"
        :dataSource="d.paperList"
        :loading="loading"
        @change="handleTableChange">
<!--        :pagination="ipagination"-->

        <span slot="action" slot-scope="text, record,index">
          <a @click="_handleEdit(record,index1,index)">编辑</a>
          <a-divider type="vertical" />
                <a-popconfirm title="确定删除吗?" @confirm="() => _handleDelete(index1,index)">
                  <a>删除</a>
                </a-popconfirm>
        </span>
      </a-table>
    </div>
    </div>

    <!-- 表单区域 -->
    <question-modal ref="addForm" @ok="modalFormOk"  @addPapersinQuestionOne="addPapersinQuestionOne" @editPapersinQuestionOne="editPapersinQuestionOne"></question-modal>
    <edit-big-modal ref="editBigForm" @setBigDescribe="setBigDescribe" @ok="modalFormOk"></edit-big-modal>
    <select-modal ref="selectForm" @addPapersinQuestion="addPapersinQuestion" @ok="modalFormOk"></select-modal>
    <total-strategy-modal ref="smartPaperForm" @smartaddPaper="smartaddPaper" @ok="modalFormOk"></total-strategy-modal>
    <strategy-modal ref="smartForm" @smartaddQuestions=smartaddQuestions @ok="modalFormOk"></strategy-modal>
    <upload-modal ref="uploadForm" @addPapersinQuestion="addPapersinQuestion" @ok="modalFormOk"></upload-modal>
  </a-card>
</template>

<script>
  import QuestionModal from './modules/QuestionModal'
  import EditBigModal from './modules/EditBigModal'
  import StrategyModal from './modules/StrategyModal'
  import TotalStrategyModal from './modules/TotalStrategyModal'
  import SelectModal from './modules/SelectModal'
  import UploadModal from'./modules/UploadModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { httpAction } from '@/api/manage'
  import {generateQuestionList,getPreview,queryPaper} from '@/api/api'

  export default {
    name: "",
    mixins:[JeecgListMixin],
    components: {
      QuestionModal,
      StrategyModal,
      SelectModal,
      UploadModal,
      EditBigModal,
      TotalStrategyModal,
    },
    data () {
      return {
        description: '试题表管理页面',
        counter:[],
        index:0,
        questionType:["single","multi","img","fill","judge","answer"],
        // 表头
        columns: [
          {
            title: '题号',
            dataIndex: '',
            key:'rowIndex',
            width:60,
            align:"center",
            customRender:function (t,r,index) {
              return parseInt(index)+1;
            }
          },
          {
            title: '问题类型',
            align:"center",
            dataIndex: 'type'
          },
          // {
          //   title: '问题标题',
          //   align:"center",
          //   dataIndex: 'title',
          //   width: 120
          // },
          {
            title: '题目描述',
            align:"center",
            dataIndex: 'content',
            width: 120
          },
          {
            title: '选项A',
            align:"center",
            dataIndex: 'optionA',
            width: 120
          },
          {
            title: '选项B',
            align:"center",
            dataIndex: 'optionB',
            width: 120
          },
          {
            title: '选项C',
            align:"center",
            dataIndex: 'optionC',
            width: 120
          },
          {
            title: '选项D',
            align:"center",
            dataIndex: 'optionD',
            width: 120
          },
          {
            title: '选项E',
            align:"center",
            dataIndex: 'optionE',
            width: 120
          },
          {
            title: '选项F',
            align:"center",
            dataIndex: 'optionF',
            width: 120
          },
          {
            title: '选项G',
            align:"center",
            dataIndex: 'optionG',
            width: 120
          },
          {
            title: '答案',
            align:"center",
            dataIndex: 'answer',
            width: 120
          },
          {
            title: '难度(0简单,1中等,2困难)',
            align:"center",
            dataIndex: 'hard',
            width: 120
          },
          {
            title: '分值',
            align:"center",
            dataIndex: 'score',
            width: 120
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
            width: 140
          }
        ],
        url: {
          list: "/exam/question/list",
          delete: "/exam/question/delete",
          deleteBatch: "/exam/question/deleteBatch",
          exportXlsUrl: "/exam/question/exportXls",
          importExcelUrl: "/exam/question/importExcel",
        },
      }
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}?examid=`+this.examid;
      },
      reversedMessage: function () {
        var sum=[];
        for(var i=0;i<this.counter.length;i++) {
          var papersum=0;
          for(var j=0;j<this.counter[i].paperList.length;j++){
            papersum+=this.counter[i].paperList[j].score;
          }
          sum.push(papersum);
        }
        // `this` 指向 vm 实例
        return sum;
      },
    },
    created () {
      this.sessionName = this.$route.params.sessionName
      this.examid = this.$route.params.examid
      this.sessionCode = this.$route.params.sessionCode
      this.examName = this.$route.params.examName
      this.initData()
    },
    methods: {
      initData(){
        if(this.$route.params.counter&&(this.$route.params.counter!=[])){
          this.counter = this.$route.params.counter
          // console.log("PaperList.vue,this.$route.params.counter:")
          // console.log(this.$route.params.counter)
        }else{
        queryPaper({examid:this.examid}).then((res)=> {
          if(res.length>0){
            //大题index
            let bigindex=0;
            //大题name
            let bigName='';

            console.log("查询结果")
            console.log(res)
            console.log(res.length)

            if(res.length>0){
              bigName=res[0].bigDescribe
              this.counter.push({bigDescribe:res[0].bigDescribe,paperList:[]})
              for(var i=0;i<res.length;i++){
                if(res[i].bigDescribe!=bigName){
                  bigindex++
                  bigName=res[i].bigDescribe
                  this.counter.push({bigDescribe:res[i].bigDescribe,paperList:[]})
                }
                var question=res[i];
                question.updateBy='';
                question.index=i
                this.counter[bigindex].paperList.push(question)
              }
            }
          }
        })
        }
      },
      addBig(){
        if(!this.counter){
          this.counter=[];
        }
        this.counter.push({bigDescribe:'',paperList:[]});
      },
      editBigModal:function(record,index){
        this.$refs.editBigForm.index = index;
        this.$refs.editBigForm.edit(record);
        this.$refs.editBigForm.title = "编辑大题信息";
      },
      setBigDescribe(even){
        this.counter[even.index].bigDescribe=even.bigDescribe;
      },
      delBig(index){
        this.counter.splice(index,1)
      },
      openUploadModal(index){
        this.$refs.uploadForm.add(),
        this.$refs.uploadForm.index=index
        this.$refs.uploadForm.title="导入试题"
      },
      _handleAdd(bigDescribe,index1){
        console.log("index1::"+index1)
        console.log(this.counter[index1].paperList.length)
        if(this.counter[index1].paperList.length==0) {
          if(index1==0){
            var no1 =1;
            var no2 =0;
          }
          else{
            console.log("新创建"+this.counter[index1-1].paperList[0].no)
            var no1 = this.counter[index1-1].paperList[0].no;
            var no2 = 100
          }
        }else{
          var no1 = this.counter[index1].paperList[0].no;
          var no2 =this.counter[index1].paperList.length;
        }
        console.log("no1:::"+no1)
        console.log("no2:::"+no2)
        let no = no1+no2
        this.$refs.addForm.index=index1;
        this.$refs.addForm.no = no;
        this.$refs.addForm.bigDescribe=bigDescribe;
        this.$refs.addForm.title = "新增试题";
        this.$refs.addForm.add();
      },
      //删除试题
      _handleDelete(index1,index){
        //var mid = 0
        // for(var j =0;j<index1;j++){
        //   mid+=this.counter[j].paperList.length
        // }
        // console.log("mid："+mid+"，index1："+index1+"，index："+index)
        //if(index1!=0&&mid!=0){
          this.counter[index1].paperList.splice(index,1)
        //}
        // else{
        //   this.counter[index1].paperList.splice(index-mid,1)
        // }
      },
      _handleEdit(record,index1,index){
        this.$refs.addForm.edit(record);
        this.$refs.addForm.index1=index1;
        this.$refs.addForm.index=index;
        console.log("record.index:::"+record.index)
        this.$refs.addForm.title = "修改试题";
      },
      //新建试题
      addPapersinQuestionOne(even){
        console.log("FORMDATA")
        console.log(even.formData)
        even.formData.id='';
        even.formData.index=this.counter[even.index].paperList.length
        this.counter[even.index].paperList.push(even.formData)
      },
      //编辑试题
      editPapersinQuestionOne(even){
        var mid = 0
        for(var j =0;j<even.index1;j++){
          mid+=this.counter[j].paperList.length
        }
        this.counter[even.index1].paperList.splice(even.index,1,even.formData);
      },
      //从试题库选择
      selectQuestion(record,index1){//record是整个大题，index1是大题的偏移量
        this.$refs.selectForm.add();
        this.$refs.selectForm.index1=index1;
        this.$refs.selectForm.title = "从试题库选择";
      },
      //从试题库添加时确认
      addPapersinQuestion(even){
        if(even.selectedRows!=null){
          var index0=0
          if(even.index1!=0){
            for(var j=0;j<even.index1;j++){
              index0+=this.counter[j].paperList.length;
            }
          }
          else;
          if(even.selectedRows.length>0){
            for(var i=0;i<even.selectedRows.length;i++){
              even.selectedRows[i].id='';
              even.selectedRows[i].createBy='';
              even.selectedRows[i].createTime='';
              even.selectedRows[i].updateBy='';
              even.selectedRows[i].updateTime='';
              even.selectedRows[i].index=index0+this.counter[even.index1].paperList.length
              console.log( even.selectedRows[i])
              this.counter[even.index1].paperList.push(even.selectedRows[i])
            }
          }
        }
      },
      //试卷智能组卷
      smartPaperGeneration:function(){
        this.$refs.smartPaperForm.add()
        this.$refs.smartPaperForm.title = "智能组卷-试卷";
      },
      smartaddPaper:function(paper){
        //删除原有大题
        this.counter.splice(0,this.counter.length)
        var index = 0;//小题.index
        //大题
        for(var i=0;i<paper.length;i++){
          this.counter.push({bigDescribe:"",paperList:[]})
          for(var j=0;j<paper[i].length;j++){
            paper[i][j].id=''
            paper[i][j].createBy=''
            paper[i][j].createTime=''
            paper[i][j].updateBy=''
            paper[i][j].updateTime=''
            paper[i][j].index=index
            this.counter[i].paperList.push(paper[i][j])
            console.log("大题index："+i+"，小题index："+j+"，index："+index)
            index++;
          }
        }
      },
      //大题智能组卷
      smartQuestionGeneration:function(index1){
        this.$refs.smartForm.add();
        this.$refs.smartForm.index1 = index1
        this.$refs.smartForm.title = "智能组卷-大题";
      },
      //智能组卷确认
      smartaddQuestions(even,index1){
        var length = this.counter[index1].paperList.length
        console.log("length:"+length)
        this.counter[index1].paperList.splice(0,length)
        if(even.length>0){
          var index0 = 0
          if(index1!=0){
            for(var i=0;i<index1;i++){
              index0+=this.counter[i].paperList.length;
            }
          }
          else;
          for(var j = 0;j<even.length;j++){
            even[j].id='';
            even[j].createBy='';
            even[j].createTime='';
            even[j].updateBy='';
            even[j].updateTime='';
            even[j].index=index0+this.counter[index1].paperList.length;
            console.log(even[j])
            this.counter[index1].paperList.push(even[j])
          }
        }
      },
      previewPaper: function(examid){
        // generatePreview(examid).then((res)=>{
        //   if(res.success){
        //     getPreview(examid).then((res)=>{
        //       if(res.success){
        //         this.$router.push({name:'exammanage-preview',params:{flag:"true",result:res,examid:examid,sessionName:this.sessionName,examName:this.examName,sessionCode:this.sessionCode}})
        //       }else{
        //         this.$error({
        //           title:res.message
        //         })
        //       }
        //     })
        //   }else{
        //     this.$error({
        //       title:res.message
        //     })
        //   }
        // })
        var res={}
        res.data = this.counter
        res.success = true
        var result=JSON.stringify(res)
        window.localStorage.setItem('result'+examid,result)
        this.$router.push({name:'exammanage-preview',params:{result:res,examid:examid,sessionName:this.sessionName,examName:this.examName,sessionCode:this.sessionCode}})
      },
      handleGenerate: function(examId) {
        // let self = this
        // self.$confirm({
        //   title: '是否生成试卷？',
        //   onOk () {
        //     generateQuestionList(examId).then((res)=>{
        //       if(res.success) {
        //         self.$success({
        //           title: '生成试卷成功！'
        //         })}
        //     })
        //   }
        // });
        let httpurl ='exam/question/adds'
        let method='post'
        let formData={examId:examId,paper:this.counter}
        console.log("试卷内容this.counter:")
        console.log(this.counter)
        httpAction(httpurl,formData,method).then((res)=>{
          if(res.success){
            this.$message.success(res.message);
          }
          else{
            this.$message.warning(res.message);
          }
        }).finally(()=>{
        })
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>