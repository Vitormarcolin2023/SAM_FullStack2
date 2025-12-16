package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.model.*;
import com.br.SAM_FullStack.SAM_FullStack.repository.AvaliacaoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.GrupoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.MentorRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.ProjetoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProjetoService {
    private final ProjetoRepository projetoRepository;
    private final GrupoRepository grupoRepository;

    @Autowired
    private MentorRepository mentorRepository;

    public ProjetoService(ProjetoRepository projetoRepository, GrupoRepository grupoRepository) {
        this.projetoRepository = projetoRepository;
        this.grupoRepository = grupoRepository;
    }

    //LISTAR PROJETO
    public List<Projeto> listAll() {
        return projetoRepository.findAll();
    }

    //BUSCAR POR ID
    public Projeto findById(long id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));
    }

    public List<Projeto> buscarPorNome(String nomeDoProjeto) {
        return projetoRepository.findByNomeDoProjetoIgnoreCaseContaining(nomeDoProjeto);
    }

    public List<Projeto> buscarPorAreaAtuacao(AreaDeAtuacao areaDeAtuacao) {
        return projetoRepository.findByAreaDeAtuacao(areaDeAtuacao);

    }
    public List<Projeto> findByPeriodo(String periodo) {
        return projetoRepository.findByPeriodo(periodo);
    }

    private void atualizarStatusProjeto(Projeto projeto) {
        LocalDate hoje = LocalDate.now();

        if (projeto.getDataFinalProjeto() != null && projeto.getDataInicioProjeto().isAfter(hoje)) {
         projeto.setStatusProjeto(StatusProjeto.EM_APROVACAO);
        } else if (projeto.getDataFinalProjeto() != null && projeto.getDataFinalProjeto().isAfter(hoje)) {
            projeto.setStatusProjeto(StatusProjeto.ATIVO);
        } else if (projeto.getDataFinalProjeto() != null && projeto.getDataFinalProjeto().isBefore(hoje)) {
            projeto.setStatusProjeto(StatusProjeto.ATIVO);
        } else {
            projeto.setStatusProjeto(StatusProjeto.ATIVO);
        }

    }

    // SALVAR
    @Transactional // É uma boa prática adicionar Transactional aqui para garantir integridade
    public Projeto save(Projeto projeto) {
        // --- INÍCIO DA SUA LÓGICA EXISTENTE (MANTIDA) ---
        if (projeto.getGrupo() != null && projeto.getGrupo().getId() != null) {
            Grupo grupoGerenciado = grupoRepository.findByIdWithAlunos(projeto.getGrupo().getId())
                    .orElse(projeto.getGrupo());
            projeto.setGrupo(grupoGerenciado);
            if (grupoGerenciado.getAlunos() != null) {
                for (Aluno aluno : grupoGerenciado.getAlunos()) {
                    if (aluno.getCurso() != null
                            && aluno.getCurso().getAreaDeAtuacao() != null
                            && projeto.getAreaDeAtuacao() != null
                            && !aluno.getCurso().getAreaDeAtuacao().getId()
                            .equals(projeto.getAreaDeAtuacao().getId())) {
                        throw new IllegalArgumentException(
                                "A área de atuação do projeto deve ser a mesma de todos os alunos do grupo."
                        );
                    }
                }

            }
        }
        // --- FIM DA SUA LÓGICA EXISTENTE ---

        // --- ALTERAÇÃO DA FEATURE-131 ---

        // Removemos a chamada 'atualizarStatusProjeto(projeto)' pois ela define status por data.
        // Agora, forçamos o status inicial do fluxo de aprovação:
        projeto.setStatusProjeto(StatusProjeto.AGUARDANDO_APROVACAO);

        // Garantimos que não haja motivo de recusa prévio (boas práticas)
        projeto.setMotivoRecusa(null);

        // O campo 'avaliadoPorMentor' se tornou redundante com os novos Status,
        // mas mantive aqui false para não quebrar compatibilidade se você ainda usa no front.
        projeto.setAvaliadoPorMentor(false);

        // TODO FUTURO: Aqui chamaremos o emailService.enviarNotificacaoMentor(...)

        return projetoRepository.save(projeto);
    }


    //atualizar
    public Projeto update (Long id, Projeto projetoUpdate){
        Projeto projetoExistente = findById(id);
        projetoExistente.setNomeDoProjeto(projetoUpdate.getNomeDoProjeto());
        projetoExistente.setDataInicioProjeto(projetoUpdate.getDataInicioProjeto());
        projetoExistente.setDataFinalProjeto(projetoUpdate.getDataFinalProjeto());
        projetoExistente.setDescricao(projetoUpdate.getDescricao());
        projetoExistente.setPeriodo(projetoUpdate.getPeriodo());
        projetoExistente.setAreaDeAtuacao(projetoUpdate.getAreaDeAtuacao());

        atualizarStatusProjeto(projetoExistente);

        return projetoRepository.save(projetoExistente);
    }

    public void delete(Long id){
        Projeto projeto = findById(id);
        projetoRepository.delete(projeto);
    }

    @Transactional
    public void desvincularMentor(Long mentorId) {
        List<Projeto> projetos = projetoRepository.findByMentorId(mentorId);
        for (Projeto p : projetos) {
            p.setMentor(null);
            projetoRepository.save(p);
        }
    }

    public List<Projeto> findByMentor(Long mentorId) {
        List<Projeto> projetos = projetoRepository.findByMentorId(mentorId);

        if (projetos.isEmpty()) {
            throw new RuntimeException("Nenhum projeto encontrado para este mentor.");
        }

        return projetos;
    }

    public List<Projeto> buscarProjetosPorProfessor(Long professorId) {
        return projetoRepository.findAllByProfessoresId(professorId);
    }

    public Projeto buscarProjetoAtivo(Long alunoId){
        return projetoRepository.findProjetoPorStatusAluno(alunoId, StatusProjeto.ATIVO)
                .orElseThrow(() -> new RuntimeException("Nenhum projeto ativo no momento"));
    }

    public List<Projeto> buscarProjetosAtivosMentores(Long mentorId){
        return projetoRepository.findAllByMentorIdAndStatusProjeto(mentorId, StatusProjeto.ATIVO);
    }

    public List<Projeto> buscarProjetosAguardandoAvaliacaoMentor(Long mentorId) {
        return projetoRepository.findAllByMentorIdAndStatusProjeto(mentorId, StatusProjeto.AGUARDANDO_AVALIACAO);
    }

    public Projeto buscarProjetoAguardandoAvaliacaoAluno(Long alunoId) {
        return projetoRepository.findProjetoPorStatusAluno(alunoId, StatusProjeto.AGUARDANDO_AVALIACAO)
                .orElseThrow(() -> new RuntimeException("Nenhum projeto aguardando avaliação"));
    }

    public Projeto findByGrupo(Long idGrupo) {
        // O .orElse(null) é importante aqui para não quebrar com erro 500
        // se o projeto não existir. Ele vai retornar null suavemente.
        return projetoRepository.findByGrupoId(idGrupo).orElse(null);
    }

    @Transactional
    public Projeto avaliarProjeto(Long idProjeto, boolean aprovado, String motivoRecusa) {
        Projeto projeto = findById(idProjeto);

        if (aprovado) {
            // Se aprovado, o projeto se torna ATIVO
            projeto.setStatusProjeto(StatusProjeto.ATIVO);

            // Opcional: Se você quiser que a lógica de datas (se já começou ou não)
            // seja aplicada IMEDIATAMENTE após a aprovação, descomente a linha abaixo:
            // atualizarStatusProjeto(projeto);

            // Limpa qualquer motivo de recusa anterior, caso exista
            projeto.setMotivoRecusa(null);

            // TODO FUTURO: Disparar e-mail de aprovação para o grupo

        } else {
            // Se recusado, validamos se existe motivo
            if (motivoRecusa == null || motivoRecusa.trim().isEmpty()) {
                throw new IllegalArgumentException("Para recusar o projeto, é obrigatório informar o motivo.");
            }

            projeto.setStatusProjeto(StatusProjeto.RECUSADO);
            projeto.setMotivoRecusa(motivoRecusa);

            // TODO FUTURO: Disparar e-mail de recusa para o grupo
        }

        return projetoRepository.save(projeto);
    }

}