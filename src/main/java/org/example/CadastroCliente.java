package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CadastroCliente extends JFrame {
    private JTable table;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField addressField;
    private DefaultTableModel model;

    public CadastroCliente() {
        setTitle("Dashboard");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(30, 60, 90)); // Azul escuro
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Criando o modelo da tabela
        model = new DefaultTableModel(new String[]{"Nome", "Email", "Telefone", "Endereço"}, 0);
        table = new JTable(model);
        JScrollPane pane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        // Painel de formulário
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(30, 60, 90)); // Azul escuro
        formPanel.setBorder(BorderFactory.createTitledBorder("Novo Cliente"));

        // Campos de texto
        addLabelAndField("Nome:", nameField = new JTextField(20), formPanel, gbc, 0);
        addLabelAndField("Email:", emailField = new JTextField(20), formPanel, gbc, 1);
        addLabelAndField("Telefone:", phoneField = new JTextField(20), formPanel, gbc, 2);
        addLabelAndField("Endereço:", addressField = new JTextField(20), formPanel, gbc, 3);

        // Definindo a cor do texto para preto
        nameField.setForeground(Color.BLACK);
        emailField.setForeground(Color.BLACK);
        phoneField.setForeground(Color.BLACK);
        addressField.setForeground(Color.BLACK);

        // Botões no formulário
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        JButton addButton = new JButton("Adicionar");
        JButton clearButton = new JButton("Limpar");
        JButton deleteButton = new JButton("Excluir");

        // Ajuste de fonte dos botões
        Font buttonFont = new Font("Arial", Font.PLAIN, 16);
        addButton.setFont(buttonFont);
        clearButton.setFont(buttonFont);
        deleteButton.setFont(buttonFont);

        buttonPanel.add(addButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(deleteButton);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // Adicionando os painéis ao JFrame
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1; gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.3; // Peso do painel de formulário
        gbc.weighty = 1;
        add(formPanel, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 3; gbc.gridheight = 1;
        gbc.weightx = 0.7; // Peso do painel da tabela
        gbc.weighty = 0.9;
        gbc.fill = GridBagConstraints.BOTH;
        add(pane, gbc);

        // Ações dos botões
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateFields()) {
                    String name = nameField.getText();
                    String email = emailField.getText();
                    String phone = phoneField.getText();
                    String address = addressField.getText();
                    model.addRow(new Object[]{name, email, phone, address});
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(null, "Todos os campos devem ser preenchidos");
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    model.removeRow(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(null, "Selecione uma linha para excluir");
                }
            }
        });

        // Definindo a cor do texto dos rótulos para preto
        for (Component component : formPanel.getComponents()) {
            if (component instanceof JLabel) {
                ((JLabel) component).setForeground(Color.BLACK);
            }
        }

        // Barra de menu
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Arquivo");
        JMenuItem exitMenuItem = new JMenuItem("Sair");
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    private void addLabelAndField(String labelText, JTextField textField, JPanel panel, GridBagConstraints gbc, int yPos) {
        gbc.gridx = 0; gbc.gridy = yPos;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1; gbc.gridy = yPos;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(textField, gbc);
    }

    private boolean validateFields() {
        return !nameField.getText().isEmpty() &&
               !emailField.getText().isEmpty() &&
               !phoneField.getText().isEmpty() &&
               !addressField.getText().isEmpty();
    }

    private void clearFields() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CadastroCliente().setVisible(true);
            }
        });
    }
}
