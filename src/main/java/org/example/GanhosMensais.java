package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GanhosMensais extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private Connection connection;

    public GanhosMensais(Connection connection) {
        super("Ganhos Mensais");
        this.connection = connection;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 400);
        setLayout(new BorderLayout());

        // Centralizar a janela
        setLocationRelativeTo(null);

        // Adionando NavBar
        JMenuBar menuBar = NavBar.createMenuBar(connection, this);
        setJMenuBar(menuBar);

        // Tabela de ganhos mensais
        model = new DefaultTableModel();
        model.addColumn("Mês");
        model.addColumn("Ano");
        model.addColumn("Total de Ganhos");

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadMonthlyEarnings();

        setVisible(true);
    }

    // Método para carregar os ganhos mensais do banco de dados
    private void loadMonthlyEarnings() {
        model.setRowCount(0); // Limpar a tabela antes de carregar novos dados

        try {
            String query = "SELECT MONTH(data_atendimento) AS mes, YEAR(data_atendimento) AS ano, SUM(preco_servico) AS total_ganhos FROM atendimentos GROUP BY YEAR(data_atendimento), MONTH(data_atendimento)";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int month = resultSet.getInt("mes");
                int year = resultSet.getInt("ano");
                double totalEarnings = resultSet.getDouble("total_ganhos");

                model.addRow(new Object[]{month, year, totalEarnings});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
