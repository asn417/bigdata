package com.asn.demo.config;
 
import com.asn.demo.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
 
/**
 * WebSecurity 配置类
 * Spring Security 的配置类，该类的三个注解分别是标识该类是配置类、开启 Security 服务、开启全局 Securtiy 注解。
 * 首先将我们自定义的 userDetailsService 注入进来，在 configure() 方法中使用 auth.userDetailsService() 方法替换掉默认的 userDetailsService。
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
@Autowired
private CustomUserDetailsService userDetailsService;
 
@Autowired
private CustomPasswordEncoder customPasswordEncoder;
 
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
auth.userDetailsService(userDetailsService).passwordEncoder(customPasswordEncoder);
}
 
@Override
protected void configure(HttpSecurity http) throws Exception {
    //使用SpringScurity提供的默认登录页面，访问/login时转到登录页面
    //http.formLogin();
    //http.logout().logoutSuccessUrl("/");
    http.authorizeRequests()
        //不需要身份认证
        .antMatchers("/", "/index", "/login").permitAll()
        .antMatchers("/js/**", "/css/**", "/images/**").permitAll()
        //拥有某种角色才能访问
        .antMatchers("/user/**").hasAnyRole("USER")
        .antMatchers("/admin/**").hasAnyRole("ADMIN")
        // 需要登录才能访问
        .anyRequest().authenticated()
        .and()
        // 设置登录页
        .formLogin().loginPage("/login").failureUrl("/login?error").permitAll()
        // 设置登录成功页
        .defaultSuccessUrl("/")
        // 自定义登录用户名和密码参数，默认为username和password
        // .usernameParameter("username")
        // .passwordParameter("password")
        .and()
        .logout().permitAll();
 
        // 关闭CSRF跨域
        http.csrf().disable();
    }
 
    @Override
    public void configure(WebSecurity web) throws Exception {
        // 设置拦截忽略文件夹，可以对静态资源放行
        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**");
    }
}