package com.br.SAM_FullStack.SAM_FullStack.autenticacao;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<JwtFilter> jwtFilter(TokenService tokenService) {
        FilterRegistrationBean<JwtFilter> registrationBean = new FilterRegistrationBean<>();

        JwtFilter filter = new JwtFilter();
        filter.setTokenService(tokenService); // injetando o TokenService manualmente

        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/*"); // todas as rotas protegidas
        registrationBean.setOrder(1); // ordem do filtro, se houver outros
        return registrationBean;
    }
}

