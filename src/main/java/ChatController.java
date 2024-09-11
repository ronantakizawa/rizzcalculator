package controller;

import view.ChatView;
import model.ChatModel;

public class ChatController {
    private ChatView view;
    private ChatModel model;
    //private GameStateController gameStateController; // Add GameStateController

    public ChatController(ChatView view) {
        this.view = view;
        this.model = new ChatModel("Francisco", "Yeat");
        //this.gameStateController = gameStateController;  // Store the GameStateController

        // Set up listeners for the user input and send button
        view.addSendButtonListener(e -> handleUserMessage());

        // Simplified with Lambda for Input Field (Enter key press)
        view.addInputFieldListener(e -> handleUserMessage());

        // Modify the exit button listener to go back to MapView
        view.addExitButtonListener(e -> {
            //gameStateController.showMapView();   // Switch back to MapView
        });

        // Display the chat history before starting a new session
        loadChatHistory();
    }


    protected void handleUserMessage() {
        String userMessage = view.getUserInput();
        if (!userMessage.isEmpty()) {
            model.saveMessage("User", userMessage);
            view.displayMessage("User", userMessage);

            // Get a CPU response
            String cpuResponse = model.getCPUResponse(userMessage);
            model.saveMessage(model.getCpuName(), cpuResponse);
            view.displayMessage(model.getCpuName(), cpuResponse);

            view.clearInput();  // Clear input field after sending
        }
    }

    protected void loadChatHistory() {
        model.displayChatHistory(view);
    }
}