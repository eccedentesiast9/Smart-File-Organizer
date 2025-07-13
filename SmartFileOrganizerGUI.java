import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.*;

public class SmartFileOrganizerGUI extends JFrame {

    private JTextArea textArea;
    private JTextField fileNameField;
    private JTextField contentField;

    public SmartFileOrganizerGUI() {
        setTitle("Smart File Organizer");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create components
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2));

        fileNameField = new JTextField();
        contentField = new JTextField();
        textArea = new JTextArea();
        textArea.setEditable(false);

        panel.add(new JLabel("File Name:"));
        panel.add(fileNameField);
        panel.add(new JLabel("Content:"));
        panel.add(contentField);

        JButton createButton = new JButton("Create File");
        JButton writeButton = new JButton("Write to File");
        JButton appendButton = new JButton("Append to File");
        JButton readButton = new JButton("Read File");
        JButton deleteButton = new JButton("Delete File");
        JButton analyzeButton = new JButton("Analyze File");
        JButton categorizeButton = new JButton("Categorize File");

        panel.add(createButton);
        panel.add(writeButton);
        panel.add(appendButton);
        panel.add(readButton);
        panel.add(deleteButton);
        panel.add(analyzeButton);
        panel.add(categorizeButton);

        add(panel, BorderLayout.NORTH);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        // Add action listeners
        createButton.addActionListener(e -> createFile());
        writeButton.addActionListener(e -> writeFile());
        appendButton.addActionListener(e -> appendToFile());
        readButton.addActionListener(e -> readFile());
        deleteButton.addActionListener(e -> deleteFile());
        analyzeButton.addActionListener(e -> analyzeFile());
        categorizeButton.addActionListener(e -> categorizeFile());
    }

    private void createFile() {
        String fileName = fileNameField.getText();
        File file = new File(fileName);
        try {
            if (file.createNewFile()) {
                textArea.append("✅ File created: " + file.getName() + "\n");
            } else {
                textArea.append("⚠️ File already exists.\n");
            }
        } catch (IOException e) {
            textArea.append("❌ Error creating file.\n");
        }
    }

    private void writeFile() {
        String fileName = fileNameField.getText();
        String content = contentField.getText();
        createBackup(fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(content);
            textArea.append("✅ Content written.\n");
        } catch (IOException e) {
            textArea.append("❌ Error writing file.\n");
        }
    }

    private void appendToFile() {
        String fileName = fileNameField.getText();
        String content = contentField.getText();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write("\n" + content);
            textArea.append("✅ Content appended.\n");
        } catch (IOException e) {
            textArea.append("❌ Error appending to file.\n");
        }
    }

    private void readFile() {
        String fileName = fileNameField.getText();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            textArea.append("📄 File Content:\n");
            while ((line = reader.readLine()) != null) {
                textArea.append(line + "\n");
            }
        } catch (IOException e) {
            textArea.append("❌ Error reading file.\n");
        }
    }

    private void deleteFile() {
        String fileName = fileNameField.getText();

        try {
            Files.deleteIfExists(Paths.get(fileName));
            textArea.append("🗑️ File deleted.\n");
        } catch (IOException e) {
            textArea.append("❌ Error deleting file.\n");
        }
    }

    private void analyzeFile() {
        String fileName = fileNameField.getText();
        int wordCount = 0;
        int charCount = 0;
        int lineCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineCount++;
                charCount += line.length();
                wordCount += line.trim().split("\\s+").length;
            }

            textArea.append("📊 File Analysis:\n");
            textArea.append("Lines: " + lineCount + "\n");
            textArea.append("Words: " + wordCount + "\n");
            textArea.append("Characters: " + charCount + "\n");
        } catch (IOException e) {
            textArea.append("❌ Error analyzing file.\n");
        }
    }

    private void categorizeFile() {
        String fileName = fileNameField.getText();
        File file = new File(fileName);
        if (!file.exists()) {
            textArea.append("❌ File not found.\n");
            return;
        }

        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1).toLowerCase();
        }

        File dir = new File("categorized/" + extension);
        if (!dir.exists()) dir.mkdirs();

        File newLocation = new File(dir, file.getName());

        try {
            Files.move(file.toPath(), newLocation.toPath(), StandardCopyOption.REPLACE_EXISTING);
            textArea.append("📦 File moved to: " + dir.getPath() + "\n");
        } catch (IOException e) {
            textArea.append("❌ Error categorizing file.\n");
        }
    }

    private void createBackup(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) return;

        File backupDir = new File("backup");
        if (!backupDir.exists()) backupDir.mkdir();

        File backupFile = new File(backupDir, file.getName() + ".bak");

        try {
            Files.copy(file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            textArea.append("🛡️ Backup created: " + backupFile.getPath() + "\n");
        } catch (IOException e) {
            textArea.append("❌ Failed to create backup.\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SmartFileOrganizerGUI gui = new SmartFileOrganizerGUI();
            gui.setVisible(true);
        });
    }
}
