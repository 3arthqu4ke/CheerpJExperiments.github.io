package me.earth.cheerpjexperiments;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class CheerpJGui {

    final JFrame frame = new JFrame("Experiment");

    final JPanel panel = new JPanel();
    final JTextArea displayArea = new JTextArea(); // TODO: use JTextPane for color instead
    final JScrollPane scrollPane = new JScrollPane(displayArea);
    final JPasswordField inputField = new JPasswordField();
    final JPanel inputPanel = new JPanel();

    final AtomicReference<Consumer<String>> commandHandler = new AtomicReference<>(str -> displayArea.append("Still initializing, command ignored.\n"));
    final List<String> history = new ArrayList<>();
    volatile boolean initialized = false;
    int historyIndex = -1;

    public void init() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        Font monospacedFont = new Font("Monospaced", Font.PLAIN, 12);
        displayArea.setFont(monospacedFont);
        inputField.setFont(monospacedFont);

        Color darkBackground = new Color(34, 34, 34);
        Color lightForeground = new Color(187, 187, 187);
        panel.setForeground(lightForeground);
        panel.setBackground(darkBackground);

        displayArea.setEditable(false);
        displayArea.setBackground(darkBackground);
        displayArea.setForeground(lightForeground);
        DefaultCaret caret = (DefaultCaret) displayArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        scrollPane.setBackground(darkBackground);
        scrollPane.setForeground(lightForeground);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setBackground(darkBackground);
        scrollPane.getVerticalScrollBar().setForeground(lightForeground);
        scrollPane.getHorizontalScrollBar().setBackground(darkBackground);
        scrollPane.getHorizontalScrollBar().setForeground(lightForeground);
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = Color.BLACK;
            }
        });

        scrollPane.getHorizontalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = Color.BLACK;
            }
        });

        inputField.setBackground(darkBackground);
        inputField.setForeground(lightForeground);

        inputField.setEchoChar((char) 0);
        inputField.addActionListener(e -> {
            char[] inputText = inputField.getPassword();
            String textString = new String(inputText);
            Arrays.fill(inputText, (char) 0);
            if (inputField.getEchoChar() != '*') {
                history.add(0, textString);
                while (history.size() > 128) {
                    history.remove(history.size() - 1);
                }

                displayArea.append(textString + "\n");
            }

            inputField.setText("");
            commandHandler.get().accept(textString);
        });

        inputPanel.setLayout(new BorderLayout());
        inputPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        inputPanel.setBackground(darkBackground);
        inputPanel.setForeground(lightForeground);
        inputPanel.add(inputField, BorderLayout.CENTER);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);
        initialized = true;
    }

}
