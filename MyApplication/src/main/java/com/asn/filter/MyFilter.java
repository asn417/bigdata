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
        System.out.println("初始化过滤器");//应用启动时就会初始化
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("过滤器对request的处理");//调用被过滤的方法前执行
        filterChain.doFilter(servletRequest,servletResponse);//执行方法
        System.out.println("过滤器对response的处理");//方法执行结束后再执行此部分
    }

    @Override
    public void destroy() {
        System.out.println("销毁过滤器，释放资源");
    }
}
