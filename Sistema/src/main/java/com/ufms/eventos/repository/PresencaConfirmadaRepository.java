package com.ufms.eventos.repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.ufms.eventos.model.Acao;
import com.ufms.eventos.model.PresencaConfirmada;
import com.ufms.eventos.model.RespostaFormulario;

public class PresencaConfirmadaRepository implements IPresencaConfirmadaRepository {
    private HashSet<PresencaConfirmada> presencasConfirmadas;
    private HashSet<RespostaFormulario> respostas;

    public PresencaConfirmadaRepository() {
        this.presencasConfirmadas = new HashSet<PresencaConfirmada>();
        this.respostas = new HashSet<RespostaFormulario>();
    }

    @Override
    public HashSet<PresencaConfirmada> getPresencasConfirmadas() {
        return new HashSet<PresencaConfirmada>(this.presencasConfirmadas);
    }

    @Override
    public boolean addPresencaConfirmada(PresencaConfirmada presencaConfirmada) {
        return this.presencasConfirmadas.add(presencaConfirmada);
    }

    @Override
    public boolean removePresencaConfirmada(PresencaConfirmada presencaConfirmada) {
        return this.presencasConfirmadas.remove(presencaConfirmada);
    }

    @Override
    public long countByAcaoId(Long idAcao) {
        if (idAcao == null) return 0;
        
        return this.presencasConfirmadas.stream()
                .filter(presenca -> presenca.getAcao() != null && idAcao.equals(presenca.getAcao().getId()))
                .count();
    }

    @Override
    public void salvar(RespostaFormulario resposta) {
        if (resposta == null) return;
        
        // Remove a resposta anterior se existir
        this.respostas.removeIf(r -> r.getUsuario().equals(resposta.getUsuario()) && 
                                r.getAcao().equals(resposta.getAcao()));
        
        // Adiciona a nova resposta
        this.respostas.add(resposta);
    }

    @Override
    public List<RespostaFormulario> listarPorAcao(String acaoNome) {
        if (acaoNome == null || acaoNome.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        return this.respostas.stream()
                .filter(r -> r.getAcao() != null && 
                       r.getAcao().getNome() != null && 
                       r.getAcao().getNome().equals(acaoNome))
                .collect(Collectors.toList());
    }
}
