package com.kexin.filter;

import com.kexin.pojo.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * 判断是否退出登录
 * */
public class SysFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        System.out.println("SysFilter doFilter()===========");
        HttpServletRequest rq = (HttpServletRequest) request;
        HttpServletResponse rp = (HttpServletResponse) response;

        // 过滤器，从Session中获取用户
        User userSession = (User) rq.getSession().getAttribute("userSession");

        if(userSession == null){  // 已经被移除或者注销了，或者未登录
            rp.sendRedirect("/smbms/error.jsp");
            // rp.sendRedirect(rq.getContextPath()+"/error.jsp");
        }else{
            chain.doFilter(request, response);
        }
    }

    public void destroy() {

    }

}
