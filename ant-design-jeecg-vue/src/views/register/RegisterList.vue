<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8" v-show=false>
            <a-form-item label="所在机构">
              <a-input disabled placeholder="所在机构" v-model="queryParam.orgCode"></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="12">
            <a-form-item label="账号">
              <a-input placeholder="请输入账号查询" v-model="queryParam.username"></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="12">
            <a-form-item label="姓名">
              <a-input placeholder="请输入姓名查询" v-model="queryParam.realname"></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="8">
            <a-form-item label="省份">
              <a-select v-model="queryParam.provcode" placeholder="请选择省份查询">
                <a-select-option value="">请选择省份查询</a-select-option>
                <a-select-option v-for="province in provinceData" :key="province">{{province}}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator" style="border-top: 5px">
      <a-button @click="handleAdd" v-has="'user:add'" type="primary" icon="plus">添加用户</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('用户信息')">导出</a-button>
      <!--<a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>-->
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay" @click="handleMenuClick">
          <a-menu-item key="1">
            <a-icon type="delete" @click="batchDel"/>
            删除
          </a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px">
          批量操作
          <a-icon type="down"/>
        </a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i>已选择&nbsp;<a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项&nbsp;&nbsp;
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table
        ref="table"
        bordered
        size="middle"
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange">
        <!--
        <template slot="avatarslot" slot-scope="text, record, index">
          <div class="anty-img-wrap">
            <a-avatar shape="square" :src="getAvatarView(record.avatar)" icon="user"/>
          </div>
        </template>
        -->

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical"/>

          <a-dropdown>
            <a class="ant-dropdown-link">
              更多 <a-icon type="down"/>
            </a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a href="javascript:;" @click="handleDetail(record)">详情</a>
              </a-menu-item>

              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>

            </a-menu>
          </a-dropdown>
        </span>


      </a-table>
    </div>
    <!-- table区域-end -->

    <user-modal ref="modalForm" @ok="modalFormOk"></user-modal>
  </a-card>
</template>

<script>
  import UserModal from './modules/UserModal'
  import {putAction} from '@/api/manage';
  import {JeecgListMixin} from '@/mixins/JeecgListMixin'

  export default {
    name: "UserList",
    mixins: [JeecgListMixin],
    components: {
      UserModal
    },
    data() {
      return {
        description: '这是用户管理页面',
        queryParam: {"orgCode":"A01*"},
        provinceData: ["北京", "上海", "重庆", "安徽", "福建", "甘肃", "广东", "广西", "贵州", "海南", "河北", "黑龙江", "河南", "香港", "湖北", "湖南", "江苏", "江西", "吉林", "辽宁", "澳门", "内蒙古", "宁夏", "青海", "山东","山西", "陕西", "四川", "台湾", "天津", "新疆", "西藏", "云南", "浙江", "海外" ],
        columns: [
          /*{
            title: '#',
            dataIndex: '',
            key:'rowIndex',
            width:60,
            align:"center",
            customRender:function (t,r,index) {
              return parseInt(index)+1;
            }
          },*/
          {
            title: '用户账号',
            align: "center",
            dataIndex: 'username',
            width: 120
          },
          {
            title: '真实姓名',
            align: "center",
            width: 120,
            dataIndex: 'realname',
          },
          /*{
            title: '头像',
            align: "center",
            width: 120,
            dataIndex: 'avatar',
            scopedSlots: {customRender: "avatarslot"}
          },*/

          {
            title: '身份证号',
            align: "center",
            dataIndex: 'idcard'
          },
          {
            title: '性别',
            align: "center",
            width: 60,
            dataIndex: 'sex_dictText'
          },
          {
            title: '省',
            align: "center",
            width: 100,
            dataIndex: 'provcode'
          },
          {
            title: '市',
            align: "center",
            width: 100,
            dataIndex: 'citycode'
          },
          {
            title: '职称',
            align: "center",
            width: 100,
            dataIndex: 'proftitle'
          },
          {
            title: '单位',
            align: "center",
            width: 100,
            dataIndex: 'orgname'
          },
          {
            title: '单位类别',
            align: "center",
            width: 100,
            dataIndex: 'orgtype'
          },
          {
            title: '工作类别',
            align: "center",
            width: 100,
            dataIndex: 'jobtype'
          },
          {
            title: '最高学历',
            align: "center",
            width: 100,
            dataIndex: 'edu'
          },

         /* {
            title: '生日',
            align: "center",
            width: 180,
            dataIndex: 'birthday'
          },
          {
            title: '手机号码',
            align: "center",
            width: 100,
            dataIndex: 'phone'
          },
          {
            title: '邮箱',
            align: "center",
            dataIndex: 'email'
          },*/
         {
            title: '注册时间',
            align: "center",
            width: 150,
            dataIndex: 'createTime',
            sorter: true
          },

          {
            title: '操作',
            dataIndex: 'action',
            scopedSlots: {customRender: 'action'},
            align: "center",
            width: 170
          }

        ],
        url: {
          imgerver: window._CONFIG['domianURL'] + "/sys/common/view",
          syncUser: "/process/extActProcess/doSyncUser",
          list: "/sys/user/list",
          delete: "/sys/user/delete",
          deleteBatch: "/sys/user/deleteBatch",
          exportXlsUrl: "/sys/user/exportXls",
          importExcelUrl: "sys/user/importExcel",
        },
      }
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      }
    },
    methods: {
      initDictConfig: function() {
        console.log('这是我自己的方法')
      },
      getAvatarView: function (avatar) {
        return this.url.imgerver + "/" + avatar;
      },
      handleMenuClick(e) {
        if (e.key == 1) {
          this.batchDel();
        }
      }
    }

  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>