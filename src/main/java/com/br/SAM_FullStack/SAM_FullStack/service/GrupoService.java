package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.dto.GrupoDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.Aluno;
import com.br.SAM_FullStack.SAM_FullStack.model.Grupo;
import com.br.SAM_FullStack.SAM_FullStack.model.Professor;
import com.br.SAM_FullStack.SAM_FullStack.model.StatusAlunoGrupo;
import com.br.SAM_FullStack.SAM_FullStack.repository.AlunoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.GrupoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.ProfessorRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Guard;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class GrupoService {

    private final GrupoRepository grupoRepository;
    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;
    private final EntityManager em;

    public List<Grupo> findAll(){
        return grupoRepository.findAll();
    }

    public Grupo findById(long id){
        return grupoRepository.findById(id).
                orElseThrow(()->
                    new RuntimeException("Grupo não encontrado.")
                );
    }

    public String save(GrupoDTO grupoDTO){
        Grupo grupo = new Grupo();
        grupo.setNome(grupoDTO.nome());

        // Pega referencia de um aluno que já existe usando o id dele
        Aluno admin = alunoRepository.getReferenceById(grupoDTO.alunoAdminId());

        // verifica se esse aluno já não está em outro grupo
        if(admin.getGrupo() != null) {
            throw new IllegalStateException("Operação não permitida. Aluno já participa de outro grupo");
        }

        grupo.setAlunoAdmin(admin); // setta  o admin

        grupo = grupoRepository.save(grupo); // salva para gerar id do grupo

        // busca todos os alunos pelos ids passados
        List<Aluno> alunos = alunoRepository.findAllById(grupoDTO.alunosIds());
        // verifica se a qtd de alunos retornado é igual a qtd de ids passados
        if(alunos.size() != grupoDTO.alunosIds().size()){
            throw  new IllegalArgumentException("Um ou mais IDs de alunos não existem");
        }

        // verifica se a quantidade de participantes é permitida
        if(alunos.size() < 3 || alunos.size() > 6){
            throw new IllegalStateException("Operação não permitida. Grupo deve conter no mínimo 3 e no máximo 6 participantes!");
        }

        // verifica se algum aluno já está em outro grupo e se o admin informado também participa do grupo
        boolean adminInformado = false;
        for (Aluno aluno : alunos){
            if(aluno.getGrupo() != null && aluno.getGrupo().getId() != grupo.getId()){
                throw new IllegalStateException("Aluno " + aluno.getNome() + " já participa de outro grupo");
            }
            if(aluno.getId().equals(admin.getId())){
                adminInformado = true;
            }
        }

        // faz a validação do admin no grupo
        if(!adminInformado){
            throw new IllegalStateException("Operação não permitida. Administrador informado não participa do grupo");
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
    public String excluirAlunoGrupo(Long idSolicitante, Long idAlunoExcluir){
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

    // visualização de lista de grupos que solicitaram exclusão de algum aluno
    public List<Grupo> findByAlunosStatusAlunoGrupo(StatusAlunoGrupo statusAlunoGrupo){
        return grupoRepository.findByAlunosStatusAlunoGrupo(statusAlunoGrupo);
    }

    // validação de exclusão de aluno pelo professor
    public String analizarExclusaoAluno(long idProf, long idGrupo, long idAluno, boolean resposta) {
        Grupo grupo = grupoRepository.findById(idGrupo)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado"));

        Professor professor = professorRepository.findById(idProf)
                .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado"));

        Aluno aluno = alunoRepository
                .findByIdAndGrupoAndStatusAlunoGrupo(idAluno, grupo, StatusAlunoGrupo.AGUARDANDO)
                .orElseThrow(() -> new IllegalStateException("Esse aluno não está aguardando exclusão nesse grupo."));

        if (resposta) {
            grupo.getAlunos().remove(aluno);
            aluno.setGrupo(null);
            aluno.setStatusAlunoGrupo(null);
            alunoRepository.save(aluno);
            grupoRepository.save(grupo);

            return "Aluno " + aluno.getNome() + " foi removido do grupo";
        } else {
            return "Solicitação de exclusão recusada. O aluno permanece no grupo";
        }
    }



    public String deletarGrupo(long idProfessor, long idGrupo) {
        // Verifica se o grupo existe
        Grupo grupo = grupoRepository.findById(idGrupo)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado"));

        Professor professor = professorRepository.findById(idProfessor)
                .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado"));

        // Remove o grupo
        grupoRepository.delete(grupo);

        return "Grupo deletado com sucesso";
    }

    public String adicionarAlunoAoGrupo(Long idAdmin, long idGrupo, Long idAluno) {
        //verifica se o grupo existe
        Grupo grupo = grupoRepository.findById(idGrupo)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado"));

        //verifica se quem está adicionando é o admin do grupo
        if(!grupo.getAlunoAdmin().getId().equals(idAdmin)) {
            throw new IllegalStateException("Apenas o admin do grupo pode adicionar alunos");
        }

        // localiza o aluno pelo id informado
        Aluno aluno = alunoRepository.findById(idAluno).orElseThrow(()->
                new IllegalStateException("Aluno com id " + idAluno + " não encontrado"));

        //verifica se o aluno já está no grupo
        if(aluno.getGrupo().getId() == grupo.getId()){
            throw new IllegalStateException("Operação não permitida. Aluno já faz parte do grupo");
        }

        // verifica se o aluno não está em outro grupo
        if(aluno.getGrupo() != null && aluno.getGrupo().getId() != grupo.getId()){
            throw new IllegalStateException("Aluno " + aluno.getNome() + " já participa de outro grupo");
        }

        if(grupo.getAlunos().size() >= 6){
            throw new IllegalStateException("Operação não permitida. O grupo já está com a capacidade máxima de alunos");
        }

        aluno.setStatusAlunoGrupo(StatusAlunoGrupo.ATIVO);
        aluno.setGrupo(grupo);
        alunoRepository.save(aluno);

        grupo.getAlunos().add(aluno);
        // Salva o grupo atualizado
        grupoRepository.save(grupo);

        return "Aluno adicionado com sucesso ao grupo";
    }


}
