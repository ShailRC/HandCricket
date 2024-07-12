package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Random;

public class Game extends JFrame {
    private JPanel userPanel, compPanel;
    private JTextField inputField;
    private JButton submitBtn, resetBtn;
    private JLabel userLbl, compLbl, scoreLbl;
    private Random rand;
    private int usrScore, compScore, targetScore;
    private boolean usrBatting, firstInningDone;

    public Game() {
        setTitle("Hand Cricket Game");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        createMenuBar(); // Add this line to create the menu bar with the exit option

        userPanel = new JPanel();
        compPanel = new JPanel();
        userLbl = new JLabel();
        compLbl = new JLabel();

        userPanel.add(userLbl);
        compPanel.add(compLbl);

        add(userPanel, BorderLayout.WEST);
        add(compPanel, BorderLayout.EAST);

        JPanel inputPanel = new JPanel();
        inputField = new JTextField(5);
        submitBtn = new JButton("Submit");
        resetBtn = new JButton("Reset Game");
        scoreLbl = new JLabel("Score: 0 - 0");

        inputPanel.add(new JLabel("Enter your number (0-6): "));
        inputPanel.add(inputField);
        inputPanel.add(submitBtn);
        inputPanel.add(resetBtn);

        add(inputPanel, BorderLayout.SOUTH);
        add(scoreLbl, BorderLayout.NORTH);

        rand = new Random();
        usrScore = 0;
        compScore = 0;
        usrBatting = true;
        firstInningDone = false;

        // Conduct toss at the start
        conductToss();

        submitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleUserInput();
            }
        });

        resetBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetGame();
            }
        });
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitMenuItem = new JMenuItem("Exit");

        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    private void conductToss() {
        String[] opts = {"Heads", "Tails"};
        int usrChoice = JOptionPane.showOptionDialog(this, "Choose Heads or Tails", "Toss", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opts, opts[0]);
        int tossRes = rand.nextInt(2); // 0 for Heads, 1 for Tails

        String tossAnimFile = (tossRes == 0) ? "h.gif" : "t.gif";
        displayTossAnimation(tossAnimFile); // Display toss animation

        // Pause briefly to allow the user to see the animation
        try {
            Thread.sleep(2000); // Adjust the delay time as needed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (usrChoice == tossRes) {
            // User wins the toss
            String[] decOpts = {"Batting", "Bowling"};
            int usrDec = JOptionPane.showOptionDialog(this, "You won the toss! What do you choose first?", "Toss Result", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, decOpts, decOpts[0]);
            usrBatting = (usrDec == 0); // 0 for Batting, 1 for Bowling
        } else {
            // Computer wins the toss
            boolean compBatting = rand.nextBoolean();
            JOptionPane.showMessageDialog(this, "Computer won the toss and decides to " + (compBatting ? "bat" : "bowl") + " first.");
            usrBatting = !compBatting; // If computer is batting, user is bowling and vice versa
        }
    }

    private void displayTossAnimation(String file) {
        String path = "/" + file;
        URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            Icon icon = new ImageIcon(imgURL);
            JOptionPane.showMessageDialog(this, new JLabel(icon), "Toss Animation", JOptionPane.PLAIN_MESSAGE);
        } else {
            System.err.println("Error: File " + path + " not found.");
        }
    }

    private void handleUserInput() {
        try {
            int usrNum = Integer.parseInt(inputField.getText());
            if (usrNum < 0 || usrNum > 6) {
                JOptionPane.showMessageDialog(this, "Please enter a number between 0 and 6");
                return;
            }
            int compNum = rand.nextInt(6) + 1;
    
            displayHandAnimation(userLbl, usrNum);
            displayHandAnimation(compLbl, compNum);
    
            if (usrNum == compNum) {
                if (usrBatting) {
                    usrBatting = false;
                    if (!firstInningDone) {
                        firstInningDone = true;
                        targetScore = usrScore;
                        JOptionPane.showMessageDialog(this, "You are out! You set a target of " + targetScore + " runs.");
                    } else {
                        determineWinner();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Computer is out!");
                    // Check if it's the first ball of the second inning
                    if (!firstInningDone) {
                        usrBatting = true; // Switch to user batting
                        JOptionPane.showMessageDialog(this, "Now it's your turn to bat.");
                    } else {
                        determineWinner();
                    }
                }
            } else {
                if (usrBatting) {
                    usrScore += usrNum;
                } else {
                    compScore += compNum;
                    if (firstInningDone && compScore >= targetScore) {
                        JOptionPane.showMessageDialog(this, "Computer wins by scoring " + compScore + " runs!");
                        resetGame();
                        return;
                    }
                }
            }
            updateScore();
            // Check if computer has completed its turn in the second innings
            if (!usrBatting && firstInningDone && compScore < targetScore) { 
                JOptionPane.showMessageDialog(this, "Now it's your turn to bat.");
                usrBatting = true; // Switch to user batting
            }
    
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter a number between 0 and 6.");
        }
    }
    
    private void displayHandAnimation(JLabel lbl, int num) {
        String path = "/" + num + ".gif";
        URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            Icon icon = new ImageIcon(imgURL);
            lbl.setIcon(icon);
        } else {
            System.err.println("Error: File " + path + " not found.");
        }
    }

    private void updateScore() {
        scoreLbl.setText("Score: " + usrScore + " - " + compScore);
    }

    private void determineWinner() {
        firstInningDone = false;
        if (usrScore > compScore) {
            JOptionPane.showMessageDialog(this, "You win! Final Score: " + usrScore + " - " + compScore);
        } else if (usrScore < compScore) {
            JOptionPane.showMessageDialog(this, "Computer wins! Final Score: " + usrScore + " - " + compScore);
        } else {
            JOptionPane.showMessageDialog(this, "It's a tie! Final Score: " + usrScore + " - " + compScore);
        }
        resetGame();
    }

    private void resetGame() {
        usrScore = 0;
        compScore = 0;
        targetScore = 0;
        usrBatting = true;
        firstInningDone = false;
        updateScore();
        userLbl.setIcon(null);
        compLbl.setIcon(null);
        JOptionPane.showMessageDialog(this, "Game has been reset.");
        conductToss(); // Conduct toss again after reset
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Game().setVisible(true);
            }
        });
    }
}