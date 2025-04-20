import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class TagExtractor extends JFrame {
    private JTextArea textArea;
    private JLabel fileLabel;
    private Set<String> stopWords = new TreeSet<>();
    private Map<String, Integer> wordFreq = new TreeMap<>();

    public TagExtractor() {
        setTitle("Tag/Keyword Extractor");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        JButton loadTextButton = new JButton("Load Text File");
        JButton loadStopWordsButton = new JButton("Load Stop Words");
        JButton saveButton = new JButton("Save Tags");
        fileLabel = new JLabel("No file selected");

        topPanel.add(loadTextButton);
        topPanel.add(loadStopWordsButton);
        topPanel.add(saveButton);
        topPanel.add(fileLabel);

        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        loadStopWordsButton.addActionListener(e -> loadStopWords());
        loadTextButton.addActionListener(e -> loadTextFile());
        saveButton.addActionListener(e -> saveOutput());
    }

    private void loadStopWords() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            stopWords.clear();
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    stopWords.add(scanner.nextLine().trim().toLowerCase());
                }
                JOptionPane.showMessageDialog(this, "Stop words loaded.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error loading stop words.");
            }
        }
    }

    private void loadTextFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            fileLabel.setText("Loaded: " + file.getName());
            wordFreq.clear();
            textArea.setText("");

            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] words = line.toLowerCase().replaceAll("[^a-z ]", "").split("\\s+");
                    for (String word : words) {
                        if (!stopWords.contains(word) && !word.isEmpty()) {
                            wordFreq.put(word, wordFreq.getOrDefault(word, 0) + 1);
                        }
                    }
                }

                // Display in JTextArea
                List<Map.Entry<String, Integer>> sorted = new ArrayList<>(wordFreq.entrySet());
                sorted.sort((a, b) -> b.getValue() - a.getValue());
                for (Map.Entry<String, Integer> entry : sorted) {
                    textArea.append(entry.getKey() + ": " + entry.getValue() + "\n");
                }

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error reading text file.");
            }
        }
    }

    private void saveOutput() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(file)) {
                for (Map.Entry<String, Integer> entry : wordFreq.entrySet()) {
                    writer.println(entry.getKey() + ": " + entry.getValue());
                }
                JOptionPane.showMessageDialog(this, "Output saved.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving file.");
            }
        }
    }
}