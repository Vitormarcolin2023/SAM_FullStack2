package com.br.SAM_FullStack.SAM_FullStack.autenticacao;

import com.br.SAM_FullStack.SAM_FullStack.dto.LoginDTO;
import com.br.SAM_FullStack.SAM_FullStack.dto.RespostaLoginDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.Aluno;
import com.br.SAM_FullStack.SAM_FullStack.model.Coordenador;
import com.br.SAM_FullStack.SAM_FullStack.model.Mentor;
import com.br.SAM_FullStack.SAM_FullStack.model.Professor;
import com.br.SAM_FullStack.SAM_FullStack.repository.AlunoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.CoordenadorRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.MentorRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.ProfessorRepository;
import com.br.SAM_FullStack.SAM_FullStack.service.AlunoService;
import com.br.SAM_FullStack.SAM_FullStack.service.CoordenadorService;
import com.br.SAM_FullStack.SAM_FullStack.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired private CustomUserDetailsService userDetailsService;
    @Autowired private TokenService tokenService;
    @Autowired private PasswordEncoder passwordEncoder;

    public RespostaLoginDTO login(LoginDTO loginDTO){
        String email = loginDTO.getEmail();
        String senha = loginDTO.getSenha();

        UserDetails user = userDetailsService.loadUserByUsername(email);

        // valida senha
        if(!passwordEncoder.matches(senha, user.getPassword())){
            throw new RuntimeException("Email ou senha inválidos");
        }

        // pega a role
        String role = user.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

        // pega o nome dinamicamente
        String nome;
        if(user instanceof Aluno) {
            nome = ((Aluno) user).getNome();
        } else if(user instanceof Mentor) {
            nome = ((Mentor) user).getNome();
        } else if(user instanceof Professor) {
            nome = ((Professor) user).getNome();
        } else if(user instanceof Coordenador) {
            nome = ((Coordenador) user).getNome();
        } else {
            nome = user.getUsername(); // fallback
        }

        String token = tokenService.generateToken(email, role, nome);
        return new RespostaLoginDTO(token, role);
    }
}
