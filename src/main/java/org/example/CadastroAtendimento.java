package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CadastroAtendimento extends JFrame {
    private JTextField nomeField, localField, dataAtendimentoField, horarioField, servicoField, precoServicoField;
    private JButton cadastrarButton;
    private Connection connection;

    public CadastroAtendimento(Connection connection) {
        this.connection = connection;

        setTitle("Cadastro de Atendimento");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Muda para DISPOSE_ON_CLOSE

        // Centralizar a janela
        setLocationRelativeTo(null);

        // Inicializar os componentes
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Adicionar campos de entrada
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        nomeField = new JTextField(20);
        panel.add(nomeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Local de Atendimento:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        localField = new JTextField(20);
        panel.add(localField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Data de Atendimento:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        dataAtendimentoField = new JTextField(20);
        panel.add(dataAtendimentoField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Horário:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        horarioField = new JTextField(20);
        panel.add(horarioField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Serviço:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        servicoField = new JTextField(20);
        panel.add(servicoField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Preço do Serviço:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 5;
        precoServicoField = new JTextField(20);
        panel.add(precoServicoField, gbc);

        // Adicionar botão de cadastro
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        cadastrarButton = new JButton("Cadastrar");
        panel.add(cadastrarButton, gbc);

        cadastrarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cadastrarAtendimento();
            }
        });

        add(panel);
    }

    private void cadastrarAtendimento() {
        String nome = nomeField.getText();
        String local = localField.getText();
        String dataAtendimento = dataAtendimentoField.getText();
        String horario = horarioField.getText();
        String servico = servicoField.getText();
        String precoServico = precoServicoField.getText();

        try {
            // Inserir os dados na tabela do banco de dados
            String insertSQL = "INSERT INTO atendimentos (nome, data_atendimento, horario, servico, local_atendimento, preco_servico) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertSQL);
            insertStatement.setString(1, nome);
            insertStatement.setString(2, dataAtendimento);
            insertStatement.setString(3, horario);
            insertStatement.setString(4, servico);
            insertStatement.setString(5, local);
            insertStatement.setString(6, precoServico);

            insertStatement.executeUpdate();

            // Limpar os campos após a inserção
            nomeField.setText("");
            localField.setText("");
            dataAtendimentoField.setText("");
            horarioField.setText("");
            servicoField.setText("");
            precoServicoField.setText("");

            JOptionPane.showMessageDialog(this, "Atendimento cadastrado com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar atendimento.");
        }
    }
}
