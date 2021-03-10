package com.kexin.service.role;

import com.kexin.dao.BaseDao;
import com.kexin.dao.role.RoleDao;
import com.kexin.dao.role.RoleDaoImpl;
import com.kexin.pojo.Role;
import org.junit.Test;

import java.sql.Connection;
import java.util.List;



public class RoleServiceImpl implements RoleService{
	// 引入Dao，私有
	private RoleDao roleDao;
	// 通过私有构造器来赋值
	public RoleServiceImpl(){
		roleDao = new RoleDaoImpl();
	}

	public List<Role> getRoleList() {
		Connection connection = null;
		List<Role> roleList = null;
		try {
			connection = BaseDao.getConnection();
			roleList = roleDao.getRoleList(connection);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			BaseDao.closeResource(connection, null, null);
		}
		return roleList;
	}

	@Test
	public void test() {
		RoleServiceImpl roleService = new RoleServiceImpl();
		List<Role> roleList = roleService.getRoleList();
		for (Role role: roleList){
			System.out.println(role.getRoleName());
		}
	}
}