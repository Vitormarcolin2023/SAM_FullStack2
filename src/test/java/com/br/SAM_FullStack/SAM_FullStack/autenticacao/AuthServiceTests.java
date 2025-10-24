package com.br.SAM_FullStack.SAM_FullStack.autenticacao;

import com.br.SAM_FullStack.SAM_FullStack.dto.LoginDTO;
import com.br.SAM_FullStack.SAM_FullStack.dto.RespostaLoginDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    private LoginDTO loginDTO;
    private final String email = "user@email.com";
    private final String senha = "123456";

    @BeforeEach
    void setup() {
        loginDTO = new LoginDTO(email, senha);
    }

    @Test
    @DisplayName("Deve realizar login com Aluno e retornar token e dados corretos")
    void login_quandoUsuarioAluno_deveRetornarTokenERoleAluno() {
        Aluno aluno = new Aluno();
        aluno.setEmail(email);
        aluno.setSenha(senha);
        aluno.setNome("Joana");
        aluno.setAuthorities(List.of(new SimpleGrantedAuthority("ROLE_ALUNO")));

        when(userDetailsService.loadUserByUsername(email)).thenReturn(aluno);
        when(passwordEncoder.matches(senha, aluno.getPassword())).thenReturn(true);
        when(tokenService.generateToken(email, "ALUNO", "Joana")).thenReturn("fake-token");

        RespostaLoginDTO resposta = authService.login(loginDTO);

        assertNotNull(resposta);
        assertEquals("ALUNO", resposta.getRole());
        assertEquals(email, resposta.getEmail());
        assertEquals("fake-token", resposta.getToken());
        assertNull(resposta.getStatus());
    }

    @Test
    @DisplayName("Deve realizar login com Mentor e retornar status do mentor")
    void login_quandoUsuarioMentor_deveRetornarStatusMentor() {
        Mentor mentor = new Mentor();
        mentor.setEmail(email);
        mentor.setSenha(senha);
        mentor.setNome("Romana Novaes");
        mentor.setStatusMentor(StatusMentor.ATIVO);
        mentor.setAuthorities(List.of(new SimpleGrantedAuthority("ROLE_MENTOR")));

        when(userDetailsService.loadUserByUsername(email)).thenReturn(mentor);
        when(passwordEncoder.matches(senha, mentor.getPassword())).thenReturn(true);
        when(tokenService.generateToken(email, "MENTOR", "Romana Novaes")).thenReturn("fake-token-mentor");

        RespostaLoginDTO resposta = authService.login(loginDTO);

        assertNotNull(resposta);
        assertEquals("MENTOR", resposta.getRole());
        assertEquals("ATIVO", resposta.getStatus());
        assertEquals("fake-token-mentor", resposta.getToken());
    }

    @Test
    @DisplayName("Deve realizar login com Professor e retornar token e nome")
    void login_quandoUsuarioProfessor_deveRetornarTokenERoleProfessor() {
        Professor professor = new Professor();
        professor.setEmail(email);
        professor.setSenha(senha);
        professor.setNome("Daniel Professor");
        professor.setAuthorities(List.of(new SimpleGrantedAuthority("ROLE_PROFESSOR")));

        when(userDetailsService.loadUserByUsername(email)).thenReturn(professor);
        when(passwordEncoder.matches(senha, professor.getPassword())).thenReturn(true);
        when(tokenService.generateToken(email, "PROFESSOR", "Daniel Professor")).thenReturn("token-professor");

        RespostaLoginDTO resposta = authService.login(loginDTO);

        assertEquals("PROFESSOR", resposta.getRole());
        assertEquals("token-professor", resposta.getToken());
        assertNull(resposta.getStatus());
    }

    @Test
    @DisplayName("Deve realizar login com Coordenador e retornar token e dados corretos")
    void login_quandoUsuarioCoordenador_deveRetornarTokenERoleCoordenador() {
        Coordenador coordenador = new Coordenador();
        coordenador.setEmail(email);
        coordenador.setSenha(senha);
        coordenador.setNome("Coordenadora Joana");
        coordenador.setAuthorities(List.of(new SimpleGrantedAuthority("ROLE_COORDENADOR")));

        when(userDetailsService.loadUserByUsername(email)).thenReturn(coordenador);
        when(passwordEncoder.matches(senha, coordenador.getPassword())).thenReturn(true);
        when(tokenService.generateToken(email, "COORDENADOR", "Coordenadora Joana")).thenReturn("token-coordenador");

        RespostaLoginDTO resposta = authService.login(loginDTO);

        assertEquals("COORDENADOR", resposta.getRole());
        assertEquals("token-coordenador", resposta.getToken());
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha incorreta")
    void login_quandoSenhaIncorreta_deveLancarExcecao() {
        UserDetails user = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(user);
        when(passwordEncoder.matches(senha, user.getPassword())).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(loginDTO));
        assertEquals("Email ou senha inválidos", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o usuário não for Aluno, Mentor, Professor ou Coordenador")
    void login_quandoUsuarioDesconhecido_deveLancarExcecao() {
        UserDetails user = new org.springframework.security.core.userdetails.User(
                email,
                senha,
                List.of(new SimpleGrantedAuthority("ROLE_DESCONHECIDO"))
        );

        when(userDetailsService.loadUserByUsername(email)).thenReturn(user);
        when(passwordEncoder.matches(senha, user.getPassword())).thenReturn(true);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(loginDTO);
        });

        assertEquals("Usuário não detectado", exception.getMessage());
    }


}
