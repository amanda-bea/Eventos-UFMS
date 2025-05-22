package com.ufms.eventos.services;

import java.util.List;
import java.util.stream.Collectors;

import com.ufms.eventos.dto.PresencaConfirmadaDTO;
import com.ufms.eventos.model.PresencaConfirmada;
import com.ufms.eventos.repository.PresencaConfirmadaRepository;

public class PresencaConfirmadaService {
    private PresencaConfirmadaRepository pr;

    public PresencaConfirmadaService() {
        this.pr = new PresencaConfirmadaRepository();
    }

    public List<PresencaConfirmadaDTO> listarPresencasPorUsuario(String nomeUsuario) {
        // Busca todas as presenças confirmadas do usuário em ações com status "Ativo"
        List<PresencaConfirmada> presencas = pr.getPresencasConfirmadas()
            .stream()
            .filter(p -> p.getUsuario().getNome().equalsIgnoreCase(nomeUsuario))
            .filter(p -> "Ativo".equalsIgnoreCase(p.getAcao().getStatus()))
            .collect(Collectors.toList());

        // Mapeia para DTO mostrando as ações em que o usuário marcou presença
        return presencas.stream()
            .map(p -> new PresencaConfirmadaDTO(
                p.getUsuario().getNome(),
                p.getAcao().getNome()
            ))
            .collect(Collectors.toList());
    }
}
