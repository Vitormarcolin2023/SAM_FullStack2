package com.br.SAM_FullStack.SAM_FullStack.autenticacao;

import com.br.SAM_FullStack.SAM_FullStack.dto.LoginDTO;
import com.br.SAM_FullStack.SAM_FullStack.dto.RespostaLoginDTO;
import com.br.SAM_FullStack.SAM_FullStack.repository.AlunoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.CoordenadorRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.MentorRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AlunoRepository alunoRepository;
    @Autowired
    private MentorRepository mentorRepository;
    @Autowired
    private CoordenadorRepository coordenadorRepository;
    @Autowired
    private ProfessorRepository professorRepository;
    @Autowired
    private TokenService tokenService;

    public RespostaLoginDTO login(LoginDTO loginDTO){

        String email = loginDTO.getEmail();
        String senha = loginDTO.getSenha();
        String role = loginDTO.getRole().toUpperCase();

        String token = null;
        String status = null;

        switch (role){
            case "ALUNO":
                var aluno = alunoRepository.findByEmailAndSenha(email, senha);
                if(aluno.isPresent()){
                    token = tokenService.generateToken(email, role);
                    status = "CONCLUIDO";
                }
                break;
            case "MENTOR":
                var mentor = mentorRepository.findByEmailAndSenha(email, senha)
                        .orElseThrow(() -> new RuntimeException("Email ou senha inválidos"));

                token = tokenService.generateToken(email, role);
                status = mentor.getStatusMentor().toString();
                break;
            case "PROFESSOR":
                var professor = professorRepository.findByEmailAndSenha(email, senha);
                if(professor.isPresent()){
                    token = tokenService.generateToken(email, role);
                    status = "CONCLUIDO";
                }
                break;
            case "COORDENADOR":
                var coordenador = coordenadorRepository.findByEmailAndSenha(email, senha);
                if(coordenador.isPresent()){
                    token = tokenService.generateToken(email, role);
                    status = "CONCLUIDO";
                }
                break;
            default:
                throw new RuntimeException("Tipo de usuário não informado!");
        }

        if(token == null){
            throw new RuntimeException("Email ou senha inválidos");
        }

        return new RespostaLoginDTO(token, role, status);
    }
}