package com.dmkj.util;

import com.dmkj.common.result.BadRequestException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 提供Jwt工具类
 * 提供Token生成和验证方法
 * @author mxf
 */
@Component
public class JwtUtil {
    // 密钥
    private static final String KEY = "3f2e1d4c5b6a79808f7e6d5c4b3a29181716151413121110";

    public static void main(String[] args) {
        Map<String, Object> claims = new HashMap<>();
        //令牌信息
        claims.put("name", "John Doe");
        String token = getToken(claims);
        parseToken(token);
    }

    /**
     * 获取JWT令牌
     * @return
     */
    public static String getToken(Map map){
        Map headers = new HashMap();
        //设置签名算法
        headers.put("alg", "HS256");
        //设置令牌类型
        headers.put("typ", "JWT");
        //生成令牌
        String jwt = Jwts.builder()
                //设置头信息
                .setHeaderParams(headers)
                //设置负载信息
                .setClaims(map)
                //使用HS256算法和密钥对JWT进行签名
                .signWith(SignatureAlgorithm.HS256,KEY)
                //将之前设置的头部信息、负载信息和签名信息组合成一个完整的JWT，并以字符串形式返回
                .compact();
        return jwt;
    }

    /**
     * 解析JWT令牌
     * @param token token
     */
    public static boolean parseToken(String token){
        try{
            //创建一个解析器
            Claims claims = Jwts.parser()
                    //设置验证签名的密钥
                    .setSigningKey(KEY)
                    //传入需要进行解析的token并解析
                    .parseClaimsJws(token)
                    //从Jws<Claims>对象中获取负载信息
                    .getBody();
            return true;
        }catch (Exception e){
            throw new BadRequestException("无效认证请重新登录");
        }
    }


}

