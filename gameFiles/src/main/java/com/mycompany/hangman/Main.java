package com.mycompany.hangman;

import com.mycompany.hangman.config.Constants;
import com.mycompany.hangman.view.AuthFrame;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ygttc
 */
public class Main {
    private static Logger logger = Logger.getLogger(Main.class.getName());
    
    public static void main(String[] args) {
        File directory = new File(Constants.TXT_DIR);
        if (!directory.exists()) {
            directory.mkdirs(); 
        }

        File imageDirectory = new File(Constants.IMAGE_DIR);
        if (!imageDirectory.exists()) {
            imageDirectory.mkdirs();
        }

        File passwordFile = new File(Constants.PASSWORD_FILE);
        boolean isPasswordSet = false;

        if (passwordFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(passwordFile))) {
                String line = br.readLine();
                if (line != null && !line.trim().isEmpty()) {
                    isPasswordSet = true;
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Hata oluştu: ", e);
            }
        }

        AuthFrame authFrame = new AuthFrame(isPasswordSet);
        authFrame.setVisible(true);
    }

    public static void writeLog(String actionType) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(Constants.LOG_FILE, true))) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            bw.write("[" + now.format(formatter) + "] - " + actionType);
            bw.newLine();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Hata oluştu: ", e);
        }
    }
}