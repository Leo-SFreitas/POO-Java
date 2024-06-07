package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GanhosMensais extends JFrame {

    private Connection connection;
    private DefaultTableModel tableModel;

    public GanhosMensais(Connection connection) {
        this.connection = connection;

        setTitle("Ganhos Mensais");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        carregarGanhosMensais();

        JMenuBar menuBar = NavBar.createMenuBar(connection);
        setJMenuBar(menuBar);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout());
        getContentPane().add(panel);

        tableModel = new DefaultTableModel(new Object[]{"Mês", "Ano", "Total"}, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
    }

    private void carregarGanhosMensais() {
        try {
            String sql = "SELECT strftime('%m', data_atendimento) AS mes, strftime('%Y', data_atendimento) AS ano, SUM(preco_servico) AS total " +
                    "FROM atendimentos " +
                    "GROUP BY strftime('%m', data_atendimento), strftime('%Y', data_atendimento)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String mes = rs.getString("mes");
                String ano = rs.getString("ano");
                double total = rs.getDouble("total");

                // Converte o mês para número de 1 a 12
                int mesNumerico = mes != null && !mes.isEmpty() ? Integer.parseInt(mes) : 0;

                tableModel.addRow(new Object[]{mesNumerico, ano, total});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar os ganhos mensais.");
        }
    }
}
