package com.mycompany.hangman.view;
 
import com.mycompany.hangman.Main;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
 
/**
 * @author ygttc
 */
public class MainFrame extends JFrame {
 
    private JTabbedPane tabbedPane;
    private GamePanel gamePanel;
    private ScorePanel scorePanel;
    private LogPanel logPanel;
 
    public MainFrame() {
        setTitle("Adam Asmaca Oyunu");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
 
        initComponents();
        Main.writeLog("Ana ekran açıldı");
    }
 
    private void initComponents() {
 
        // Menü Çubuğu (sadece Çıkış için)
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Oyun");
 
        JMenuItem exitItem = new JMenuItem("Çıkış");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.writeLog("Programdan çıkıldı");
                System.exit(0);
            }
        });
 
        gameMenu.add(exitItem);
        menuBar.add(gameMenu);
        setJMenuBar(menuBar);
 
        // Oyuna Başla & Yeniden Başlat
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
 
        JButton startButton = new JButton("Oyuna Başla");
        startButton.setFont(new Font("Arial", Font.BOLD, 13));
        startButton.setBackground(new Color(34, 139, 34));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabbedPane.setSelectedIndex(0);
                gamePanel.startNewGame();
                Main.writeLog("Oyun başlatıldı");
            }
        });
 
        JButton restartButton = new JButton("Yeniden Başlat");
        restartButton.setFont(new Font("Arial", Font.BOLD, 13));
        restartButton.setBackground(new Color(70, 130, 180));
        restartButton.setForeground(Color.WHITE);
        restartButton.setFocusPainted(false);
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabbedPane.setSelectedIndex(0);
                gamePanel.startNewGame();
                Main.writeLog("Oyun yeniden başlatıldı");
            }
        });
 
        toolBar.add(startButton);
        toolBar.addSeparator(new Dimension(10, 0));
        toolBar.add(restartButton);
 
        // --- Sekmeler ---
        tabbedPane = new JTabbedPane();
 
        gamePanel = new GamePanel();
        scorePanel = new ScorePanel();
        logPanel = new LogPanel();
 
        tabbedPane.addTab("Oyun", gamePanel);
        tabbedPane.addTab("Eski Skorlar", scorePanel);
        tabbedPane.addTab("Loglar", logPanel);
 
        tabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            @Override
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                int selectedIndex = tabbedPane.getSelectedIndex();
                if (selectedIndex == 1) {
                    scorePanel.refreshData();
                } else if (selectedIndex == 2) {
                    logPanel.refreshData();
                }
            }
        });
 
        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
 
    public GamePanel getGamePanel() {
        return gamePanel;
    }
 
    public ScorePanel getScorePanel() {
        return scorePanel;
    }
 
    public LogPanel getLogPanel() {
        return logPanel;
    }
}