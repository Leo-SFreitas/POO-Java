package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class GanhosMensais extends JFrame {

    private Connection connection;
    private DefaultTableModel tableModel;
    private Timer timer;
    private final int[] mesAtual = new int[1];
    private final int[] anoAtual = new int[1];

    public GanhosMensais(Connection connection) {
        this.connection = connection;

        setTitle("Ganhos Mensais");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        carregarGanhosMensais();

        // Iniciar o timer para atualizar os ganhos mensais a cada mês
        iniciarTimer();
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
            // Obter o total de receita agrupado por mês e ano
            String sql = "SELECT strftime('%m', data_atendimento) AS mes, strftime('%Y', data_atendimento) AS ano, SUM(preco_servico) AS total " +
                    "FROM atendimentos " +
                    "GROUP BY strftime('%m', data_atendimento), strftime('%Y', data_atendimento)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Obter o valor do mês formatado
                String mes = rs.getString("mes");
                String nomeMes = "";
                if (mes != null) {
                    nomeMes = LocalDate.of(2000, Integer.parseInt(mes), 1).getMonth().name();
                }

                // Obter o valor do ano
                String ano = rs.getString("ano");

                // Obter o valor do total
                double total = rs.getDouble("total");

                // Adicionar a linha à tabela
                tableModel.addRow(new Object[]{nomeMes, ano, total});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar os ganhos mensais.");
        }
    }

    private void iniciarTimer() {
        // Obter a data atual do sistema
        Calendar cal = Calendar.getInstance();
        mesAtual[0] = cal.get(Calendar.MONTH);
        anoAtual[0] = cal.get(Calendar.YEAR);

        // Programar o timer para executar a tarefa a cada mês
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Verificar se o mês mudou
                Calendar calendarioAtualizado = Calendar.getInstance();
                int novoMes = calendarioAtualizado.get(Calendar.MONTH);
                if (novoMes != mesAtual[0]) {
                    // Limpar a tabela e recarregar os ganhos mensais
                    tableModel.setRowCount(0);
                    carregarGanhosMensais();
                    mesAtual[0] = novoMes;
                }
                // Verificar se o ano mudou
                int novoAno = calendarioAtualizado.get(Calendar.YEAR);
                if (novoAno != anoAtual[0]) {
                    // Atualizar o ano na tabela
                    anoAtual[0] = novoAno;
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        tableModel.setValueAt(anoAtual[0], i, 1);
                    }
                }
            }
        }, 0, 1000 * 60 * 60 * 24); // Executar a cada dia para verificar a mudança de mês
    }

    // Método para parar o timer quando a janela for fechada
    @Override
    public void dispose() {
        super.dispose();
        timer.cancel();
    }
}
