import javax.swing.*;
import java.awt.*;

public class Tela_Inicial {

    public static void main(String[] args) {
        // Cria a janela principal
        JFrame frame = new JFrame("Tela Inicial"); // Altera o título da janela para "Tela Inicial"
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());

        // Define as constraints para o GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Cria o primeiro texto
        JLabel label1 = new JLabel("Olá, Isabel", SwingConstants.CENTER);
        label1.setFont(new Font("Serif", Font.BOLD, 16));
        frame.add(label1, gbc);

        // Cria o segundo texto
        JLabel label2 = new JLabel("O que você precisa fazer agora?", SwingConstants.CENTER);
        label2.setFont(new Font("Serif", Font.PLAIN, 14));
        frame.add(label2, gbc);

        // Cria um painel para os botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Cria os botões com os novos textos
        JButton button1 = new JButton("Cadastrar Novo Cliente");
        JButton button2 = new JButton("Agendar Atendimento");
        JButton button3 = new JButton("Receitas");

        // Adiciona os botões ao painel
        buttonPanel.add(button1);
        buttonPanel.add(button2);
        buttonPanel.add(button3);

        // Define o tamanho preferido do painel de botões
        buttonPanel.setPreferredSize(new Dimension(800, 200));

        // Adiciona o painel com os botões à janela
        frame.add(buttonPanel, gbc);

        // Ajusta o tamanho da janela para o tamanho preferido dos componentes
        frame.pack();

        // Define o tamanho da janela para 800x400
        frame.setSize(800, 400);

        // Centraliza a janela na tela
        frame.setLocationRelativeTo(null);

        // Torna a janela visível
        frame.setVisible(true);
    }
}