package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;

public class Pesquisa extends JFrame {
    private final JComboBox<String> clienteComboBox;
    private final JTextField nomeField;
    private final JTextField aniversarioField;
    private final JTextField telefoneField;
    private final JTextField enderecoField;
    private final DefaultTableModel atendimentosModel;
    private final Connection connection;

    public Pesquisa(Connection connection) {
        this.connection = connection;

        // Adionando NavBar
        JMenuBar menuBar = NavBar.createMenuBar(connection, this);
        setJMenuBar(menuBar);

        setTitle("Pesquisa Aprofundada");
        setSize(800, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Centralizar a janela
        setLocationRelativeTo(null);

        // Painel superior com a seleção do cliente
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        topPanel.add(new JLabel("Cliente:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        clienteComboBox = new JComboBox<>();
        loadClientsIntoComboBox();
        topPanel.add(clienteComboBox, gbc);

        JButton buscarButton = new JButton("Buscar");
        gbc.gridx = 2;
        gbc.gridy = 0;
        topPanel.add(buscarButton, gbc);

        add(topPanel, BorderLayout.LINE_START);

        // Painel central com as informações do cliente e tabela de atendimentos
        JPanel centerPanel = new JPanel(new GridBagLayout());

        gbc.anchor = GridBagConstraints.WEST; // Alinhamento à esquerda
        gbc.insets = new Insets(5, 5, 5,5); // Margens entre os componentes

        // Rótulos
        JLabel nomeLabel = new JLabel("Nome:");
        JLabel aniversarioLabel = new JLabel("Data de Aniversário:");
        JLabel telefoneLabel = new JLabel("Telefone:");
        JLabel enderecoLabel = new JLabel("Endereço:");

        // Campos de texto para exibir informações do cliente
        nomeField = new JTextField(20);
        nomeField.setEditable(false); // Tornar não editável

        aniversarioField = new JTextField(20);
        aniversarioField.setEditable(false); // Tornar não editável

        telefoneField = new JTextField(20);
        telefoneField.setEditable(false); // Tornar não editável

        enderecoField = new JTextField(20);
        enderecoField.setEditable(false); // Tornar não editável


        // Adicionando os componentes ao painel
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(nomeLabel, gbc);
        gbc.gridx = 1;
        centerPanel.add(nomeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        centerPanel.add(aniversarioLabel, gbc);
        gbc.gridx = 1;
        centerPanel.add(aniversarioField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        centerPanel.add(telefoneLabel, gbc);
        gbc.gridx = 1;
        centerPanel.add(telefoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        centerPanel.add(enderecoLabel, gbc);
        gbc.gridx = 1;
        centerPanel.add(enderecoField, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // Tabela de atendimentos
        atendimentosModel = new DefaultTableModel(new String[]{"Data", "Horário", "Serviço", "Local", "Preço"}, 0);
        JTable atendimentosTable = new JTable(atendimentosModel);
        JScrollPane scrollPane = new JScrollPane(atendimentosTable);
        add(scrollPane, BorderLayout.SOUTH);

        buscarButton.addActionListener(e -> {
            String clienteSelecionado = (String) clienteComboBox.getSelectedItem();
            if (clienteSelecionado != null) {
                buscarCliente(clienteSelecionado);
                buscarAtendimentos(clienteSelecionado);
            }
        });
    }

    private void loadClientsIntoComboBox() {
        try {
            String sql = "SELECT nome FROM clientes ORDER BY nome";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String nomeCliente = rs.getString("nome");
                clienteComboBox.addItem(nomeCliente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes do banco de dados.");
        }
    }

    private void buscarCliente(String nomeCliente) {
        try {
            String sql = "SELECT nome, data_aniversario, telefone, endereco FROM clientes WHERE nome = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, nomeCliente);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nomeField.setText(rs.getString("nome"));
                aniversarioField.setText(rs.getString("data_aniversario"));
                telefoneField.setText(rs.getString("telefone"));
                enderecoField.setText(rs.getString("endereco"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao buscar informações do cliente.");
        }
    }

    private void buscarAtendimentos(String nomeCliente) {
        try {
            atendimentosModel.setRowCount(0); // Limpar os dados atuais da tabela
            String sql = "SELECT dia_atendimento, mes_atendimento, ano_atendimento, horario, servico, local_atendimento, preco_servico " +
                    "FROM atendimentos " +
                    "JOIN clientes ON atendimentos.id_cliente = clientes.id " +
                    "WHERE clientes.nome = ?"+
                    "ORDER BY ano_atendimento, mes_atendimento, dia_atendimento";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, nomeCliente);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String data = rs.getInt("dia_atendimento") + "/" + rs.getInt("mes_atendimento") + "/" + rs.getInt("ano_atendimento");
                String horario = rs.getString("horario");
                String servico = rs.getString("servico");
                String local = rs.getString("local_atendimento");
                String preco = rs.getString("preco_servico");

                String formattedPreco = formatCurrency(Double.parseDouble(preco));

                atendimentosModel.addRow(new Object[]{data, horario, servico, local, formattedPreco});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao buscar atendimentos do cliente.");
        }
    }
    // Em ambas as classes Pesquisa e TelaAtendimento

    private String formatCurrency(double value) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return currencyFormat.format(value);
    }

}
