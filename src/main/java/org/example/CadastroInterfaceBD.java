package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CadastroInterfaceBD extends JFrame {
    private JTextField nomeField, telefoneField, localField;
    private JButton cadastrarButton;
    private Connection connection;
    private Statement statement;

    public CadastroInterfaceBD() {
        setTitle("Cadastro de Atendimento");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Centralizar a janela
        setLocationRelativeTo(null);

        // Inicializar os componentes
        initComponents();

        // Conectar ao banco de dados
        connectToDatabase();
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


    private void connectToDatabase() {
        try {
            // Carregar o driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Conexão com o banco de dados
            String url = "jdbc:mysql://localhost:3306/manicure";
            String user = "root";
            String password = "";
            connection = DriverManager.getConnection(url, user, password);

            // Criar uma instrução SQL
            statement = connection.createStatement();
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC não encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }


    private void cadastrarAtendimento() {
        String nome = nomeField.getText();
        String telefone = telefoneField.getText();
        String local = localField.getText();

        // Verificar se o número de telefone tem 11 dígitos, se não, limpa campo telefone para inserir número correto
        if (telefone.length() != 11) {
            JOptionPane.showMessageDialog(this, "O número de telefone deve ter no máximo 11 dígitos.");
            telefoneField.setText(""); // Limpar o campo de telefone

            return; // Sair do método se o número de telefone não tiver 11 dígitos
        }

        try {
            // Inserir os dados na tabela do banco de dados
            String sql = "INSERT INTO atendimentos (nome, telefone, local) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, nome);
            preparedStatement.setString(2, telefone);
            preparedStatement.setString(3, local);

            preparedStatement.executeUpdate();

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CadastroInterfaceBD().setVisible(true);
            }
        });
    }
}