    package com.br.SAM_FullStack.SAM_FullStack.service;

    import com.br.SAM_FullStack.SAM_FullStack.dto.ReuniaoDTO;
    import com.br.SAM_FullStack.SAM_FullStack.model.*;
    import com.br.SAM_FullStack.SAM_FullStack.repository.AlunoRepository;
    import com.br.SAM_FullStack.SAM_FullStack.repository.GrupoRepository;
    import com.br.SAM_FullStack.SAM_FullStack.repository.MentorRepository;
    import com.br.SAM_FullStack.SAM_FullStack.repository.ReuniaoRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;

    import java.util.List;

    @Service
    @RequiredArgsConstructor
    public class ReuniaoService1 {

        private final ReuniaoRepository reuniaoRepository;
        private final GrupoRepository grupoRepository;
        private final MentorRepository mentorRepository;
        private final AlunoRepository alunoRepository;

        // Metodo para coordenação - ver todas
        public List<Reuniao> findAll() {
            return reuniaoRepository.findAll();
        }

        // Metodo para grupo - ver só reuniões do grupo
        public List<Reuniao> findAllByGrupo(long idGrupo) {
            return reuniaoRepository.findAllGrupo(idGrupo);
        }

        // Metodo para mentor - ver só reuniões do mentor
        public List<Reuniao> findAllByMentor(long idMentor) {
            return reuniaoRepository.findAllMentor(idMentor);
        }

        // Buscar por ID
        public Reuniao findById(long id) {
            return reuniaoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Reunião não encontrada com id " + id));
        }

        public String save(ReuniaoDTO reuniaoDTO) {
            Grupo grupo = grupoRepository.findById(reuniaoDTO.getGrupoId())
                    .orElseThrow(() -> new IllegalStateException("Grupo não encontrado"));
            Mentor mentor = mentorRepository.findById(reuniaoDTO.getMentorId())
                    .orElseThrow(() -> new IllegalStateException("Mentor não encontrado"));

            // Verifique se a área de atuação do mentor é a mesma de todos os alunos do grupo.
            AreaDeAtuacao areaMentor = mentor.getAreaDeAtuacao();
            List<Aluno> alunosDoGrupo = alunoRepository.findAllByGrupoId(grupo.getId());

            for (Aluno aluno : alunosDoGrupo) {
                if (aluno.getCurso().getAreaDeAtuacao() == null || !aluno.getCurso().getAreaDeAtuacao().equals(areaMentor)) {
                    throw new IllegalStateException("A área de atuação de pelo menos um aluno não corresponde à do mentor.");
                }
            }
            Reuniao reuniao = new Reuniao();
            reuniao.setAssunto(reuniaoDTO.getAssunto());
            reuniao.setData(reuniaoDTO.getData());
            reuniao.setHora(reuniaoDTO.getHora());
            reuniao.setFormatoReuniao(reuniaoDTO.getFormatoReuniao());
            reuniao.setGrupo(grupo);
            reuniao.setMentor(mentor);
            reuniao.setStatusReuniao(StatusReuniao.PENDENTE);

            reuniaoRepository.save(reuniao);
            return "Solicitação de reunião enviada";
        }

        // Atualizado para receber um ReuniaoDTO
        public String update(long id, ReuniaoDTO reuniaoAtualizadaDTO) {
            Reuniao reuniao = this.findById(id);

            if (reuniao == null) {
                throw new IllegalStateException("Reunião com id " + id + " não encontrada");
            }

            // Busque as entidades novamente com base nos IDs do DTO
            Grupo grupo = grupoRepository.findById(reuniaoAtualizadaDTO.getGrupoId())
                    .orElseThrow(() -> new IllegalStateException("Grupo não encontrado"));
            Mentor mentor = mentorRepository.findById(reuniaoAtualizadaDTO.getMentorId())
                    .orElseThrow(() -> new IllegalStateException("Mentor não encontrado"));

            reuniao.setAssunto(reuniaoAtualizadaDTO.getAssunto());
            reuniao.setData(reuniaoAtualizadaDTO.getData());
            reuniao.setHora(reuniaoAtualizadaDTO.getHora());
            reuniao.setFormatoReuniao(reuniaoAtualizadaDTO.getFormatoReuniao());

            // Atribua as entidades atualizadas
            reuniao.setGrupo(grupo);
            reuniao.setMentor(mentor);

            // O status também pode vir do DTO, se for o caso
            // reuniao.setStatusReuniao(reuniaoAtualizadaDTO.getStatusReuniao());

            reuniaoRepository.save(reuniao);
            return ("Reunião atualizada com sucesso!");
        }

        // Deletar reunião
        public String delete(long id) {
            Reuniao reuniao = this.findById(id);
            if (reuniao == null){
                throw new IllegalStateException("Reunião não encontrada");
            }
            reuniaoRepository.delete(reuniao);
            return ("Reunião deletada com sucesso");
        }
    }
