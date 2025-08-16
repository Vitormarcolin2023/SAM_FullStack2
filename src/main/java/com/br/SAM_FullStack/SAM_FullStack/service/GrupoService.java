package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.dto.GrupoDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.Aluno;
import com.br.SAM_FullStack.SAM_FullStack.model.Grupo;
import com.br.SAM_FullStack.SAM_FullStack.repository.AlunoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.GrupoRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GrupoService {

    private final GrupoRepository grupoRepository;
    private final AlunoRepository alunoRepository;
    private final EntityManager em;

    public List<Grupo> findAll(){
        return grupoRepository.findAll();
    }

    public String save(GrupoDTO grupoDTO){
        Grupo grupo = new Grupo();
        grupo.setNome(grupoDTO.nome());

        // Pega referencia de um aluno que já existe usando o id dele
        Aluno admin = alunoRepository.getReferenceById(grupoDTO.alunoAdminId());
        grupo.setAlunoAdmin(admin);

        grupo = grupoRepository.save(grupo); // salva para gerar id do grupo

        // busca todos os alunos pelos ids passados
        List<Aluno> alunos = alunoRepository.findAllById(grupoDTO.alunosIds());
        // verifica se a qtd de alunos retornado é igual a qtd de ids passados
        if(alunos.size() != grupoDTO.alunosIds().size()){
            throw  new IllegalArgumentException("Um ou mais IDs de alunos não existem");
        }

        // verifica se algum aluno já está em outro grupo
        for (Aluno aluno : alunos){
            if(aluno.getGrupo() != null && aluno.getGrupo().getId() != grupo.getId()){
                throw new IllegalStateException("Aluno " + aluno.getNome() + " já participa de outro grupo");
            }
        }

        // setta o grupo no aluno, porque aluno é o dono da relação
        for (Aluno aluno : alunos){
            aluno.setGrupo(grupo);
        }
        alunoRepository.saveAll(alunos); // atualiza as alterações feitas em alunos depois de adionar o id do grupo para cada

        grupo.setAlunos(alunos); // atualiza a lista de alunos no grupo também

        return "Novo grupo cadastrado com sucesso!";
    }


}
