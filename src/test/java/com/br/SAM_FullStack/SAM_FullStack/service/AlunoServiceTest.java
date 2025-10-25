package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.model.Aluno;
import com.br.SAM_FullStack.SAM_FullStack.repository.AlunoRepository;
import com.br.SAM_FullStack.SAM_FullStack.autenticacao.TokenService;
import com.br.SAM_FullStack.SAM_FullStack.model.Curso; // Assuming Curso exists
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("Testes Unitários do AlunoService")
class AlunoServiceTest {

    /*@Autowired
    AlunoRepository alunoRepository;

    @MockitoBean
    EmailService emailService;
    @MockitoBean
    PasswordEncoder passwordEncoder;
    @MockitoBean
    TokenService tokenService;

    @InjectMocks
    AlunoService alunoService;

    Aluno aluno;
    Curso mockCurso;

    @BeforeEach
    void setUp() {
        mockCurso = new Curso();
        mockCurso.setId(1L);
        mockCurso.setNome("Eng Teste");

        aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("Aluno Teste");
        aluno.setEmail("teste@email.com");
        aluno.setRa(12345);
        aluno.setSenha("senha123"); // Senha não encriptada para teste
        aluno.setCurso(mockCurso);

        ReflectionTestUtils.setField(alunoService, "emailService", emailService);
        ReflectionTestUtils.setField(alunoService, "passwordEncoder", passwordEncoder);
        ReflectionTestUtils.setField(alunoService, "tokenService", tokenService);
    }

    @Test
    @DisplayName("Deve retornar aluno ao buscar por ID existente")
    void findById_quandoIdExistente_deveRetornarAluno() {
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        Aluno alunoEncontrado = alunoService.findById(1L);
        assertNotNull(alunoEncontrado);
        assertEquals(1L, alunoEncontrado.getId());
        verify(alunoRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar por ID inexistente")
    void findById_quandoIdNaoExistente_deveLancarRuntimeException() {
        when(alunoRepository.findById(anyLong())).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> alunoService.findById(99L));
        assertEquals("Aluno não encontrado com ID: 99", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar aluno ao buscar por RA existente")
    void findByRa_quandoRaExistente_deveRetornarAluno() {
        when(alunoRepository.findByRa(12345)).thenReturn(Optional.of(aluno));
        Aluno alunoEncontrado = alunoService.findByRa(12345);
        assertNotNull(alunoEncontrado);
        assertEquals(12345, alunoEncontrado.getRa());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar por RA inexistente")
    void findByRa_quandoRaNaoExistente_deveLancarRuntimeException() {
        when(alunoRepository.findByRa(anyInt())).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> alunoService.findByRa(99999));
        assertEquals("Aluno não encontrado com RA: 99999", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar lista de todos os alunos")
    void findAll_deveRetornarListaDeAlunos() {
        List<Aluno> alunos = Arrays.asList(aluno, new Aluno());
        when(alunoRepository.findAll()).thenReturn(alunos);
        List<Aluno> resultado = alunoService.findAll();
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }


    @Test
    @DisplayName("Deve salvar aluno com RA não existente e enviar email")
    void save_quandoRaNaoExistente_deveSalvarEEnviarEmail() {
        when(alunoRepository.findByRa(aluno.getRa())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(aluno.getSenha())).thenReturn("senhaEncriptada");
        when(alunoRepository.save(any(Aluno.class))).thenAnswer(invocation -> {
            Aluno a = invocation.getArgument(0);
            a.setId(1L);
            return a;
        });
        when(emailService.enviarEmailTexto(anyString(), anyString(), anyString()))
                .thenReturn("Email Enviado - Mock");

        Aluno alunoSalvo = alunoService.save(aluno);

        assertNotNull(alunoSalvo);
        assertEquals("senhaEncriptada", alunoSalvo.getSenha());
        verify(alunoRepository).findByRa(aluno.getRa());
        verify(passwordEncoder).encode("senha123");
        verify(alunoRepository).save(aluno);
        verify(emailService).enviarEmailTexto(
                eq(aluno.getEmail()),
                eq("Aluno Cadastrado com Sucesso"),
                contains("Olá Aluno Teste")
        );
    }


    @Test
    @DisplayName("Deve lançar exceção ao salvar aluno com RA existente")
    void save_quandoRaExistente_deveLancarRuntimeException() {
        Aluno alunoExistente = new Aluno();
        when(alunoRepository.findByRa(aluno.getRa())).thenReturn(Optional.of(alunoExistente));
        when(emailService.enviarEmailTexto(anyString(), anyString(), anyString()))
                .thenReturn("Email Enviado - Mock Falha");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> alunoService.save(aluno));

        assertEquals("Aluno com RA 12345 já está cadastrado!", exception.getMessage());
        verify(emailService).enviarEmailTexto(
                eq(aluno.getEmail()),
                eq("Falha no Cadastro: RA já existente"),
                contains("Olá Aluno Teste")
        );
        verify(alunoRepository, never()).save(any(Aluno.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Deve atualizar aluno com sucesso sem alterar email e sem nova senha")
    void update_quandoSucessoSemAlterarEmailESenha_deveRetornarAlunoAtualizado() {
        Aluno alunoUpdate = new Aluno();
        alunoUpdate.setNome("Novo Nome");
        alunoUpdate.setEmail("teste@email.com");
        alunoUpdate.setRa(54321);
        alunoUpdate.setSenha(null);

        String originalEncodedPassword = "encodedOriginalPassword";
        aluno.setSenha(originalEncodedPassword);

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(alunoRepository.save(any(Aluno.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Aluno alunoAtualizado = alunoService.update(1L, alunoUpdate);

        assertNotNull(alunoAtualizado);
        assertEquals("Novo Nome", alunoAtualizado.getNome());
        assertEquals(54321, alunoAtualizado.getRa());
        assertEquals("teste@email.com", alunoAtualizado.getEmail());
        assertEquals(originalEncodedPassword, alunoAtualizado.getSenha());
        verify(alunoRepository).findById(1L);
        verify(alunoRepository, never()).findByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(alunoRepository).save(aluno);
    }

    @Test
    @DisplayName("Deve atualizar aluno com nova senha")
    void update_quandoSenhaNova_deveEncriptarESalvar() {
        Aluno alunoUpdate = new Aluno();
        alunoUpdate.setNome("Aluno Teste");
        alunoUpdate.setEmail("teste@email.com");
        alunoUpdate.setRa(12345);
        alunoUpdate.setSenha("novaSenha123");

        aluno.setSenha("senhaAntigaEncriptada");

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(passwordEncoder.encode("novaSenha123")).thenReturn("novaSenhaEncriptada");
        when(alunoRepository.save(any(Aluno.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Aluno alunoAtualizado = alunoService.update(1L, alunoUpdate);

        assertNotNull(alunoAtualizado);
        assertEquals("novaSenhaEncriptada", alunoAtualizado.getSenha());
        verify(alunoRepository).findById(1L);
        verify(passwordEncoder).encode("novaSenha123");
        verify(alunoRepository).save(aluno);
    }


    @Test
    @DisplayName("Deve atualizar aluno com email alterado e disponível")
    void update_quandoEmailAlteradoEDisponivel_deveRetornarAlunoAtualizado() {
        Aluno alunoUpdate = new Aluno();
        alunoUpdate.setNome("Novo Nome");
        alunoUpdate.setEmail("novoemail@email.com");
        alunoUpdate.setSenha(null);

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(alunoRepository.findByEmail("novoemail@email.com")).thenReturn(Optional.empty());
        when(alunoRepository.save(any(Aluno.class))).thenReturn(aluno);

        Aluno alunoAtualizado = alunoService.update(1L, alunoUpdate);

        assertNotNull(alunoAtualizado);
        assertEquals("novoemail@email.com", alunoAtualizado.getEmail());
        verify(alunoRepository).findByEmail("novoemail@email.com");
        verify(alunoRepository).save(aluno);
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar aluno com e-mail que já existe")
    void update_quandoEmailAlteradoEJaExiste_deveLancarRuntimeException() {
        Aluno alunoUpdate = new Aluno();
        alunoUpdate.setEmail("emailjaexistente@email.com");
        alunoUpdate.setSenha(null);

        Aluno outroAluno = new Aluno();
        outroAluno.setId(2L);

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(alunoRepository.findByEmail("emailjaexistente@email.com")).thenReturn(Optional.of(outroAluno));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> alunoService.update(1L, alunoUpdate));

        assertEquals("O e-mail 'emailjaexistente@email.com' já está cadastrado.", exception.getMessage());
        verify(alunoRepository, never()).save(any(Aluno.class));
    }

    @Test
    @DisplayName("Deve deletar aluno com sucesso")
    void delete_quandoIdExistente_deveChamarDelete() {
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        doNothing().when(alunoRepository).delete(aluno);
        alunoService.delete(1L);
        verify(alunoRepository).delete(aluno);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar aluno inexistente")
    void delete_quandoIdNaoExistente_deveLancarRuntimeException() {
        when(alunoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> alunoService.delete(99L));
        verify(alunoRepository, never()).delete(any(Aluno.class));
    }


    @Test
    @DisplayName("Deve salvar lista de alunos")
    void saveAll_deveSalvarTodosAlunos() {
        Aluno aluno1 = new Aluno();
        aluno1.setRa(111);
        aluno1.setEmail("a1@email.com");
        aluno1.setSenha("senha1");
        aluno1.setCurso(mockCurso);

        Aluno aluno2 = new Aluno();
        aluno2.setRa(222);
        aluno2.setEmail("a2@email.com");
        aluno2.setSenha("senha2");
        aluno2.setCurso(mockCurso);

        List<Aluno> alunosParaSalvar = Arrays.asList(aluno1, aluno2);

        when(alunoRepository.findByRa(111)).thenReturn(Optional.empty());
        when(passwordEncoder.encode("senha1")).thenReturn("enc1");
        when(alunoRepository.save(aluno1)).thenAnswer(inv -> { inv.getArgument(0, Aluno.class).setId(10L); return aluno1; });

        when(alunoRepository.findByRa(222)).thenReturn(Optional.empty());
        when(passwordEncoder.encode("senha2")).thenReturn("enc2");
        when(alunoRepository.save(aluno2)).thenAnswer(inv -> { inv.getArgument(0, Aluno.class).setId(11L); return aluno2; });

        when(emailService.enviarEmailTexto(anyString(), eq("Aluno Cadastrado com Sucesso"), anyString()))
                .thenReturn("Email Enviado - Mock");

        List<Aluno> alunosSalvos = alunoService.saveAll(alunosParaSalvar);

        assertNotNull(alunosSalvos);
        assertEquals(2, alunosSalvos.size());
        verify(alunoRepository).findByRa(111);
        verify(passwordEncoder).encode("senha1");
        verify(alunoRepository).save(aluno1);
        verify(emailService).enviarEmailTexto(eq("a1@email.com"), eq("Aluno Cadastrado com Sucesso"), anyString());

        verify(alunoRepository).findByRa(222);
        verify(passwordEncoder).encode("senha2");
        verify(alunoRepository).save(aluno2);
        verify(emailService).enviarEmailTexto(eq("a2@email.com"), eq("Aluno Cadastrado com Sucesso"), anyString());

        verify(passwordEncoder, times(2)).encode(anyString());
        verify(alunoRepository, times(2)).save(any(Aluno.class));
        verify(emailService, times(2)).enviarEmailTexto(anyString(), eq("Aluno Cadastrado com Sucesso"), anyString());
    }


    @Test
    @DisplayName("Deve retornar alunos ao buscar por nome")
    void buscarPorNome_deveRetornarListaDeAlunos() {
        List<Aluno> alunos = Arrays.asList(aluno);
        when(alunoRepository.findByNomeContainingIgnoreCase("Teste")).thenReturn(alunos);
        List<Aluno> resultado = alunoService.buscarPorNome("Teste");
        assertFalse(resultado.isEmpty());
        assertEquals("Aluno Teste", resultado.get(0).getNome());
    }

    @Test
    @DisplayName("Deve retornar alunos ordenados por nome")
    void buscarTodosOrdenadoPorNome_deveRetornarListaOrdenada() {
        List<Aluno> alunos = Arrays.asList(aluno);
        when(alunoRepository.findAllByOrderByNomeAsc()).thenReturn(alunos);
        List<Aluno> resultado = alunoService.buscarTodosOrdenadoPorNome();
        assertFalse(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar aluno ao buscar por e-mail existente")
    void findByEmail_quandoEmailExistente_deveRetornarAluno() {
        when(alunoRepository.findByEmail("teste@email.com")).thenReturn(Optional.of(aluno));
        Aluno alunoEncontrado = alunoService.findByEmail("teste@email.com");
        assertNotNull(alunoEncontrado);
        assertEquals("teste@email.com", alunoEncontrado.getEmail());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar por e-mail inexistente")
    void findByEmail_quandoEmailNaoExistente_deveLancarRuntimeException() {
        when(alunoRepository.findByEmail("naoexiste@email.com")).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            alunoService.findByEmail("naoexiste@email.com");
        });
        assertEquals("Aluno não encontrado com o E-mail: naoexiste@email.com", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar lista de alunos ao buscar por curso")
    void findByCurso_quandoExistemAlunos_deveRetornarLista() {
        Long cursoId = 1L;
        when(alunoRepository.findByCursoId(cursoId)).thenReturn(Arrays.asList(aluno));
        List<Aluno> resultado = alunoService.findByCurso(cursoId);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals(aluno.getId(), resultado.get(0).getId());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nenhum aluno encontrado no curso")
    void findByCurso_quandoNenhumAlunoNoCurso_deveLancarRuntimeException() {
        Long cursoId = 99L;
        when(alunoRepository.findByCursoId(cursoId)).thenReturn(Collections.emptyList());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> alunoService.findByCurso(cursoId));
        assertEquals("Nenhum aluno encontrado nesse curso", exception.getMessage());
    }*/
}