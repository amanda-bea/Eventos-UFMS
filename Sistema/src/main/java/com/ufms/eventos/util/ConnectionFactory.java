package com.ufms.eventos.util;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

public class ConnectionFactory {
    public static Connection getConnection() throws Exception{
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            // conexao com DB
            String login = "root"; //mudar aqui quando for usar
            String senha = "SQL123"; //mudar aqui quando for usar
            String url = "jdbc:mysql://localhost:3306/dbeventos"; //colocar  nme do banco
            return DriverManager.getConnection(url, login, senha);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }
    public static void main(String[] args) {
        try {
            Connection conn = ConnectionFactory.getConnection();
            JOptionPane.showMessageDialog(null, "CONECTOUU");
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}

// compilar: javac -cp "lib/mysql-connector-j-9.3.0.jar" -d bin src/main/java/com/ufms/eventos/util/ConnectionFactory.java
// java -cp "bin;lib/mysql-connector-j-9.3.0.jar" com.ufms.eventos.util.ConnectionFactory