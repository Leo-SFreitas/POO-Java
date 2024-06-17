package org.example;

import javax.swing.*;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DatabaseManager databaseManager = new DatabaseManager();
            databaseManager.initializeDatabase();
            Connection connection = databaseManager.getConnection();
            if (connection != null) {
                new TelaInicial(connection).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Erro ao conectar ao banco de dados.");
            }
        });
    }
}