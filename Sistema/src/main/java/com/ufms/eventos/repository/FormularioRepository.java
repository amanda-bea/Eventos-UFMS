package com.ufms.eventos.repository;

import com.ufms.eventos.model.Formulario; //ideia do chat, vou estudar isso aqui ainda

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class FormularioRepository {

    private final List<Formulario> banco = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Formulario salvar(Formulario inscricao) {
        inscricao.setId(idGenerator.getAndIncrement());
        banco.add(inscricao);
        return inscricao;
    }

    public List<Formulario> listarTodas() {
        return banco;
    }
}
