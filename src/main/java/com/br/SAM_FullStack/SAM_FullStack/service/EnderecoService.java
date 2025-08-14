package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.model.Endereco;
import com.br.SAM_FullStack.SAM_FullStack.repository.EnderecoRepository;

import java.util.List;

public class EnderecoService {
    private final EnderecoRepository enderecoRepository;

    public EnderecoService(EnderecoRepository enderecoRepository) {
        this.enderecoRepository = enderecoRepository;
    }

    // Listar todos
    public List<Endereco> listAll() {
        return enderecoRepository.findAll();
    }

    // Buscar por ID
    public Endereco findById(Long id) {
        return enderecoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));
    }

    // Salvar
    public Endereco save(Endereco endereco) {
        return enderecoRepository.save(endereco);
    }

    // Atualizar
    public Endereco update(Long id, Endereco enderecoUpdate) {
        Endereco enderecoExistente = findById(id);

        enderecoExistente.setRua(enderecoUpdate.getRua());
        enderecoExistente.setNumero(enderecoUpdate.getNumero());
        enderecoExistente.setBairro(enderecoUpdate.getBairro());
        enderecoExistente.setCidade(enderecoUpdate.getCidade());
        enderecoExistente.setEstado(enderecoUpdate.getEstado());
        enderecoExistente.setCep(enderecoUpdate.getCep());

        return enderecoRepository.save(enderecoExistente);
    }

    // Deletar
    public void delete(Long id) {
        Endereco endereco = findById(id);
        enderecoRepository.delete(endereco);
    }
}
