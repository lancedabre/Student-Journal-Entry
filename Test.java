import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.sql.*;

public class Test {

    public static void main(String[] args) {
        // ==============================
        // DATABASE SETUP
        // ==============================
        setupDatabase();

        // Show login dialog
        if (showLoginDialog()) {
            createMainApplication();
        } else {
            System.exit(0);
        }
    }

    // ==============================
    // DATABASE SETUP
    // ==============================
    private static void setupDatabase() {
        String url = "jdbc:sqlite:mydatabase.db"; // database file will be created automatically
        String sql = """
            CREATE TABLE IF NOT EXISTS journal (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                content TEXT NOT NULL
            );
            """;

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Database and journal table ready.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==============================
    // LOGIN DIALOG
    // ==============================
    private static boolean showLoginDialog() {
        JDialog loginDialog = new JDialog((Frame) null, "Login", true);
        loginDialog.setSize(350, 250);
        loginDialog.setLocationRelativeTo(null);
        loginDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username field
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField usernameField = new JTextField(15);
        panel.add(usernameField, gbc);

        // Password field
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPasswordField passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        // Gender dropdown
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        String[] genders = {"Select Gender", "Male", "Female", "Other", "Prefer not to say"};
        JComboBox<String> genderCombo = new JComboBox<>(genders);
        panel.add(genderCombo, gbc);

        // Remember me checkbox
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JCheckBox rememberCheckbox = new JCheckBox("Remember me");
        panel.add(rememberCheckbox, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton loginButton = new JButton("Login");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);

        loginDialog.add(panel);

        final boolean[] loginSuccess = {false};

        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(loginDialog, "Please enter username and password!", "Login Error", JOptionPane.ERROR_MESSAGE);
            } else if (genderCombo.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(loginDialog, "Please select your gender!", "Login Error", JOptionPane.ERROR_MESSAGE);
            } else {
                loginSuccess[0] = true;
                JOptionPane.showMessageDialog(loginDialog, "Welcome, " + username + "!", "Login Successful", JOptionPane.INFORMATION_MESSAGE);
                loginDialog.dispose();
            }
        });

        cancelButton.addActionListener(e -> loginDialog.dispose());

        passwordField.addActionListener(e -> loginButton.doClick());

        loginDialog.setVisible(true);
        return loginSuccess[0];
    }

    // ==============================
    // SHOW ALL ENTRIES IN DB
    // ==============================
    public static void showAllEntries() {
        String sql = "SELECT * FROM journal";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:mydatabase.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("=== Journal Entries in Database ===");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + ": " + rs.getString("content"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==============================
    // MAIN APPLICATION
    // ==============================
    private static void createMainApplication() {
        JFrame frame = new JFrame("Journal Entry");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem showItem = new JMenuItem("Show All");
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(showItem);
        fileMenu.addSeparator();

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    frame,
                    "Save and exit?",
                    "Confirm Exit",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        JTextArea textArea = new JTextArea(15, 30);
        textArea.setLineWrap(true);
        JScrollPane textScrollPane = new JScrollPane(textArea);

        JButton addButton = new JButton("Add to Explorer");

        DefaultListModel<String> explorerModel = new DefaultListModel<>();
        JList<String> explorerList = new JList<>(explorerModel);
        JScrollPane explorerScroll = new JScrollPane(explorerList);

        JTabbedPane explorerTabs = new JTabbedPane();
        explorerTabs.addTab("Explorer", explorerScroll);

        addButton.addActionListener(e -> {
            String input = textArea.getText().trim();
            if (!input.isEmpty()) {
                explorerModel.addElement(input);
                textArea.setText("");
            }
        });

        explorerList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedEntry = explorerList.getSelectedValue();
                if (selectedEntry != null) {
                    textArea.setText(selectedEntry);
                }
            }
        });

        // ==============================
        // SAVE ENTRIES TO DATABASE
        // ==============================
        saveItem.addActionListener(e -> {
            String sql = "INSERT INTO journal(content) VALUES(?)";
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:mydatabase.db");
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                for (int i = 0; i < explorerModel.size(); i++) {
                    ps.setString(1, explorerModel.get(i));
                    ps.executeUpdate();
                }

                JOptionPane.showMessageDialog(frame, "Journal entries saved to database!", "Save", JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error saving entries!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        openItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Open functionality not implemented yet.", "Open", JOptionPane.INFORMATION_MESSAGE);
        });

        // Show all entries in DB
        showItem.addActionListener(e -> {
            showAllEntries();
            JOptionPane.showMessageDialog(frame, "Check console for database entries.", "Show All", JOptionPane.INFORMATION_MESSAGE);
        });

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(textScrollPane, BorderLayout.CENTER);
        rightPanel.add(addButton, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, explorerTabs, rightPanel);
        splitPane.setDividerLocation(200);

        frame.add(splitPane);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        frame,
                        "Save and exit?",
                        "Confirm Exit",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    frame.dispose();
                }
            }
        });

        frame.setVisible(true);
    }
}