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

        boolean autenticado = false;

        switch (loginDTO.getRole().toUpperCase()){
            case "ALUNO":
                autenticado = alunoRepository.findByEmailAndSenha(loginDTO.getEmail(), loginDTO.getSenha()).isPresent();
                break;
            case "MENTOR":
                autenticado = mentorRepository.findByEmailAndSenha(loginDTO.getEmail(), loginDTO.getSenha()).isPresent();
                break;
            case "PROFESSOR":
                autenticado = professorRepository.findByEmailAndSenha(loginDTO.getEmail(), loginDTO.getSenha()).isPresent();
                break;
            case "COORDENADOR":
                autenticado = coordenadorRepository.findByEmailAndSenha(loginDTO.getEmail(), loginDTO.getSenha()).isPresent();
                break;
            default:
                throw new RuntimeException("Tipo de usuário não informado!");
        }

        if(!autenticado){
            throw new RuntimeException("Email ou senha inválidos");
        }

        var token = tokenService.generateToken(loginDTO.getEmail(), loginDTO.getRole());

        return new RespostaLoginDTO(token);
    }

}
