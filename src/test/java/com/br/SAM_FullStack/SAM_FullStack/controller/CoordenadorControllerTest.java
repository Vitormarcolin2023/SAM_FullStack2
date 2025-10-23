package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.dto.CoordenadorDTO;
import com.br.SAM_FullStack.SAM_FullStack.dto.CoordenadorUpdateDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.Coordenador;
import com.br.SAM_FullStack.SAM_FullStack.model.Mentor;
import com.br.SAM_FullStack.SAM_FullStack.model.Projeto;
import com.br.SAM_FullStack.SAM_FullStack.service.CoordenadorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoordenadorControllerTest {

    @Mock
    private CoordenadorService coordenadorService;

    @InjectMocks
    private CoordenadorController coordenadorController;

    private CoordenadorDTO coordenadorDTO;
    private CoordenadorUpdateDTO coordenadorUpdateDTO;
    private Coordenador coordenador;
    private final Long coordenadorId = 1L;
    private final String email = "test@example.com";

    @BeforeEach
    void setUp() {
        coordenadorDTO = new CoordenadorDTO();
        coordenadorDTO.setNome("Nome Teste");
        coordenadorDTO.setEmail(email);
        coordenadorDTO.setSenha("senha123");
        coordenadorDTO.setCursosIds(Arrays.asList(10L, 20L));

        coordenadorUpdateDTO = new CoordenadorUpdateDTO();
        coordenadorUpdateDTO.setNome("Nome Atualizado");
        coordenadorUpdateDTO.setEmail("novo_email@example.com");
        coordenadorUpdateDTO.setSenha("novasenha123");
        coordenadorUpdateDTO.setCursosIds(Arrays.asList(30L));

        coordenador = new Coordenador();
        coordenador.setId(coordenadorId);
        coordenador.setNome("Nome Teste");
        coordenador.setEmail(email);
        coordenador.setSenha("senha123");
        coordenador.setCursos(new ArrayList<>());
    }

    @Test
    @DisplayName("Salvar: deve retornar HTTP 201 CREATED e o objeto Coordenador salvo")
    void save_deveRetornarCreatedCoordenador() {
        when(coordenadorService.save(any(CoordenadorDTO.class))).thenReturn(coordenador);

        ResponseEntity<Coordenador> response = coordenadorController.save(coordenadorDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(coordenador.getEmail(), response.getBody().getEmail());
    }

    @Test
    @DisplayName("Atualizar: deve retornar HTTP 200 OK com mensagem de sucesso")
    void update_deveRetornarOkComMensagem() {
        String successMessage = "Coordenador atualizado com sucesso.";
        when(coordenadorService.update(any(CoordenadorUpdateDTO.class), eq(coordenadorId))).thenReturn(successMessage);

        ResponseEntity<String> response = coordenadorController.update(coordenadorUpdateDTO, coordenadorId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(successMessage, response.getBody());
    }

    @Test
    @DisplayName("Deletar: deve retornar HTTP 200 OK com mensagem de sucesso")
    void delete_deveRetornarOkComMensagem() {
        doNothing().when(coordenadorService).delete(coordenadorId);

        ResponseEntity<String> response = coordenadorController.delete(coordenadorId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Coordenador excluído com sucesso", response.getBody());
    }

    @Test
    @DisplayName("Ativar Mentor: deve retornar HTTP 200 OK com mensagem de ativação")
    void ativarMentor_deveRetornarOkComMensagem() {
        String message = "Mentor ativado.";
        when(coordenadorService.ativarMentor(coordenadorId)).thenReturn(message);

        ResponseEntity<String> response = coordenadorController.ativarMentor(coordenadorId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(message, response.getBody());
    }

    @Test
    @DisplayName("Inativar Mentor: deve retornar HTTP 200 OK com mensagem de inativação")
    void inativarMentor_deveRetornarOkComMensagem() {
        String message = "Mentor inativado.";
        when(coordenadorService.inativarMentor(coordenadorId)).thenReturn(message);

        ResponseEntity<String> response = coordenadorController.inativarMentor(coordenadorId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(message, response.getBody());
    }

    @Test
    @DisplayName("Buscar todos os Mentores: deve retornar HTTP 200 OK com a lista de Mentores")
    void findAllMentores_deveRetornarOkComLista() {
        List<Mentor> mentores = Arrays.asList(new Mentor(), new Mentor());
        when(coordenadorService.findAllMentores()).thenReturn(mentores);

        ResponseEntity<List<Mentor>> response = coordenadorController.findAllMentores();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    @DisplayName("Buscar todos os Projetos: deve retornar HTTP 200 OK com a lista de Projetos")
    void findAllProjetos_deveRetornarOkComLista() {
        List<Projeto> projetos = Arrays.asList(new Projeto(), new Projeto());
        when(coordenadorService.findAllProjetos()).thenReturn(projetos);

        ResponseEntity<List<Projeto>> response = coordenadorController.findAllProjetos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    @DisplayName("Buscar todos os Projetos: quando vazia, deve retornar HTTP 200 OK com lista vazia")
    void findAllProjetos_quandoVazia_deveRetornarOkComListaVazia() {
        List<Projeto> projetos = Collections.emptyList();
        when(coordenadorService.findAllProjetos()).thenReturn(projetos);

        ResponseEntity<List<Projeto>> response = coordenadorController.findAllProjetos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    @Test
    @DisplayName("Buscar por Email: quando encontrado, deve retornar HTTP 200 OK e o objeto Coordenador")
    void buscarPorEmail_quandoEncontrado_deveRetornarOkCoordenador() {
        when(coordenadorService.buscarPorEmail(email)).thenReturn(coordenador);

        ResponseEntity<Coordenador> response = coordenadorController.buscarPorEmail(email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(email, response.getBody().getEmail());
    }

    @Test
    @DisplayName("Buscar por Email: quando não encontrado, deve retornar HTTP 404 NOT_FOUND")
    void buscarPorEmail_quandoNaoEncontrado_deveRetornarNotFound() {
        when(coordenadorService.buscarPorEmail(anyString())).thenReturn(null);

        ResponseEntity<Coordenador> response = coordenadorController.buscarPorEmail("naoexiste@example.com");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Buscar por ID: quando encontrado, deve retornar HTTP 200 OK e o objeto Coordenador")
    void getCoordenadorPorId_quandoEncontrado_deveRetornarOkCoordenador() {
        when(coordenadorService.findById(coordenadorId)).thenReturn(coordenador);

        ResponseEntity<Coordenador> response = coordenadorController.getCoordenadorPorId(coordenadorId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(coordenadorId, response.getBody().getId());
    }

    @Test
    @DisplayName("Buscar por ID: quando não encontrado (null), deve retornar HTTP 404 NOT_FOUND")
    void getCoordenadorPorId_quandoNaoEncontrado_deveRetornarNotFound() {
        when(coordenadorService.findById(coordenadorId)).thenReturn(null);

        ResponseEntity<Coordenador> response = coordenadorController.getCoordenadorPorId(coordenadorId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}