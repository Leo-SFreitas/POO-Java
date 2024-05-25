package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionBD {
    private Connection connection;

    public ConnectionBD() {
        connectToDatabase();
    }

    private void connectToDatabase() {
        try {
            // Carregar o driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Conexão com o banco de dados
            String url = "jdbc:mysql://localhost:3306/manicure";
            String user = "root";
            String password = "";
            connection = DriverManager.getConnection(url, user, password);

        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC não encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }

    private void createTables() {
        String createClientesTable = "CREATE TABLE IF NOT EXISTS CLIENTES ("
                + "ID_CLIENTE INT PRIMARY KEY AUTO_INCREMENT, "
                + "NOME VARCHAR(80) NOT NULL, "
                + "TELEFONE CHAR(11) UNIQUE NOT NULL, "
                + "EMAIL VARCHAR(50) UNIQUE)";

        String createServicosTable = "CREATE TABLE IF NOT EXISTS SERVICOS ("
                + "ID_SERVICO INT PRIMARY KEY AUTO_INCREMENT, "
                + "TIPO_SERVICO VARCHAR(30) NOT NULL, "
                + "PRECO DOUBLE NOT NULL)";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createClientesTable);
            statement.executeUpdate(createServicosTable);
            System.out.println("Tabelas criadas com sucesso.");
        } catch (SQLException e) {
            System.err.println("Erro ao criar tabelas: " + e.getMessage());
        }
    }
}
