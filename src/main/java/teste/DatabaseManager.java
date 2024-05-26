package teste;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:Manicure_dois.db"; // Nome do arquivo do banco de dados

    // Método para conectar ao banco de dados
    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL); // Esta linha cria o banco de dados se ele não existir
            System.out.println("Conexão com SQLite estabelecida.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    // Método para inicializar o banco de dados e criar as tabelas
    public static void initializeDatabase() {
        String clientesTable = "CREATE TABLE IF NOT EXISTS clientes ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nome TEXT NOT NULL,"
                + "telefone TEXT NOT NULL,"
                + "endereco TEXT NOT NULL,"
                + "data_aniversario TEXT NOT NULL"
                + ");";

        String atendimentosTable = "CREATE TABLE IF NOT EXISTS atendimentos ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "data_atendimento TEXT NOT NULL,"
                + "horario TEXT NOT NULL,"
                + "servico TEXT NOT NULL,"
                + "local_atendimento TEXT NOT NULL,"
                + "preco_servico TEXT NOT NULL"
                + ");";

        try (Connection conn = connect(); // Conecta (e cria) o banco de dados Manicure.db
             Statement stmt = conn.createStatement()) {
            stmt.execute(clientesTable); // Cria a tabela clientes se não existir
            stmt.execute(atendimentosTable); // Cria a tabela atendimentos se não existir
            System.out.println("Tabelas criadas com sucesso.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
