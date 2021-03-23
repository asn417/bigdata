package com.asn.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;

/**
 * 过滤器：
 */
@Component
public class MyFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("初始化过滤器");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("过滤器对request的处理");
        filterChain.doFilter(servletRequest,servletResponse);
        System.out.println("过滤器对response的处理");
    }

    @Override
    public void destroy() {
        System.out.println("销毁过滤器，释放资源");
    }
}
