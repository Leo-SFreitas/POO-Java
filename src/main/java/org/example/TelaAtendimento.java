package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TelaAtendimento extends JFrame {
    private final JTable atendimentosTable;
    private final Connection connection;
    private final DefaultTableModel tableModel;
    private final java.util.List<Integer> atendimentoIds = new java.util.ArrayList<>();

    public TelaAtendimento(Connection connection) {
        super("Atendimentos");
        this.connection = connection;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 400);

        // Centralizar a janela
        setLocationRelativeTo(null);

        // Configurando a tabela de atendimentos
        tableModel = new DefaultTableModel(new Object[]{"Nome", "Local", "Data", "Horário", "Serviço", "Preço"}, 0);
        atendimentosTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(atendimentosTable);
        add(scrollPane, BorderLayout.CENTER);

        // Painel para os botões
        JPanel buttonPanel = new JPanel();
        JButton abrirCadastroButton = new JButton("Cadastrar atendimento");
        JButton deletarAtendimentoButton = new JButton("Deletar atendimento");
        JButton editarAtendimentoButton = new JButton("Editar atendimento");
        JButton deletarTudoButton = new JButton("Deletar tudo");

        buttonPanel.add(abrirCadastroButton);
        buttonPanel.add(deletarAtendimentoButton);
        buttonPanel.add(editarAtendimentoButton);
        buttonPanel.add(deletarTudoButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Adicionando NavBar
        JMenuBar menuBar = NavBar.createMenuBar(connection, this);
        setJMenuBar(menuBar);

        // Ação do botão para abrir pop-up de cadastro de atendimento
        abrirCadastroButton.addActionListener(e -> abrirCadastroAtendimento());

        // Ação do botão para deletar o atendimento selecionado
        deletarAtendimentoButton.addActionListener(e -> deletarAtendimento());

        // Ação do botão para editar o atendimento selecionado
        editarAtendimentoButton.addActionListener(e -> editarAtendimento());

        // Ação do botão para deletar todos os atendimentos
        deletarTudoButton.addActionListener(e -> deletarTodosAtendimentos());

        // Carregar os dados do banco de dados ao iniciar a tela
        carregarDadosAtendimentos();

        setVisible(true);
    }

    // Função para abrir tela de cadastro de atendimento
    private void abrirCadastroAtendimento() {
        // Atualiza a tabela após cadastro
        CadastroAtendimento cadastro = new CadastroAtendimento(connection, this::carregarDadosAtendimentos);
        cadastro.setVisible(true);
    }

    // Método para carregar os dados dos atendimentos do banco de dados
    private void carregarDadosAtendimentos() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM atendimentos ORDER BY ano_atendimento, mes_atendimento, dia_atendimento, horario");

            tableModel.setRowCount(0); // Limpar a tabela antes de carregar novos dados
            atendimentoIds.clear(); // Limpar a lista de IDs

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int dia = resultSet.getInt("dia_atendimento");
                int mes = resultSet.getInt("mes_atendimento");
                int ano = resultSet.getInt("ano_atendimento");

                String dataAtendimento = String.format("%02d/%02d/%02d", dia, mes, ano % 100); // Formatar a data como dd/mm/aa
                double preco = resultSet.getDouble("preco_servico");
                String formattedPreco = formatCurrency(preco);

                atendimentoIds.add(id); // Adicionar o ID à lista

                tableModel.addRow(new Object[]{
                        resultSet.getString("cliente"),
                        resultSet.getString("local_atendimento"),
                        dataAtendimento,
                        resultSet.getString("horario"),
                        resultSet.getString("servico"),
                        formattedPreco
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

        int id = atendimentoIds.get(selectedRow);

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

        int id = atendimentoIds.get(selectedRow);
        String cliente = (String) tableModel.getValueAt(selectedRow, 0);
        String local = (String) tableModel.getValueAt(selectedRow, 1);
        String data = (String) tableModel.getValueAt(selectedRow, 2);
        String horario = (String) tableModel.getValueAt(selectedRow, 3);
        String servico = (String) tableModel.getValueAt(selectedRow, 4);
        String precoStr = (String) tableModel.getValueAt(selectedRow, 5);

        // Converte a data de dd/MM/yy para yyyy-MM-dd
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        LocalDate localDate = LocalDate.parse(data, formatter);
        String dataFormatada = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Remover o símbolo de moeda e converter para Double
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        double preco;
        try {
            Number number = currencyFormat.parse(precoStr);
            preco = number.doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao converter o preço.");
            return;
        }

        // Atualiza a tabela após edição
        CadastroAtendimento cadastro = new CadastroAtendimento(connection, this::carregarDadosAtendimentos, id, cliente, local, dataFormatada, horario, servico, preco);
        cadastro.setVisible(true);
    }

    // Método para deletar todos os atendimentos
    private void deletarTodosAtendimentos() {
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza de que deseja deletar todos os atendimentos? Caso delete, todo o registro de atendimento será perdido, afetando diretamente os cálculos da Receita e a visualização dos dados da Pesquisa aprofundada", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String deleteAllSQL = "DELETE FROM atendimentos";
                PreparedStatement deleteAllStatement = connection.prepareStatement(deleteAllSQL);

                int rowsAffected = deleteAllStatement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Todos os atendimentos foram deletados com sucesso.");
                    carregarDadosAtendimentos();  // Atualiza a tabela após deleção
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao deletar todos os atendimentos.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao deletar todos os atendimentos.");
            }
        }
    }

    private String formatCurrency(double value) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return currencyFormat.format(value);
    }

}
