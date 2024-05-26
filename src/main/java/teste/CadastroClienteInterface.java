package teste;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CadastroClienteInterface extends JFrame {
    private JTextField txtNome, txtEndereco, txtTelefone, txtAniversario;
    private JButton btnSalvar, btnAtualizar, btnExcluir, btnBuscar;

    public CadastroClienteInterface() {
        setTitle("Cadastro de Clientes");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 2));

        add(new JLabel("Nome:"));
        txtNome = new JTextField();
        add(txtNome);

        add(new JLabel("Telefone:"));
        txtTelefone = new JTextField();
        add(txtTelefone);

        add(new JLabel("Endereco:"));
        txtEndereco = new JTextField();
        add(txtEndereco);

        add(new JLabel("Aniversario:"));
        txtAniversario = new JTextField();
        add(txtAniversario);

        btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarCliente();
            }
        });
        add(btnSalvar);

        btnAtualizar = new JButton("Atualizar");
        btnAtualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                atualizarCliente();
            }
        });
        add(btnAtualizar);

        btnExcluir = new JButton("Excluir");
        btnExcluir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                excluirCliente();
            }
        });
        add(btnExcluir);

        btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarCliente();
            }
        });
        add(btnBuscar);
    }

    private void salvarCliente() {
        String telefone = txtTelefone.getText();
        if (telefone.length() != 11 || !telefone.matches("\\d{11}")) {
            JOptionPane.showMessageDialog(this, "O telefone deve conter exatamente 11 números.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "INSERT INTO clientes(nome, telefone, endereco, data_aniversario) VALUES(?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, txtNome.getText());
            pstmt.setString(2, telefone);
            pstmt.setString(3, txtEndereco.getText());
            pstmt.setString(4, txtAniversario.getText());
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Cliente salvo com sucesso.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void atualizarCliente() {
        // Lógica para atualizar um cliente no banco de dados
    }

    private void excluirCliente() {
        // Lógica para excluir um cliente do banco de dados
    }

    private void buscarCliente() {
        // Lógica para buscar um cliente no banco de dados
    }
}