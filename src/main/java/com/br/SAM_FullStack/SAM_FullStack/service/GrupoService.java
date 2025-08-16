package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.dto.GrupoDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.Aluno;
import com.br.SAM_FullStack.SAM_FullStack.model.Grupo;
import com.br.SAM_FullStack.SAM_FullStack.model.StatusAlunoGrupo;
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

    public Grupo findById(long id){
        return grupoRepository.findById(id).
                orElseThrow(()->
                    new RuntimeException("Aluno não encontrado.")
                );
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
            aluno.setStatusAlunoGrupo(StatusAlunoGrupo.ATIVO);
            aluno.setGrupo(grupo);
        }
        alunoRepository.saveAll(alunos); // atualiza as alterações feitas em alunos depois de adionar o id do grupo para cada

        grupo.setAlunos(alunos); // atualiza a lista de alunos no grupo também

        return "Novo grupo cadastrado com sucesso!";
    }

    // Apenas o professor pode deletar um grupo
    public void delete(long idGrupo, long idSolicitante){

    }

    // O aluno admin do grupo solicita a exclusão de um integrante do grupo e o professor aceita ou declina
    public String excluirAlunoGrupo(Integer idSolicitante, Integer idAlunoExcluir){
        // localiza o grupo que o aluno solicitante faz parte
        Grupo grupo = grupoRepository.findByAlunosId(idSolicitante).orElseThrow(()->
                new IllegalArgumentException("Nenhum Grupo encontrado para o aluno solicitante"));

        // verifica se o solicitante é o admin do grupo
        if(!idSolicitante.equals(grupo.getAlunoAdmin().getId())){
            throw new IllegalStateException("Operação apenas atourizada para o Administrador do grupo!");
        }

        // localiza o aluno pelo id informado
        Aluno alunoExc = alunoRepository.findById(idAlunoExcluir).orElseThrow(()->
                new IllegalStateException("Aluno com id " + idAlunoExcluir + " não encontrado"));

        // verifica se o aluno informado pertence ao grupo do solicitante
        if(alunoExc.getGrupo() == null || alunoExc.getGrupo().getId() != grupo.getId()){
            throw new IllegalStateException("Aluno informado não pertence ao grupo do solicitante");
        }

        // troca o status para AGUARDANDO para que seja feita uma filtragem pelo professor
        alunoExc.setStatusAlunoGrupo(StatusAlunoGrupo.AGUARDANDO);
        alunoRepository.save(alunoExc);

        return "Solicitação enviada pera o professor responsável para análise";
    }

}
