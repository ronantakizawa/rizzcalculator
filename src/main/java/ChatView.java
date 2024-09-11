package view;

import controller.ChatController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ChatView extends JPanel {
    private JTextArea chatArea;
    private JTextField userInput;
    private JButton sendButton, exitButton;
    private ChatController chatController;// Add controller field

    public ChatView() {
        // Set layout
        this.setLayout(new BorderLayout());

        // Create chat area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);

        // Create input field and buttons
        userInput = new JTextField(30);
        sendButton = new JButton("Send");
        exitButton = new JButton("Exit");

        // Panel for input field and buttons
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(userInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.add(exitButton, BorderLayout.WEST);

        // Add components to panel
        this.add(chatScrollPane, BorderLayout.CENTER);
        this.add(inputPanel, BorderLayout.SOUTH);
    }

    // Setter for ChatController
    public void setController(ChatController controller) {
        this.chatController = controller;
    }

    // Add action listener for the send button
    public void addSendButtonListener(ActionListener listener) {
        sendButton.addActionListener(listener);
    }

    // Add action listener for the input field (e.g., pressing Enter)
    public void addInputFieldListener(ActionListener listener) {
        userInput.addActionListener(listener);
    }

    // Add action listener for the exit button
    public void addExitButtonListener(ActionListener listener) {
        exitButton.addActionListener(listener);
    }

    // Method to get user input
    public String getUserInput() {
        return userInput.getText();
    }

    // Method to clear input field
    public void clearInput() {
        userInput.setText("");
    }

    // Method to display a message in the chat area
    public void displayMessage(String sender, String message) {
        chatArea.append(sender + ": " + message + "\n");
    }
}