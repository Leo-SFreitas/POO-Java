package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CadastroAtendimento extends JFrame {
    private JComboBox<String> clienteComboBox;
    private JRadioButton residencialRadioButton, consultorioRadioButton;
    private ButtonGroup localButtonGroup;
    private JTextField dataAtendimentoField, horarioField, precoServicoField;
    private JComboBox<String> servicoComboBox;
    private JButton cadastrarButton;
    private Connection connection;
    private Runnable onCadastroSucesso;

    public CadastroAtendimento(Connection connection, Runnable onCadastroSucesso) {
        this.connection = connection;
        this.onCadastroSucesso = onCadastroSucesso;

        setTitle("Cadastro de Atendimento");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Centralizar a janela
        setLocationRelativeTo(null);

        // Inicializar os componentes
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Adicionar campos de entrada
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Cliente:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        clienteComboBox = new JComboBox<>();
        loadClientsIntoComboBox();
        panel.add(clienteComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Local de Atendimento:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        residencialRadioButton = new JRadioButton("Residencial");
        consultorioRadioButton = new JRadioButton("Consultório");
        localButtonGroup = new ButtonGroup();
        localButtonGroup.add(residencialRadioButton);
        localButtonGroup.add(consultorioRadioButton);
        JPanel localPanel = new JPanel(new FlowLayout());
        localPanel.add(residencialRadioButton);
        localPanel.add(consultorioRadioButton);
        panel.add(localPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Data de Atendimento:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        dataAtendimentoField = new JTextField(20);
        panel.add(dataAtendimentoField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Horário:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        horarioField = new JTextField(20);
        panel.add(horarioField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Serviço:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        servicoComboBox = new JComboBox<>(new String[]{"Mão", "Pé", "Mão e Pé"});
        panel.add(servicoComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Preço do Serviço:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 5;
        precoServicoField = new JTextField(20);
        panel.add(precoServicoField, gbc);

        // Adicionar botão de cadastro
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        cadastrarButton = new JButton("Cadastrar");
        panel.add(cadastrarButton, gbc);

        cadastrarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cadastrarAtendimento();
            }
        });

        // Adicionar ActionListener para o botão Residencial
        residencialRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                preencherEnderecoResidencial();
            }
        });

        add(panel);
    }

    // Método para carregar os clientes existentes no banco de dados para o JComboBox
    private void loadClientsIntoComboBox() {
        try {
            String sql = "SELECT nome FROM clientes";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String nomeCliente = rs.getString("nome");
                clienteComboBox.addItem(nomeCliente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes do banco de dados.");
        }
    }

    private void preencherEnderecoResidencial() {
        String clienteSelecionado = (String) clienteComboBox.getSelectedItem();
        if (clienteSelecionado != null && !clienteSelecionado.isEmpty()) {
            try {
                String sql = "SELECT endereco FROM clientes WHERE nome = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, clienteSelecionado);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String endereco = rs.getString("endereco");
                    // Exibir o endereço em algum componente visual ou usar em algum lugar
                    System.out.println("Endereço do cliente: " + endereco);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao buscar o endereço do cliente.");
            }
        }
    }

    private void cadastrarAtendimento() {
        String clienteSelecionado = (String) clienteComboBox.getSelectedItem();
        String local;
        if (residencialRadioButton.isSelected()) {
            local = getEnderecoCliente(clienteSelecionado); // Pega o endereço do cliente
        } else {
            local = "Consultório";
        }
        String dataAtendimento = dataAtendimentoField.getText();
        String horario = horarioField.getText();
        String servico = (String) servicoComboBox.getSelectedItem();
        String precoServico = precoServicoField.getText();

        // Verificar se todos os campos foram preenchidos
        if (clienteSelecionado.isEmpty() || dataAtendimento.isEmpty() || horario.isEmpty() || servico.isEmpty() || precoServico.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos os campos devem ser preenchidos.");
            return;
        }

        try {
            // Obter o ID do cliente selecionado
            String getIdClienteSQL = "SELECT id FROM clientes WHERE nome = ?";
            PreparedStatement getIdClienteStatement = connection.prepareStatement(getIdClienteSQL);
            getIdClienteStatement.setString(1, clienteSelecionado);
            ResultSet rs = getIdClienteStatement.executeQuery();
            if (rs.next()) {
                int idCliente = rs.getInt("id");
                // Inserir os dados na tabela do banco de dados
                String insertSQL = "INSERT INTO atendimentos (id_cliente, cliente, data_atendimento, horario, servico, local_atendimento, preco_servico) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement insertStatement = connection.prepareStatement(insertSQL);
                insertStatement.setInt(1, idCliente);
                insertStatement.setString(2, clienteSelecionado);
                insertStatement.setString(3, dataAtendimento);
                insertStatement.setString(4, horario);
                insertStatement.setString(5, servico);
                insertStatement.setString(6, local);
                insertStatement.setString(7, precoServico);

                insertStatement.executeUpdate();

                // Limpar os campos após a inserção
                clienteComboBox.setSelectedIndex(0);
                localButtonGroup.clearSelection();
                dataAtendimentoField.setText("");
                horarioField.setText("");
                servicoComboBox.setSelectedIndex(0);
                precoServicoField.setText("");

                JOptionPane.showMessageDialog(this, "Atendimento cadastrado com sucesso!");
                onCadastroSucesso.run();  // Notificar sucesso
                dispose();  // Fechar a janela após o cadastro
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao obter o ID do cliente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar atendimento.");
        }
    }

    private String getEnderecoCliente(String nomeCliente) {
        try {
            String sql = "SELECT endereco FROM clientes WHERE nome = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, nomeCliente);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("endereco");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao buscar o endereço do cliente.");
        }
        return "Residencial";
    }
}
