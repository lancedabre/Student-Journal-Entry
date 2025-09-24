import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class StudentJournal extends JFrame {

    // GUI Components
    private JTextArea entryTextArea;
    private JButton saveButton;
    private JLabel statusLabel;

    // --- NEW: Database Manager ---
    private DatabaseManager dbManager;

    public StudentJournal() {
        // --- Initialize Database Manager ---
        dbManager = new DatabaseManager();

        // --- Frame Setup ---
        setTitle("Student Daily Journal");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });

        createMenuBar();

        // --- Component Creation ---
        JLabel titleLabel = new JLabel("My Academic Log", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        entryTextArea = new JTextArea();
        entryTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        entryTextArea.setLineWrap(true);
        entryTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(entryTextArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Today's Entry:"));

        saveButton = new JButton("Save to Database");
        saveButton.setFont(new Font("SansSerif", Font.BOLD, 16));

        statusLabel = new JLabel("Ready. Write your thoughts and click save.", SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);

        // --- Add Components to Frame ---
        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- Add Action Listener for Save Button ---
        saveButton.addActionListener(e -> saveEntry());
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem saveMenuItem = new JMenuItem("Save Entry");
        saveMenuItem.addActionListener(e -> saveEntry());
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(e -> handleExit());
        
        fileMenu.add(saveMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    /**
     * Gets text from the text area and uses the DatabaseManager to save it.
     */
    private void saveEntry() {
        String entryText = entryTextArea.getText();

        if (entryText.trim().isEmpty()) {
            statusLabel.setText("Cannot save an empty entry!");
            statusLabel.setForeground(Color.RED);
            return;
        }

        // --- UPDATED: Use the DatabaseManager to save ---
        boolean success = dbManager.saveEntry(entryText);

        if (success) {
            statusLabel.setText("Entry saved successfully to the database!");
            statusLabel.setForeground(new Color(0, 128, 0)); // Dark Green
            entryTextArea.setText(""); // Clear the text area after saving
        } else {
            statusLabel.setText("Error: Could not save entry to the database.");
            statusLabel.setForeground(Color.RED);
            // The specific error is printed to the console by the DatabaseManager
        }
    }

    private void handleExit() {
        if (!entryTextArea.getText().trim().isEmpty()) {
            int choice = JOptionPane.showConfirmDialog(
                this, 
                "You have an unsaved entry. Do you want to save it before exiting?", 
                "Confirm Exit", 
                JOptionPane.YES_NO_CANCEL_OPTION, 
                JOptionPane.QUESTION_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                saveEntry();
                // Check if save was successful before closing.
                // A simple check is to see if the text area is now empty.
                if (entryTextArea.getText().trim().isEmpty()) {
                     System.exit(0);
                }
            } else if (choice == JOptionPane.NO_OPTION) {
                System.exit(0);
            }
        } else {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentJournal().setVisible(true));
    }
}

