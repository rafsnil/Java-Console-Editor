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

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.googlecode.lanterna.graphics.SimpleTheme.makeTheme;

public class LanternaFileEditor {

//    public LanternaFileEditor(Object object) {
//        this.object = object;
//    }

    private static String loadObjectInTerminal(Object object) {
        //This function should generate an yaml object from the object and display it in the terminal
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);
        String yamlString = yaml.dump(object);
        String trimmedYaml = yamlString.substring(yamlString.indexOf("\n") + 1);
        return trimmedYaml;
    }

    public static Object displayAndEditFile(Object object) throws IOException {
        AtomicReference<Object> returnValue = new AtomicReference<>();
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Terminal terminal = terminalFactory.createTerminal();

        Screen screen = terminalFactory.createScreen();
        screen.startScreen();

        TerminalSize terminalSize = terminal.getTerminalSize();
        final TextBox textBox = new TextBox(new TerminalSize(terminalSize.getColumns(), terminalSize.getRows() - 3), TextBox.Style.MULTI_LINE);
        textBox.setText(loadObjectInTerminal(object));


//        textBox.setCaretPosition(0, 0); // Set caret to the beginning
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
                String yamlString = saveObject(textBox.getText());
                returnValue.set(convertYamlToObject(yamlString));
                screen.stopScreen();
                System.out.println(returnValue);
                System.out.println("Changes saved successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Button cancelButton = new Button("Cancel", () -> {
            try {
                returnValue.set(convertYamlToObject(object.toString()));
                screen.stopScreen();
                System.out.println(returnValue);
                System.out.println("Changes discarded.");
            } catch (IOException e) {
                throw new RuntimeException(e);
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
        System.out.println("Returning from displayAndEditFile");

        return returnValue;
    }

    private static String saveObject(String newContent) throws IOException {
        return newContent;
    }

    //A function to convert Yaml to the given object T
    private static Object convertYamlToObject(String yaml) {
        Yaml y = new Yaml();
        return y.loadAs(yaml, Object.class);
    }

}

