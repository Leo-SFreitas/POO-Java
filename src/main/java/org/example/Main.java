package org.example;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ConnectionBD connectionBD = new ConnectionBD();
                new CadastroInterfaceBD(connectionBD.getConnection()).setVisible(true);
            }
        });
    }
}

