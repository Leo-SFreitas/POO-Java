package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CadastroCliente extends JFrame {
    private JTable table;
    private JTextField nameField;
    private JTextField birthdayField;
    private JTextField phoneField;
    private JTextField addressField;
    private DefaultTableModel model;
    private Connection connection;
    private JButton saveButton;
    private int editingClientId = -1; // Variável para armazenar o ID do cliente que está sendo editado

    public CadastroCliente(Connection connection) {
        this.connection = connection;

        setTitle("Dashboard");
        setSize(1000, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(218, 215, 219)); // Azul escuro
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Criando o modelo da tabela
        model = new DefaultTableModel(new String[]{"ID", "Nome", "Aniversário", "Telefone", "Endereço"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Impede a edição das células
            }
        };
        table = new JTable(model);
        table.removeColumn(table.getColumnModel().getColumn(0)); // Oculta a coluna ID na interface
        JScrollPane pane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        // Painel de formulário
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(218, 215, 219));
        formPanel.setBorder(BorderFactory.createTitledBorder("Novo Cliente"));

        // Campos de texto
        addLabelAndField("Nome:", nameField = new JTextField(20), formPanel, gbc, 0);
        addLabelAndField("Aniversário:", birthdayField = new JTextField(20), formPanel, gbc, 1);
        addLabelAndField("Telefone:", phoneField = new JTextField(20), formPanel, gbc, 2);
        addLabelAndField("Endereço:", addressField = new JTextField(20), formPanel, gbc, 3);

        // Botões no formulário
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 5, 5));

        JButton addButton = new JButton("Adicionar");
        JButton editarButton = new JButton("Editar");
        JButton deleteButton = new JButton("Excluir");
        saveButton = new JButton("Salvar");
        saveButton.setEnabled(false); // Desativa o botão Salvar inicialmente

        // Ajuste de fonte dos botões
        Font buttonFont = new Font("Arial", Font.PLAIN, 16);
        addButton.setFont(buttonFont);
        editarButton.setFont(buttonFont);
        deleteButton.setFont(buttonFont);
        saveButton.setFont(buttonFont);

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editarButton);
        buttonPanel.add(saveButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // Adicionando os painéis ao JFrame
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.3; // Peso do painel de formulário
        gbc.weighty = 1;
        add(formPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
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
                    String aniversario = birthdayField.getText();
                    String phone = phoneField.getText();
                    String address = addressField.getText();
                    addClientToDatabase(name, aniversario, phone, address);
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(null, "Todos os campos devem ser preenchidos");
                }
            }
        });

        editarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    editingClientId = (int) model.getValueAt(selectedRow, 0);
                    nameField.setText((String) model.getValueAt(selectedRow, 1));
                    birthdayField.setText((String) model.getValueAt(selectedRow, 2));
                    phoneField.setText((String) model.getValueAt(selectedRow, 3));
                    addressField.setText((String) model.getValueAt(selectedRow, 4));
                    saveButton.setEnabled(true); // Ativa o botão Salvar
                } else {
                    JOptionPane.showMessageDialog(null, "Selecione uma linha para editar");
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int id = (int) model.getValueAt(selectedRow, 0);
                    deleteClientFromDatabase(id);
                    model.removeRow(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(null, "Selecione uma linha para excluir");
                }
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateFields() && editingClientId != -1) {
                    String name = nameField.getText();
                    String aniversario = birthdayField.getText();
                    String phone = phoneField.getText();
                    String address = addressField.getText();
                    updateClientInDatabase(editingClientId, name, aniversario, phone, address);
                    clearFields();
                    saveButton.setEnabled(false); // Desativa o botão Salvar após a edição
                } else {
                    JOptionPane.showMessageDialog(null, "Todos os campos devem ser preenchidos");
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
        JMenu fileMenu = new JMenu("Opções do Aplicativo");
        JMenuItem cadastrarAtendimento = new JMenuItem("Cadastrar Atendimentos");
        JMenuItem atendimentoCadastrados = new JMenuItem("Agenda de Atendimentos");
        JMenuItem exitMenuItem = new JMenuItem("Sair");
        fileMenu.add(cadastrarAtendimento);
        fileMenu.add(atendimentoCadastrados);
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        // Adicionar ActionListener para "Cadastrar Atendimentos"
        cadastrarAtendimento.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TelaAtendimento(connection).setVisible(true);
            }
        });

        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        loadClientsFromDatabase();
    }

    private void addLabelAndField(String labelText, JTextField textField, JPanel panel, GridBagConstraints gbc, int yPos) {
        gbc.gridx = 0;
        gbc.gridy = yPos;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        gbc.gridy = yPos;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(textField, gbc);
    }

    private boolean validateFields() {
        return !nameField.getText().isEmpty() &&
                !birthdayField.getText().isEmpty() &&
                !phoneField.getText().isEmpty() &&
                !addressField.getText().isEmpty();
    }

    private void clearFields() {
        nameField.setText("");
        birthdayField.setText("");
        phoneField.setText("");
        addressField.setText("");
        editingClientId = -1; // Reseta o ID do cliente em edição
    }

    private void addClientToDatabase(String name, String aniversario, String phone, String address) {
        String sql = "INSERT INTO clientes (nome, data_aniversario, telefone, endereco) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, aniversario);
            stmt.setString(3, phone);
            stmt.setString(4, address);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Cliente adicionado com sucesso!");
            loadClientsFromDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao adicionar cliente ao banco de dados.");
        }
    }

    private void deleteClientFromDatabase(int id) {
        String sql = "DELETE FROM clientes WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Cliente excluído com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao excluir cliente do banco de dados.");
        }
    }

    private void updateClientInDatabase(int id, String name, String aniversario, String phone, String address) {
        String sql = "UPDATE clientes SET nome = ?, data_aniversario = ?, telefone = ?, endereco = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, aniversario);
            stmt.setString(3, phone);
            stmt.setString(4, address);
            stmt.setInt(5, id);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Cliente atualizado com sucesso!");
            loadClientsFromDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao atualizar cliente no banco de dados.");
        }
    }

    // Dentro da classe CadastroCliente, método loadClientsFromDatabase()
    private void loadClientsFromDatabase() {
        try {
            if (connection != null && !connection.isClosed()) { // Verifica se a conexão está aberta
                String sql = "SELECT * FROM clientes";
                try (PreparedStatement stmt = connection.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {

                    // Limpar tabela
                    model.setRowCount(0);

                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String nome = rs.getString("nome");
                        String aniversario = rs.getString("data_aniversario");
                        String telefone = rs.getString("telefone");
                        String endereco = rs.getString("endereco");
                        model.addRow(new Object[]{id, nome, aniversario, telefone, endereco});
                    }
                }
            } else {
                // Se a conexão estiver fechada, informe o usuário ou realize alguma outra ação apropriada
                JOptionPane.showMessageDialog(this, "A conexão com o banco de dados está fechada.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados do banco de dados.");
        }
    }
}
