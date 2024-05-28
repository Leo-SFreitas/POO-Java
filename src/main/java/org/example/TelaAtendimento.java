package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

        // Carregar os dados do banco de dados ao iniciar a tela
        carregarDadosAtendimentos();

        setVisible(true);
    }

    // função para abrir tela de cadastro de atendimento
    private void abrirCadastroAtendimento() {
        // Cria e exibe a pop-up de cadastro
        CadastroAtendimento cadastro = new CadastroAtendimento(connection);
        cadastro.setVisible(true);
    }

    // Método para carregar os dados dos atendimentos do banco de dados
    private void carregarDadosAtendimentos() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM atendimentos");

            StringBuilder stringBuilder = new StringBuilder();
            while (resultSet.next()) {
                stringBuilder.append("Nome: ").append(resultSet.getString("nome")).append("\n");
                stringBuilder.append("Local: ").append(resultSet.getString("local_atendimento")).append("\n");
                stringBuilder.append("Data: ").append(resultSet.getString("data_atendimento")).append("\n");
                stringBuilder.append("Horário: ").append(resultSet.getString("horario")).append("\n");
                stringBuilder.append("Serviço: ").append(resultSet.getString("servico")).append("\n");
                stringBuilder.append("Preço: ").append(resultSet.getString("preco_servico")).append("\n\n");
            }

            saidaTextArea.setText(stringBuilder.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados dos atendimentos.");
        }
    }
}
