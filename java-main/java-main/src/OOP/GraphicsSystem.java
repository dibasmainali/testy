package OOP;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.nio.file.StandardOpenOption;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import uk.ac.leedsbeckett.oop.LBUGraphics;

public class GraphicsSystem extends LBUGraphics {
    private boolean fill;
    private Color penColor;
    private final String[] VALID_COMMANDS = {
        "about", "penup", "pendown", "turnleft", "turnright", "forward", "backward", 
        "reset", "new", "black", "green", "red", "white", "load", "save", "help", 
        "circle", "etriangle", "triangle", "rectangle", "pencolor", "square", 
        "random", "fill", "display"
    };
    public ArrayList<String> commandHistory = new ArrayList<>();
    private static final String DEFAULT_SAVE_FOLDER = System.getProperty("user.home") + "/turtle_graphics/";

    public GraphicsSystem() {
        JFrame mainFrame = new JFrame("Turtle Graphics System");       
        mainFrame.setLayout(new FlowLayout());  
        mainFrame.add(this);
        mainFrame.pack();
        mainFrame.setVisible(true);
        turnLeft();                 
        penDown();
        
        // Create default save folder if it doesn't exist
        new File(DEFAULT_SAVE_FOLDER).mkdirs();
    }

    @Override
    public void about() {
        clear();
        reset();
        super.about();
        getGraphicsContext().drawString("Anil Acharya", 250, 350);
        JLabel name = new JLabel("ANIL ACHARYA");
        name.setBounds(400, 400, 400, 400);
        name.setForeground(Color.green);
        add(name);
        displayMessage("LBU GRAPHICS V4.5");
    }

