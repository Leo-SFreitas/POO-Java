package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:Manicure.db"; // Nome do arquivo do banco de dados
    private Connection connection;

    // Método para conectar ao banco de dados
    public Connection connect() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL); // Esta linha cria o banco de dados se ele não existir
                System.out.println("Conexão com SQLite estabelecida.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }

    // Método para inicializar o banco de dados e criar as tabelas
    public void initializeDatabase() {
        String clientesTable = "CREATE TABLE IF NOT EXISTS clientes ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nome VARCHAR(80) NOT NULL,"
                + "telefone INTEGER NOT NULL,"
                + "endereco VARCHAR(80) NOT NULL,"
                + "data_aniversario DATE NOT NULL"
                + ");";

        String atendimentosTable = "CREATE TABLE IF NOT EXISTS atendimentos ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "id_cliente INTEGER NOT NULL,"
                + "cliente VARCHAR(80),"
                + "data_atendimento DATE NOT NULL,"
                + "horario TEXT NOT NULL,"
                + "servico VARCHAR(80) NOT NULL,"
                + "local_atendimento VARCHAR(80) NOT NULL,"
                + "preco_servico DOUBLE NOT NULL"
                + ");";

        try (Statement stmt = connect().createStatement()) {
            stmt.execute(clientesTable); // Cria a tabela clientes se não existir
            stmt.execute(atendimentosTable); // Cria a tabela atendimentos se não existir
            System.out.println("Tabelas criadas com sucesso.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Método para retornar a conexão ativa
    public Connection getConnection() {
        return connect();
    }
}