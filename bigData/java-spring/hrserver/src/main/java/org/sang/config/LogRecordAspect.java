package org.sang.config;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Aspect
@Configuration //定义一个切面
public class LogRecordAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(LogRecordAspect.class);
    
    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;
    
    // 定义切点Pointcut
    @Pointcut("execution(* org.sang.controller..*.*(..))")
    public void excudeService() {
    }
    
    @Around("excudeService()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes)ra;
        HttpServletRequest request = sra.getRequest();
        
        String url = request.getRequestURL().toString();
        String methodType = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        Object[] args = pjp.getArgs();
        String params = "";
        ObjectMapper jackson = new ObjectMapper();
        //获取请求参数集合并进行遍历拼接
        if (args.length > 0) {
            if ("POST".equals(methodType)) {
                Object object = args[0];
                Map map = getKeyAndValue(object);
                params = jackson.writeValueAsString(map);
                
            }
            else if ("GET".equals(methodType)) {
                params = queryString;
            }
        }
        
        logger.info("请求开始===地址:" + url);
        logger.info("请求开始===类型:" + methodType);
        logger.info("请求开始===参数:" + params);
        
        // result的值就是被拦截方法的返回值
        Object result = pjp.proceed();
        //        Gson gson = new Gson();
        logger.info("请求结束===返回值:" + jackson.writeValueAsString(result));
        queryMethod(url, methodType);
        
        System.err.println("请求开始===地址:" + url);
        System.err.println("请求开始===类型:" + methodType);
        System.err.println("请求开始===参数:" + params);
        System.err.println("请求结束===返回值:" + jackson.writeValueAsString(result));
        return result;
    }
    
    private void queryMethod(String urlStr, String methodType) {
        
        List<HashMap<String, String>> urlList = new ArrayList<HashMap<String, String>>();
        
        HashMap<String, String> hashMapName = new HashMap<String, String>();
        
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> m : map.entrySet()) {
            
            HashMap<String, String> hashMap = new HashMap<String, String>();
            RequestMappingInfo info = m.getKey();
            HandlerMethod method = m.getValue();
            PatternsRequestCondition p = info.getPatternsCondition();
            for (String url : p.getPatterns()) {
                hashMap.put("url", url);
            }
            hashMap.put("className", method.getMethod().getDeclaringClass().getName()); // 类名  
            hashMap.put("method", method.getMethod().getName()); // 方法名  
            RequestMethodsRequestCondition methodsCondition = info.getMethodsCondition();
            String type = methodsCondition.toString();
            if (type != null && type.startsWith("[") && type.endsWith("]")) {
                type = type.substring(1, type.length() - 1);
                hashMap.put("type", type); // 方法名  
            }
            urlList.add(hashMap);
        }
        ObjectMapper jackson = new ObjectMapper();
        try {
            for (HashMap<String, String> hashMap : urlList) {
                System.err.println(jackson.writeValueAsString(hashMap));
            }
            logger.info("对应关系:" + jackson.writeValueAsString(urlList));
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
    
    public static Map<String, Object> getKeyAndValue(Object obj) {
        Map<String, Object> map = new HashMap<>();
        // 得到类对象
        Class userCla = (Class)obj.getClass();
        /* 得到类中的所有属性集合 */
        Field[] fs = userCla.getDeclaredFields();
        for (int i = 0; i < fs.length; i++) {
            Field f = fs[i];
            f.setAccessible(true); // 设置些属性是可以访问的
            Object val = new Object();
            try {
                val = f.get(obj);
                // 得到此属性的值
                map.put(f.getName(), val);// 设置键值
            }
            catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            
        }
        return map;
    }
}