    public void setPenColor(int red, int green, int blue) {
        if (red >= 0 && red <= 255 && green >= 0 && green <= 255 && blue >= 0 && blue <= 255) {
            this.penColor = new Color(red, green, blue);
            setPenColour(penColor);
        } else {
            JOptionPane.showMessageDialog(null, "RGB values must be between 0-255", "Invalid Color", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void processCommand(String command) {
        try {
            command = command.trim().toLowerCase();
            
            if (command.contains(" ")) {
                String[] parts = command.split(" ");
                String cmd = parts[0];
                switch (cmd) {
                    case "forward":
                        forward(Integer.parseInt(parts[1]));
                        commandHistory.add(command);
                        break;
                    case "turnleft":
                        turnLeft(Integer.parseInt(parts[1]));
                        commandHistory.add(command);
                        break;
                    case "turnright":
                        turnRight(Integer.parseInt(parts[1]));
                        commandHistory.add(command);
                        break;
                    case "backward":
                        forward(-Integer.parseInt(parts[1]));
                        commandHistory.add(command);
                        break;
                    case "circle":
                        circle(Integer.parseInt(parts[1]));
                        commandHistory.add(command);
                        break;
                    case "triangle":
                        if (parts.length == 4) {
                            int a = Integer.parseInt(parts[1]);
                            int b = Integer.parseInt(parts[2]);
                            int c = Integer.parseInt(parts[3]);
                            if (isValidTriangle(a, b, c)) {
                                triangle(a, b, c);
                                commandHistory.add(command);
                            } else {
                                JOptionPane.showMessageDialog(null, "Invalid triangle sides", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } else if (parts.length == 2) {
                            triangle(Integer.parseInt(parts[1]));
                            commandHistory.add(command);
                        }
                        break;
                    case "rectangle":
                        rectangle(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                        commandHistory.add(command);
                        break;
                    case "square":
                        square(Integer.parseInt(parts[1]));
                        commandHistory.add(command);
                        break;
                    case "pencolor":
                        if (parts.length == 4) {
                            setPenColor(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
                            commandHistory.add(command);
                        }
                        break;
                    default:
                        showInvalidCommandError();
                }
            } else {
                switch (command) {
                    case "about":
                        about();
                        commandHistory.add(command);
                        break;
                    case "penup":
                        penUp();
                        commandHistory.add(command);
                        break;
                    case "pendown":
                        penDown();
                        commandHistory.add(command);
                        break;
                    case "clear":
                        clear();
                        commandHistory.add(command);
                        break;
                    case "load":
                        load();
                        commandHistory.add(command);
                        break;
                    case "save":
                        save();
                        commandHistory.add(command);
                        break;
                    case "reset":
                        reset();
                        penDown();
                        commandHistory.add(command);
                        break;
                    case "new":
                        clear();
                        commandHistory.add(command);
                        break;
                    case "black":
                        setPenColour(Color.black);
                        commandHistory.add(command);
                        break;
                    case "red":
                        setPenColour(Color.red);
                        commandHistory.add(command);
                        break;
                    case "green":
                        setPenColour(Color.green);
                        commandHistory.add(command);
                        break;
                    case "white":
                        setPenColour(Color.white);
                        commandHistory.add(command);
                        break;
                    case "help":
                        showHelp();
                        break;
                    case "display":
                        displayImage();
                        break;
                    case "random":
                        randomColor();
                        commandHistory.add(command);
                        break;
                    case "fill":
                        fill = !fill;
                        displayMessage("Fill is now " + (fill ? "ON" : "OFF"));
                        commandHistory.add(command);
                        break;
                    default:
                        showInvalidCommandError();
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid number format", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ArrayIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(null, "Missing parameters for command", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isValidTriangle(int a, int b, int c) {
        return a + b > c && a + c > b && b + c > a;
    }

    private void triangle(int sideA, int sideB, int sideC) {
        Graphics canvas = getGraphicsContext();
        canvas.setColor(penColor != null ? penColor : Color.BLUE);
        
        int x = xPos;
        int y = yPos;
        int[] xPoints = {x, x + sideA, x + sideB};
        int[] yPoints = {y, y, y - sideC};
        
        if (fill) {
            canvas.fillPolygon(xPoints, yPoints, 3);
        } else {
            canvas.drawPolygon(xPoints, yPoints, 3);
        }
    }

    private void triangle(int side) {
        Graphics canvas = getGraphicsContext();
        canvas.setColor(penColor != null ? penColor : Color.PINK);
        
        int height = (int) (side * Math.sqrt(3) / 2);
        int[] xPoints = {xPos, xPos + side/2, xPos - side/2};
        int[] yPoints = {yPos, yPos + height, yPos + height};
        
        if (fill) {
            canvas.fillPolygon(xPoints, yPoints, 3);
        } else {
            canvas.drawPolygon(xPoints, yPoints, 3);
        }
    }

    @Override
    public void circle(int radius) {
        Graphics canvas = getGraphicsContext();
        canvas.setColor(penColor != null ? penColor : Color.RED);
        
        if (fill) {
            canvas.fillOval(xPos - radius, yPos - radius, radius*2, radius*2);
        } else {
            canvas.drawOval(xPos - radius, yPos - radius, radius*2, radius*2);
        }
    }

    private void rectangle(int width, int height) {
        Graphics canvas = getGraphicsContext();
        canvas.setColor(penColor != null ? penColor : Color.YELLOW);
        
        if (fill) {
            canvas.fillRect(xPos - width/2, yPos, width, height);
        } else {
            canvas.drawRect(xPos - width/2, yPos, width, height);
        }
    }

    private void square(int side) {
        Graphics canvas = getGraphicsContext();
        canvas.setColor(penColor != null ? penColor : Color.BLUE);
        
        if (fill) {
            canvas.fillRect(xPos - side/2, yPos, side, side);
        } else {
            canvas.drawRect(xPos - side/2, yPos, side, side);
        }
    }

    public void save() {
        String[] options = {"Save Commands", "Save Image", "Cancel"};
        int choice = JOptionPane.showOptionDialog(null, "What would you like to save?", "Save", 
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        
        if (choice == 0) {
            saveCommands();
        } else if (choice == 1) {
            saveImage();
        }
    }

    private void saveCommands() {
        JFileChooser fileChooser = new JFileChooser(DEFAULT_SAVE_FOLDER);
        fileChooser.setDialogTitle("Save Commands");
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            Path filePath = Paths.get(file.getAbsolutePath());
            
            try {
                Files.write(filePath, commandHistory, StandardOpenOption.CREATE);
                JOptionPane.showMessageDialog(null, "Commands saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error saving commands: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveImage() {
        JFileChooser fileChooser = new JFileChooser(DEFAULT_SAVE_FOLDER);
        fileChooser.setDialogTitle("Save Image");
        fileChooser.setSelectedFile(new File("turtle_graphics.png"));
        
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                Robot robot = new Robot();
                Rectangle captureArea = new Rectangle(getLocationOnScreen(), getSize());
                BufferedImage image = robot.createScreenCapture(captureArea);
                ImageIO.write(image, "png", file);
                JOptionPane.showMessageDialog(null, "Image saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (AWTException | IOException e) {
                JOptionPane.showMessageDialog(null, "Error saving image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void load() {
        String[] options = {"Load Commands", "Load Image", "Cancel"};
        int choice = JOptionPane.showOptionDialog(null, "What would you like to load?", "Load", 
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        
        if (choice == 0) {
            loadCommands();
        } else if (choice == 1) {
            loadImage();
        }
    }

    private void loadCommands() {
        JFileChooser fileChooser = new JFileChooser(DEFAULT_SAVE_FOLDER);
        fileChooser.setDialogTitle("Load Commands");
        
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    processCommand(scanner.nextLine());
                }
                JOptionPane.showMessageDialog(null, "Commands loaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(null, "File not found: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadImage() {
        JFileChooser fileChooser = new JFileChooser(DEFAULT_SAVE_FOLDER);
        fileChooser.setDialogTitle("Load Image");
        
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                JFrame frame = new JFrame("Loaded Image");
                frame.setContentPane(new JLabel(new ImageIcon(ImageIO.read(file))));
                frame.pack();
                frame.setVisible(true);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error loading image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void displayImage() {
        JFileChooser fileChooser = new JFileChooser(DEFAULT_SAVE_FOLDER);
        fileChooser.setDialogTitle("Select Image to Display");
        
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                JFrame frame = new JFrame("Display Image");
                frame.setContentPane(new JLabel(new ImageIcon(ImageIO.read(file))));
                frame.pack();
                frame.setVisible(true);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error displaying image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void randomColor() {
        Random random = new Random();
        setPenColor(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        displayMessage("Random color applied");
    }

    private void showHelp() {
        String helpText = """
            TURTLE GRAPHICS SYSTEM COMMANDS
            
            PEN COMMANDS:
              penup       - Lift the pen (stop drawing)
              pendown     - Lower the pen (start drawing)
              pencolor r g b - Set pen color (r,g,b values 0-255)
              black       - Set pen to black
              red         - Set pen to red
              green       - Set pen to green
              white       - Set pen to white
              random      - Set random pen color
            
            MOVEMENT COMMANDS:
              forward n   - Move forward n pixels
              backward n  - Move backward n pixels
              turnleft d  - Turn left d degrees
              turnright d - Turn right d degrees
            
            SHAPE COMMANDS:
              circle r    - Draw circle with radius r
              square s    - Draw square with side s
              rectangle w h - Draw rectangle with width w and height h
              triangle s  - Draw equilateral triangle with side s
              triangle a b c - Draw triangle with sides a, b, c
            
            SCREEN COMMANDS:
              clear       - Clear the drawing
              reset       - Reset turtle position
              new         - Clear the drawing (alias for clear)
              fill        - Toggle fill mode for shapes
              save        - Save commands or image
              load        - Load commands or image
              display     - Display an image
            
            OTHER COMMANDS:
              about       - Show about information
              help        - Show this help message
            """;
        
        JOptionPane.showMessageDialog(null, helpText, "Help", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showInvalidCommandError() {
        JOptionPane.showMessageDialog(null, 
            "Invalid command. Type 'help' for a list of valid commands.", 
            "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        new GraphicsSystem();
    }
}