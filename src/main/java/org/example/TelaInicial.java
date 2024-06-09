package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

public class TelaInicial extends JFrame {

    public TelaInicial(Connection connection) {
        // Cria a janela principal
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        // Define as constraints para o GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        setTitle("Tela Inicial");

        // Cria o primeiro texto
        JLabel labelTitulo = new JLabel("Olá, Isabel", SwingConstants.CENTER);
        labelTitulo.setFont(new Font("Serif", Font.BOLD, 16));
        add(labelTitulo, gbc);

        // Cria o segundo texto
        JLabel labelSubTitulo = new JLabel("O que você precisa fazer agora?", SwingConstants.CENTER);
        labelSubTitulo.setFont(new Font("Serif", Font.PLAIN, 14));
        add(labelSubTitulo, gbc);

        // Cria um painel para os botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Cria os botões com os novos textos e aplica a fonte Arial
        JButton Atendimento = new JButton("Agendar Atendimento");
        Atendimento.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton cadastroCliente = new JButton("Cadastrar Novo Cliente");
        cadastroCliente.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton Receita = new JButton("Receitas");
        Receita.setFont(new Font("Arial", Font.PLAIN, 14));

        // Adiciona os botões ao painel
        buttonPanel.add(Atendimento);
        buttonPanel.add(cadastroCliente);
        buttonPanel.add(Receita);

        // Adicionar ActionListener para "Agenda Atendimentos"
        Atendimento.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TelaAtendimento telaAtendimentoScreen = new TelaAtendimento(connection);
                telaAtendimentoScreen.setVisible(true);
                dispose(); // Fecha a janela atual
            }
        });

        // Adicionar ActionListener para "Cadastro Cliente"
        cadastroCliente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CadastroCliente cadastroClienteScreen = new CadastroCliente(connection);
                cadastroClienteScreen.setVisible(true);
                dispose(); // Fecha a janela atual
            }
        });

        // Adicionar ActionListener para "Receitas"
        Receita.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Receitas(connection).setVisible(true);
                dispose(); // Fecha a janela atual
            }
        });

        // Define o tamanho preferido do painel de botões
        buttonPanel.setPreferredSize(new Dimension(800, 200));

        // Adiciona o painel com os botões à janela
        add(buttonPanel, gbc);

        // Ajusta o tamanho da janela para o tamanho preferido dos componentes
        pack();

        // Define o tamanho da janela para 800x400
        setSize(800, 400);

        // Centraliza a janela na tela
        setLocationRelativeTo(null);

        // Torna a janela visível
        setVisible(true);
    }
}
