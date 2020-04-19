package org.sang.config;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class LogFilter {
    
    //    public static Log log = LogFactory.getLog(LogFilter.class);
    
    //    @Bean
    //    public RemoteIpFilter remoteIpFilter() {
    //        return new RemoteIpFilter();
    //    }
    
    @Bean
    public FilterRegistrationBean testFilterRegistration() {
        
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new MyFilter());
        registration.addUrlPatterns("/*");
        registration.addInitParameter("paramName", "paramValue");
        registration.setName("MyFilter");
        registration.setOrder(1);
        return registration;
    }
    
    public class MyFilter implements Filter {
        
        private ObjectMapper jackson = null;
        
        @Override
        public void destroy() {
            //            log.info("filter destory");
            // TODO Auto-generated method stub
        }
        
        @Override
        public void doFilter(ServletRequest srequest, ServletResponse sresponse,
            FilterChain filterChain) throws IOException, ServletException {
            // TODO Auto-generated method stub
            HttpServletRequest request = (HttpServletRequest)srequest;
            System.err.println("this is MyFilter,url :" + request.getRequestURI());
            //            log.info("filter do before");
            Cookie[] cookies = request.getCookies();
            
//            System.err.println("方法名:" + );
            System.err.println("方法类型:" +request.getMethod());
            System.err.println("请求ip :" +request.getHeader("X-Forwarded-For"));
            Map<String, String[]> parameterMap = request.getParameterMap();
            System.err.println("请求入参 :" +jackson.writeValueAsString(parameterMap));
            
            
            filterChain.doFilter(srequest, sresponse);
            //            log.info("filter do after");
            System.err.println("请求结束了 :" + request.getRequestURI());
        }
        
        @Override
        public void init(FilterConfig arg0) throws ServletException {
            //            log.info("filter init");
            // TODO Auto-generated method stub
            
             this.jackson = new ObjectMapper();
        }
    }
}