package laurencewarne.secondspace.server.system.command;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.google.common.collect.ImmutableSet;

import laurencewarne.secondspace.server.component.Command;
import lombok.Getter;
import lombok.NonNull;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Abstract class for systems which process {@link Command} components.
 */
public abstract class CommandExecutorSystem extends IteratingSystem {

    private ComponentMapper<Command> mCommand;

    /** Argument parser object used to parse command arguments.*/
    @NonNull @Getter
    private ArgumentParser parser;

    @Override
    public void initialize() {
	parser = ArgumentParsers.newFor(getName()).build();
	addArguments(parser);
    }

    /**
     * Add arguments to this object's ArgumentParser instance.
     */
    public abstract void addArguments(ArgumentParser parser);

    /** 
     * Name of the command - not used for parsing the command, use validCommands instead. This is used as the argument for ArgumentParser.newFor.
     *
     * @return name of the command
     */    
    public abstract String getName();

    /**
     * Handle a parsing error produced by argparse4j.
     *
     * @param command the string of the {@link Command} which caused the error
     * @param errorString the error string returned by argparse4j
     */
    protected abstract void handleParsingError(String command, String errorString);

    /** 
     * Returns a  set of strings representing commands which this system should recognise and execute. This is a Collection so that you can make use of shorthand aliases.
     *
     *  @return a set of strings which are valid names for this command.
     */
    public abstract ImmutableSet<String> getValidCommands();

    @Override
    public void process(int id) {
	final Command command = mCommand.get(id);
	final String commandString = command.getCommandString();
	final String[] commandArr = commandString.split("\\s+");
	final boolean isValidCommand = commandArr.length > 0 &&
	    getValidCommands().contains(commandArr[0]);
	// parseArgs() doesn't want the command string itself
	final String[] args = Arrays.copyOfRange(
	    commandArr, 1, commandArr.length
	);
	if (isValidCommand) {
	    Namespace res;
	    try {
		res = parser.parseArgs(args);
	    } catch (ArgumentParserException e) {
		final StringWriter s = new StringWriter();
		final PrintWriter p = new PrintWriter(s);
		// Writes output to the string writer
		parser.handleError(e, p);
		// Delegate to impl so it can log it, etc
		handleParsingError(commandString, s.toString());
		world.delete(id);
		return;
	    }
	    /* 
	       Don't want to do anything on help option except display help.
	       We don't add this to isValidCommand as parseArgs() will print out a
	       help message for us.
	    */
	    if (!Arrays.stream(commandArr).anyMatch("-h"::equals)) {
		executeCommand(res);
	    }
	    // Remove commmand to prevent further processing
	    world.delete(id);
	}
    }

    /**
     * Execute command with the given arguments.
     * 
     * @param res {@link Namespace} object containing the command arguments
     */
    public abstract void executeCommand(Namespace res);
}
