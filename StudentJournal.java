import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

public class Test {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            if (showLoginDialog()) {
                createMainApplication();
            } else {
                System.exit(0);
            }
        });
    }

    private static boolean showLoginDialog() {
        JDialog loginDialog = new JDialog((JFrame) null, "Login", true);
        loginDialog.setSize(350, 250);
        loginDialog.setLocationRelativeTo(null);
        loginDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField usernameField = new JTextField(15);
        panel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPasswordField passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        // Gender
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        String[] genders = {"Select Gender", "Male", "Female", "Other", "Prefer not to say"};
        JComboBox<String> genderCombo = new JComboBox<>(genders);
        panel.add(genderCombo, gbc);

        // Remember Me
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JCheckBox rememberCheckbox = new JCheckBox("Remember me");
        panel.add(rememberCheckbox, gbc);

        // Buttons
        JButton loginButton = new JButton("Login");
        JButton cancelButton = new JButton("Cancel");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        loginDialog.add(panel);

        final boolean[] loginSuccess = {false};

        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(loginDialog, "Enter username & password");
            } else if (genderCombo.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(loginDialog, "Select gender");
            } else {
                loginSuccess[0] = true;
                JOptionPane.showMessageDialog(loginDialog, "Welcome " + username + "!");
                loginDialog.dispose();
            }
        });

        cancelButton.addActionListener(e -> loginDialog.dispose());

        passwordField.addActionListener(e -> loginButton.doClick());

        loginDialog.setVisible(true);

        return loginSuccess[0];
    }

    private static void createMainApplication() {
        JFrame frame = new JFrame("Journal Entry");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem exitItem = new JMenuItem("Exit");

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        frame.setJMenuBar(menuBar);

        JTextArea textArea = new JTextArea(15, 30);
        textArea.setLineWrap(true);
        JScrollPane textScroll = new JScrollPane(textArea);

        JButton addButton = new JButton("Add to Explorer");

        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> list = new JList<>(model);
        JScrollPane listScroll = new JScrollPane(list);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Explorer", listScroll);

        addButton.addActionListener(e -> {
            String text = textArea.getText().trim();
            if (!text.isEmpty()) {
                model.addElement(text);
                textArea.setText("");
            }
        });

        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = list.getSelectedValue();
                if (selected != null) textArea.setText(selected);
            }
        });

        saveItem.addActionListener(e -> {
            System.out.println("Saving...");
            for (int i = 0; i < model.size(); i++)
                System.out.println(model.get(i));
            JOptionPane.showMessageDialog(frame, "Saved!");
        });

        openItem.addActionListener(e ->
                JOptionPane.showMessageDialog(frame, "Open not implemented.")
        );

        exitItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(frame, "Save before exit?");
            if (confirm == JOptionPane.YES_OPTION) System.exit(0);
        });

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(textScroll, BorderLayout.CENTER);
        rightPanel.add(addButton, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabs, rightPanel);
        split.setDividerLocation(200);

        frame.add(split);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(frame, "Save before exit?");
                if (confirm == JOptionPane.YES_OPTION) frame.dispose();
            }
        });

        frame.setVisible(true);
    }
}