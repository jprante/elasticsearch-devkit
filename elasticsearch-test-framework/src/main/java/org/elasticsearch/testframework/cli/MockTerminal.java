package org.elasticsearch.testframework.cli;

import org.elasticsearch.cli.Terminal;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * A terminal for tests which captures all output, and
 * can be plugged with fake input.
 */
public class MockTerminal extends Terminal {

    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private final PrintWriter writer = new PrintWriter(new OutputStreamWriter(buffer, StandardCharsets.UTF_8));

    // A deque would be a perfect data structure for the FIFO queue of input values needed here. However,
    // to support the valid return value of readText being null (defined by Console), we need to be able
    // to store nulls. However, java the java Deque api does not allow nulls because it uses null as
    // a special return value from certain methods like peek(). So instead of deque, we use an array list here,
    // and keep track of the last position which was read. It means that we will hold onto all input
    // setup for the mock terminal during its lifetime, but this is normally a very small amount of data
    // so in reality it will not matter.
    private final List<String> textInput = new ArrayList<>();
    private int textIndex = 0;
    private final List<String> secretInput = new ArrayList<>();
    private int secretIndex = 0;

    public MockTerminal() {
        super("\n"); // always *nix newlines for tests
    }

    @Override
    public String readText(String prompt) {
        if (textIndex >= textInput.size()) {
            throw new IllegalStateException("No text input configured for prompt [" + prompt + "]");
        }
        return textInput.get(textIndex++);
    }

    @Override
    public char[] readSecret(String prompt) {
        if (secretIndex >= secretInput.size()) {
            throw new IllegalStateException("No secret input configured for prompt [" + prompt + "]");
        }
        return secretInput.get(secretIndex++).toCharArray();
    }

    @Override
    public PrintWriter getWriter() {
        return writer;
    }

    /** Adds an an input that will be return from {@link #readText(String)}. Values are read in FIFO order. */
    public void addTextInput(String input) {
        textInput.add(input);
    }

    /** Adds an an input that will be return from {@link #readText(String)}. Values are read in FIFO order. */
    public void addSecretInput(String input) {
        secretInput.add(input);
    }

    /** Returns all output written to this terminal. */
    public String getOutput() throws UnsupportedEncodingException {
        return buffer.toString("UTF-8");
    }

    /** Wipes the input and output. */
    public void reset() {
        buffer.reset();
        textIndex = 0;
        textInput.clear();
        secretIndex = 0;
        secretInput.clear();
    }
}
