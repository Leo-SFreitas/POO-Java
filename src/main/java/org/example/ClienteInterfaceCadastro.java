package org.example;

import javax.swing.*;
import java.awt.*;

public class ClienteInterfaceCadastro extends JFrame {
    private JTextField clienteNomeField, clienteTelefoneField, clienteEnderecoField;
    private JTextArea outputArea;

    public ClienteInterfaceCadastro(){
        setTitle("Cadastro Cliente");
        setSize(600,400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Configurando painéis de entrada e saida
        JPanel inputpanel = new JPanel();
        inputpanel.setLayout(new BoxLayout(inputpanel, BoxLayout.Y_AXIS));

        //configurando a area de saida
        outputArea = new JTextArea(15,40);
        outputArea.setEditable(false);
        JPanel outputPanel = new JPanel();
        outputPanel.add(new JLabel("Clientes cadastrados"));
        outputPanel.add(new JScrollPane(outputArea));


        //Pop up input cadastro cliente
        JPanel clientePanel = new JPanel(new GridLayout(3,2)); //Janela de pop up cliente
        clienteNomeField = new JTextField();
        clienteTelefoneField = new JTextField();
        clienteEnderecoField = new JTextField();
        //configurando a exibição dos componentes dentro da janela do pop up
        clientePanel.add(new JLabel("Nome: "));
        clientePanel.add(clienteNomeField);
        clientePanel.add(new JLabel("Telefone: "));
        clientePanel.add(clienteTelefoneField);
        clientePanel.add(new JLabel("Endereço: "));
        clientePanel.add(clienteEnderecoField);
        inputpanel.add(clientePanel);

        //botao de cadastrar
        JButton cadastrarCliente = new JButton("Cadastrar");
        JButton editarCliente = new JButton("Editar");
        JButton deletarCliente = new JButton("Deletar");
        //Deve ter alguma forma melhor pra exibir
        inputpanel.add(cadastrarCliente);
        inputpanel.add(editarCliente);
        inputpanel.add(deletarCliente);

        //EXIBINDO ELEMENTOS NA TELA
        add(outputPanel, BorderLayout.CENTER);
        add(inputpanel, BorderLayout.SOUTH);
        setVisible(true);
    }


    public static void main(String[] args){

        new ClienteInterfaceCadastro();
    }

}
