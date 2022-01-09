<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">
    
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="考试id">
          <a-input disabled placeholder="" v-decorator="['examId', {}]"/>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="组题试题库">
          <a-input disabled placeholder="" v-decorator="['sessionName', {}]"/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol">
          输入题型和数量才有效，分值和难度默认随机分配
        </a-form-item>
    <a-row>
      <a-col :span="6">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="题型">
              <a-select placeholder="请选择问题类型" v-decorator="['type1',validatorRules.type, {}]" >
                <a-select-option value="single">单选题</a-select-option>
                <a-select-option value="multi">多选题</a-select-option>
                <a-select-option value="judge">判断题</a-select-option>
                <a-select-option value="quest">问答题</a-select-option>
              </a-select>
            </a-form-item>
      </a-col>
      <a-col :span="6">
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="数量">
          <a-input-number :min=0 v-decorator="[ 'number1', validatorRules.number,{}]"  />
        </a-form-item>
      </a-col>
      <a-col :span="6">
      <a-form-item
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        label="分值">
        <a-input-number :min=0 v-decorator="[ 'score1', {initialValue:0}]" />
      </a-form-item>
      </a-col>
      <a-col :span="6">
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="难度">
          <a-select v-decorator="[ 'hard1',{}]" >
            <a-select-option value="-1">随机</a-select-option>
            <a-select-option value="0">简单</a-select-option>
            <a-select-option value="1">中等</a-select-option>
            <a-select-option value="2">困难</a-select-option>
          </a-select>
        </a-form-item>
      </a-col>
    </a-row>

        <a-row>
          <a-col :span="6">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="题型">
              <a-select placeholder="请选择问题类型" v-decorator="['type2', {}]" >
                <a-select-option value="single">单选题</a-select-option>
                <a-select-option value="multi">多选题</a-select-option>
                <a-select-option value="judge">判断题</a-select-option>
                <a-select-option value="quest">问答题</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="数量">
              <a-input-number :min=0 v-decorator="[ 'number2', {}]"  />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="分值">
              <a-input-number :min=0 v-decorator="[ 'score2', {initialValue:0}]" />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="难度">
              <a-select v-decorator="[ 'hard2',{}]" >
                <a-select-option value="-1">随机</a-select-option>
                <a-select-option value="0">简单</a-select-option>
                <a-select-option value="1">中等</a-select-option>
                <a-select-option value="2">困难</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="6">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="题型">
              <a-select placeholder="请选择问题类型" v-decorator="['type3',{}]" >
                <a-select-option value="single">单选题</a-select-option>
                <a-select-option value="multi">多选题</a-select-option>
                <a-select-option value="judge">判断题</a-select-option>
                <a-select-option value="quest">问答题</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="数量">
              <a-input-number :min=0 v-decorator="[ 'number3',{}]"  />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="分值">
              <a-input-number :min=0 v-decorator="[ 'score3', {initialValue:0}]" />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="难度">
              <a-select v-decorator="[ 'hard3', {}]" >
                <a-select-option value="-1">随机</a-select-option>
                <a-select-option value="0">简单</a-select-option>
                <a-select-option value="1">中等</a-select-option>
                <a-select-option value="2">困难</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="6">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="题型">
              <a-select placeholder="请选择问题类型" v-decorator="['type4', {}]" >
                <a-select-option value="single">单选题</a-select-option>
                <a-select-option value="multi">多选题</a-select-option>
                <a-select-option value="judge">判断题</a-select-option>
                <a-select-option value="quest">问答题</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="数量">
              <a-input-number :min=0 v-decorator="[ 'number4',{}]"  />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="分值">
              <a-input-number :min=0 v-decorator="[ 'score4', {initialValue:0}]" />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="难度">
              <a-select v-decorator="[ 'hard4',{}]" >
                <a-select-option value="-1">随机</a-select-option>
                <a-select-option value="0">简单</a-select-option>
                <a-select-option value="1">中等</a-select-option>
                <a-select-option value="2">困难</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="6">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="题型">
              <a-select placeholder="请选择问题类型" v-decorator="['type5', {}]" >
                <a-select-option value="single">单选题</a-select-option>
                <a-select-option value="multi">多选题</a-select-option>
                <a-select-option value="judge">判断题</a-select-option>
                <a-select-option value="quest">问答题</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="数量">
              <a-input-number :min=0 v-decorator="[ 'number5',{}]"  />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="分值">
              <a-input-number :min=0 v-decorator="[ 'score5', {initialValue:0}]" />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="难度">
              <a-select v-decorator="[ 'hard5',{}]" >
                <a-select-option value="-1">随机</a-select-option>
                <a-select-option value="0">简单</a-select-option>
                <a-select-option value="1">中等</a-select-option>
                <a-select-option value="2">困难</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="6">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="题型">
              <a-select placeholder="请选择问题类型" v-decorator="['type6', {}]" >
                <a-select-option value="single">单选题</a-select-option>
                <a-select-option value="multi">多选题</a-select-option>
                <a-select-option value="judge">判断题</a-select-option>
                <a-select-option value="quest">问答题</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="数量">
              <a-input-number :min=0 v-decorator="[ 'number6',{}]"  />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="分值">
              <a-input-number :min=0 v-decorator="[ 'score6', {initialValue:0}]" />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="难度">
              <a-select v-decorator="[ 'hard6',{}]" >
                <a-select-option value="-1">随机</a-select-option>
                <a-select-option value="0">简单</a-select-option>
                <a-select-option value="1">中等</a-select-option>
                <a-select-option value="2">困难</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import {smartGenpaper} from  '@/api/api'
  import pick from 'lodash.pick'
  import ARow from 'ant-design-vue/es/grid/Row'

  export default {
    name: "TotalStrategyModal",
    components: { ARow },
    data () {
      return {
        title:"操作",
        visible: false,
        index1:0,
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 8 },//左侧留白大小
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 10 },//文本框宽度
        },
        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules:{
          type:{rules:[{required:true,message:'请至少输入一条规则',validator:'click'}]},
          // hard:{rules:[{required:true,message:'请至少输入一条规则',validator:'click'}]},
          number:{rules:[{required:true,type:'number',message:'请至少输入一条规则',validator:'click'}]},
        },
      }
    },
    watch:{

    },
    created () {

    },
    methods: {
      add () {
        console.log(this.$route.params)
        this.edit({examId: this.$route.params.examid,sessionName:this.$route.params.sessionName});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'examId','sessionName','type1','hard1','number1','score1','type2','hard2','number2','score2',
                                                                          'type3','hard3','number3','score3','type4','hard4','number4','score4',
                                                                          'type5','hard5','number5','score5','type6','hard6','number6','score6'))
        });},
      close () {
        this.$emit('close');
        this.visible = false;
      },
      handleOk () {
        const that = this;
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true;
            let formData = Object.assign(this.model, values);
            console.log("表单数据：")
            console.log(formData)
            smartGenpaper(formData).then((res)=>{
              if(res.success){
                console.log("随机选题结果res.result::::")
                console.log(res.result)
                that.$emit('smartaddPaper',res.result);
                that.$message.success(res.message);
              }else{
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
              that.close();
            })
          }
        })
      },
      handleCancel () {
        this.close()
      },
    }
  }
</script>

<style lang="less" scoped>

</style>