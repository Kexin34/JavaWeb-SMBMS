package com.kexin.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * 操作数据库的基类--静态类
 * @author Administrator
 *
 */
public class BaseDao {
    //静态代码块,在类加载的时候就初始化
    static{
        init();
    }

    private static String driver;
    private static String url;
    private static String user;
    private static String password;

    //初始化连接参数,从配置文件里获得
    public static void init(){
        Properties params = new Properties();
        String configFile = "db.properties";
        // 通过类加载器读取对应的资源
        InputStream is = BaseDao.class.getClassLoader().getResourceAsStream(configFile);
        try {
            params.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        driver = params.getProperty("driver");
        url = params.getProperty("url");
        user = params.getProperty("user");
        password = params.getProperty("password");

    }


    /**
     * 获取数据库的连接
     * @return connection
     */
    public static Connection getConnection(){
        Connection connection = null;
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return connection;
    }

    /**
     * 编写查询公共类, 查询操作
     * @param connection
     * @param pstm
     * @param rs
     * @param sql
     * @param params
     * @return rs
     */
    public static ResultSet execute(Connection connection, PreparedStatement pstm, ResultSet rs,
                                    String sql, Object[] params) throws Exception{
        // 预编译的sql，在后面直接执行就可以了，不需要作为参数放入
        pstm = connection.prepareStatement(sql);
        for(int i = 0; i < params.length; i++){
            // setObject 占位符从1开始，但是我们的数组是从0开始
            pstm.setObject(i + 1, params[i]);
        }
        rs = pstm.executeQuery();    // 执行sql
        return rs;
    }

    /**
     * 编写增删改公共方法 -> 统一为更新操作
     * @param connection
     * @param pstm
     * @param sql
     * @param params
     * @return
     * @throws Exception
     */
    public static int execute(Connection connection,PreparedStatement pstm,
                              String sql,Object[] params) throws Exception{
        int updateRows = 0;
        pstm = connection.prepareStatement(sql);
        for(int i = 0; i < params.length; i++){
            pstm.setObject(i + 1, params[i]);
        }
        updateRows = pstm.executeUpdate();
        return updateRows;
    }

    /**
     * 关闭连接，释放资源
     * @param connection
     * @param pstm
     * @param rs
     * @return
     */
    public static boolean closeResource(Connection connection, PreparedStatement pstm, ResultSet rs){
        boolean flag = true;

        if(rs != null){   // 存在，所以要关闭
            try {
                rs.close();
                rs = null;  // 万一还存在，让GC回收
            } catch (SQLException e) {
                e.printStackTrace();
                flag = false;   // 关闭如果失败，说明释放没有成功
            }
        }
        if(pstm != null){
            try {
                pstm.close();
                pstm = null;   //GC回收
            } catch (SQLException e) {
                e.printStackTrace();
                flag = false;
            }
        }
        if(connection != null){
            try {
                connection.close();
                connection = null;  //GC回收
            } catch (SQLException e) {
                e.printStackTrace();
                flag = false;
            }
        }

        return flag;
    }

}
