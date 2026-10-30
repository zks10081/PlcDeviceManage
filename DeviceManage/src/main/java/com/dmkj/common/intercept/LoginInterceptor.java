package com.dmkj.common.intercept;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.dmkj.common.aspect.annotation.Auth;
import com.dmkj.common.result.BadRequestException;
import com.dmkj.util.JwtUtil;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,Object handler){

        //判断处理器是否为HandlerMethod 类型，即是否是一个方法处理器
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 检查处理器方法所属的 bean 是否为 BasicErrorController 类型，如果是，则直接放行
            if(handlerMethod.getBean() instanceof BasicErrorController){
                return true;
            }
            //如果有auth注解，则该方法跳过认证
            Auth auth = handlerMethod.getMethod().getAnnotation(Auth.class);
            //注解存在，且不需认证，跳过
            if(auth!=null && auth.require()){
                return true;
            }else {
                //取出token进行认证
                String token = request.getHeader("token");
                if(StringUtils.isNotBlank(token)){
                    if(JwtUtil.parseToken(token)){
                        return true;
                    }else {
                        throw new BadRequestException("认证失败！");
                    }
                }else {
                    throw new BadRequestException("无效认证请重试！");
                }

            }
        } else {
            // 非 HandlerMethod 类型的处理器，直接放行
            return true;
        }
    }

}
