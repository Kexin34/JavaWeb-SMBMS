package com.kexin.service.user;

import com.kexin.dao.BaseDao;
import com.kexin.dao.user.UserDao;
import com.kexin.dao.user.UserDaoImpl;
import com.kexin.pojo.User;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;


/**
 * service层捕获异常，进行事务处理
 * 事务处理：调用不同dao的多个方法，必须使用同一个connection（connection作为参数传递）
 * 事务完成之后，需要在service层进行connection的关闭，在dao层关闭（PreparedStatement和ResultSet对象）
 * @author Administrator
 *
 */

public class UserServiceImpl implements UserService {
    // 业务层都会调用dao层，所以我们要引入dao层
    private UserDao userDao;
    // （一旦UserServiceImpl被new出来的时候，userDao就被实体化了，可以直接用了）
    public UserServiceImpl() {
        userDao = new UserDaoImpl();
    }

    public User login(String userCode, String userPassword) {
        Connection connection = null;
        User user = null;

        try {
            connection = BaseDao.getConnection();
            // 通过业务层调用对应的具体数据库操作
            user = userDao.getLoginUser(connection, userCode);
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            BaseDao.closeResource(connection, null, null);
        }
        //匹配密码
        if(user != null){
            if(user.getUserPassword() == null || !user.getUserPassword().equals(userPassword))
                user = null;
        }
        return user;
    }

    public boolean updatePwd(int id, String pwd) {
        Connection connection = null;
        boolean flag = false;
        // 修改密码
        try {
            connection = BaseDao.getConnection();
            if (userDao.updatePwd(connection, id, pwd) > 0) {
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection, null, null);
        }
        return flag;
    }

    @Test
    public void test() {
        UserServiceImpl userService = new UserServiceImpl();
        User admin = userService.login("admin", "1234567");
        System.out.println(admin.getUserPassword());
    }
}
