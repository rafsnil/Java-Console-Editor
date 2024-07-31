package com.github;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class ConsoleUiObjectEditor {

    public static Object displayAndEditObject(Object object) throws IOException {
        AtomicReference<Object> returnValue = new AtomicReference<>();

        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Terminal terminal = terminalFactory.createTerminal();

        Screen screen = terminalFactory.createScreen();
        screen.startScreen();

        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.DEFAULT));
        BasicWindow window = new BasicWindow();


        try {
            TerminalSize terminalSize = terminal.getTerminalSize();

            TextBox textBox = new TextBox(new TerminalSize(terminalSize.getColumns(), terminalSize.getRows() - 3), TextBox.Style.MULTI_LINE);
            textBox.setText(loadObjectInTerminal(object));
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
                    String yamlString = saveChangedObjectFromConsole(textBox.getText());
                    returnValue.set(convertYamlDataToExpectedObject(yamlString));
                    System.out.println(returnValue);
                    System.out.println("Changes saved successfully.");
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                } finally {
                    window.close();
                }
            });
            buttonPanel.addComponent(saveButton);

            Button cancelButton = new Button("Cancel", () -> {
                try {
                    returnValue.set(convertYamlDataToExpectedObject(object.toString()));
                    System.out.println(returnValue);
                    System.out.println("Changes discarded.");
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                } finally {
                    window.close();
                }
            });
            buttonPanel.addComponent(cancelButton);

            mainPanel.addComponent(textBoxPanel);
            mainPanel.addComponent(buttonPanel, LinearLayout.createLayoutData(LinearLayout.Alignment.End));
            mainPanel.addComponent(buttonPanel, LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));


            window.setHints(Arrays.asList(Window.Hint.NO_DECORATIONS, Window.Hint.FULL_SCREEN));
            window.setComponent(mainPanel);

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


            return returnValue.get();
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        } finally {
            screen.stopScreen();
            gui.removeWindow(window);
            terminal.close();
        }
    }

    private static String loadObjectInTerminal(Object object) {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);
        String yamlString = yaml.dump(object);
        return yamlString.substring(yamlString.indexOf("\n") + 1);
    }

    private static String saveChangedObjectFromConsole(String newUpdatedContent) {
        return newUpdatedContent;
    }

    private static Object convertYamlDataToExpectedObject(String yaml) {
        try {
            Yaml y = new Yaml();
            return y.loadAs(yaml, Object.class);
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

}

