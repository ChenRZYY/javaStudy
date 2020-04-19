package org.sang.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by sang on 2017/12/29.
 */
@RestController
@RequestMapping("/")
public class RegLoginController {
    
    @RequestMapping("/login_p")
    public ModelAndView login() {
//        return "redirect:/index.html";
        ModelAndView mav=new ModelAndView(""); // 绝对路径OK
        //ModelAndView mav=new ModelAndView("forwardTarget"); // 相对路径也OK
//        mav.addObject("param1", "value1");
//        mav.addObject("param2", "value2");
        mav.setViewName("redirect:/index.html#/");

        return mav ;
    }
    
    /*
     * forward 示例: 以字符串的形式构建目标url, url 需要加上 forward: 前缀
     * */
    @RequestMapping("/forwardTest1")
    public String forwardTest1() {
        return "forward:/index.html";
    }
    
    //    @RequestMapping("/login_p")
    //    public RespBean login() {
    //        return RespBean.error("尚未登录，请登录!");
    //    }
    //    
    @GetMapping("/employee/advanced/hello")
    public String hello() {
        return "hello";
    }
    
    @GetMapping("/employee/basic/hello")
    public String basicHello() {
        return "basicHello";
    }
}
