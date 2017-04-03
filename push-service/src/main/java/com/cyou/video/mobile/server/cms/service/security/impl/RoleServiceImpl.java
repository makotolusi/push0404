package com.cyou.video.mobile.server.cms.service.security.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.cyou.video.mobile.server.cms.common.Consts;
import com.cyou.video.mobile.server.cms.model.VerifyException;
import com.cyou.video.mobile.server.cms.model.security.Manager;
import com.cyou.video.mobile.server.cms.model.security.Role;
import com.cyou.video.mobile.server.cms.service.security.RoleService;
import com.cyou.video.mobile.server.common.Constants;

/**
 * CMS角色业务实现
 * 
 * @author jyz
 */
@Service("roleService")
public class RoleServiceImpl implements RoleService {

  @Autowired
  private MongoOperations mongoTemplate;

  @Override
  public List<Role> listRole(HttpServletRequest request) throws Exception {
    List<Role> list = mongoTemplate.findAll(Role.class);
    Manager manager = (Manager) request.getSession().getAttribute(Consts.SESSION_MANAGER);
    // if(manager.getId() > 1) { //如果当前登录的是超级管理员，则去掉超级管理员角色
    // if(list != null && list.size() > 0) {
    // list.remove(0);
    // }
    // }

    return list;
  }

  @Override
  public void addAppIds(String roleId, List<Integer> appIds) throws Exception {
    Role role = this.getRoleById(roleId);
    if(role.getAppIds() == null || role.getAppIds().isEmpty()) {
      role.setAppIds(appIds);
    }
    else {
      role.getAppIds().addAll(appIds);
    }
    mongoTemplate.save(role);
  }

  @Override
  public void removeAppIds(String roleId, List<Integer> appIds) throws Exception {
    Role role = this.getRoleById(roleId);
    role.getAppIds().removeAll(appIds);
    mongoTemplate.save(role);
  }

  @Override
  public Role getRoleById(String id) throws Exception {
    return mongoTemplate.findOne(new Query(new Criteria("id").is(id)), Role.class);
  }

  @Override
  public void createRole(Role role) throws Exception {
    if(role == null) {
      throw new VerifyException(Constants.CUSTOM_ERROR_CODE.PARAMATER_MISSING.getValue(),
          Constants.CUSTOM_ERROR_CODE.PARAMATER_MISSING.toString() + "_role");
    }
    mongoTemplate.save(role);
  }

  @Override
  public void updateRole(Role role) throws Exception {
    if(role == null) {
      throw new VerifyException(Constants.CUSTOM_ERROR_CODE.PARAMATER_MISSING.getValue(),
          Constants.CUSTOM_ERROR_CODE.PARAMATER_MISSING.toString() + "_role");
    }
    if(StringUtils.isBlank(role.getName())) {
      throw new VerifyException(Constants.CUSTOM_ERROR_CODE.PARAMATER_MISSING.getValue(),
          Constants.CUSTOM_ERROR_CODE.PARAMATER_MISSING.toString() + "_role.name");
    }
    // if(role.getId() < 1) {
    // throw new
    // VerifyException(Constants.CUSTOM_ERROR_CODE.PARAMATER_INVALID.getValue(),
    // Constants.CUSTOM_ERROR_CODE.PARAMATER_INVALID.toString() + "_role.id");
    // }
    // Role reference = roleDao.getRoleById(role.getId());
    // if(reference == null) {
    // throw new
    // VerifyException(Constants.CUSTOM_ERROR_CODE.OBJECT_NOT_FOUND.getValue(),
    // Constants.CUSTOM_ERROR_CODE.OBJECT_NOT_FOUND.toString() + "_role");
    // }
    // if(! reference.getName().equals(role.getName())) { //用名称来判断唯一性
    // if(roleDao.getRole(role.getName()) != null) {
    // throw new
    // VerifyException(Constants.CUSTOM_ERROR_CODE.UNIQUENESS_CONSTRAINT.getValue(),
    // Constants.CUSTOM_ERROR_CODE.UNIQUENESS_CONSTRAINT.toString() +
    // "_role.name");
    // }
    // reference.setName(role.getName());
    // roleDao.updateRole(reference);
    // }
  }

  @Override
  public void deleteRole(Role role) throws Exception {
    if(role.getId() == null) {
      throw new VerifyException(Constants.CUSTOM_ERROR_CODE.PARAMATER_INVALID.getValue(),
          Constants.CUSTOM_ERROR_CODE.PARAMATER_INVALID.toString() + "_role.id");
    }
    mongoTemplate.updateMulti(new Query(new Criteria("roles").in(role)), new Update().pull("roles", role), Manager.class);
    mongoTemplate.remove(role);
  }

  @Override
  public void saveRoleOperationRela(int roleId, List<String> operationId) throws Exception {
    if(roleId < 1) {
      throw new VerifyException(Constants.CUSTOM_ERROR_CODE.PARAMATER_INVALID.getValue(),
          Constants.CUSTOM_ERROR_CODE.PARAMATER_INVALID.toString() + "_role.id");
    }
    Role reference = null;// roleDao.getRoleById(roleId);
    if(reference == null) {
      throw new VerifyException(Constants.CUSTOM_ERROR_CODE.OBJECT_NOT_FOUND.getValue(),
          Constants.CUSTOM_ERROR_CODE.OBJECT_NOT_FOUND.toString() + "_role");
    }
    if(operationId == null) {
      operationId = new ArrayList<String>();
    }
  }
}
