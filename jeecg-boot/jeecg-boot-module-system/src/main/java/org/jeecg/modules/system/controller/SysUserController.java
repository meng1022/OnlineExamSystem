package org.jeecg.modules.system.controller;


import java.io.IOException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.PasswordUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.entity.SysUserDepart;
import org.jeecg.modules.system.entity.SysUserRole;
import org.jeecg.modules.system.model.DepartIdModel;
import org.jeecg.modules.system.model.SysUserDepartsVO;
import org.jeecg.modules.system.service.ISysUserDepartService;
import org.jeecg.modules.system.service.ISysUserRoleService;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.system.vo.SysDepartUsersVO;
import org.jeecg.modules.system.vo.SysUserRoleVO;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * ????????? ???????????????
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
@Slf4j
@RestController
@RequestMapping("/sys/user")
public class SysUserController {

	@Autowired
	private ISysUserService sysUserService;

	@Autowired
	private ISysUserRoleService sysUserRoleService;

	@Autowired
	private ISysUserDepartService sysUserDepartService;

	@Autowired
	private ISysUserRoleService userRoleService;
    @RequiresRoles(value = { "admin","EXAMADMIN" },logical = Logical.OR )
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Result<IPage<SysUser>> queryPageList(SysUser user,@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,HttpServletRequest req) {
		Result<IPage<SysUser>> result = new Result<IPage<SysUser>>();
		QueryWrapper<SysUser> queryWrapper = QueryGenerator.initQueryWrapper(user, req.getParameterMap());
		Page<SysUser> page = new Page<SysUser>(pageNo, pageSize);
		IPage<SysUser> pageList = sysUserService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
    @RequiresRoles(value = { "admin","EXAMADMIN" },logical = Logical.OR )
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public Result<SysUser> add(@RequestBody JSONObject jsonObject) {
		Result<SysUser> result = new Result<SysUser>();
		String selectedRoles = jsonObject.getString("selectedroles");
		try {
			SysUser user = JSON.parseObject(jsonObject.toJSONString(), SysUser.class);
			user.setCreateTime(new Date());//??????????????????
			String salt = oConvertUtils.randomGen(8);
			user.setSalt(salt);
			String passwordEncode = PasswordUtil.encrypt(user.getUsername(), user.getPassword(), salt);
			user.setPassword(passwordEncode);
			user.setStatus(1);
			user.setDelFlag("0");
			sysUserService.addUserWithRole(user, selectedRoles);
			result.success("???????????????");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("????????????");
		}
		return result;
	}
    @RequiresRoles(value = { "admin","EXAMADMIN" },logical = Logical.OR )
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	public Result<SysUser> edit(@RequestBody JSONObject jsonObject) {
		Result<SysUser> result = new Result<SysUser>();
		try {
			SysUser sysUser = sysUserService.getById(jsonObject.getString("id"));
			if(sysUser==null) {
				result.error500("?????????????????????");
			}else {
				SysUser user = JSON.parseObject(jsonObject.toJSONString(), SysUser.class);
				user.setUpdateTime(new Date());
				//String passwordEncode = PasswordUtil.encrypt(user.getUsername(), user.getPassword(), sysUser.getSalt());
				user.setPassword(sysUser.getPassword());
				String roles = jsonObject.getString("selectedroles");
				sysUserService.editUserWithRole(user, roles);
				result.success("????????????!");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("????????????");
		}
		return result;
	}

	/**
	 * ????????????
	 */
    @RequiresRoles(value = { "admin","EXAMADMIN" },logical = Logical.OR )
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public Result<SysUser> delete(@RequestParam(name="id",required=true) String id) {
		Result<SysUser> result = new Result<SysUser>();
		// ??????SysUserDepart???????????????????????????LambdaQueryWrapper
		LambdaQueryWrapper<SysUserDepart> query = new LambdaQueryWrapper<SysUserDepart>();
		SysUser sysUser = sysUserService.getById(id);
		if(sysUser==null) {
			result.error500("?????????????????????");
		}else {
			// ???????????????????????????,?????????ID????????????????????????
			query.eq(SysUserDepart::getUserId, id);
			boolean ok = sysUserService.removeById(id);
			sysUserDepartService.remove(query);
			if(ok) {
				result.success("????????????!");
			}
		}

		return result;
	}

	/**
	 * ??????????????????
	 */
    @RequiresRoles(value = { "admin","EXAMADMIN" },logical = Logical.OR )
	@RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
	public Result<SysUser> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		// ??????SysUserDepart?????????????????????????????????LambdaQueryWrapper
		LambdaQueryWrapper<SysUserDepart> query = new LambdaQueryWrapper<SysUserDepart>();
		String[] idArry = ids.split(",");
		Result<SysUser> result = new Result<SysUser>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("??????????????????");
		}else {
			this.sysUserService.removeByIds(Arrays.asList(ids.split(",")));
			// ??????????????????,?????????SysUserDepart??????????????????????????????
			for(String id : idArry) {
				query.eq(SysUserDepart::getUserId, id);
				this.sysUserDepartService.remove(query);
			}
			result.success("????????????!");
		}
		return result;
	}

