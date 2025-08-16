package com.br.SAM_FullStack.SAM_FullStack.dto;

import java.util.List;

public record GrupoDTO(
   String nome,
   Integer alunoAdminId,
   List<Integer> alunosIds
) {}
