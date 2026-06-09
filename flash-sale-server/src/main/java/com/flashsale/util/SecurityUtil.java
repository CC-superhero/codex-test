package com.flashsale.util;

import com.flashsale.common.BizException;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Claims) {
            Claims claims = (Claims) auth.getPrincipal();
            return claims.get("userId", Long.class);
        }
        throw new BizException(401, "未登录");
    }
}
