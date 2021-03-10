package com.kexin.service.user;

import com.kexin.pojo.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface UserService {
    /**
     * 增加用户信息
     * @param user
     * @return
     */
    //public boolean add(User user);

    // 用户登录
    public User login(String userCode, String userPassword);

    //根据userId修改密码
    public boolean updatePwd(int id, String pwd);

    //根据条件查询用户列表
    //public List<User> getUserList(String queryUserName, int queryUserRole, int currentPageNo, int pageSize);

     //根据条件查询用户表记录数
    //public int getUserCount(String queryUserName, int queryUserRole);

    /**
     * 根据userCode查询出User
     * @param userCode
     * @return
     */
    //public User selectUserCodeExist(String userCode);

    /**
     * 根据ID删除user
     * @param delId
     * @return
     */
    //public boolean deleteUserById(Integer delId);

    /**
     * 根据ID查找user
     * @param id
     * @return
     */
    //public User getUserById(String id);

    /**
     * 修改用户信息
     * @param user
     * @return
     */
    //public boolean modify(User user);


}
