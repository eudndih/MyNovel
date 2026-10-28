package com.novel.interceptor;


import com.novel.utils.JWTUtils;
import com.novel.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        UserContext.remove();
        String authorization = request.getHeader("Authorization");
        if (authorization == null ||
                !authorization.startsWith("Bearer ")) {
            return true;
        }
        try {
            String token = authorization.substring(7);
            Long userId = Long.valueOf(JWTUtils.parseToken(token));
            UserContext.setUserId(userId);
        } catch (Exception e) {
            UserContext.remove();
        }
        return true;
    }
    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        UserContext.remove();
    }
}
