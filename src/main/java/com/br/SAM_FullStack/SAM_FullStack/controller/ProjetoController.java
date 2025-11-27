package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.model.AreaDeAtuacao;
import com.br.SAM_FullStack.SAM_FullStack.model.Projeto;
import com.br.SAM_FullStack.SAM_FullStack.model.StatusProjeto;
import com.br.SAM_FullStack.SAM_FullStack.repository.ProjetoRepository;
import com.br.SAM_FullStack.SAM_FullStack.service.ProjetoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/projetos")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ProjetoController {

    private final ProjetoService projetoService;

    // Listar todos os projetos
    @GetMapping("/findAll")
    public ResponseEntity<List<Projeto>> listAll() {
        return ResponseEntity.ok(projetoService.listAll());
    }

    // Buscar projeto por ID
    @GetMapping("/findById/{id}")
    public ResponseEntity<Projeto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(projetoService.findById(id));
    }

    // Buscar por nome
    @GetMapping("/buscar-por-nome")
    public List<Projeto> buscarPorNome(@RequestParam String nome) {
        return projetoService.buscarPorNome(nome);
    }

    // Buscar por área de atuação
    @GetMapping("/buscar-por-atuacao")
    public List<Projeto> buscarPorAtuacao(@RequestParam String areaNome) {
        AreaDeAtuacao area = new AreaDeAtuacao();
        area.setNome(areaNome);
        return projetoService.buscarPorAreaAtuacao(area);
    }

    // Buscar por período
    @GetMapping("/buscar-por-periodo")
    public List<Projeto> buscarPorPeriodo(@RequestParam String periodo) {
        return projetoService.findByPeriodo(periodo);
    }

    // Salvar projeto
    @PostMapping("/save")
    public ResponseEntity<Projeto> save(@RequestBody Projeto projeto) {
        return ResponseEntity.ok(projetoService.save(projeto));
    }

    // Atualizar projeto
    @PutMapping("/update/{id}")
    public ResponseEntity<Projeto> update(@PathVariable Long id, @RequestBody Projeto projeto) {
        return ResponseEntity.ok(projetoService.update(id, projeto));
    }

    // Deletar projeto
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        projetoService.delete(id);
        return ResponseEntity.ok("Projeto excluído com sucesso");
    }

    // Projetos do aluno
    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<List<Projeto>> getProjetosDoAluno(@PathVariable Long alunoId) {
        return ResponseEntity.ok(projetoService.buscarProjetosDoAluno(alunoId));
    }

    // Projetos do mentor
    @GetMapping("/mentor/{mentorId}")
    public ResponseEntity<List<Projeto>> getProjetosDoMentor(@PathVariable Long mentorId) {
        return ResponseEntity.ok(projetoService.buscarProjetosDoMentor(mentorId));
    }

    // Projetos do professor
    @GetMapping("/professor/{professorId}")
    public ResponseEntity<List<Projeto>> getProjetosDoProfessor(@PathVariable Long professorId) {
        return ResponseEntity.ok(projetoService.buscarProjetosDoProfessor(professorId));
    }

    // Todos os projetos (coordenador)
    @GetMapping("/coordenador")
    public ResponseEntity<List<Projeto>> getTodosProjetos() {
        return ResponseEntity.ok(projetoService.buscarTodosProjetos());
    }

    // Buscar projeto ativo do aluno
    @GetMapping("/buscar-projeto-ativo/{alunoId}")
    public ResponseEntity<Projeto> buscarProjetoAtivo(@PathVariable Long alunoId){
        return ResponseEntity.ok(projetoService.buscarProjetoAtivo(alunoId));
    }

    // Projetos ativos do mentor
    @GetMapping("/buscar-projetos-ativos-mentor/{mentorId}")
    public ResponseEntity<List<Projeto>> buscarProjetosAtivosMentor(@PathVariable Long mentorId){
        return ResponseEntity.ok(projetoService.buscarProjetosAtivosMentores(mentorId));
    }

    // Projetos aguardando avaliação pelo mentor
    @GetMapping("/buscar-projetos-nao-avaliados-mentor/{mentorId}")
    public ResponseEntity<List<Projeto>> buscarProjetosNaoAvaliadosMentor(@PathVariable Long mentorId) {
        return ResponseEntity.ok(projetoService.buscarProjetosAguardandoAvaliacaoMentor(mentorId));
    }

    // Projeto aguardando avaliação pelo aluno
    @GetMapping("/buscar-projeto-nao-avaliado-aluno/{alunoId}")
    public ResponseEntity<Projeto> buscarProjetoNaoAvalidadoAluno(@PathVariable Long alunoId) {
        return ResponseEntity.ok(projetoService.buscarProjetoAguardandoAvaliacaoAluno(alunoId));
    }
}
