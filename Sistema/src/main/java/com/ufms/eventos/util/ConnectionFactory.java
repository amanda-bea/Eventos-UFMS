package com.ufms.eventos.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

public class ConnectionFactory {
    private static final String LOGIN = "postgres"; //mudar aqui quando for usar
    private static final String SENHA = "postgres"; //mudar aqui quando for usar
    private static final String DATABASE = "dbeventos";
    
    public static Connection getConnection() throws Exception {
        try {
            Class.forName("org.postgresql.Driver");
            // Primeiro verifica se o banco existe, se não, cria
            criarBancoDeDadosSeNecessario();
            // conexao com DB
            String url = "jdbc:postgresql://localhost:5432/" + DATABASE;
            return DriverManager.getConnection(url, LOGIN, SENHA);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    
    /**
     * Cria o banco de dados se ele não existir
     */
    private static void criarBancoDeDadosSeNecessario() throws Exception {
        // Conecta ao banco postgres (banco padrão)
        String url = "jdbc:postgresql://localhost:5432/postgres";
        try (Connection conn = DriverManager.getConnection(url, LOGIN, SENHA);
             Statement stmt = conn.createStatement()) {
            
            // Verifica se o banco dbeventos existe
            java.sql.ResultSet rs = stmt.executeQuery(
                    "SELECT 1 FROM pg_database WHERE datname = '" + DATABASE + "'");
            
            if (!rs.next()) {
                // Se não existe, cria o banco
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
        } catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Cria todas as tabelas do banco de dados baseadas nas classes do modelo
     */
    public static void criarTabelas(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        
        System.out.println("Iniciando criação de tabelas...");
        
        // Criação da tabela Usuario
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS usuarios (" +
                "id SERIAL PRIMARY KEY, " +
                "nome VARCHAR(255) NOT NULL, " +
                "email VARCHAR(255) NOT NULL UNIQUE, " +
                "senha VARCHAR(255) NOT NULL, " +
                "telefone VARCHAR(50), " +
                "tipo VARCHAR(20) NOT NULL" +
                ")");
        System.out.println("Tabela usuarios criada com sucesso");
        
        // Tabela específica para Admin
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS admins (" +
                "id SERIAL PRIMARY KEY, " +
                "usuario_id INTEGER NOT NULL REFERENCES usuarios(id), " +
                "cargo VARCHAR(100)" +
                ")");
        System.out.println("Tabela admins criada com sucesso");
        
        // Tabela específica para Organizador
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS organizadores (" +
                "id SERIAL PRIMARY KEY, " +
                "usuario_id INTEGER NOT NULL REFERENCES usuarios(id)" +
                ")");
        System.out.println("Tabela organizadores criada com sucesso");
        
        // Tabela específica para Participante
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS participantes (" +
                "id SERIAL PRIMARY KEY, " +
                "usuario_id INTEGER NOT NULL REFERENCES usuarios(id)" +
                ")");
        System.out.println("Tabela participantes criada com sucesso");
        
        // Criação da tabela Departamento (para armazenar os valores do enum)
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS departamentos (" +
                "id SERIAL PRIMARY KEY, " +
                "nome VARCHAR(50) NOT NULL UNIQUE" +
                ")");
        System.out.println("Tabela departamentos criada com sucesso");
        
        // Inserção dos valores do enum Departamento
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
        
        // Criação da tabela Categoria (para armazenar os valores do enum)
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS categorias (" +
                "id SERIAL PRIMARY KEY, " +
                "nome VARCHAR(50) NOT NULL UNIQUE, " +
                "display_name VARCHAR(50) NOT NULL" +
                ")");
        System.out.println("Tabela categorias criada com sucesso");
        
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
        
        // Criação da tabela Evento
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS eventos (" +
                "id SERIAL PRIMARY KEY, " +
                "nome VARCHAR(255) NOT NULL UNIQUE, " +
                "data_inicio DATE NOT NULL, " +
                "data_fim DATE NOT NULL, " +
                "descricao TEXT, " +
                "organizador_id INTEGER REFERENCES organizadores(id), " +
                "departamento_id INTEGER REFERENCES departamentos(id), " +
                "categoria_id INTEGER REFERENCES categorias(id), " +
                "imagem VARCHAR(255), " +
                "link VARCHAR(255), " +
                "status VARCHAR(50) NOT NULL, " +
                "mensagem_rejeicao TEXT" +
                ")");
        System.out.println("Tabela eventos criada com sucesso");
        
        // Criação da tabela Acao
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS acoes (" +
                "id SERIAL PRIMARY KEY, " +
                "evento_id INTEGER REFERENCES eventos(id), " +
                "nome VARCHAR(255) NOT NULL UNIQUE, " + // Adicionado UNIQUE
                "data DATE NOT NULL, " +
                "descricao TEXT, " +
                "local VARCHAR(255), " +
                "horario_inicio TIME NOT NULL, " +
                "horario_fim TIME NOT NULL, " +
                "departamento_id INTEGER REFERENCES departamentos(id), " +
                "contato VARCHAR(255), " +
                "modalidade VARCHAR(50) NOT NULL, " +
                "imagem VARCHAR(255), " +
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
        
        // Criação da tabela PresencaConfirmada
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS presencas_confirmadas (" +
                "id SERIAL PRIMARY KEY, " +
                "usuario_id INTEGER REFERENCES usuarios(id), " +
                "acao_id INTEGER REFERENCES acoes(id), " +
                "data_confirmacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")");
        System.out.println("Tabela presencas_confirmadas criada com sucesso");
    }
}

// compilar: javac -cp "lib/postgresql-42.7.3.jar" -d bin src/main/java/com/ufms/eventos/util/ConnectionFactory.java
// cd Sistema && javac -cp "lib/postgresql-42.7.3.jar" -d bin src/main/java/com/ufms/eventos/util/ConnectionFactory.java
// java -cp "bin;lib/postgresql-42.7.3.jar" com.ufms.eventos.util.ConnectionFactory
