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
public class LogPanel extends JPanel {
    
    private JTable logTable;
    private DefaultTableModel tableModel;
    private JButton clearButton;
    private Logger logger = Logger.getLogger(LogPanel.class.getName());
    
    public LogPanel() {
        setLayout(new BorderLayout());
        initComponents();
    }
    
    private void initComponents() {
        String[] columnNames = {"Tarih/Saat", "İşlem"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        logTable = new JTable(tableModel);
        logTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        logTable.setFillsViewportHeight(true);
        
        JScrollPane scrollPane = new JScrollPane(logTable);
        add(scrollPane, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        clearButton = new JButton("Temizle");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearLogs();
            }
        });
        bottomPanel.add(clearButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    public void refreshData() {
        tableModel.setRowCount(0);

        File logFile = new File(Constants.LOG_FILE);
        if (!logFile.exists()) {
            return;
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Format: [dd-MM-yyyy HH:mm:ss]
                String[] parts = line.split(" - ");
                if (parts.length >= 2) {
                    String dateTime = parts[0].substring(1, parts[0].length() - 1);
                    String action = parts[1];
                    
                    tableModel.addRow(new Object[]{dateTime, action});
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Hata oluştu: ", e);
        }
    }
    
    private void clearLogs() {
        String password = JOptionPane.showInputDialog(this, "Temizlemek için şifrenizi girin:", "Şifre Gerekli", JOptionPane.QUESTION_MESSAGE);
        
        if (password == null || password.trim().isEmpty()) {
            return;
        }
        
        if (checkPassword(password)) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Tüm log kayıtları silinecek. Emin misiniz?", 
                "Onay", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(Constants.LOG_FILE))) {
                    bw.write("");
                    Main.writeLog("Log kayıtları temizlendi");
                    refreshData();
                    JOptionPane.showMessageDialog(this, "Loglar başarıyla temizlendi!", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Hata oluştu: ", e);
                    JOptionPane.showMessageDialog(this, "Temizleme başarısız!", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Hatalı şifre!", "Hata", JOptionPane.ERROR_MESSAGE);
            Main.writeLog("Hatalı şifre ile log temizleme denemesi");
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
