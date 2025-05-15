package com.ufms.eventos.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

@Data
public class Usuario {
    private String nome;
    private String email;
    private String senha;
    private String telefone; // Formato: telefone ou e-mail

    public Usuario(String nome, String email, String senha, String telefone) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
    }

    public Usuario() {
        // Construtor padrão
    }

    @Override
    public String toString() {
        return "Usuário{" +
                "nome='" + nome + '\'' +
                "telefone='" + telefone + '\'' +
                ", email='" + email + '\'' +
                ", senha='" + senha + '\'' +
                '}';
    }
    // Métodos para Organizador de Eventos

    // Mostrar todos os eventos do sistema
    public void visualizarEventos(List<Evento> eventos) {
        System.out.println("Lista de eventos disponíveis:");
        for (Evento evento : eventos) {
            System.out.println(evento);
        }
    }

    // Solicitar um novo evento
    public void solicitarEvento() {
    Scanner scanner = new Scanner(System.in);

    try {
        System.out.println("Digite o nome do evento:");
        String nome = scanner.nextLine();

        System.out.println("Digite a data do evento (dd/MM/yyyy):");
        String dataInput = scanner.nextLine();
        LocalDate data = LocalDate.parse(dataInput, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        System.out.println("Digite a descrição do evento:");
        String descricao = scanner.nextLine();

        System.out.println("Digite o local do evento:");
        String local = scanner.nextLine();

        System.out.println("Digite o horário de início do evento (HH:mm):");
        String horarioInicioInput = scanner.nextLine();
        LocalTime horarioInicio = LocalTime.parse(horarioInicioInput, DateTimeFormatter.ofPattern("HH:mm"));

        System.out.println("Digite o horário de término do evento (HH:mm):");
        String horarioFimInput = scanner.nextLine();
        LocalTime horarioFim = LocalTime.parse(horarioFimInput, DateTimeFormatter.ofPattern("HH:mm"));

        String organizador = this.nome;

        System.out.println("Digite o nome do departamento do organizador:");
        String departamento = scanner.nextLine();

        System.out.println("Digite o contato do organizador:");
        String contato = scanner.nextLine();

        System.out.println("Digite a modalidade do evento (Ex: Presencial):");
        String modalidade = scanner.nextLine();

        System.out.println("Digite a categoria do evento (Ex: Cultura, Educação, Entretenimento, Esportes):");
        String categoria = scanner.nextLine();

        System.out.println("Digite a URL da imagem do evento (exemplo):");
        String imagem = scanner.nextLine();

        System.out.println("Digite o link para mais informações (opcional):");
        String link = scanner.nextLine();

        System.out.println("Digite a capacidade do evento (opcional, 0 para ignorar):");
        int capacidade = scanner.nextInt();
        scanner.nextLine(); // Consumir a quebra de linha

        // Criar o objeto Evento
        Evento evento = new Evento(
            nome, data, descricao, local, horarioInicio, horarioFim, organizador, departamento,
            contato, modalidade, categoria, imagem, link, capacidade
        );

        System.out.println("Evento solicitado com sucesso: " + evento);
    } catch (Exception e) {
        System.out.println("Erro ao criar o evento: " + e.getMessage());
    } finally {
        scanner.close();
    }
}

    // Editar um evento existente
    public void editarEvento(List<Evento> eventos) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Digite o nome do evento que deseja editar:");
        String nomeEvento = scanner.nextLine();

        Evento evento = eventos.stream()
            .filter(e -> e.getNome().equalsIgnoreCase(nomeEvento))
            .findFirst()
            .orElse(null);

        if (evento == null) {
            System.out.println("Evento não encontrado.");
            scanner.close();
            return;
        }

        if (!"Ativo".equalsIgnoreCase(evento.getStatus())) {
            System.out.println("O evento não pode ser editado porque não está ativo.");
            scanner.close();
            return;
        }

        System.out.println("Editando o evento: " + evento.getNome());

        System.out.println("Digite a nova data do evento (dd/MM/yyyy) ou pressione Enter para manter:");
        String novaData = scanner.nextLine();
        if (!novaData.isEmpty()) {
            try {
                LocalDate dataConvertida = LocalDate.parse(novaData, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                evento.setData(dataConvertida);
            } catch (Exception e) {
                System.out.println("Data inválida. Mantendo o valor atual.");
            }
        }

        System.out.println("Digite o novo local do evento ou pressione Enter para manter:");
        String novoLocal = scanner.nextLine();
        if (!novoLocal.isEmpty()) {
            evento.setLocal(novoLocal);
        }

        System.out.println("Digite o novo horário de início (HH:mm) ou pressione Enter para manter:");
        String novoHorarioInicio = scanner.nextLine();
        if (!novoHorarioInicio.isEmpty()) {
            try {
                LocalTime horarioConvertido = LocalTime.parse(novoHorarioInicio, DateTimeFormatter.ofPattern("HH:mm"));
                evento.setHorarioInicio(horarioConvertido);
            } catch (Exception e) {
                System.out.println("Horário inválido. Mantendo o valor atual.");
            }
        }

        System.out.println("Digite o novo horário de término (HH:mm) ou pressione Enter para manter:");
        String novoHorarioFim = scanner.nextLine();
        if (!novoHorarioFim.isEmpty()) {
            try {
                LocalTime horarioConvertido = LocalTime.parse(novoHorarioFim, DateTimeFormatter.ofPattern("HH:mm"));
                evento.setHorarioFim(horarioConvertido);
            } catch (Exception e) {
                System.out.println("Horário inválido. Mantendo o valor atual.");
            }
        }

        System.out.println("Digite a nova capacidade do evento ou pressione Enter para manter:");
        String novaCapacidade = scanner.nextLine();
        if (!novaCapacidade.isEmpty()) {
            try {
                evento.setCapacidade(Integer.parseInt(novaCapacidade));
            } catch (NumberFormatException e) {
                System.out.println("Capacidade inválida. Mantendo o valor atual.");
            }
        }

        System.out.println("Evento atualizado com sucesso: " + evento);
        scanner.close();
    }

    // Cancelar um evento
    public void cancelarEvento(List<Evento> eventos) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Digite o nome do evento que deseja cancelar:");
        String nomeEvento = scanner.nextLine();

        Evento evento = eventos.stream()
            .filter(e -> e.getNome().equalsIgnoreCase(nomeEvento))
            .findFirst()
            .orElse(null);

        if (evento == null) {
            System.out.println("Evento não encontrado.");
            scanner.close();
            return;
        }

        if (!"Ativo".equalsIgnoreCase(evento.getStatus())) {
            System.out.println("O evento não pode ser cancelado porque não está ativo.");
            scanner.close();
            return;
        }

        evento.setStatus("Cancelado");
        System.out.println("Evento cancelado com sucesso: " + evento);
        scanner.close();
    }

    // Visualizar eventos criados pelo usuário
    public void visualizarEventosCriados(List<Evento> eventos) {
        System.out.println("Lista de eventos criados pelo usuário:");
        eventos.stream()
            .filter(evento -> evento.getOrganizador().equalsIgnoreCase(this.nome))
            .forEach(System.out::println);
    }

    // Métodos para Participante normal
    public void visualizarPresencas(List<Evento> eventos) {
        System.out.println("Lista de eventos com presença confirmada:");
        // Implementar lógica para exibir eventos com presença confirmada
    }
}