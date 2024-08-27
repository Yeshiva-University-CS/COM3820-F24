package edu.yu.parallel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private final JPanel innerPanelContainer;
    private final SquareLayout squareLayout;
    private ActionListener spinHandler;
    private ActionListener resetHandler;
    private ActionListener closeHandler;
    private JButton spinButton;

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
        spinButton.addActionListener(e -> {
            spinReels(e);
        });
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

    // Method to handle the Spin button action
    private void spinReels(ActionEvent e) {
        if (spinHandler != null) {
            spinButton.setEnabled(false); // Disable the spin button
            spinHandler.actionPerformed(e);
        }
    }

    // Method to handle the Reset button action
    private void resetReels(ActionEvent e) {
        if (resetHandler != null) {
            resetHandler.actionPerformed(e);
        }
    }

    // Method to handle the Close button action
    private void closeHandler(ActionEvent e) {
        if (closeHandler != null) {
            closeHandler.actionPerformed(e);
        }
        dispose();
    }

    // Method to set the spin handler
    public void setSpinHandler(ActionListener spinHandler) {
        this.spinHandler = spinHandler;
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

    // Method to renable Spin button
    public void enableSpinButton() {
        spinButton.setEnabled(true);
    }
}
