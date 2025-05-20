//CAMADA QUE FAZ A INTERAÇÃO ENTRE O CONTROLLER E O REPOSITORY
//CAMADA DE SERVIÇO
//FAZ A REQUISIÇÃO DOS DADOS DO BANCO DE DADOS COM O REPOSITORY
//CONTÉM A LÓGICA/CASOS DE USO

package com.ufms.eventos.services;

import com.ufms.eventos.model.Categoria;
import com.ufms.eventos.model.Departamento;
import com.ufms.eventos.model.Evento;
import com.ufms.eventos.model.Formulario;
import com.ufms.eventos.model.Organizador;
import com.ufms.eventos.repository.EventoRepository;
import com.ufms.eventos.repository.OrganizadorRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.HashSet;

import com.ufms.eventos.dto.EventoDTO;

public class EventoService {
    private EventoRepository er;
    private OrganizadorRepository or;

    public EventoService() {
        this.er = new EventoRepository();
        this.or = new OrganizadorRepository();
    }

    // listar todos os eventios na tela inicial
    public List<EventoDTO> buscarEventosAtivos() {
        HashSet<Evento> eventos = er.getEventos();

        return eventos.stream()
                .filter(e -> "Ativo".equalsIgnoreCase(e.getStatus()))
                .map(EventoDTO::new)
                .collect(Collectors.toList());
    }

    public boolean solicitarEvento(EventoDTO eventoDTO, Organizador organizador) {
        // Adiciona o organizador ao repositório, se necessário
        this.or.addOrganizador(organizador);

        // Converte os campos de String para os tipos corretos
        java.time.LocalDate dataInicio = java.time.LocalDate.parse(eventoDTO.getDataInicio());
        java.time.LocalDate dataFim = java.time.LocalDate.parse(eventoDTO.getDataFim());

        // Converte enums de String para Enum
        Departamento departamento = Departamento.valueOf(eventoDTO.getDepartamento());
        Categoria categoria = Categoria.valueOf(eventoDTO.getCategoria());

        // Define status e mensagem de rejeição iniciais
        String status = "Aguardando aprovação";
        String mensagemRejeicao = null;

        // Adiciona o evento ao repositório, incluindo o organizador e os enums convertidos
        return this.er.addEvento(
            eventoDTO.getNome(),
            dataInicio,
            dataFim,
            eventoDTO.getDescricao(),
            organizador,
            departamento,
            categoria,
            eventoDTO.getImagem(),
            eventoDTO.getLink(),
            status,
            mensagemRejeicao
        );
    }

    public boolean excluirSolicitacaoEvento(String nomeEvento, Organizador organizador) {
        Evento evento = er.getEvento(nomeEvento);
        if (evento != null && "Aguardando aprovação".equalsIgnoreCase(evento.getStatus())
            && evento.getOrganizador().equals(organizador)) {
            return er.removeEvento(evento);
        }
        return false;
    }

    // Método utilitário para listar categorias fixas
    public List<String> listarCategorias() {
        return Arrays.stream(Categoria.values())
                     .map(Enum::name)
                     .collect(Collectors.toList());
    }

    // Método utilitário para listar departamentos fixos
    public List<String> listarDepartamentos() {
        return Arrays.stream(Departamento.values())
                     .map(Enum::name)
                     .collect(Collectors.toList());
    }

    public String adicionarCampoExtra(Formulario formulario, String campo) {
        if (formulario.getCamposExtras().containsKey(campo)) {
            return "Campo já existe!";
        }
        formulario.getCamposExtras().put(campo, "");
        return "Campo adicionado com sucesso!";
    }

}

