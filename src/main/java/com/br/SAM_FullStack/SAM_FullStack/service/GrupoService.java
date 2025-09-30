package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.dto.GrupoDTO;
import com.br.SAM_FullStack.SAM_FullStack.dto.GrupoUpdateDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.Aluno;
import com.br.SAM_FullStack.SAM_FullStack.model.Grupo;
import com.br.SAM_FullStack.SAM_FullStack.model.Professor;
import com.br.SAM_FullStack.SAM_FullStack.model.StatusAlunoGrupo;
import com.br.SAM_FullStack.SAM_FullStack.repository.AlunoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.GrupoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.ProfessorRepository;
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
    private final ProfessorRepository professorRepository;

    public List<Grupo> findAll() {
        return grupoRepository.findAll();
    }

    public Grupo findById(long id) {
        return grupoRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Grupo não encontrado.")
        );
    }

    public Grupo findByAlunoId(Long alunoId) {
        return grupoRepository.findByAlunosId(alunoId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado em nenhum grupo."));
    }

    public GrupoDTO save(GrupoDTO grupoDTO) {
        Grupo grupo = new Grupo();
        grupo.setNome(grupoDTO.nome());

        Aluno admin = alunoRepository.getReferenceById(grupoDTO.alunoAdminId());

        if (admin.getGrupo() != null) {
            throw new IllegalStateException("Operação não permitida. Aluno já participa de outro grupo");
        }

        grupo.setAlunoAdmin(admin);
        grupo = grupoRepository.save(grupo);

        List<Aluno> alunos = alunoRepository.findAllById(grupoDTO.alunosIds());
        if (alunos.size() != grupoDTO.alunosIds().size()) {
            throw new IllegalArgumentException("Um ou mais IDs de alunos não existem");
        }

        if (alunos.size() < 3 || alunos.size() > 6) {
            grupoRepository.delete(grupo); // Remove o grupo criado se a validação falhar
            throw new IllegalStateException("Grupo deve ter entre 3 e 6 participantes");
        }

        boolean adminInformado = alunos.stream().anyMatch(a -> a.getId().equals(admin.getId()));
        if (!adminInformado) {
            grupoRepository.delete(grupo); // Remove o grupo criado se a validação falhar
            throw new IllegalStateException("Administrador informado não participa do grupo");
        }

        for (Aluno aluno : alunos) {
            if (aluno.getGrupo() != null) {
                grupoRepository.delete(grupo);
                throw new IllegalStateException("Operação não permitida. O aluno " + aluno.getNome() + " já participa de outro grupo.");
            }
            aluno.setStatusAlunoGrupo(StatusAlunoGrupo.ATIVO);
            aluno.setGrupo(grupo);
        }
        alunoRepository.saveAll(alunos);
        grupo.setAlunos(alunos);

        Grupo salvo = grupoRepository.save(grupo);

        return new GrupoDTO(salvo.getId(), salvo.getNome(), admin.getId(), alunos.stream().map(Aluno::getId).toList());
    }

    public String updateGrupoInfo(Long groupId, Long adminId, GrupoUpdateDTO grupoUpdateDTO) {
        Grupo grupo = grupoRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado."));

        if (!grupo.getAlunoAdmin().getId().equals(adminId)) {
            throw new IllegalStateException("Apenas o admin do grupo pode editar as informações.");
        }

        if (grupoUpdateDTO.nome() != null && !grupoUpdateDTO.nome().isBlank()) {
            grupo.setNome(grupoUpdateDTO.nome());
        }

        grupoRepository.save(grupo);
        return "Informações do grupo atualizadas com sucesso.";
    }

    public String adicionarAlunoAoGrupo(Long idAdmin, long idGrupo, Long idAluno) {
        Grupo grupo = grupoRepository.findById(idGrupo)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado"));

        if (!grupo.getAlunoAdmin().getId().equals(idAdmin)) {
            throw new IllegalStateException("Apenas o admin do grupo pode adicionar alunos");
        }

        Aluno aluno = alunoRepository.findById(idAluno).orElseThrow(() ->
                new IllegalStateException("Aluno com id " + idAluno + " não encontrado"));

        if (aluno.getGrupo() != null) {
            throw new IllegalStateException("Operação não permitida. O aluno " + aluno.getNome() + " já faz parte de um grupo.");
        }

        if (grupo.getAlunos().size() >= 6) {
            throw new IllegalStateException("Operação não permitida. O grupo já está com a capacidade máxima de alunos");
        }

        aluno.setStatusAlunoGrupo(StatusAlunoGrupo.ATIVO);
        aluno.setGrupo(grupo);
        alunoRepository.save(aluno);

        return "Aluno adicionado com sucesso ao grupo";
    }

    public String removerAlunoDiretamente(Long idGrupo, Long idAlunoRemover, Long idAdmin) {
        Grupo grupo = grupoRepository.findById(idGrupo)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado."));

        if (!grupo.getAlunoAdmin().getId().equals(idAdmin)) {
            throw new IllegalStateException("Apenas o administrador do grupo pode remover membros.");
        }

        if (idAlunoRemover.equals(idAdmin)) {
            throw new IllegalStateException("O administrador não pode remover a si mesmo do grupo.");
        }

        Aluno alunoParaRemover = alunoRepository.findById(idAlunoRemover)
                .orElseThrow(() -> new IllegalArgumentException("Aluno a ser removido não encontrado."));

        if (alunoParaRemover.getGrupo() == null || alunoParaRemover.getGrupo().getId() != idGrupo) {
            throw new IllegalStateException("O aluno informado não pertence a este grupo.");
        }

        alunoParaRemover.setGrupo(null);
        alunoParaRemover.setStatusAlunoGrupo(null);
        alunoRepository.save(alunoParaRemover);

        return "Aluno " + alunoParaRemover.getNome() + " foi removido do grupo com sucesso.";
    }


    public List<Grupo> findByAlunosStatusAlunoGrupo(StatusAlunoGrupo statusAlunoGrupo) {
        return grupoRepository.findByAlunosStatusAlunoGrupo(statusAlunoGrupo);
    }

    public String analizarExclusaoAluno(String senhaProf, long idGrupo, long idAluno, boolean resposta) {
        Grupo grupo = grupoRepository.findById(idGrupo)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado"));

        Professor professor = professorRepository.findBySenha(senhaProf)
                .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado"));

        Aluno aluno = alunoRepository
                .findByIdAndGrupoAndStatusAlunoGrupo(idAluno, grupo, StatusAlunoGrupo.AGUARDANDO)
                .orElseThrow(() -> new IllegalStateException("Esse aluno não está aguardando exclusão nesse grupo."));

        if (resposta) {
            aluno.setGrupo(null);
            aluno.setStatusAlunoGrupo(null);
            alunoRepository.save(aluno);
            return "Aluno " + aluno.getNome() + " foi removido do grupo";
        } else {
            aluno.setStatusAlunoGrupo(StatusAlunoGrupo.ATIVO); // Reverte para ATIVO se recusado
            alunoRepository.save(aluno);
            return "Solicitação de exclusão recusada. O aluno permanece no grupo";
        }
    }

    public String deletarGrupo(long idGrupo, long idProfessor) {
        Grupo grupo = grupoRepository.findById(idGrupo)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado"));

        Professor professor = professorRepository.findById(idProfessor)
                .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado"));

        for (Aluno aluno : grupo.getAlunos()) {
            aluno.setGrupo(null);
            aluno.setStatusAlunoGrupo(null);
            alunoRepository.save(aluno);
        }

        grupoRepository.delete(grupo);

        return "Grupo deletado com sucesso";
    }

    public GrupoDTO findByAluno(Aluno aluno) {
        Grupo grupo = aluno.getGrupo();
        if (grupo != null) {
            return new GrupoDTO(grupo.getId(), grupo.getNome(), null, null);
        }
        return null;
    }
}