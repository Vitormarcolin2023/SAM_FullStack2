package com.br.SAM_FullStack.SAM_FullStack.autenticacao;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class JwtFilter implements Filter {

    private TokenService tokenService;

    public void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String path = req.getRequestURI(); // pega a URL da requisição

        // Ignora login (e outras rotas públicas, se quiser)
        if (path.equals("/auth/login") ||
                path.equals("/areas/findAll") ||
                path.equals("/mentores/save") ||
                path.equals("/alunos/save")) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = req.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String email = tokenService.validateToken(token);
                req.setAttribute("usuarioLogado", email);
            } catch (Exception e) {
                throw new ServletException("Token inválido ou expirado");
            }
        } else {
            throw new ServletException("Token ausente");
        }

        chain.doFilter(request, response);
    }

}

