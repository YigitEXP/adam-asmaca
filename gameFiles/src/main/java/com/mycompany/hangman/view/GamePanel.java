package com.mycompany.hangman.view;

import com.mycompany.hangman.Main;
import com.mycompany.hangman.config.Constants;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ygttc
 */
public class GamePanel extends JPanel {

    private String currentWord;
    private char[] guessedWord;
    private int wrongAttempts;
    private Timer timer;
    private int seconds;
    private boolean gameActive;
    private Logger logger = Logger.getLogger(GamePanel.class.getName());

    private List<Character> guessedLetters = new ArrayList<>();

    private JLabel[] letterLabels;
    private JLabel timerLabel;
    private JLabel messageLabel;
    private JLabel imageLabel;
    private JTextField letterGuessField;
    private JTextField wordGuessField;
    private JButton guessLetterButton;
    private JButton guessWordButton;
    private JPanel lettersPanel;

    private DefaultTableModel guessedLettersTableModel;
    private JTable guessedLettersTable;

    public GamePanel() {
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        timerLabel = new JLabel("Süre: 0 saniye", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));

        messageLabel = new JLabel("Oyunu başlatmak için \"Oyuna Başla\" butonuna tıklayın", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        messageLabel.setForeground(new Color(60, 60, 60));

        topPanel.add(timerLabel, BorderLayout.NORTH);
        topPanel.add(messageLabel, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(220, 220));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        imagePanel.add(imageLabel);

        JPanel middlePanel = new JPanel(new BorderLayout(5, 10));

        lettersPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        lettersPanel.setBorder(BorderFactory.createTitledBorder("Kelime"));
        middlePanel.add(lettersPanel, BorderLayout.NORTH);

        String[] colNames = {"Harf", "Sonuç"};
        guessedLettersTableModel = new DefaultTableModel(colNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        guessedLettersTable = new JTable(guessedLettersTableModel);
        guessedLettersTable.setFont(new Font("Arial", Font.PLAIN, 13));
        guessedLettersTable.setRowHeight(22);
        guessedLettersTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        guessedLettersTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        guessedLettersTable.setFillsViewportHeight(true);

        JScrollPane tableScrollPane = new JScrollPane(guessedLettersTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Tahmin Edilen Harfler"));
        tableScrollPane.setPreferredSize(new Dimension(200, 180));

        middlePanel.add(tableScrollPane, BorderLayout.CENTER);

        centerPanel.add(imagePanel, BorderLayout.WEST);
        centerPanel.add(middlePanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        bottomPanel.add(new JLabel("Harf Tahmini:"), gbc);

        gbc.gridx = 1;
        letterGuessField = new JTextField(5);
        bottomPanel.add(letterGuessField, gbc);

        gbc.gridx = 2;
        guessLetterButton = new JButton("Tahmin Et");
        guessLetterButton.setBackground(new Color(70, 130, 180));
        guessLetterButton.setForeground(Color.WHITE);
        guessLetterButton.setFocusPainted(false);
        guessLetterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guessLetter();
            }
        });
        bottomPanel.add(guessLetterButton, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        bottomPanel.add(new JLabel("Kelime Tahmini:"), gbc);

        gbc.gridx = 1;
        wordGuessField = new JTextField(15);
        bottomPanel.add(wordGuessField, gbc);

        gbc.gridx = 2;
        guessWordButton = new JButton("Tahmin Et");
        guessWordButton.setBackground(new Color(70, 130, 180));
        guessWordButton.setForeground(Color.WHITE);
        guessWordButton.setFocusPainted(false);
        guessWordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guessWord();
            }
        });
        bottomPanel.add(guessWordButton, gbc);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setGameControlsEnabled(false);
    }

    public void startNewGame() {
        if (timer != null) {
            timer.stop();
        }

        guessedLetters.clear();
        guessedLettersTableModel.setRowCount(0);

        currentWord = selectRandomWord();
        guessedWord = new char[currentWord.length()];
        for (int i = 0; i < guessedWord.length; i++) {
            guessedWord[i] = '*';
        }

        wrongAttempts = 0;
        seconds = 0;
        gameActive = true;

        updateLetterLabels();
        updateImage();
        messageLabel.setText("Oyun başladı! Kelimede " + currentWord.length() + " harf var.");
        messageLabel.setForeground(new Color(0, 100, 0));
        letterGuessField.setText("");
        wordGuessField.setText("");
        setGameControlsEnabled(true);

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seconds++;
                timerLabel.setText("Süre: " + seconds + " saniye");
            }
        });
        timer.start();

        Main.writeLog("Yeni oyun başlatıldı - Kelime: " + currentWord);
    }

    private String selectRandomWord() {
        List<String> words = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(Constants.WORDS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().length() >= 6) {
                    words.add(line.trim().toUpperCase());
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Hata oluştu: ", e);
            words.add("BİLGİSAYAR");
            words.add("PROGRAMLAMA");
            words.add("YAZILIM");
        }

        if (words.isEmpty()) {
            return "BİLGİSAYAR";
        }

        Random random = new Random();
        return words.get(random.nextInt(words.size()));
    }

    private void updateLetterLabels() {
        lettersPanel.removeAll();
        letterLabels = new JLabel[guessedWord.length];

        for (int i = 0; i < guessedWord.length; i++) {
            letterLabels[i] = new JLabel(String.valueOf(guessedWord[i]), SwingConstants.CENTER);
            letterLabels[i].setFont(new Font("Arial", Font.BOLD, 22));
            letterLabels[i].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
            letterLabels[i].setPreferredSize(new Dimension(38, 48));
            letterLabels[i].setOpaque(true);
            if (guessedWord[i] != '*') {
                letterLabels[i].setBackground(new Color(144, 238, 144));
            } else {
                letterLabels[i].setBackground(Color.WHITE);
            }
            lettersPanel.add(letterLabels[i]);
        }

        lettersPanel.revalidate();
        lettersPanel.repaint();
    }

    private void updateImage() {
        String[] extensions = {".png", ".jpg", ".jpeg", ".gif"};
        boolean loaded = false;

        for (String ext : extensions) {
            String imagePath = Constants.IMAGE_DIR + "adam" + wrongAttempts + ext;
            File imageFile = new File(imagePath);
            if (imageFile.exists() && imageFile.canRead()) {
                try {
                    ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());
                    if (icon.getIconWidth() > 0) {
                        Image scaledImage = icon.getImage().getScaledInstance(210, 210, Image.SCALE_SMOOTH);
                        imageLabel.setIcon(new ImageIcon(scaledImage));
                        imageLabel.setText("");
                        loaded = true;
                        break;
                    }
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Hata oluştu: ", e);
                }
            }
        }

        if (!loaded) {
            imageLabel.setIcon(null);
            imageLabel.setText("<html><center>Resim bulunamadı:<br><small>" 
                    + Constants.IMAGE_DIR + "adam" + wrongAttempts + ".png</small></center></html>");
            imageLabel.setFont(new Font("Arial", Font.PLAIN, 11));
            imageLabel.setForeground(Color.RED);
        }
    }

    private void guessLetter() {
        if (!gameActive) return;

        String guess = letterGuessField.getText().trim().toUpperCase();

        if (guess.length() != 1) {
            messageLabel.setText("Lütfen tek bir harf girin!");
            messageLabel.setForeground(Color.RED);
            return;
        }

        char letter = guess.charAt(0);

        if (!Character.isLetter(letter)) {
            messageLabel.setText("Lütfen geçerli bir harf girin!");
            messageLabel.setForeground(Color.RED);
            return;
        }

        if (guessedLetters.contains(letter)) {
            messageLabel.setText("'" + letter + "' harfini zaten denediniz!");
            messageLabel.setForeground(new Color(200, 100, 0));
            letterGuessField.setText("");
            return;
        }

        guessedLetters.add(letter);

        boolean found = false;
        for (int i = 0; i < currentWord.length(); i++) {
            if (currentWord.charAt(i) == letter) {
                guessedWord[i] = letter;
                found = true;
            }
        }

        if (found) {
            guessedLettersTableModel.addRow(new Object[]{String.valueOf(letter), "Doğru"});
            updateLetterLabels();
            messageLabel.setText("'" + letter + "' harfi kelimede var!");
            messageLabel.setForeground(new Color(0, 130, 0));

            if (isWordComplete()) {
                endGame(true);
            }
        } else {
            wrongAttempts++;
            guessedLettersTableModel.addRow(new Object[]{String.valueOf(letter), "Yanlış"});
            updateImage();
            messageLabel.setText("'" + letter + "' harfi yok! Yanlış: " + wrongAttempts + "/11");
            messageLabel.setForeground(Color.RED);

            if (wrongAttempts >= 11) {
                endGame(false);
            }
        }

        letterGuessField.setText("");
        letterGuessField.requestFocus();

        int lastRow = guessedLettersTable.getRowCount() - 1;
        if (lastRow >= 0) {
            guessedLettersTable.scrollRectToVisible(guessedLettersTable.getCellRect(lastRow, 0, true));
        }
    }

    private void guessWord() {
        if (!gameActive) return;

        String guess = wordGuessField.getText().trim().toUpperCase();

        if (guess.isEmpty()) {
            messageLabel.setText("Lütfen bir kelime girin!");
            messageLabel.setForeground(Color.RED);
            return;
        }

        if (guess.equals(currentWord)) {
            for (int i = 0; i < currentWord.length(); i++) {
                guessedWord[i] = currentWord.charAt(i);
            }
            updateLetterLabels();
            endGame(true);
        } else {
            wrongAttempts++;
            updateImage();
            messageLabel.setText("Yanlış kelime! Yanlış deneme: " + wrongAttempts + "/11");
            messageLabel.setForeground(Color.RED);

            if (wrongAttempts >= 11) {
                endGame(false);
            }
        }

        wordGuessField.setText("");
        wordGuessField.requestFocus();
    }

    private boolean isWordComplete() {
        for (char c : guessedWord) {
            if (c == '*') {
                return false;
            }
        }
        return true;
    }

    private void endGame(boolean won) {
        gameActive = false;
        timer.stop();
        setGameControlsEnabled(false);

        if (won) {
            messageLabel.setText("TEBRİKLER! Kazandınız! Kelime: " + currentWord + " | Süre: " + seconds + " sn");
            messageLabel.setForeground(new Color(0, 128, 0));
        } else {
            messageLabel.setText("Kaybettiniz! Doğru kelime: " + currentWord + " | Süre: " + seconds + " sn");
            messageLabel.setForeground(Color.RED);
            updateImage();
        }

        saveGameResult(won);
        Main.writeLog("Oyun bitti - Sonuç: " + (won ? "KAZANDI" : "KAYBETTI") + ", Süre: " + seconds + " saniye");
    }

    private void saveGameResult(boolean won) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(Constants.GAMES_FILE, true))) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String result = won ? "Kazandı" : "Kaybetti";
            bw.write("[" + now.format(formatter) + "] - Kelime: " + currentWord
                    + " - Süre: " + seconds + " saniye - Sonuç: " + result);
            bw.newLine();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Hata oluştu: ", e);
        }
    }

    private void setGameControlsEnabled(boolean enabled) {
        letterGuessField.setEnabled(enabled);
        wordGuessField.setEnabled(enabled);
        guessLetterButton.setEnabled(enabled);
        guessWordButton.setEnabled(enabled);
    }
}