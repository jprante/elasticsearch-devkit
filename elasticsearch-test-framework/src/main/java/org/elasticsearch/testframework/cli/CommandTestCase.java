package org.elasticsearch.testframework.cli;

import org.elasticsearch.cli.Command;
import org.elasticsearch.cli.Terminal;
import org.elasticsearch.testframework.ESTestCase;
import org.junit.Before;

/**
 * A base test case for cli tools.
 */
public abstract class CommandTestCase extends ESTestCase {

    /** The terminal that execute uses. */
    protected final MockTerminal terminal = new MockTerminal();

    /** The last command that was executed. */
    protected Command command;

    @Before
    public void resetTerminal() {
        terminal.reset();
        terminal.setVerbosity(Terminal.Verbosity.NORMAL);
    }

    /** Creates a Command to test execution. */
    protected abstract Command newCommand();

    /**
     * Runs the command with the given args.
     *
     * Output can be found in {@link #terminal}.
     * The command created can be found in {@link #command}.
     */
    public String execute(String... args) throws Exception {
        command = newCommand();
        command.mainWithoutErrorHandling(args, terminal);
        return terminal.getOutput();
    }
}
