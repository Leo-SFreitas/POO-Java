package teste;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class AtendimentoInterface extends JFrame {
    private JTextArea txtAtendimentos;
    private JButton btnCadastrarAtendimento;

    public AtendimentoInterface() {
        setTitle("Atendimentos");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        txtAtendimentos = new JTextArea();
        txtAtendimentos.setEditable(false);
        add(new JScrollPane(txtAtendimentos), BorderLayout.CENTER);

        btnCadastrarAtendimento = new JButton("Cadastrar Atendimento");
        btnCadastrarAtendimento.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cadastrarAtendimento();
            }
        });
        add(btnCadastrarAtendimento, BorderLayout.SOUTH);

        carregarAtendimentos();
    }

    private void carregarAtendimentos() {
        String sql = "SELECT * FROM atendimentos";

        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("ID: ").append(rs.getInt("id")).append("\n");
                sb.append("Data do Atendimento: ").append(rs.getString("data_atendimento")).append("\n");
                sb.append("Serviço: ").append(rs.getString("servico")).append("\n");
                sb.append("Local do Atendimento: ").append(rs.getString("local_atendimento")).append("\n");
                sb.append("Preço do Serviço: ").append(rs.getString("preco_servico")).append("\n\n");
            }
            txtAtendimentos.setText(sb.toString());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void cadastrarAtendimento() {
        JDialog popupCadastro = new JDialog(this, "Novo Atendimento", true);
        popupCadastro.setSize(300, 200);
        popupCadastro.setLayout(new GridLayout(5, 2));

        popupCadastro.add(new JLabel("Data do atendimento:"));
        JTextField txtDataAtendimento = new JTextField();
        popupCadastro.add(txtDataAtendimento);

        popupCadastro.add(new JLabel("Horário do atendimento:"));
        JTextField txtHorario = new JTextField();
        popupCadastro.add(txtHorario);

        popupCadastro.add(new JLabel("Serviço:"));
        JTextField txtServico = new JTextField();
        popupCadastro.add(txtServico);

        popupCadastro.add(new JLabel("Local do atendimento:"));
        JTextField txtLocalAtendimento = new JTextField();
        popupCadastro.add(txtLocalAtendimento);

        popupCadastro.add(new JLabel("Preço do serviço:"));
        JTextField txtPrecoServico = new JTextField();
        popupCadastro.add(txtPrecoServico);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sql = "INSERT INTO atendimentos(data_atendimento, horario, servico, local_atendimento, preco_servico) VALUES(?, ?, ?, ?, ?)";

                try (Connection conn = DatabaseManager.connect();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, txtDataAtendimento.getText());
                    pstmt.setString(2, txtHorario.getText());
                    pstmt.setString(3, txtServico.getText());
                    pstmt.setString(4, txtLocalAtendimento.getText());
                    pstmt.setString(5, txtPrecoServico.getText());
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(popupCadastro, "Atendimento cadastrado com sucesso.");
                    carregarAtendimentos();
                    popupCadastro.dispose();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(popupCadastro, ex.getMessage());
                }
            }
        });
        popupCadastro.add(btnSalvar);

        popupCadastro.setVisible(true);
    }
}