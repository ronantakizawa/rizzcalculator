package main;
import controller.ChatController;
//import controller.GameStateController;
import view.ChatView;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Create a new JFrame for the application window
        JFrame frame = new JFrame("Chat Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        
        // Create instances of the View and the GameStateController
        ChatView chatView = new ChatView();
        //GameStateController gameStateController = new GameStateController();  // Assuming this exists

        // Create the ChatController with the view and game state controller
        ChatController chatController = new ChatController(chatView);
        
        // Set the chat controller in the view (optional if needed in your design)
        chatView.setController(chatController);
        
        // Add the chat view to the frame
        frame.add(chatView, BorderLayout.CENTER);

        // Set the frame visibility
        frame.setVisible(true);
    }
}
