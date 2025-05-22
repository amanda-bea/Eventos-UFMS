package com.ufms.eventos.repository;

import java.util.HashSet;

import com.ufms.eventos.model.PresencaConfirmada;

public class PresencaConfirmadaRepository {
    private HashSet<PresencaConfirmada> presencasConfirmadas;

    public PresencaConfirmadaRepository() {
        this.presencasConfirmadas = new HashSet<PresencaConfirmada>();
    }

    public HashSet<PresencaConfirmada> getPresencasConfirmadas() {
        return new HashSet<PresencaConfirmada>(this.presencasConfirmadas);
    }

    public boolean addPresencaConfirmada(PresencaConfirmada presencaConfirmada) {
        return this.presencasConfirmadas.add(presencaConfirmada);
    }

    public boolean removePresencaConfirmada(PresencaConfirmada presencaConfirmada) {
        return this.presencasConfirmadas.remove(presencaConfirmada);
    }


}