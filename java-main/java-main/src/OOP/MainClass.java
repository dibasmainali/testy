package OOP;

import java.awt.FlowLayout;
import javax.swing.JFrame;
import uk.ac.leedsbeckett.oop.LBUGraphics;

public class MainClass extends LBUGraphics {
    private GraphicsSystem graphicsSystem;

    public static void main(String[] args) {
        // Create and show the main application window
        new MainClass();
    }

    public MainClass() {
        // Set up the main frame
        JFrame mainFrame = new JFrame("Turtle Graphics Application");
        mainFrame.setLayout(new FlowLayout());
        
        // Create and add the graphics system
        graphicsSystem = new GraphicsSystem();
        mainFrame.add(graphicsSystem);
        
        // Configure frame properties
        mainFrame.pack();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
        
        // Show about information
        graphicsSystem.about();
    }

    @Override
    public void processCommand(String command) {
        // Delegate command processing to the GraphicsSystem instance
        graphicsSystem.processCommand(command);
    }
}