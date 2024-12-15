package gui;

import models.Customer;

import javax.swing.*;
import java.awt.*;

public class StartPanel extends JPanel {

    public StartPanel(ViewManager parentFrame) {
//        parentFrame.showBookingPanel(new Customer("0", "test t", "a@test.b","070","2")); //TODO ta bort


        Font font = new Font("Times New Roman", Font.BOLD, 16);

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(400, 500));

        //Skapar en skalad bild
        ImageIcon scaledIcon = ImageFactory.createScaledImageIcon("src/resources/background.jpg", 400, 500);

        // Lägger den skalade bilden som bakgrunden
        JLabel backgroundLabel = new JLabel(scaledIcon);
        backgroundLabel.setLayout(new GridBagLayout());  // Gör så att komponenter kan placeras ovanpå
        add(backgroundLabel);

        // Texten högst upp
        JLabel welcomeLabel = new JLabel("Logga in", SwingConstants.CENTER);
        welcomeLabel.setFont(font.deriveFont(30.0f));
        welcomeLabel.setForeground(Color.BLACK);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Padding runt texten

        // Panel för inloggningsknappar
        JPanel loginButtonPanel = new JPanel();
        loginButtonPanel.setLayout(new GridLayout(2, 1, 15, 15));
        loginButtonPanel.setOpaque(false); // Gör panelen genomskinlig

        // Knappar för inloggning
        JButton userLoginButton = new JButton("Som kund");
        userLoginButton.addActionListener(e -> parentFrame.showCard("Login"));

        JButton businessLoginButton = new JButton("Som företag");
        businessLoginButton.addActionListener(e -> parentFrame.showCard("Login"));


        // Stylar knapparna
        JButton[] loginButtons = {userLoginButton, businessLoginButton};
        for (JButton button : loginButtons) {
            button.setFont(font.deriveFont(18.0f));
            button.setPreferredSize(new Dimension(240, 40));
            button.setFocusable(false);
            button.setBackground(Color.WHITE); // TILLFÄLLIG FÄRG kanske
            button.setForeground(Color.BLACK);
//            button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // Lägger till kant
        }

        loginButtonPanel.add(userLoginButton);
        loginButtonPanel.add(businessLoginButton);

        // Lägger till texten, knapparna och registreringslänk i GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0); // Mellanrum mellan komponenterna
        gbc.gridx = 0; // Centrerar
        gbc.gridy = 0; // Översta komponenten
        gbc.anchor = GridBagConstraints.CENTER;
        backgroundLabel.add(welcomeLabel, gbc);

        gbc.gridy = 1; // Under texten
        backgroundLabel.add(loginButtonPanel, gbc);

        gbc.gridy = 2; // Nederst


        // "Registrera dig"-länken
        JLabel registerLabel = new JLabel("<html><u>Registrera dig</u></html>", SwingConstants.CENTER);
        registerLabel.setFont(font.deriveFont(Font.PLAIN));
        registerLabel.setForeground(Color.BLUE);
        registerLabel.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Gör muspekaren till en hand vid hovring över
        registerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 60, 0)); // Padding ovanför länken

        // MouseListnener för "länken"
        registerLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                parentFrame.showCard("Register");
            }
        });

        backgroundLabel.add(registerLabel, gbc);
    }
}
