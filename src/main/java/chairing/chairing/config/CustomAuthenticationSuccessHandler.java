package chairing.chairing.config;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import chairing.chairing.domain.user.UserRole;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
                                        Authentication authentication) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // 사용자 역할에 따라 다른 경로로 리다이렉트
        String redirectUrl = "/";

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            
            if (role.equals(UserRole.ADMIN.name())) {
                redirectUrl = "/admin";
                break;
            } else if (role.equals(UserRole.GUARDIAN.name())) {
                redirectUrl = "/guardian";
                break;
            } else if (role.equals(UserRole.NORMAL.name())) {
                redirectUrl = "/normal";
                break;
            }
        }

        response.sendRedirect(redirectUrl);
    }
}
