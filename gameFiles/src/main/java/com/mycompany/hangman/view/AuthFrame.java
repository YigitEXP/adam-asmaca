package com.mycompany.hangman.view;

import com.mycompany.hangman.Main;
import com.mycompany.hangman.config.Constants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ygttc
 */
public class AuthFrame extends JFrame {
    
    private boolean isPasswordSet;
    private int wrongAttempts = 0;
    private JTextField passwordField;
    private JLabel titleLabel;
    private JLabel messageLabel;
    private JButton submitButton;
    private Logger logger = Logger.getLogger(AuthFrame.class.getName());
    
    public AuthFrame(boolean isPasswordSet) {
        this.isPasswordSet = isPasswordSet;
        setTitle("Adam Asmaca - Giriş");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        initComponents();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        titleLabel = new JLabel(isPasswordSet ? "Şifre Giriniz" : "Şifre Belirleyiniz", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);
        
        gbc.gridy = 1;
        mainPanel.add(Box.createVerticalStrut(10), gbc);
        
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        mainPanel.add(new JLabel("Şifre:"), gbc);
        
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        mainPanel.add(passwordField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setForeground(Color.RED);
        mainPanel.add(messageLabel, gbc);
        
        gbc.gridy = 4;
        submitButton = new JButton(isPasswordSet ? "Giriş Yap" : "Şifre Kaydet");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handlePasswordSubmit();
            }
        });
        mainPanel.add(submitButton, gbc);
        
        add(mainPanel);
        getRootPane().setDefaultButton(submitButton);
    }
    
    private void handlePasswordSubmit() {
        String password = passwordField.getText();
        
        if (password == null || password.trim().isEmpty()) {
            messageLabel.setText("Şifre boş olamaz!");
            return;
        }
        
        if (isPasswordSet) {
            if (checkPassword(password)) {
                Main.writeLog("Başarılı giriş");
                messageLabel.setText("");
                openMainFrame();
            } else {
                wrongAttempts++;
                Main.writeLog("Hatalı şifre denemesi (" + wrongAttempts + "/3)");
                if (wrongAttempts >= 3) {
                    Main.writeLog("3 hatalı deneme - program kapatıldı");
                    JOptionPane.showMessageDialog(this, "3 kez hatalı şifre girdiniz. Program kapatılıyor.", "Hata", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                } else {
                    messageLabel.setText("Hatalı şifre!"+ (3 - wrongAttempts) + "hakkınız kaldı.");
                    passwordField.setText("");
                }
            }
        } else {
            savePassword(password);
            Main.writeLog("Şifre belirlendi");
            JOptionPane.showMessageDialog(this, "Şifre başarıyla belirlendi!", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
            openMainFrame();
        }
    }
    
    private boolean checkPassword(String inputPassword) {
        try (BufferedReader br = new BufferedReader(new FileReader(Constants.PASSWORD_FILE))) {
            String storedPassword = br.readLine();
            return storedPassword != null && storedPassword.equals(inputPassword);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Hata oluştu: ", e);
            return false;
        }
    }
    
    private void savePassword(String password) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(Constants.PASSWORD_FILE))) {
            bw.write(password);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Hata oluştu: ", e);
        }
    }
    
    private void openMainFrame() {
        this.dispose();
        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
    }
}
