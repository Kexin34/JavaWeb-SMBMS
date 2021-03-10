package com.kexin.servlet.user;

import com.alibaba.fastjson.JSONArray;
import com.kexin.pojo.Role;
import com.kexin.pojo.User;
import com.kexin.service.role.RoleServiceImpl;
import com.kexin.service.user.UserService;
import com.kexin.service.user.UserServiceImpl;
import com.kexin.util.Constants;
import com.kexin.util.PageSupport;
import com.mysql.cj.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.PrintWriter;

// 实现Servlet复用
public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        if (method != null && method.equals("savepwd")){
            this.updatePwd(req, resp);
        } else if (method != null && method.equals("pwdmodify")){
            this.pwdModify(req, resp);
        }else if(method != null && method.equals( "query") ){
            this.query(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    // 修改密码
    private void updatePwd(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // 从session里面拿ID:
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);

        String newpassword = req.getParameter("newpassword");

        boolean flag = false;

        if (o != null && !StringUtils.isNullOrEmpty(newpassword)){
            UserService userService = new UserServiceImpl();
            flag = userService.updatePwd(((User)o).getId(), newpassword);
            if (flag) {
                req.setAttribute("message", "修改密码成功，请退出，使用新密码登陆");
                // 密码修改成功，移除当前session
                req.getSession().removeAttribute(Constants.USER_SESSION);
            }else {
                req.setAttribute("message", "修改密码失败");
                // 密码修改失败，移除当前session
            }
        } else {
            req.setAttribute("message", "新密码有问题");
        }
        req.getRequestDispatcher("pwdmodify.jsp").forward(req, resp);
    }

    // 验证旧密码，session中有用户的密码
    private void pwdModify(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Object o = request.getSession().getAttribute(Constants.USER_SESSION);
        String oldpassword = request.getParameter("oldpassword");

        // map：结果集
        Map<String, String> resultMap = new HashMap<String, String>();

        if(o == null ){   //session过期
            resultMap.put("result", "sessionerror");
        }else if(StringUtils.isNullOrEmpty(oldpassword)){//旧密码输入为空
            resultMap.put("result", "error");
        }else{
            String sessionPwd = ((User)o).getUserPassword(); // session中的用户老密码
            System.out.println("sessionPwd:" + sessionPwd);
            System.out.println("user type oldpassword:" + oldpassword);
            if(oldpassword.equals(sessionPwd)){
                resultMap.put("result", "true");
            }else{//旧密码输入不正确
                resultMap.put("result", "false");
            }
        }

        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        /*
         * resultMap = ["result","sessionerror","result",error]
         * josn格式={key,value
         */
        // JSONArray阿里巴巴的JSON工具，转换格式
        writer.write(JSONArray.toJSONString(resultMap));
        writer.flush();
        writer.close();
    }

    //重点、难点
    private void query(HttpServletRequest req, HttpServletResponse resp) {
        // 查询用户列表

        // 1.从前端获取数据：
        // 查询用户列表,角色
        String queryUserName = req.getParameter("queryname");
        String temp = req.getParameter("queryUserRole"); // 临时
        String pageIndex = req.getParameter("pageIndex");
        int queryUserRole = 0;

        // 2.获取用户列表
        UserServiceImpl userService = new UserServiceImpl();
        List<User> userList = null;

        // 第一此请求肯定是走第一页，页面大小固定的
        // 设置页面容量
        int pageSize = Constants.pageSize;;  // 把它设置在配置文件里,后面方便修改
        //当前页码
        int currentPageNo = 1;

        // 3. 判断一下前端输入
        if(queryUserName == null){
            queryUserName = "";  // 如果未空，手动赋值
        }
        if(temp != null && !temp.equals("")){ // 保证下拉框获取不为空
            queryUserRole = Integer.parseInt(temp);  // 给查询role赋值：0，1，2，3...
        }
        if(pageIndex != null) {   // 前端给pageIndex，默认是1
            currentPageNo = Integer.parseInt(pageIndex);
        }

        // 获取用户总数（分页	上一页：下一页的情况）
        // 总数量（表）
        int totalCount	= userService.getUserCount(queryUserName, queryUserRole);

        // 总页数支持
        PageSupport pageSupport = new PageSupport();
        pageSupport.setCurrentPageNo(currentPageNo);
        pageSupport.setPageSize(pageSize);
        pageSupport.setTotalCount(totalCount);

        int totalPageCount = pageSupport.getTotalPageCount();//总共有几页
        //(totalCount+pageSize-1/pageSize)取整
        // pageSupport.getTotalCount()

        //System.out.println("totalCount ="+totalCount);
        //System.out.println("pageSize ="+pageSize);
        //System.out.println("totalPageCount ="+totalPageCount);

        //控制首页和尾页
        //如果页面小于 1 就显示第一页的东西
        if(currentPageNo < 1) {
            currentPageNo = 1;
        }else if(currentPageNo > totalPageCount) {//如果页面大于了最后一页就显示最后一页
            currentPageNo = totalPageCount;
        }

        // 获取用户列表展示，前端可以获取list
        userList = userService.getUserList(queryUserName, queryUserRole, currentPageNo, pageSize);
        req.setAttribute("userList", userList);

        RoleServiceImpl roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();
        req.setAttribute("roleList", roleList);
        req.setAttribute("totalCount", totalCount);
        req.setAttribute("currentPageNo", currentPageNo);
        req.setAttribute("totalPageCount", totalPageCount);
        req.setAttribute("queryUserName", queryUserName);
        req.setAttribute("queryUserRole", queryUserRole);

        //返回前端
        try {
            req.getRequestDispatcher("userlist.jsp").forward(req, resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
