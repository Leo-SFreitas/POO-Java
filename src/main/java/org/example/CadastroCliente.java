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
    private final JTable table;
    private final JTextField nomeField;
    private final JTextField aniversárioField;
    private final JTextField telefoneField;
    private final JTextField enderecoField;
    private final DefaultTableModel model;
    private final Connection connection;
    private final JButton salvarButton;
    private int editingClientId = -1; // Variável para armazenar o ID do cliente que está sendo editado

    public CadastroCliente(Connection connection) {
        this.connection = connection;

        setTitle("Cadastro de Cliente");
        setSize(1000, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(218, 215, 219)); // Azul escuro
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Centralizar a janela
        setLocationRelativeTo(null);

        // Adionando NavBar
        JMenuBar menuBar = NavBar.createMenuBar(connection, this);
        setJMenuBar(menuBar);

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
        addLabelAndField("Nome:", nomeField = new JTextField(20), formPanel, gbc, 0);
        addLabelAndField("Aniversário:", aniversárioField = new JTextField(20), formPanel, gbc, 1);
        addLabelAndField("Telefone:", telefoneField = new JTextField(20), formPanel, gbc, 2);
        addLabelAndField("Endereço:", enderecoField = new JTextField(20), formPanel, gbc, 3);

        // Botões no formulário
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 5, 5));

        JButton addButton = new JButton("Adicionar");
        JButton editarButton = new JButton("Editar");
        JButton deletarButton = new JButton("Excluir");
        salvarButton = new JButton("Salvar");
        salvarButton.setEnabled(false); // Desativa o botão Salvar inicialmente

        // Ajuste de fonte dos botões
        Font buttonFont = new Font("Arial", Font.PLAIN, 16);
        addButton.setFont(buttonFont);
        editarButton.setFont(buttonFont);
        deletarButton.setFont(buttonFont);
        salvarButton.setFont(buttonFont);

        buttonPanel.add(addButton);
        buttonPanel.add(deletarButton);
        buttonPanel.add(editarButton);
        buttonPanel.add(salvarButton);

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
        addButton.addActionListener(e -> {
            if (validarCampos()) {
                String nome = nomeField.getText();
                String aniversario = aniversárioField.getText();
                String telefone = telefoneField.getText();
                String endereco = enderecoField.getText();
                addClienteToBD(nome, aniversario, telefone, endereco);
                limparCampos();
            } else {
                JOptionPane.showMessageDialog(null, "Todos os campos devem ser preenchidos");
            }
        });

        editarButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                editingClientId = (int) model.getValueAt(selectedRow, 0);
                nomeField.setText((String) model.getValueAt(selectedRow, 1));
                aniversárioField.setText((String) model.getValueAt(selectedRow, 2));
                telefoneField.setText((String) model.getValueAt(selectedRow, 3));
                enderecoField.setText((String) model.getValueAt(selectedRow, 4));
                salvarButton.setEnabled(true); // Ativa o botão Salvar
            } else {
                JOptionPane.showMessageDialog(null, "Selecione uma linha para editar");
            }
        });

        deletarButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) model.getValueAt(selectedRow, 0);
                deleteCliente(id);
                model.removeRow(selectedRow);
            } else {
                JOptionPane.showMessageDialog(null, "Selecione uma linha para excluir");
            }
        });

        salvarButton.addActionListener(e -> {
            if (validarCampos() && editingClientId != -1) {
                String name = nomeField.getText();
                String aniversario = aniversárioField.getText();
                String phone = telefoneField.getText();
                String address = enderecoField.getText();
                updateCliente(editingClientId, name, aniversario, phone, address);
                limparCampos();
                salvarButton.setEnabled(false); // Desativa o botão Salvar após a edição
            } else {
                JOptionPane.showMessageDialog(null, "Todos os campos devem ser preenchidos");
            }
        });

        // Definindo a cor do texto dos rótulos para preto
        for (Component component : formPanel.getComponents()) {
            if (component instanceof JLabel) {
                component.setForeground(Color.BLACK);
            }
        }

        setJMenuBar(menuBar);

        // Centraliza a janela na tela
        setLocationRelativeTo(null);

        loadClientesFromBD();
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

    private boolean validarCampos() {
        return !nomeField.getText().isEmpty() &&
                !telefoneField.getText().isEmpty() &&
                !enderecoField.getText().isEmpty();
    }

    private void limparCampos() {
        nomeField.setText("");
        aniversárioField.setText("");
        telefoneField.setText("");
        enderecoField.setText("");
        editingClientId = -1; // Reseta o ID do cliente em edição
    }

    private void addClienteToBD(String name, String aniversario, String phone, String address) {
        String sql = "INSERT INTO clientes (nome, data_aniversario, telefone, endereco) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, aniversario);
            stmt.setString(3, phone);
            stmt.setString(4, address);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Cliente adicionado com sucesso!");
            loadClientesFromBD();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao adicionar cliente ao banco de dados.");
        }
    }

    private void deleteCliente(int id) {
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

    private void updateCliente(int id, String name, String aniversario, String phone, String address) {
        String sql = "UPDATE clientes SET nome = ?, data_aniversario = ?, telefone = ?, endereco = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, aniversario);
            stmt.setString(3, phone);
            stmt.setString(4, address);
            stmt.setInt(5, id);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Cliente atualizado com sucesso!");
            loadClientesFromBD();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao atualizar cliente no banco de dados.");
        }
    }

    private void loadClientesFromBD() {
        model.setRowCount(0); // Limpa os dados atuais da tabela
        String sql = "SELECT id, nome, data_aniversario, telefone, endereco FROM clientes ORDER BY nome"; // Ordena pelo nome

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("nome");
                String aniversario = rs.getString("data_aniversario");
                String phone = rs.getString("telefone");
                String address = rs.getString("endereco");
                model.addRow(new Object[]{id, name, aniversario, phone, address});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes do banco de dados.");
        }
    }
}
