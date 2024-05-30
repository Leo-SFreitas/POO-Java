package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class TelaAtendimento extends JFrame {
    private JTable atendimentosTable;
    private JButton abrirCadastroButton, deletarAtendimentoButton;
    private Connection connection;
    private DefaultTableModel tableModel;

    public TelaAtendimento(Connection connection) {
        super("Atendimentos");
        this.connection = connection;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 400);

        // Configurando a tabela de atendimentos
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nome", "Local", "Data", "Horário", "Serviço", "Preço"}, 0);
        atendimentosTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(atendimentosTable);
        add(scrollPane, BorderLayout.CENTER);

        // Painel para os botões
        JPanel buttonPanel = new JPanel();
        abrirCadastroButton = new JButton("Cadastrar atendimento");
        deletarAtendimentoButton = new JButton("Deletar atendimento");

        buttonPanel.add(abrirCadastroButton);
        buttonPanel.add(deletarAtendimentoButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Ação do botão para abrir pop-up de cadastro de atendimento
        abrirCadastroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirCadastroAtendimento();
            }
        });

        // Ação do botão para deletar o atendimento selecionado
        deletarAtendimentoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deletarAtendimento();
            }
        });

        // Carregar os dados do banco de dados ao iniciar a tela
        carregarDadosAtendimentos();

        setVisible(true);
    }

    // Função para abrir tela de cadastro de atendimento
    private void abrirCadastroAtendimento() {
        CadastroAtendimento cadastro = new CadastroAtendimento(connection, new Runnable() {
            @Override
            public void run() {
                carregarDadosAtendimentos();  // Atualiza a tabela após cadastro
            }
        });
        cadastro.setVisible(true);
    }

    // Método para carregar os dados dos atendimentos do banco de dados
    private void carregarDadosAtendimentos() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM atendimentos");

            tableModel.setRowCount(0); // Limpar a tabela antes de carregar novos dados

            while (resultSet.next()) {
                tableModel.addRow(new Object[]{
                        resultSet.getInt("id"),
                        resultSet.getString("cliente"),
                        resultSet.getString("local_atendimento"),
                        resultSet.getString("data_atendimento"),
                        resultSet.getString("horario"),
                        resultSet.getString("servico"),
                        resultSet.getDouble("preco_servico")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados dos atendimentos.");
        }
    }

    // Método para deletar o atendimento selecionado
    private void deletarAtendimento() {
        int selectedRow = atendimentosTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um atendimento para deletar.");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);

        try {
            String deleteSQL = "DELETE FROM atendimentos WHERE id = ?";
            PreparedStatement deleteStatement = connection.prepareStatement(deleteSQL);
            deleteStatement.setInt(1, id); // Substitui o ? pelo valor do ID

            int rowsAffected = deleteStatement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Atendimento deletado com sucesso.");
                carregarDadosAtendimentos();  // Atualiza a tabela após deleção
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao deletar atendimento.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao deletar atendimento.");
        }
    }
}
