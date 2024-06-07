package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CadastroAtendimento extends JFrame {
    // Declarações dos componentes
    private JComboBox<String> clienteComboBox;
    private JRadioButton residencialRadioButton, consultorioRadioButton;
    private ButtonGroup localButtonGroup;
    private JTextField diaField, anoField, horarioField, precoServicoField, servicoField;
    private JComboBox<Integer> mesComboBox;
    private JButton cadastrarButton;
    private Connection connection;
    private Runnable onCadastroSucesso;
    private int atendimentoId; // ID do atendimento em modo de edição, -1 se for novo cadastro

    public CadastroAtendimento(Connection connection, Runnable onCadastroSucesso) {
        this(connection, onCadastroSucesso, -1, null, null, null, null, null, null);
    }

    public CadastroAtendimento(Connection connection, Runnable onCadastroSucesso, int atendimentoId, String cliente, String local, String data, String horario, String servico, Double preco) {
        this.connection = connection;
        this.onCadastroSucesso = onCadastroSucesso;
        this.atendimentoId = atendimentoId;

        setTitle(atendimentoId == -1 ? "Cadastro de Atendimento" : "Editar Atendimento");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Centralizar a janela
        setLocationRelativeTo(null);

        // Inicializar os componentes
        initComponents();

        // Preencher os campos se estiver em modo de edição
        if (atendimentoId != -1) {
            clienteComboBox.setSelectedItem(cliente);
            if (local.equals("Consultório")) {
                consultorioRadioButton.setSelected(true);
            } else {
                residencialRadioButton.setSelected(true);
            }

            // Dividir a data em dia, mês e ano
            LocalDate dataLocalDate = LocalDate.parse(data, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            diaField.setText(String.valueOf(dataLocalDate.getDayOfMonth()));
            mesComboBox.setSelectedItem(dataLocalDate.getMonthValue());
            anoField.setText(String.valueOf(dataLocalDate.getYear()));

            horarioField.setText(horario);
            servicoField.setText(servico);
            precoServicoField.setText(preco.toString());
            cadastrarButton.setText("Salvar");
        }
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
        panel.add(new JLabel("Dia:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        diaField = new JTextField(20);
        panel.add(diaField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Mês:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        mesComboBox = new JComboBox<>();
        for (int i = 1; i <= 12; i++) {
            mesComboBox.addItem(i);
        }
        panel.add(mesComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Ano:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        anoField = new JTextField(20);
        panel.add(anoField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Horário:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 5;
        horarioField = new JTextField(20);
        panel.add(horarioField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Serviço:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 6;
        servicoField = new JTextField(20);
        panel.add(servicoField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(new JLabel("Preço do Serviço:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 7;
        precoServicoField = new JTextField(20);
        panel.add(precoServicoField, gbc);

        // Adicionar botão de cadastro/edição
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        cadastrarButton = new JButton(atendimentoId == -1 ? "Cadastrar" : "Salvar");

        panel.add(cadastrarButton, gbc);

        cadastrarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (atendimentoId == -1) {
                    cadastrarAtendimento();
                } else {
                    editarAtendimento();
                }
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
        int dia = Integer.parseInt(diaField.getText());
        int mes = (int) mesComboBox.getSelectedItem();
        int ano = Integer.parseInt(anoField.getText());
        String dataAtendimento = String.format("%04d-%02d-%02d", ano, mes, dia);
        String horario = horarioField.getText();
        String servico = (String) servicoField.getText();
        String precoServico = precoServicoField.getText();

        // Verificar se todos os campos foram preenchidos
        if (clienteSelecionado.isEmpty() || horario.isEmpty() || servico.isEmpty() || precoServico.isEmpty()) {
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
                String insertSQL = "INSERT INTO atendimentos (id_cliente, cliente, dia_atendimento, mes_atendimento, ano_atendimento, horario, servico, local_atendimento, preco_servico) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement insertStatement = connection.prepareStatement(insertSQL);
                insertStatement.setInt(1, idCliente);
                insertStatement.setString(2, clienteSelecionado);
                insertStatement.setInt(3, dia);
                insertStatement.setInt(4, mes);
                insertStatement.setInt(5, ano);
                insertStatement.setString(6, horario);
                insertStatement.setString(7, servico);
                insertStatement.setString(8, local);
                insertStatement.setString(9, precoServico);

                insertStatement.executeUpdate();

                // Limpar os campos após a inserção
                clienteComboBox.setSelectedIndex(0);
                localButtonGroup.clearSelection();
                diaField.setText("");
                mesComboBox.setSelectedIndex(0);
                anoField.setText("");
                horarioField.setText("");
                servicoField.setText("");
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

    private void editarAtendimento() {
        String clienteSelecionado = (String) clienteComboBox.getSelectedItem();
        String local;
        if (residencialRadioButton.isSelected()) {
            local = getEnderecoCliente(clienteSelecionado); // Pega o endereço do cliente
        } else {
            local = "Consultório";
        }
        int dia = Integer.parseInt(diaField.getText());
        int mes = (int) mesComboBox.getSelectedItem();
        int ano = Integer.parseInt(anoField.getText());
        String dataAtendimento = String.format("%04d-%02d-%02d", ano, mes, dia);
        String horario = horarioField.getText();
        String servico = servicoField.getText();
        String precoServico = precoServicoField.getText();

        // Verificar se todos os campos foram preenchidos
        if (clienteSelecionado.isEmpty() || horario.isEmpty() || servico.isEmpty() || precoServico.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos os campos devem ser preenchidos.");
            return;
        }

        try {
            String updateSQL = "UPDATE atendimentos SET cliente = ?, dia_atendimento = ?, mes_atendimento = ?, ano_atendimento = ?, horario = ?, servico = ?, local_atendimento = ?, preco_servico = ? WHERE id = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateSQL);
            updateStatement.setString(1, clienteSelecionado);
            updateStatement.setInt(2, dia);
            updateStatement.setInt(3, mes);
            updateStatement.setInt(4, ano);
            updateStatement.setString(5, horario);
            updateStatement.setString(6, servico);
            updateStatement.setString(7, local);
            updateStatement.setString(8, precoServico);
            updateStatement.setInt(9, atendimentoId);

            updateStatement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Atendimento atualizado com sucesso!");
            onCadastroSucesso.run();  // Notificar sucesso
            dispose();  // Fechar a janela após a atualização
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao atualizar atendimento.");
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
