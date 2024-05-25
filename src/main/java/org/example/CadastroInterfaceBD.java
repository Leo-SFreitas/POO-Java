package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CadastroInterfaceBD extends JFrame {
    private JTextField nomeField, aniversarioField, telefoneField, localField;
    private JButton cadastrarButton;
    private Connection connection;

    public CadastroInterfaceBD(Connection connection) {
        this.connection = connection;

        setTitle("Cadastro de Atendimento");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
        panel.add(new JLabel("Telefone:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        telefoneField = new JTextField(20);
        panel.add(telefoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Local de Atendimento:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        localField = new JTextField(20);
        panel.add(localField, gbc);

        // Adicionar botão de cadastro
        gbc.gridx = 0;
        gbc.gridy = 3;
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
        String telefone = telefoneField.getText();
        String local = localField.getText();

        // Verificar se o número de telefone tem 11 dígitos
        if (telefone.length() != 11) {
            JOptionPane.showMessageDialog(this, "O número de telefone deve ter 11 dígitos.");
            telefoneField.setText(""); // Limpar o campo de telefone
            return; // Sair do método se o número de telefone não tiver 11 dígitos
        }

        try {
            // Criar a tabela se não existir
            String createTableSQL = "CREATE TABLE IF NOT EXISTS clientes (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nome VARCHAR(255) NOT NULL, " +
                    "telefone VARCHAR(11) NOT NULL, " +
                    "local VARCHAR(255) NOT NULL)";
            PreparedStatement createTableStatement = connection.prepareStatement(createTableSQL);
            createTableStatement.executeUpdate();

            // Inserir os dados na tabela do banco de dados
            String insertSQL = "INSERT INTO clientes (nome, telefone, local) VALUES (?, ?, ?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertSQL);
            insertStatement.setString(1, nome);
            insertStatement.setString(2, telefone);
            insertStatement.setString(3, local);

            insertStatement.executeUpdate();

            // Limpar os campos após a inserção
            nomeField.setText("");
            telefoneField.setText("");
            localField.setText("");

            JOptionPane.showMessageDialog(this, "Atendimento cadastrado com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar atendimento.");
        }
    }
}