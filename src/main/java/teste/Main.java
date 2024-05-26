package teste;

import javax.swing.*;


public class Main {
    public static void main(String[] args) {
        // Inicializa o banco de dados e as tabelas
        DatabaseManager.initializeDatabase();

        // Configura o look and feel do sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Cria a interface de cadastro de clientes
        SwingUtilities.invokeLater(() -> {
            CadastroClienteInterface cadastroClienteFrame = new CadastroClienteInterface();
            cadastroClienteFrame.setVisible(true);
        });

        // Cria a interface de atendimento
        SwingUtilities.invokeLater(() -> {
            AtendimentoInterface atendimentoFrame = new AtendimentoInterface();
            atendimentoFrame.setVisible(true);
        });
    }
}
