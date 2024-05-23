package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaAtendimento extends JFrame {

    private JTextArea saidaTextArea;
    private JButton abrirCadastroButton;

    public TelaAtendimento() {
        super("Atendimentos");
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
            public void actionPerformed(ActionEvent e) { abrirCadastroAtendimento();}
        });
        add(abrirCadastroButton,BorderLayout.SOUTH);

        setVisible(true);

    }

    // função para abrir tela de cadastro de atendimento
    private void abrirCadastroAtendimento(){
        //cria e exibe pop-up de cadastro
        TelaAtendimento cadastro = new TelaAtendimento();
        cadastro.setVisible(true);
    }

    //MÉTODO PARA ATUALIZAR SAIDA COM DADOS DO CADASTRO - TROCAR POR CALENDÁRIO

    public static void main(String[] args){ new TelaAtendimento(); }
}