	/**
	  * ??????&????????????
	 * @param jsonObject
	 * @return
	 */
    @RequiresRoles(value = { "admin","EXAMADMIN" },logical = Logical.OR )
	@RequestMapping(value = "/frozenBatch", method = RequestMethod.PUT)
	public Result<SysUser> frozenBatch(@RequestBody JSONObject jsonObject) {
		Result<SysUser> result = new Result<SysUser>();
		try {
			String ids = jsonObject.getString("ids");
			String status = jsonObject.getString("status");
			String[] arr = ids.split(",");
			for (String id : arr) {
				if(oConvertUtils.isNotEmpty(id)) {
					this.sysUserService.update(new SysUser().setStatus(Integer.parseInt(status)),
							new UpdateWrapper<SysUser>().lambda().eq(SysUser::getId,id));
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("????????????"+e.getMessage());
		}
		result.success("????????????!");
		return result;

    }
    @RequiresRoles(value = { "admin","EXAMADMIN" },logical = Logical.OR )
    @RequestMapping(value = "/queryById", method = RequestMethod.GET)
    public Result<SysUser> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<SysUser> result = new Result<SysUser>();
        SysUser sysUser = sysUserService.getById(id);
        if (sysUser == null) {
            result.error500("?????????????????????");
        } else {
            result.setResult(sysUser);
            result.setSuccess(true);
        }
        return result;
    }
    @RequiresRoles(value = { "admin","EXAMADMIN" },logical = Logical.OR )
    @RequestMapping(value = "/queryUserRole", method = RequestMethod.GET)
    public Result<List<String>> queryUserRole(@RequestParam(name = "userid", required = true) String userid) {
        Result<List<String>> result = new Result<>();
        List<String> list = new ArrayList<String>();
        List<SysUserRole> userRole = sysUserRoleService.list(new QueryWrapper<SysUserRole>().lambda().eq(SysUserRole::getUserId, userid));
        if (userRole == null || userRole.size() <= 0) {
            result.error500("?????????????????????????????????");
        } else {
            for (SysUserRole sysUserRole : userRole) {
                list.add(sysUserRole.getRoleId());
            }
            result.setSuccess(true);
            result.setResult(list);
        }
        return result;
    }


    /**
     * ??????????????????????????????<br>
     * ?????????????????? ???????????????????????????????????????
     *
     * @param sysUser
     * @return
     */
    @RequestMapping(value = "/checkOnlyUser", method = RequestMethod.GET)
    public Result<Boolean> checkUsername(SysUser sysUser) {
        Result<Boolean> result = new Result<>();
        result.setResult(true);//??????????????????false?????????????????????
        String id = sysUser.getId();
        log.info("--??????????????????????????????---id:" + id);
        try {
            SysUser oldUser = null;
            if (oConvertUtils.isNotEmpty(id)) {
                oldUser = sysUserService.getById(id);
            } else {
                sysUser.setId(null);
            }
            //??????????????????????????????????????????
            SysUser newUser = sysUserService.getOne(new QueryWrapper<SysUser>(sysUser));
            if (newUser != null) {
                //???????????????????????????????????????????????????????????????????????????
                if (oldUser == null) {
                    //oldUser??????=>????????????=>?????????????????????????????????false
                    result.setSuccess(false);
                    result.setMessage("?????????????????????");
                    return result;
                } else if (!id.equals(newUser.getId())) {
                    //??????=>????????????=>????????????ID????????????-
                    result.setSuccess(false);
                    result.setMessage("?????????????????????");
                    return result;
                }
            }

        } catch (Exception e) {
            result.setSuccess(false);
            result.setResult(false);
            result.setMessage(e.getMessage());
            return result;
        }
        result.setSuccess(true);
        return result;
    }

    /**
     * ????????????
     */
    @RequiresRoles(value = { "admin","EXAMADMIN" },logical = Logical.OR )
    @RequestMapping(value = "/changPassword", method = RequestMethod.PUT)
    public Result<SysUser> changPassword(@RequestBody SysUser sysUser) {
        Result<SysUser> result = new Result<SysUser>();
        String password = sysUser.getPassword();
        sysUser = this.sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, sysUser.getUsername()));
        if (sysUser == null) {
            result.error500("?????????????????????");
        } else {
            String salt = oConvertUtils.randomGen(8);
            sysUser.setSalt(salt);
            String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(), password, salt);
            sysUser.setPassword(passwordEncode);
            this.sysUserService.updateById(sysUser);
            result.setResult(sysUser);
            result.success("?????????????????????");
        }
        return result;
    }

    /**
     * ???????????????????????????
     *
     * @param key
     * @return
     */
    @RequestMapping(value = "/userProvList", method = RequestMethod.GET)
    public Result<JSONArray> getUserProvList(@RequestParam(name = "key", required = true) String key) {
        Result<JSONArray> result = new Result<>();
        if(!key.equals("Hkang123!")) {
            result.error500("???????????????!");
            return result;
        }
        try {
            Map<String,List<String>> map = new HashMap<>();
            List<SysUser> sysUserList = sysUserService.list(new QueryWrapper<SysUser>().orderByAsc("provcode"));
            for(int i=0; i<sysUserList.size();i++){
                SysUser sysUser = sysUserList.get(i);
                System.out.println(sysUser.getOrgCode());
                if(sysUser.getOrgCode()!= null && !sysUser.getUsername().contains("admin")){
                    if(map.containsKey(sysUser.getProvcode())){
                        List<String> userlist = map.get(sysUser.getProvcode());
                        userlist.add(sysUser.getUsername());
                        map.replace(sysUser.getProvcode(),userlist);
                    }else{
                        List<String> userlist = new ArrayList<>();
                        userlist.add(sysUser.getUsername());
                        map.put(sysUser.getProvcode(),userlist);
                    }
                }
            }
            JSONArray jsonArray = new JSONArray();
            for (String mapkey : map.keySet()) {
                JSONObject jsonObject = new JSONObject();
                JSONArray users = new JSONArray();
                for(int i=0; i<map.get(mapkey).size();i++){
                    users.add(map.get(mapkey).get(i));
                }
                jsonObject.put("prov",mapkey);
                jsonObject.put("users",users);
                jsonArray.add(jsonObject);
            }

            result.setSuccess(true);
            result.setMessage("????????????");
            result.setResult(jsonArray);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("??????????????????????????????: " + e.getMessage());
            return result;
        }

    }



    /**
     * ??????????????????????????????????????????
     *
     * @param userId
     * @return
     */
    @RequiresRoles(value = { "admin","EXAMADMIN" },logical = Logical.OR )
    @RequestMapping(value = "/userDepartList", method = RequestMethod.GET)
    public Result<List<DepartIdModel>> getUserDepartsList(@RequestParam(name = "userId", required = true) String userId) {
        Result<List<DepartIdModel>> result = new Result<>();
        try {
            List<DepartIdModel> depIdModelList = this.sysUserDepartService.queryDepartIdsOfUser(userId);
            if (depIdModelList != null && depIdModelList.size() > 0) {
                result.setSuccess(true);
                result.setMessage("????????????");
                result.setResult(depIdModelList);
            } else {
                result.setSuccess(false);
                result.setMessage("????????????");
            }
            return result;
        } catch (Exception e) {
        	log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("??????????????????????????????: " + e.getMessage());
            return result;
        }

    }

    /**
     * ????????????????????????????????????
     *
     * @param sysUserDepartsVO
     * @return
     */
    @RequiresRoles(value = { "admin","EXAMADMIN" },logical = Logical.OR )
    @RequestMapping(value = "/addUDepartIds", method = RequestMethod.POST)
    public Result<String> addSysUseWithrDepart(@RequestBody SysUserDepartsVO sysUserDepartsVO) {
        boolean ok = this.sysUserDepartService.addSysUseWithrDepart(sysUserDepartsVO);
        Result<String> result = new Result<String>();
        try {
            if (ok) {
                result.setMessage("????????????!");
                result.setSuccess(true);
            } else {
                throw new Exception("????????????!");
            }
            return result;
        } catch (Exception e) {
        	log.error(e.getMessage(), e);
            result.setSuccess(true);
            result.setMessage("???????????????????????????????????????: " + e.getMessage());
            return result;
        }

    }

    /**
     * ????????????id???????????????????????????
     *
     * @param sysUserDepartsVO
     * @return
     */
    @RequiresRoles(value = { "admin","EXAMADMIN" },logical = Logical.OR )
    @RequestMapping(value = "/editUDepartIds", method = RequestMethod.PUT)
    public Result<String> editSysUserWithDepart(@RequestBody SysUserDepartsVO sysUserDepartsVO) {
        Result<String> result = new Result<String>();
        boolean ok = sysUserDepartService.editSysUserWithDepart(sysUserDepartsVO);
        if (ok) {
            result.setMessage("????????????!");
            result.setSuccess(true);
            return result;
        }
        result.setMessage("????????????!");
        result.setSuccess(false);
        return result;
    }

    /**
     * ???????????????????????????????????????????????????,???????????????,?????????id??????????????????
     *
     * @return
     */
    @RequiresRoles(value = { "admin","EXAMADMIN" },logical = Logical.OR )
    @RequestMapping(value = "/generateUserId", method = RequestMethod.GET)
    public Result<String> generateUserId() {
        Result<String> result = new Result<>();
        System.out.println("????????????,????????????ID==============================");
        String userId = UUID.randomUUID().toString().replace("-", "");
        result.setSuccess(true);
        result.setResult(userId);
        return result;
    }

    /**
     * ????????????id??????????????????
     *
     * @param id
     * @return
     */
    @RequiresRoles(value = { "admin","EXAMADMIN" },logical = Logical.OR )
    @RequestMapping(value = "/queryUserByDepId", method = RequestMethod.GET)
    public Result<List<SysUser>> queryUserByDepId(@RequestParam(name = "id", required = true) String id) {
        Result<List<SysUser>> result = new Result<>();
        List<SysUser> userList = sysUserDepartService.queryUserByDepId(id);
        try {
            result.setSuccess(true);
            result.setResult(userList);
            return result;
        } catch (Exception e) {
        	log.error(e.getMessage(), e);
            result.setSuccess(false);
            return result;
        }
    }

    /**
     * ??????????????????????????????????????????
     *
     * @return
     */
    @RequiresRoles(value = { "admin","EXAMADMIN" },logical = Logical.OR )
    @RequestMapping(value = "/queryUserRoleMap", method = RequestMethod.GET)
    public Result<Map<String, String>> queryUserRole() {
        Result<Map<String, String>> result = new Result<>();
        Map<String, String> map = userRoleService.queryUserRole();
        result.setResult(map);
        result.setSuccess(true);
        return result;
    }

    /**
     * ??????excel
     *
     * @param request
     */
    @RequiresRoles(value = { "admin","EXAMADMIN" },logical = Logical.OR )
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(SysUser sysUser,HttpServletRequest request) {
        // Step.1 ??????????????????
        QueryWrapper<SysUser> queryWrapper = QueryGenerator.initQueryWrapper(sysUser, request.getParameterMap());
        //Step.2 AutoPoi ??????Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<SysUser> pageList = sysUserService.list(queryWrapper);
        //??????????????????
        mv.addObject(NormalExcelConstants.FILE_NAME, "????????????");
        mv.addObject(NormalExcelConstants.CLASS, SysUser.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("??????????????????", "?????????:Jeecg", "????????????"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * ??????excel????????????
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresRoles(value = { "admin","EXAMADMIN" },logical = Logical.OR )
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// ????????????????????????
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<SysUser> listSysUsers = ExcelImportUtil.importExcel(file.getInputStream(), SysUser.class, params);
                for (SysUser sysUserExcel : listSysUsers) {
                    if (sysUserExcel.getPassword() == null) {
                        // ??????????????????123456???
                        sysUserExcel.setPassword("123456");
                    }
                    sysUserService.save(sysUserExcel);
                }
                return Result.ok("????????????????????????????????????" + listSysUsers.size());
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                return Result.error("??????! ??????????????????????????????????????????.");
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                	log.error(e.getMessage(), e);
                }
            }
        }
        return Result.error("?????????????????????");
    }

    /**
	 * @???????????????id ????????????
	 * @param userIds
	 * @return
	 */
    @RequiresRoles(value = { "admin","EXAMADMIN" },logical = Logical.OR )
	@RequestMapping(value = "/queryByIds", method = RequestMethod.GET)
	public Result<Collection<SysUser>> queryByIds(@RequestParam String userIds) {
		Result<Collection<SysUser>> result = new Result<>();
		String[] userId = userIds.split(",");
		Collection<String> idList = Arrays.asList(userId);
		Collection<SysUser> userRole = sysUserService.listByIds(idList);
		result.setSuccess(true);
		result.setResult(userRole);
		return result;
	}

	/**
     * ??????????????????
     */
    @RequestMapping(value = "/updatePassword", method = RequestMethod.PUT)
    public Result<SysUser> changPassword(@RequestBody JSONObject json) {
        Result<SysUser> result = new Result<SysUser>();
        String username = json.getString("username");
        String oldpassword = json.getString("oldpassword");
        SysUser user = this.sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if(user==null) {
            result.error500("???????????????!");
            return result;
        }
        String passwordEncode = PasswordUtil.encrypt(username, oldpassword, user.getSalt());
        if(!user.getPassword().equals(passwordEncode)) {
            result.error500("?????????????????????!");
            return result;
        }

        String password = json.getString("password");
        String confirmpassword = json.getString("confirmpassword");
        if(oConvertUtils.isEmpty(password)) {
            result.error500("??????????????????!");
            return result;
        }

        if(!password.equals(confirmpassword)) {
            result.error500("???????????????????????????!");
            return result;
        }
        String newpassword = PasswordUtil.encrypt(username, password, user.getSalt());
        this.sysUserService.update(new SysUser().setPassword(newpassword), new LambdaQueryWrapper<SysUser>().eq(SysUser::getId, user.getId()));
        result.success("?????????????????????");
        return result;
    }


    /**
     * ??????????????????
     */
    @RequiresRoles(value = { "admin","EXAMADMIN" },logical = Logical.OR )
    @RequestMapping(value = "/updatePasswordBatch", method = RequestMethod.GET)
    public Result<String> changPasswordBatch(@RequestParam String key) {
        Result<String> result = new Result<>();
        if(!key.equals("Hkang123!")) {
            result.error500("???????????????!");
            return result;
    }
        List<SysUser> sysUserList = sysUserService.list();
        for(int i = 0; i<sysUserList.size();i++){
            SysUser sysUser = sysUserList.get(i);
            if(!sysUser.getUsername().contains("admin")){
                String password = sysUser.getIdcard().substring(sysUser.getIdcard().length()-6,sysUser.getIdcard().length());
                String newpassword = PasswordUtil.encrypt(sysUser.getUsername(), password, sysUser.getSalt());
                this.sysUserService.update(new SysUser().setPassword(newpassword), new LambdaQueryWrapper<SysUser>().eq(SysUser::getId, sysUser.getId()));
            }

        }
        result.success("?????????????????????");
        return result;
    }

    @RequiresRoles(value = { "admin","EXAMADMIN" },logical = Logical.OR )
    @RequestMapping(value = "/userRoleList", method = RequestMethod.GET)
    public Result<IPage<SysUser>> userRoleList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                               @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<SysUser>> result = new Result<IPage<SysUser>>();
        Page<SysUser> page = new Page<SysUser>(pageNo, pageSize);
        String roleId = req.getParameter("roleId");
        String username = req.getParameter("username");
        IPage<SysUser> pageList = sysUserService.getUserByRoleId(page,roleId,username);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * ???????????????????????????
     *
     * @param
     * @return
     */
    @RequiresRoles(value = { "admin","EXAMADMIN" },logical = Logical.OR )
    @RequestMapping(value = "/addSysUserRole", method = RequestMethod.POST)
    public Result<String> addSysUserRole(@RequestBody SysUserRoleVO sysUserRoleVO) {
        Result<String> result = new Result<String>();
        try {
            String sysRoleId = sysUserRoleVO.getRoleId();
            for(String sysUserId:sysUserRoleVO.getUserIdList()) {
                SysUserRole sysUserRole = new SysUserRole(sysUserId,sysRoleId);
                QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<SysUserRole>();
                queryWrapper.eq("role_id", sysRoleId).eq("user_id",sysUserId);
                SysUserRole one = sysUserRoleService.getOne(queryWrapper);
                if(one==null){
                    sysUserRoleService.save(sysUserRole);
                }

            }

            result.setMessage("????????????!");
            result.setSuccess(true);
            return result;
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("?????????: " + e.getMessage());
            return result;
        }
    }
    /**
     *   ?????????????????????????????????
     * @param
     * @return
     */
    @RequiresRoles(value = { "admin","EXAMADMIN" },logical = Logical.OR )
    @RequestMapping(value = "/deleteUserRole", method = RequestMethod.DELETE)
    public Result<SysUserRole> deleteUserRole(@RequestParam(name="roleId") String roleId,
                                                    @RequestParam(name="userId",required=true) String userId
    ) {
        Result<SysUserRole> result = new Result<SysUserRole>();
        try {
            QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<SysUserRole>();
            queryWrapper.eq("role_id", roleId).eq("user_id",userId);
            sysUserRoleService.remove(queryWrapper);
            result.success("????????????!");
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.error500("???????????????");
        }
        return result;
    }

    /**
     * ???????????????????????????????????????
     *
     * @param
     * @return
     */
    @RequiresRoles(value = { "admin","EXAMADMIN" },logical = Logical.OR )
    @RequestMapping(value = "/deleteUserRoleBatch", method = RequestMethod.DELETE)
    public Result<SysUserRole> deleteUserRoleBatch(
            @RequestParam(name="roleId") String roleId,
            @RequestParam(name="userIds",required=true) String userIds) {
        Result<SysUserRole> result = new Result<SysUserRole>();
        try {
            QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<SysUserRole>();
            queryWrapper.eq("role_id", roleId).in("user_id",Arrays.asList(userIds.split(",")));
            sysUserRoleService.remove(queryWrapper);
            result.success("????????????!");
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.error500("???????????????");
        }
        return result;
    }

    /**
     * ??????????????????
     */
    @RequiresRoles(value = { "admin","EXAMADMIN" },logical = Logical.OR )
    @RequestMapping(value = "/departUserList", method = RequestMethod.GET)
    public Result<IPage<SysUser>> departUserList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<SysUser>> result = new Result<IPage<SysUser>>();
        Page<SysUser> page = new Page<SysUser>(pageNo, pageSize);
        String depId = req.getParameter("depId");
        String username = req.getParameter("username");
        IPage<SysUser> pageList = sysUserService.getUserByDepId(page,depId,username);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * ????????????????????????????????????
     */
    @RequiresRoles(value = { "admin","EXAMADMIN" },logical = Logical.OR )
    @RequestMapping(value = "/editSysDepartWithUser", method = RequestMethod.POST)
    public Result<String> editSysDepartWithUser(@RequestBody SysDepartUsersVO sysDepartUsersVO) {
        Result<String> result = new Result<String>();
        try {
            String sysDepId = sysDepartUsersVO.getDepId();
            for(String sysUserId:sysDepartUsersVO.getUserIdList()) {
                SysUserDepart sysUserDepart = new SysUserDepart(null,sysUserId,sysDepId);
                QueryWrapper<SysUserDepart> queryWrapper = new QueryWrapper<SysUserDepart>();
                queryWrapper.eq("dep_id", sysDepId).eq("user_id",sysUserId);
                SysUserDepart one = sysUserDepartService.getOne(queryWrapper);
                if(one==null){
                    sysUserDepartService.save(sysUserDepart);
                }
            }

            result.setMessage("????????????!");
            result.setSuccess(true);
            return result;
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("?????????: " + e.getMessage());
            return result;
        }
    }

    /**
     *   ?????????????????????????????????
     */
    @RequiresRoles(value = { "admin","EXAMADMIN" },logical = Logical.OR )
    @RequestMapping(value = "/deleteUserInDepart", method = RequestMethod.DELETE)
    public Result<SysUserDepart> deleteUserInDepart(@RequestParam(name="depId") String depId,
                                                    @RequestParam(name="userId",required=true) String userId
    ) {
        Result<SysUserDepart> result = new Result<SysUserDepart>();
        try {
            QueryWrapper<SysUserDepart> queryWrapper = new QueryWrapper<SysUserDepart>();
            queryWrapper.eq("dep_id", depId).eq("user_id",userId);
            sysUserDepartService.remove(queryWrapper);
            result.success("????????????!");
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.error500("???????????????");
        }
        return result;
    }

    /**
     * ???????????????????????????????????????
     */
    @RequiresRoles(value = { "admin","EXAMADMIN" },logical = Logical.OR )
    @RequestMapping(value = "/deleteUserInDepartBatch", method = RequestMethod.DELETE)
    public Result<SysUserDepart> deleteUserInDepartBatch(
            @RequestParam(name="depId") String depId,
            @RequestParam(name="userIds",required=true) String userIds) {
        Result<SysUserDepart> result = new Result<SysUserDepart>();
        try {
            QueryWrapper<SysUserDepart> queryWrapper = new QueryWrapper<SysUserDepart>();
            queryWrapper.eq("dep_id", depId).in("user_id",Arrays.asList(userIds.split(",")));
            sysUserDepartService.remove(queryWrapper);
            result.success("????????????!");
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.error500("???????????????");
        }
        return result;
    }

}
