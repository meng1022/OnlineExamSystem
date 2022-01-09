import { getAction,deleteAction,putAction,postAction} from '@/api/manage'

////根路径
// const doMian = "/jeecg-boot/";
////图片预览请求地址
// const imgView = "http://localhost:8080/jeecg-boot/sys/common/view/";

//角色管理
const addRole = (params)=>postAction("/sys/role/add",params);
const editRole = (params)=>putAction("/sys/role/edit",params);
// const getRoleList = (params)=>getAction("/sys/role/list",params);
// const deleteRole = (params)=>deleteAction("/sys/role/delete",params);
// const deleteRoleList = (params)=>deleteAction("/sys/role/deleteBatch",params);
const checkRoleCode = (params)=>getAction("/sys/role/checkRoleCode",params);
const queryall = (params)=>getAction("/sys/role/queryall",params);

//用户管理
const addUser = (params)=>postAction("/sys/user/add",params);
const editUser = (params)=>putAction("/sys/user/edit",params);
const queryUserRole = (params)=>getAction("/sys/user/queryUserRole",params);
const getUserList = (params)=>getAction("/sys/user/list",params);
// const deleteUser = (params)=>deleteAction("/sys/user/delete",params);
// const deleteUserList = (params)=>deleteAction("/sys/user/deleteBatch",params);
const frozenBatch = (params)=>putAction("/sys/user/frozenBatch",params);
//验证用户账号是否唯一
const checkUsername = (params)=>getAction("/sys/user/checkOnlyUser",params);
//改变密码
const changPassword = (params)=>putAction("/sys/user/changPassword",params);

//权限管理
const addPermission= (params)=>postAction("/sys/permission/add",params);
const editPermission= (params)=>putAction("/sys/permission/edit",params);
const getPermissionList = (params)=>getAction("/sys/permission/list",params);
// const deletePermission = (params)=>deleteAction("/sys/permission/delete",params);
// const deletePermissionList = (params)=>deleteAction("/sys/permission/deleteBatch",params);
const queryTreeList = (params)=>getAction("/sys/permission/queryTreeList",params);
const queryTreeListForRole = (params)=>getAction("/sys/role/queryTreeList",params);
const queryListAsync = (params)=>getAction("/sys/permission/queryListAsync",params);
const queryRolePermission = (params)=>getAction("/sys/permission/queryRolePermission",params);
const saveRolePermission = (params)=>postAction("/sys/permission/saveRolePermission",params);
//const queryPermissionsByUser = (params)=>getAction("/sys/permission/queryByUser",params);
const queryPermissionsByUser = (params)=>getAction("/sys/permission/getUserPermissionByToken",params);
const loadAllRoleIds = (params)=>getAction("/sys/permission/loadAllRoleIds",params);
const getPermissionRuleList = (params)=>getAction("/sys/permission/getPermRuleListByPermId",params);
const queryPermissionRule = (params)=>getAction("/sys/permission/queryPermissionRule",params);
// 部门管理
const queryDepartTreeList = (params)=>getAction("/sysdepart/sysDepart/queryTreeList",params);
const queryIdTree = (params)=>getAction("/sysdepart/sysDepart/queryIdTree",params);
const queryParentName   = (params)=>getAction("/sysdepart/sysDepart/queryParentName",params);
const searchByKeywords   = (params)=>getAction("/sysdepart/sysDepart/searchBy",params);
const deleteByDepartId   = (params)=>deleteAction("/sysdepart/sysDepart/delete",params);

//日志管理
//const getLogList = (params)=>getAction("/sys/log/list",params);
const deleteLog = (params)=>deleteAction("/sys/log/delete",params);
const deleteLogList = (params)=>deleteAction("/sys/log/deleteBatch",params);

//数据字典
const addDict = (params)=>postAction("/sys/dict/add",params);
const editDict = (params)=>putAction("/sys/dict/edit",params);
//const getDictList = (params)=>getAction("/sys/dict/list",params);
const treeList = (params)=>getAction("/sys/dict/treeList",params);
// const delDict = (params)=>deleteAction("/sys/dict/delete",params);
//const getDictItemList = (params)=>getAction("/sys/dictItem/list",params);
const addDictItem = (params)=>postAction("/sys/dictItem/add",params);
const editDictItem = (params)=>putAction("/sys/dictItem/edit",params);
//const delDictItem = (params)=>deleteAction("/sys/dictItem/delete",params);
//const delDictItemList = (params)=>deleteAction("/sys/dictItem/deleteBatch",params);

