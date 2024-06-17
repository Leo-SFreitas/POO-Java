package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

public class NavBar {

    public static JMenuBar createMenuBar(Connection connection, JFrame currentFrame) {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Opções do Aplicativo");

        JMenuItem atendimentosInterface = new JMenuItem("Agenda Atendimentos");
        JMenuItem cadastroClienteInterface = new JMenuItem("Cadastro Clientes");
        JMenuItem receitasInterface = new JMenuItem("Receitas");
        JMenuItem pesquisaInterface = new JMenuItem("Pesquisa Aprofundada");
        JMenuItem exitMenuItem = new JMenuItem("Sair");

        fileMenu.add(atendimentosInterface);
        fileMenu.add(cadastroClienteInterface);
        fileMenu.add(receitasInterface);
        fileMenu.add(pesquisaInterface);
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);

        // Adicionar ActionListener para "Agenda Atendimentos"
        atendimentosInterface.addActionListener(e -> {
            currentFrame.dispose();
            new TelaAtendimento(connection).setVisible(true);
        });

        // Adicionar ActionListener para "Cadastro Cliente"
        cadastroClienteInterface.addActionListener(e -> {
            currentFrame.dispose();
            new CadastroCliente(connection).setVisible(true);
        });

        // Adicionar ActionListener para "Receitas"
        receitasInterface.addActionListener(e -> {
            currentFrame.dispose();
            new Receitas(connection).setVisible(true);
        });
        //Adicionar ActionListener para "Pesquisa"
        pesquisaInterface.addActionListener(e -> {
            currentFrame.dispose();
            new Pesquisa(connection).setVisible(true);
        });

        // Adicionar ActionListener para "Sair"
        exitMenuItem.addActionListener(e -> System.exit(0));

        return menuBar;
    }
}