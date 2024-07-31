package com.github;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.virtual.DefaultVirtualTerminal;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class ConsoleObjectEditor {

    private static String loadObjectInConsoleEditor(Object object) {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);
        String yamlString = yaml.dump(object);
        return yamlString.substring(yamlString.indexOf("\n") + 1);
    }

    public static Object displayAndEditObjectInConsole(Object object) throws IOException {
        AtomicReference<Object> returnValue = new AtomicReference<>();

        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        DefaultVirtualTerminal terminal = new DefaultVirtualTerminal();

        terminal.enterPrivateMode();

        Screen screen = terminalFactory.createScreen();
        screen.startScreen();
        MultiWindowTextGUI gui = null;
        BasicWindow window = new BasicWindow();

        try{
            TerminalSize terminalSize = terminal.getTerminalSize();
            TextBox textBox = new TextBox(new TerminalSize(terminalSize.getColumns(), terminalSize.getRows() - 3), TextBox.Style.MULTI_LINE);
            textBox.setText(loadObjectInConsoleEditor(object));

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
                    String yamlString = saveEditedObject(textBox.getText());
                    returnValue.set(convertYamlDataToObject(yamlString));
                    System.out.println(returnValue);
                    System.out.println("Changes saved successfully.");
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                } finally {
                    window.close();
                }
            });

            Button cancelButton = new Button("Cancel", () -> {
                try {
                    returnValue.set(convertYamlDataToObject(object.toString()));
                    System.out.println(returnValue);
                    System.out.println("Changes discarded.");
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                } finally {
                    window.close();
                }
            });

            buttonPanel.addComponent(saveButton);
            buttonPanel.addComponent(cancelButton);
            mainPanel.addComponent(textBoxPanel);
            mainPanel.addComponent(buttonPanel, LinearLayout.createLayoutData(LinearLayout.Alignment.End));
            mainPanel.addComponent(buttonPanel, LinearLayout.createLayoutData(LinearLayout.Alignment.Beginning));


            window.setHints(Arrays.asList(Window.Hint.NO_DECORATIONS, Window.Hint.FULL_SCREEN));
            window.setComponent(mainPanel);

            gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.DEFAULT));

            gui.setTheme(SimpleTheme.makeTheme(
                    true, // activeIsBold
                    TextColor.ANSI.DEFAULT, // baseForeground
                    TextColor.ANSI.DEFAULT, // baseBackground
                    TextColor.ANSI.WHITE, // editableForeground
                    TextColor.ANSI.DEFAULT, // editableBackground
                    TextColor.ANSI.WHITE_BRIGHT, // selectedForeground
                    TextColor.ANSI.DEFAULT, // selectedBackground
                    TextColor.ANSI.DEFAULT // guiBackground
            ));

            gui.addWindowAndWait(window);
            System.out.println("Returning from displayAndEditFile");
            return returnValue.get();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());

        } finally {
            screen.stopScreen();
            gui.removeWindow(window);
            terminal.close();
            terminal.exitPrivateMode();
        }
    }

    private static String saveEditedObject(String newContent) throws IOException {
        return newContent;
    }

    private static Object convertYamlDataToObject(String yaml) {
        Yaml y = new Yaml();
        return y.loadAs(yaml, Object.class);
    }

}

