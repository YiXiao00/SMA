package com.smarthome.sso.web.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SigninInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private RedisTemplate redisTemplate;

    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            String inputSession = "";
            for (Cookie cookie: cookies){
                if ("sessionId".equals(cookie.getName())){
                    inputSession = cookie.getValue();
                    break;
                }
            }
            if (!"".equals(inputSession)){
                String record = (String)redisTemplate.opsForValue().get(inputSession);
                if (!("".equals(record))){
                    return true;    //already signed in
                }

            }
        }
        //not signed in
        response.sendRedirect("/homepage");
        return true;
    }
}
