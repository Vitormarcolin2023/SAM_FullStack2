package com.br.SAM_FullStack.SAM_FullStack.autenticacao;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class JwtFilter implements Filter {

    @Autowired
    private TokenService tokenService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String authHeader = req.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String email = tokenService.validateToken(token);
                req.setAttribute("usuarioLogado", email);
            } catch (Exception e) {
                throw new ServletException("Token inválido ou expirado");
            }
        }
        chain.doFilter(request, response);
    }
}