//字典标签专用（通过code获取字典数组）
export const ajaxGetDictItems = (code, params)=>getAction(`/sys/dict/getDictItems/${code}`,params);

//系统通告
const doReleaseData = (params)=>getAction("/sys/annountCement/doReleaseData",params);
const doReovkeData = (params)=>getAction("/sys/annountCement/doReovkeData",params);
//获取系统访问量
const getLoginfo = (params)=>getAction("/sys/loginfo",params);
//数据日志访问
// const getDataLogList = (params)=>getAction("/sys/dataLog/list",params);

// 根据部门主键查询用户信息
const queryUserByDepId = (params)=>getAction("/sys/user/queryUserByDepId",params);

// 查询用户角色表里的所有信息
const queryUserRoleMap = (params)=>getAction("/sys/user/queryUserRoleMap",params);
// 重复校验
const duplicateCheck = (params)=>getAction("/sys/duplicate/check",params);
//获取测试考试列表页
const getExamList = (params)=>getAction("/exam/exam/list",params);
//获取试卷
const getQuestionList = (params)=>getAction("exam/qset/getQsetByExamId?examId="+params,null);
//答题开始时间
const setStartAnswerTime = (params)=>postAction("/exam/qset/setStartAnswerTime?examId="+params.examId,null);
//获取试卷题目
const queryPaper=(params)=>getAction("exam/question/queryPaper",params);
//智能组卷
const smartGenquestion=(params)=>getAction("exam/questionbase/designquestion",params);
const smartGenpaper=(params)=>getAction("exam/questionbase/designpaper",params);

//生成试卷
const generateQuestionList = (params)=>getAction("/exam/qset/genQsetByExamId?examId="+params,null);
//提交试卷
const postQuestionList = (params)=>postAction("/exam/submit/submit",params);
//答卷入库
const dump2db = (params)=>getAction("/exam/submit/dump2db?examId="+params,null);
//获取柱状图数据
const getBarChartData = (params)=>getAction("/exam/score/getbarchartdata?examId="+params)
//客观题判卷
const getScore1 = (params)=>getAction("/exam/score/grade1ByExamId?examId="+params,null);
//问答题
const getScore2 = (params)=>getAction("/exam/score/grade2ByExamId?examId="+params,null);
//试题点评
const getComment = (params)=>getAction("/exam/qaset/getQAsetByExamId?examId="+params,null);
//提交试题缓存结果
const postCacheQuestionList = (params)=>postAction("/exam/scache/submit",params);
//获取试题缓存结果
const getCacheQuestionList = (params)=>getAction("exam/scache/getSCacheByExamId?examId="+params,null);
//获取场内用户
const getInfieldList = (params)=>getAction("/sys/user/userProvList?key=Hkang123!",null);
//获取考生单人成绩列表
const getOwnScore = (params)=>getAction("/exam/total/getOwnTotalByExamName?examName=" + params,null);
//考生答案比对
const getOwnQuestion = (params)=>getAction("/exam/qaset/getOwnQAsetByExamId?examId="+params,null);


export {
  // imgView,
  // doMian,
  addRole,
  editRole,
  checkRoleCode,
  addUser,
  editUser,
  queryUserRole,
  getUserList,
  queryall,
  frozenBatch,
  checkUsername,
  changPassword,
  getPermissionList,
  addPermission,
  editPermission,
  queryTreeList,
  queryListAsync,
  queryRolePermission,
  saveRolePermission,
  queryPermissionsByUser,
  loadAllRoleIds,
  getPermissionRuleList,
  queryPermissionRule,
  queryDepartTreeList,
  queryIdTree,
  queryParentName,
  searchByKeywords,
  deleteByDepartId,
  deleteLog,
  deleteLogList,
  addDict,
  editDict,
  treeList,
  addDictItem,
  editDictItem,
  doReleaseData,
  doReovkeData,
  getLoginfo,
  queryUserByDepId,
  queryUserRoleMap,
  duplicateCheck,
  queryTreeListForRole,
  getExamList,
  getQuestionList,
  generateQuestionList,
  postQuestionList,
  dump2db,
  smartGenquestion,
  smartGenpaper,
  getBarChartData,
  queryPaper,
  getScore1,
  getScore2,
  getComment,
  postCacheQuestionList,
  getCacheQuestionList,
  getInfieldList,
  getOwnScore,
  getOwnQuestion,
  setStartAnswerTime,
}



