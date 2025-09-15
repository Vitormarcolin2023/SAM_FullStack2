package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.dto.LoginDTO;
import com.br.SAM_FullStack.SAM_FullStack.dto.RespostaLoginDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.Aluno;
import com.br.SAM_FullStack.SAM_FullStack.repository.AlunoRepository;
import com.br.SAM_FullStack.SAM_FullStack.autenticacao.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlunoService {

    private final AlunoRepository alunoRepository;
    @Autowired
    private EmailService emailService;

    @Autowired
    private TokenService tokenService;

    public Aluno findById(Long id) {
        return alunoRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Aluno não encontrado com ID: " + id));
    }

    public Aluno findByRa(Integer ra){
        return alunoRepository.findByRa(ra).orElseThrow(() ->
                new RuntimeException("Aluno não encontrado com RA: " + ra));
    }

    public List<Aluno> findAll(){
        return alunoRepository.findAll();
    }

    public Aluno save(Aluno aluno) {
        Optional<Aluno> alunoOptional = alunoRepository.findByRa(aluno.getRa());
        if (alunoOptional.isPresent()) {
            Aluno alunoExistente = alunoOptional.get();
            log.error("Tentativa de cadastrar aluno com RA já existente. RA: {}, Aluno: {}", aluno.getRa(), alunoExistente);
            emailService.enviarEmailTexto(
                    aluno.getEmail(),
                    "Falha no Cadastro: RA já existente",
                    "Olá " + aluno.getNome() + ", já existe um aluno cadastrado com o RA " + aluno.getRa() + "."
            );
            throw new RuntimeException("Aluno com RA " + aluno.getRa() + " já está cadastrado!");

        } else {
            log.info("RA {} disponível. Cadastrando novo aluno: {}", aluno.getRa(), aluno.getNome());
            Aluno alunoSalvo = alunoRepository.save(aluno);
            emailService.enviarEmailTexto(
                    alunoSalvo.getEmail(),
                    "Aluno Cadastrado com Sucesso",
                    "Olá " + alunoSalvo.getNome() + ", seu cadastro foi realizado com sucesso! RA é: " + alunoSalvo.getRa() + "Curso: " + alunoSalvo.getCurso()
            );
            return alunoSalvo;
        }
    }

    public Aluno update(Long id, Aluno alunoUpdate){

        Aluno alunoExistente = findById(id);
        alunoExistente.setNome(alunoUpdate.getNome());
        alunoExistente.setRa(alunoUpdate.getRa());
        alunoExistente.setSenha(alunoUpdate.getSenha());
        alunoExistente.setEmail(alunoUpdate.getEmail());

        return alunoRepository.save(alunoExistente);
    }

    public void delete(Long id){
        Aluno aluno = findById(id);
        alunoRepository.delete(aluno);
    }

    public List<Aluno> saveAll(List<Aluno> alunos) {
        for (Aluno aluno : alunos) {
            this.save(aluno);
        }
        return alunos;
    }

    public List<Aluno> buscarPorNome(String nome) {
        // Apenas repassa a chamada para o método mágico do repositório
        return alunoRepository.findByNomeContainingIgnoreCase(nome);
    }

    public List<Aluno> buscarTodosOrdenadoPorNome() {
        return alunoRepository.findAllByOrderByNomeAsc();
    }
}

