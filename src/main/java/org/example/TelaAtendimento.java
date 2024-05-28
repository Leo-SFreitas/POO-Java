package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

public class TelaAtendimento extends JFrame {
    private JTextArea saidaTextArea;
    private JButton abrirCadastroButton;
    private Connection connection;

    public TelaAtendimento(Connection connection) {
        super("Atendimentos");
        this.connection = connection; // Adiciona a conexão ao banco de dados
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);

        // Criando área de texto para SAIDA das entradas ao cadastrar
        saidaTextArea = new JTextArea();
        saidaTextArea.setEditable(false);
        JScrollPane scrollPainel = new JScrollPane(saidaTextArea);
        add(scrollPainel, BorderLayout.CENTER);

        // Configurando botão para abrir pop-up de cadastro de atendimento
        abrirCadastroButton = new JButton("Cadastrar atendimento");
        abrirCadastroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirCadastroAtendimento();
            }
        });
        add(abrirCadastroButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    // função para abrir tela de cadastro de atendimento
    private void abrirCadastroAtendimento() {
        // Cria e exibe a pop-up de cadastro
        CadastroAtendimento cadastro = new CadastroAtendimento(connection);
        cadastro.setVisible(true);
    }

    //RETIRAR ESSE MAIN
    // Método principal
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                DatabaseManager databaseManager = new DatabaseManager();
                databaseManager.initializeDatabase();
                Connection connection = databaseManager.getConnection();
                if (connection != null) {
                    new TelaAtendimento(connection).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Erro ao conectar ao banco de dados.");
                }
            }
        });
    }
}