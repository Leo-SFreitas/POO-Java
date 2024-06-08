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
        JMenuItem exitMenuItem = new JMenuItem("Sair");

        fileMenu.add(atendimentosInterface);
        fileMenu.add(cadastroClienteInterface);
        fileMenu.add(receitasInterface);
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);

        // Adicionar ActionListener para "Agenda Atendimentos"
        atendimentosInterface.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentFrame.dispose();
                new TelaAtendimento(connection).setVisible(true);
            }
        });

        // Adicionar ActionListener para "Cadastro Cliente"
        cadastroClienteInterface.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentFrame.dispose();
                new CadastroCliente(connection).setVisible(true);
            }
        });

        // Adicionar ActionListener para "Receitas"
        receitasInterface.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentFrame.dispose();
                new GanhosMensais(connection).setVisible(true);
            }
        });

        // Adicionar ActionListener para "Sair"
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        return menuBar;
    }
}