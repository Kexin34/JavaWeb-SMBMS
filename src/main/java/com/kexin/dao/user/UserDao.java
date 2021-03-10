package com.kexin.dao.user;

import com.kexin.pojo.User;

import java.sql.Connection;
import java.sql.SQLException;


public interface UserDao {

    // 得到要登陆的用户
    public User getLoginUser(Connection connection, String userCode) throws Exception;

    // 修改当前用户密码
    public int updatePwd(Connection connection, int id, String password) throws SQLException;
}
