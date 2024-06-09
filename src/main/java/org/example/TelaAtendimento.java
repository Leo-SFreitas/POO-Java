package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TelaAtendimento extends JFrame {
    private JTable atendimentosTable;
    private JButton abrirCadastroButton, deletarAtendimentoButton, editarAtendimentoButton;
    private Connection connection;
    private DefaultTableModel tableModel;

    public TelaAtendimento(Connection connection) {
        super("Atendimentos");
        this.connection = connection;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 400);

        // Centralizar a janela
        setLocationRelativeTo(null);

        // Configurando a tabela de atendimentos
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nome", "Local", "Data", "Horário", "Serviço", "Preço"}, 0);
        atendimentosTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(atendimentosTable);
        add(scrollPane, BorderLayout.CENTER);

        // Painel para os botões
        JPanel buttonPanel = new JPanel();
        abrirCadastroButton = new JButton("Cadastrar atendimento");
        deletarAtendimentoButton = new JButton("Deletar atendimento");
        editarAtendimentoButton = new JButton("Editar atendimento");

        buttonPanel.add(abrirCadastroButton);
        buttonPanel.add(deletarAtendimentoButton);
        buttonPanel.add(editarAtendimentoButton);
        add(buttonPanel, BorderLayout.SOUTH);

        //Adionando NavBar
        JMenuBar menuBar = NavBar.createMenuBar(connection, this);
        setJMenuBar(menuBar);

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

        // Ação do botão para editar o atendimento selecionado
        editarAtendimentoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editarAtendimento();
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
                int dia = resultSet.getInt("dia_atendimento");
                int mes = resultSet.getInt("mes_atendimento");
                int ano = resultSet.getInt("ano_atendimento");
                String dataAtendimento = String.format("%02d/%02d/%02d", dia, mes, ano % 100); // Formatar a data como dd/mm/aa

                tableModel.addRow(new Object[]{
                        resultSet.getInt("id"),
                        resultSet.getString("cliente"),
                        resultSet.getString("local_atendimento"),
                        dataAtendimento,
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

    // Método para editar o atendimento selecionado
    private void editarAtendimento() {
        int selectedRow = atendimentosTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um atendimento para editar.");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String cliente = (String) tableModel.getValueAt(selectedRow, 1);
        String local = (String) tableModel.getValueAt(selectedRow, 2);
        String data = (String) tableModel.getValueAt(selectedRow, 3);
        String horario = (String) tableModel.getValueAt(selectedRow, 4);
        String servico = (String) tableModel.getValueAt(selectedRow, 5);
        Double preco = (Double) tableModel.getValueAt(selectedRow, 6);

        // Converte a data de dd/MM/yy para yyyy-MM-dd
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        LocalDate localDate = LocalDate.parse(data, formatter);
        String dataFormatada = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        CadastroAtendimento cadastro = new CadastroAtendimento(connection, new Runnable() {
            @Override
            public void run() {
                carregarDadosAtendimentos();  // Atualiza a tabela após edição
            }
        }, id, cliente, local, dataFormatada, horario, servico, preco);
        cadastro.setVisible(true);
    }
}
