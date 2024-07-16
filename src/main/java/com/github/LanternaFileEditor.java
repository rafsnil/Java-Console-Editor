package com.github;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

import static com.googlecode.lanterna.graphics.SimpleTheme.makeTheme;

public class LanternaFileEditor {

    private List<String> fileContent;
    private String filePath;

    public LanternaFileEditor(String filePath) {
        this.filePath = filePath;
        this.fileContent = new ArrayList<>();
    }

    public void loadFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.add(line);
            }
        }
    }

    public void displayAndEditFile() throws IOException {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Terminal terminal = terminalFactory.createTerminal();
        Screen screen = terminalFactory.createScreen();
        screen.startScreen();

        TerminalSize terminalSize = terminal.getTerminalSize();
        final TextBox textBox = new TextBox(new TerminalSize(terminalSize.getColumns(), terminalSize.getRows() - 3), TextBox.Style.MULTI_LINE);
        textBox.setText(String.join("\n", fileContent));
        textBox.setCaretPosition(0, 0); // Set caret to the beginning
//        textBox.setBorder(BorderStyle.NONE); // Make TextBox borderless
        textBox.setSize(new TerminalSize(terminalSize.getColumns(), terminalSize.getRows()));

        Panel mainPanel = new Panel();
        mainPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        Panel textBoxPanel = new Panel();
        textBoxPanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        textBoxPanel.addComponent(textBox);

        Panel buttonPanel = new Panel();
        buttonPanel.setLayoutManager(new GridLayout(2));

        Button saveButton = new Button("Save", () -> {
            try {
                saveFile(textBox.getText());
                screen.stopScreen();
                System.out.println("Changes saved successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Button cancelButton = new Button("Cancel", () -> {
            try {
                screen.stopScreen();
                System.out.println("Changes discarded.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        buttonPanel.addComponent(saveButton);
        buttonPanel.addComponent(cancelButton);
        mainPanel.addComponent(textBoxPanel);
        mainPanel.addComponent(buttonPanel, LinearLayout.createLayoutData(LinearLayout.Alignment.End));
        mainPanel.addComponent(buttonPanel, LinearLayout.createLayoutData(LinearLayout.Alignment.Beginning));

        BasicWindow window = new BasicWindow();
        window.setHints(Arrays.asList(Window.Hint.NO_DECORATIONS, Window.Hint.FULL_SCREEN));
        window.setComponent(mainPanel);

        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.DEFAULT));
        SimpleTheme theme = makeTheme(
                true, // activeIsBold
                TextColor.ANSI.DEFAULT, // baseForeground
                TextColor.ANSI.DEFAULT, // baseBackground
                TextColor.ANSI.WHITE, // editableForeground
                TextColor.ANSI.DEFAULT, // editableBackground
                TextColor.ANSI.WHITE_BRIGHT, // selectedForeground
                TextColor.ANSI.DEFAULT, // selectedBackground
                TextColor.ANSI.DEFAULT // guiBackground
        );
        gui.setTheme(theme);
        gui.addWindowAndWait(window);
    }

    private void saveFile(String newContent) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(newContent);
        }
    }

    public static void main(String[] args) {
//        if (args.length != 1) {
//            System.out.println("Usage: java LanternaFileEditor <file-path>");
//            return;
//        }
//
//        String filePath = args[0];
        String filePath = Paths.get("").toAbsolutePath().resolve("gg.yaml").toString();
        LanternaFileEditor editor = new LanternaFileEditor(filePath);

        try {
            editor.loadFile();
            editor.displayAndEditFile();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

