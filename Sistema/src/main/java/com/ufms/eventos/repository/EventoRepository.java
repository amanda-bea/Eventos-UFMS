package com.ufms.eventos.repository;

import com.ufms.eventos.model.Evento;
import java.util.List;

public interface EventoRepository {
    void salvar(Evento evento) throws Exception;
    Evento buscarPorId(Long id) throws Exception;
    List<Evento> listarTodos() throws Exception;
    void atualizar(Evento evento) throws Exception;
    void deletar(Long id) throws Exception;
}
