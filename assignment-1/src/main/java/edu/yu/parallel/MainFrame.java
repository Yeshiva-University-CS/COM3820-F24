package edu.yu.parallel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private final JPanel innerPanelContainer;
    private final SquareLayout squareLayout;
    private ActionListener spinHandler;
    private ActionListener stopHandler;
    private ActionListener resetHandler;
    private ActionListener closeHandler;
    private JButton spinButton;
    private boolean isSpinning = false;  // Flag to track the state of the spin button

    public MainFrame(String title, int width, int height) {
        super(title);
        setLayout(new BorderLayout());

        // Create a panel for the inner panels with SquareLayout
        innerPanelContainer = new JPanel();
        squareLayout = new SquareLayout(10, 20);
        innerPanelContainer.setLayout(squareLayout);

        // Add the inner panel container to the center of the main frame
        add(innerPanelContainer, BorderLayout.CENTER);

        // Create a panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        // Create the Spin button and its action listener
        this.spinButton = new JButton("Spin");
        spinButton.addActionListener(this::toggleSpinStop);
        buttonPanel.add(spinButton);

        // Create the Reset button and its action listener
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(this::resetReels);
        buttonPanel.add(resetButton);

        // Create the Close button and its action listener
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(this::closeHandler);
        buttonPanel.add(closeButton);

        // Add the button panel to the bottom of the main frame
        add(buttonPanel, BorderLayout.SOUTH);

        // Set frame properties
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(width, height);
        setLocationRelativeTo(null);
    }

    // Method to add a panel to the inner panel container
    public void addPanel(InnerPanel panel) {
        innerPanelContainer.add(panel);
        innerPanelContainer.revalidate();
        innerPanelContainer.repaint();
    }

    // Method to toggle between Spin and Stop
    private void toggleSpinStop(ActionEvent e) {
        if (isSpinning) {
            stopReels(e);  // Stop the spinning
            spinButton.setText("Spin");  // Change button text back to "Spin"
        } else {
            spinReels(e);  // Start spinning
            spinButton.setText("Stop");  // Change button text to "Stop"
        }
        isSpinning = !isSpinning;  // Toggle the state
    }

    // Method to handle the Spin button action
    private void spinReels(ActionEvent e) {
        if (spinHandler != null) {
            spinHandler.actionPerformed(e);
        }
    }

    // Method to handle the Stop button action
    private void stopReels(ActionEvent e) {
        if (stopHandler != null) {
            stopHandler.actionPerformed(e);
        }
    }

    // Method to handle the Reset button action
    private void resetReels(ActionEvent e) {
        if (resetHandler != null) {
            resetHandler.actionPerformed(e);
        }
        resetSpinButton();  // Reset the spin button state as well when resetting
    }

    // Method to handle the Close button action
    private void closeHandler(ActionEvent e) {
        if (closeHandler != null) {
            closeHandler.actionPerformed(e);
        }
        dispose();
    }

    // Method to reset the spin button to its initial state
    public void resetSpinButton() {
        isSpinning = false;  // Set the spinning state to false
        spinButton.setText("Spin");  // Set the button text to "Spin"
    }

    // Method to set the spin handler
    public void setSpinHandler(ActionListener spinHandler) {
        this.spinHandler = spinHandler;
    }

    // Method to set the stop handler
    public void setStopHandler(ActionListener stopHandler) {
        this.stopHandler = stopHandler;
    }

    // Method to set the reset handler
    public void setResetHandler(ActionListener resetHandler) {
        this.resetHandler = resetHandler;
    }

    // Method to set the close handler
    public void setCloseHandler(ActionListener closeHandler) {
        this.closeHandler = closeHandler;
    }

    // Method to make the frame visible
    public void showFrame() {
        setVisible(true);
    }
}
