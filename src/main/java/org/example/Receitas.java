package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.awt.event.*;
import java.io.*;

public class Receitas extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private Connection connection;

    public Receitas(Connection connection) {
        super("Receitas");
        this.connection = connection;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 400);
        setLayout(new BorderLayout());

        // Centralizar a janela
        setLocationRelativeTo(null);

        // Adicionar NavBar
        JMenuBar menuBar = NavBar.createMenuBar(connection, this);
        setJMenuBar(menuBar);

        // Tabela de ganhos mensais
        model = new DefaultTableModel();
        model.addColumn("Mes");
        model.addColumn("Ano");
        model.addColumn("Total de Atendimentos");
        model.addColumn("Total de Ganhos");

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadMonthlyEarnings();

        // Adicionar botão para exportar para CSV
        JButton exportButton = new JButton("Exportar para planilha");
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportToCSV();
            }
        });
        add(exportButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Método para carregar os ganhos mensais do banco de dados
    private void loadMonthlyEarnings() {
        model.setRowCount(0); // Limpar a tabela antes de carregar novos dados

        try {
            // Ajustando a consulta para agrupar por mês e ano, somar os ganhos e contar atendimentos
            String query = "SELECT mes_atendimento AS mes, ano_atendimento AS ano, " +
                    "SUM(preco_servico) AS total_ganhos, COUNT(*) AS total_atendimentos " +
                    "FROM atendimentos " +
                    "GROUP BY ano_atendimento, mes_atendimento";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int month = resultSet.getInt("mes");
                int year = resultSet.getInt("ano");
                int totalAtendimentos = resultSet.getInt("total_atendimentos");
                double totalEarnings = resultSet.getDouble("total_ganhos");

                String monthName = getMonthName(month);
                String formattedEarnings = formatCurrency(totalEarnings);

                model.addRow(new Object[]{monthName, year, totalAtendimentos, formattedEarnings});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método auxiliar para exportar para CSV
    private void exportToCSV() {
        try {
            FileWriter fw = new FileWriter("receitas.csv", StandardCharsets.UTF_8);
            BufferedWriter bw = new BufferedWriter(fw);

            // Escrever cabeçalho
            for (int i = 0; i < table.getColumnCount(); i++) {
                bw.write(table.getColumnName(i));
                if (i < table.getColumnCount() - 1) {
                    bw.write(",");
                }
            }
            bw.newLine();

            // Escrever dados
            for (int i = 0; i < table.getRowCount(); i++) {
                for (int j = 0; j < table.getColumnCount(); j++) {
                    bw.write(String.valueOf(table.getValueAt(i, j)));
                    if (j < table.getColumnCount() - 1) {
                        bw.write(",");
                    }
                }
                bw.newLine();
            }

            bw.close();
            fw.close();

            JOptionPane.showMessageDialog(this, "Dados exportados para receitas.csv com sucesso!");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao exportar para CSV.");
        }
    }

    // Método auxiliar para obter o nome do mês por extenso
    private String getMonthName(int month) {
        String[] months = new DateFormatSymbols(new Locale("pt", "BR")).getMonths();
        if (month >= 1 && month <= 12) {
            return months[month - 1];
        }
        return "Desconhecido";  // Retorno padrão para valores de mês inválidos
    }

    // Método auxiliar para formatar o valor monetário
    private String formatCurrency(double value) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return currencyFormat.format(value);
    }
}
