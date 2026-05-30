package com.mycompany.hangman.view;

import com.mycompany.hangman.Main;
import com.mycompany.hangman.config.Constants;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ygttc
 */
public class ScorePanel extends JPanel {
    
    private JTable scoreTable;
    private DefaultTableModel tableModel;
    private JButton clearButton;
    private Logger logger = Logger.getLogger(ScorePanel.class.getName());
    
    public ScorePanel() {
        setLayout(new BorderLayout());
        initComponents();
    }
    
    private void initComponents() {
        String[] columnNames = {"Tarih/Saat", "Kelime", "Süre", "Sonuç"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        scoreTable = new JTable(tableModel);
        scoreTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        scoreTable.setFillsViewportHeight(true);
        
        JScrollPane scrollPane = new JScrollPane(scoreTable);
        add(scrollPane, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        clearButton = new JButton("Temizle");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearScores();
            }
        });
        bottomPanel.add(clearButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    public void refreshData() {
        tableModel.setRowCount(0);
        
        File gamesFile = new File(Constants.GAMES_FILE);
        if (!gamesFile.exists()) {
            return;
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(gamesFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" - ");
                if (parts.length >= 4) {
                    String dateTime = parts[0].substring(1, parts[0].length() - 1);
                    String word = parts[1].substring("Kelime: ".length());
                    String time = parts[2].substring("Süre: ".length(), parts[2].length() - " saniye".length());
                    String result = parts[3].substring("Sonuç: ".length());
                    
                    tableModel.addRow(new Object[]{dateTime, word, time, result});
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Hata oluştu: ", e);
        }
    }
    
    private void clearScores() {
        String password = JOptionPane.showInputDialog(this, "Temizlemek için şifrenizi girin:", "Şifre Gerekli", JOptionPane.QUESTION_MESSAGE);
        
        if (password == null || password.trim().isEmpty()) {
            return;
        }
        
        if (checkPassword(password)) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Tüm oyun kayıtları silinecek. Emin misiniz?", 
                "Onay", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(Constants.GAMES_FILE))) {
                    bw.write("");
                    Main.writeLog("Oyun kayıtları temizlendi");
                    refreshData();
                    JOptionPane.showMessageDialog(this, "Kayıtlar başarıyla temizlendi!", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Hata oluştu: ", e);
                    JOptionPane.showMessageDialog(this, "Temizleme başarısız!", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Hatalı şifre!", "Hata", JOptionPane.ERROR_MESSAGE);
            Main.writeLog("Hatalı şifre ile temizleme denemesi");
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
}
