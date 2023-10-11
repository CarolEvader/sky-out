package com.sky.filter;


import com.alibaba.fastjson.JSONObject;
import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.entity.LoginUser;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import com.sky.utils.RedisCache;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private RedisCache redisCache;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = request.getHeader("Token");
        if(!StringUtils.isNotEmpty(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
        String userId = claims.get(JwtClaimsConstant.EMP_ID).toString();

        JSONObject jsonObject = redisCache.getCacheObject("login:" + userId);

        LoginUser loginUser = jsonObject.toJavaObject(LoginUser.class);

        if(Objects.isNull(loginUser)) {
            throw new RuntimeException("用户未登录");
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser, null, null);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        BaseContext.setCurrentId(loginUser.getUser().getId());

        filterChain.doFilter(request, response);
    }
}
