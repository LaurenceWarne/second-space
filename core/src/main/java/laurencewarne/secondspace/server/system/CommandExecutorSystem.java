package laurencewarne.secondspace.server.system;

import java.util.Arrays;
import java.util.Set;

import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.google.common.collect.ImmutableSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.server.component.Command;
import lombok.NonNull;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Abstract class for systems which process {@link Command} components.
 */
public abstract class CommandExecutorSystem extends BaseEntitySystem {

    private final Logger logger = LoggerFactory.getLogger(
	AddRectangleCommandExecutorSystem.class
    );
    private ComponentMapper<Command> mCommand;

    /** Argument parser object used to parse command arguments.*/
    @NonNull
    protected ArgumentParser parser;
    /** Name of the command - not used for parsing the command, use validCommands instead.*/
    @NonNull
    protected String name;
    /** A set of strings representing commands which this system should recognise and execute. This is a Collection so that you can make use of shorthand aliases.*/
    @NonNull
    protected Set<String> validCommands = ImmutableSet.of();

    @Override
    public void initialize() {
	parser = ArgumentParsers.newFor(name).build();
    }

    /**
     * Add arguments to this object's ArgumentParser instance.
     */
    public abstract void addArguments();
    
    @Override
    public void processSystem() {
	
    }

    @Override
    public void inserted(int id) {
	final Command command = mCommand.get(id);
	final String commandString = command.getCommandString();
	final String[] commandArr = commandString.split("\\s+");
	final boolean isValidCommand = commandArr.length > 0 &&
	    validCommands.contains(commandArr[0]) &&
	    // Don't want to do anything on help option except display help
	    !Arrays.stream(commandArr).anyMatch("-h"::equals);
	if (isValidCommand) {
	    Namespace res;
	    try {
		res = parser.parseArgs(commandArr);
	    } catch (ArgumentParserException e) {
		logger.error(
		    "Error parsing '{}' command: {}",
		    commandArr[0],
		    e.getStackTrace().toString()
		);
		return;
	    }
	    executeCommand(res);
	    // Remove commmand to prevent further processing
	    mCommand.remove(id);
	}
    }

    /**
     * Execute command with the given arguments.
     * 
     * @param args Namespace object containing the command arguments
     */
    public abstract void executeCommand(Namespace res);
}
