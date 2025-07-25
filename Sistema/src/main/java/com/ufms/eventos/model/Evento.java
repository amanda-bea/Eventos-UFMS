package com.ufms.eventos.model;

import lombok.Data;
import java.time.LocalDate;

import com.ufms.eventos.dto.EventoDTO;

@Data
public class Evento {
    private Long id;
    private String nome;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String descricao;
    private Organizador organizador;
    private Departamento departamento;
    private Categoria categoria; // Ex: Cultura, Educação, Saúde, etc.
    private String imagem; // Path da imagem do evento
    private String link; // Link para mais informações (opcional)
    private String status; // Ativo, Inativo, Cancelado, etc.
    private String mensagemRejeicao;

    public Evento(EventoDTO evento) {
        this.nome = evento.getNome();
        this.dataInicio = LocalDate.parse(evento.getDataInicio());
        this.dataFim = LocalDate.parse(evento.getDataFim());
        this.descricao = evento.getDescricao();
        //this.organizador = evento.getOrganizador(); //não deve aparecer no DTO
        this.departamento = Departamento.valueOf(evento.getDepartamento());
        this.categoria = Categoria.valueOf(evento.getCategoria());
        this.imagem = evento.getImagem();
        this.link = evento.getLink();
    }

    public Evento() {
    }
        
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Evento other = (Evento) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }


}