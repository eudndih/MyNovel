package com.novel.interceptor;

import com.novel.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        // 只公开精确匹配的只读资源，不能将所有 GET 请求一律放行。
        if ("POST".equals(request.getMethod()) && path.matches("/api/chapters/\\d+/read/?")) return true;
        if ("GET".equals(request.getMethod()) && (
                path.matches("/api/novels(?:/\\d+(?:/chapters)?)?/?") ||
                path.matches("/api/chapters/\\d+/?") ||
                path.matches("/api/comments/novel/\\d+/?"))) return true;
        Long userId = UserContext.getUserId();
        if(userId == null){
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    "{\"code\":401,\"message\":\"请先登录\",\"data\":null}");
            return false;
        }
        return true;
    }
}
