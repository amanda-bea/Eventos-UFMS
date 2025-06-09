package com.ufms.eventos.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

public class ConnectionFactory {

    private static final String LOGIN = "postgres";
    private static final String SENHA = "123";
    private static final String DATABASE = "dbeventos";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            criarBancoDeDadosSeNecessario();
            String url = "jdbc:postgresql://localhost:5432/" + DATABASE;
            return DriverManager.getConnection(url, LOGIN, SENHA);
        } catch (Exception e) {
            throw new SQLException("Erro ao conectar ao banco de dados: " + e.getMessage(), e);
        }
    }

    private static void criarBancoDeDadosSeNecessario() throws Exception {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        try (Connection conn = DriverManager.getConnection(url, LOGIN, SENHA);
             Statement stmt = conn.createStatement()) {
            var rs = stmt.executeQuery("SELECT 1 FROM pg_database WHERE datname = '" + DATABASE + "'");
            if (!rs.next()) {
                stmt.executeUpdate("CREATE DATABASE " + DATABASE);
                System.out.println("Banco de dados " + DATABASE + " criado com sucesso!");
            }
        }
    }

    public static void main(String[] args) {
        try {
            Connection conn = ConnectionFactory.getConnection();
            criarTabelas(conn);
            JOptionPane.showMessageDialog(null, "Conexão estabelecida e tabelas criadas com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void criarTabelas(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        // (Conteúdo da criação de tabelas — mantido exatamente como você escreveu, completo)

        // Exemplo de uma tabela (copiar o restante do seu código original aqui)
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS usuarios (" +
                "nome VARCHAR(255) PRIMARY KEY, " +
                "email VARCHAR(255) NOT NULL UNIQUE, " +
                "senha VARCHAR(255) NOT NULL, " +
                "telefone VARCHAR(50)" +
                ")");
        System.out.println("Tabela usuarios criada com sucesso");
        
        // Tabela específica para Admin - sem ID próprio, usando nome como FK e PK
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS admins (" +
                "nome VARCHAR(255) PRIMARY KEY, " +  // Nome como PK
                "cargo VARCHAR(100), " +
                "FOREIGN KEY (nome) REFERENCES usuarios(nome)" +  // Nome como FK para usuarios
                ")");
        System.out.println("Tabela admins criada com sucesso");
        
        // Tabela específica para Organizador - sem ID próprio, usando nome como FK e PK
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS organizadores (" +
                "nome VARCHAR(255) PRIMARY KEY, " +  // Nome como PK
                "FOREIGN KEY (nome) REFERENCES usuarios(nome)" +  // Nome como FK para usuarios
                ")");
        System.out.println("Tabela organizadores criada com sucesso");
        
        // Tabela específica para Participante - sem ID próprio, usando nome como FK e PK
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS participantes (" +
                "nome VARCHAR(255) PRIMARY KEY, " +  // Nome como PK
                "FOREIGN KEY (nome) REFERENCES usuarios(nome)" +  // Nome como FK para usuarios
                ")");
        System.out.println("Tabela participantes criada com sucesso");
        
        /// Tabela Departamento (só para lookup, não tem ID na classe Java)
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS departamentos (" +
                "nome VARCHAR(50) PRIMARY KEY" +  // Nome do enum como PK
                ")");
        
        // Inserção dos valores do enum Departamento (mantido como estava)
        String[] departamentos = {"ESAN", "FACFAN", "FACH", "FAED", "FAALC", "FACOM", "FADIR", 
                                "FAENG", "FAMED", "FAMEZ", "FAODO", "INBIO", "INFI", "INMA", 
                                "INQUI", "INISA", "CPAQ", "CPCS", "CPCX", "CPNV", "CPNA", 
                                "CPAR", "CPPP", "CPTL", "CPAN", "AGEAD"};
        
        for (String dep : departamentos) {
            try {
                stmt.executeUpdate("INSERT INTO departamentos (nome) VALUES ('" + dep + "') ON CONFLICT (nome) DO NOTHING");
            } catch (SQLException e) {
                // Ignora erro de duplicação
                if (!e.getMessage().contains("duplicate key value")) {
                    throw e;
                }
            }
        }

        // Tabela Categoria (só para lookup, não tem ID na classe Java)
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS categorias (" +
                "nome VARCHAR(50) PRIMARY KEY, " +  // Nome do enum como PK
                "display_name VARCHAR(50) NOT NULL" +  // Nome formatado para exibição
                ")");

    
        // Inserção dos valores do enum Categoria
        String[][] categorias = {
            {"CULTURA", "Cultura"}, 
            {"EDUCACAO", "Educação"},
            {"SAUDE", "Saúde"},
            {"TECNOLOGIA", "Tecnologia"},
            {"ESPORTE", "Esporte"},
            {"LAZER", "Lazer"},
            {"OUTROS", "Outros"}
        };
        
        for (String[] cat : categorias) {
            try {
                stmt.executeUpdate("INSERT INTO categorias (nome, display_name) VALUES ('" + cat[0] + "', '" + cat[1] + "') ON CONFLICT (nome) DO NOTHING");
            } catch (SQLException e) {
                // Ignora erro de duplicação
                if (!e.getMessage().contains("duplicate key value")) {
                    throw e;
                }
            }
        }
        
        // Tabela Evento com referências aos enums
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS eventos (" +
            "id SERIAL PRIMARY KEY, " +
            "nome VARCHAR(255) NOT NULL UNIQUE, " +
            "data_inicio DATE NOT NULL, " +
            "data_fim DATE NOT NULL, " +
            "descricao TEXT, " +
            "organizador_nome VARCHAR(255) REFERENCES organizadores(nome), " +
            "departamento VARCHAR(50) REFERENCES departamentos(nome), " +
            "categoria VARCHAR(50) REFERENCES categorias(nome), " +
            "imagem VARCHAR(255), " +
            "link VARCHAR(255), " +
            "status VARCHAR(50) NOT NULL, " +
            "mensagem_rejeicao TEXT" +
            ")");
        System.out.println("Tabela eventos criada com sucesso");    
        
        // Mantendo o restante como estava...
        // Criação da tabela Acao
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS acoes (" +
                "id SERIAL PRIMARY KEY, " +
                "evento_id INTEGER REFERENCES eventos(id), " +
                "nome VARCHAR(255) NOT NULL UNIQUE, " +
                "data DATE NOT NULL, " +
                "descricao TEXT, " +
                "local VARCHAR(255), " +
                "horario_inicio TIME NOT NULL, " +
                "horario_fim TIME NOT NULL, " +
                "departamento VARCHAR(50) REFERENCES departamentos(nome), " +
                "contato VARCHAR(255), " +
                "modalidade VARCHAR(50) NOT NULL, " +
                "link VARCHAR(255), " +
                "status VARCHAR(50) NOT NULL, " +
                "capacidade INTEGER, " +
                "mensagem_rejeicao TEXT" +
                ")");
        System.out.println("Tabela acoes criada com sucesso");
        
        // Criação da tabela ConfiguracaoFormulario
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS configuracoes_formulario (" +
                "id SERIAL PRIMARY KEY, " +
                "acao_id INTEGER NOT NULL, " +
                "usar_nome BOOLEAN NOT NULL DEFAULT true, " +
                "usar_email BOOLEAN NOT NULL DEFAULT true, " +
                "usar_cpf BOOLEAN NOT NULL DEFAULT false, " +
                "usar_curso BOOLEAN NOT NULL DEFAULT false, " +
                "FOREIGN KEY (acao_id) REFERENCES acoes(id)" +
                ")");
        System.out.println("Tabela configuracoes_formulario criada com sucesso");
        
        // Criação da tabela para campos personalizados de formulário
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS campos_personalizados (" +
                "id SERIAL PRIMARY KEY, " +
                "configuracao_id INTEGER REFERENCES configuracoes_formulario(id), " +
                "nome_campo VARCHAR(255) NOT NULL" +
                ")");
        System.out.println("Tabela campos_personalizados criada com sucesso");
        
        // Criação da tabela RespostaFormulario
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS respostas_formulario (" +
                "id SERIAL PRIMARY KEY, " +
                "nome VARCHAR(255), " +
                "email VARCHAR(255), " +
                "cpf VARCHAR(14), " +
                "curso VARCHAR(255), " +
                "acao_id INTEGER REFERENCES acoes(id)" +
                ")");
        System.out.println("Tabela respostas_formulario criada com sucesso");
        
        // Tabela para respostas extras
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS respostas_extras (" +
                "id SERIAL PRIMARY KEY, " +
                "resposta_id INTEGER REFERENCES respostas_formulario(id), " +
                "nome_campo VARCHAR(255) NOT NULL, " +
                "valor TEXT" +
                ")");
        System.out.println("Tabela respostas_extras criada com sucesso");
        
        // Atualização da tabela PresencaConfirmada - agora usando nome do usuário
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS presencas_confirmadas (" +
                "id SERIAL PRIMARY KEY, " +
                "usuario_nome VARCHAR(255) REFERENCES usuarios(nome), " + // Alterado para referenciar nome
                "acao_id INTEGER REFERENCES acoes(id), " +
                "data_confirmacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")");
        System.out.println("Tabela presencas_confirmadas criada com sucesso");
    }
}
// compilar: 
// cd Sistema && javac -cp "lib/postgresql-42.7.javac -cp "lib/postgresql-42.7.3.jar" -d bin src/main/java/com/ufms/eventos/util/ConnectionFactory.java3.jar" -d bin src/main/java/com/ufms/eventos/util/ConnectionFactory.java
// java -cp "bin;lib/postgresql-42.7.3.jar" com.ufms.eventos.util.ConnectionFactory