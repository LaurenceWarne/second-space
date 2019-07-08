package laurencewarne.secondspace.server.system;

import java.util.Arrays;
import java.util.Set;

import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.google.common.collect.ImmutableSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.server.component.Command;
import laurencewarne.secondspace.server.component.PhysicsRectangleData;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * A {@link BaseEntitySystem} implementation which runs "addRectangle" commands from {@link Command} components. "addRectangle" commands add {@link PhysicsDataRectangle} components to the world.
 */
@All(Command.class)
public class AddRectangleCommandExecutorSystem extends BaseEntitySystem {

    private final Logger logger = LoggerFactory.getLogger(
	AddRectangleCommandExecutorSystem.class
    );
    private ComponentMapper<Command> mCommand;
    private ComponentMapper<PhysicsRectangleData> mPhysicsRectangleData;

    private ArgumentParser parser;
    private Set<String> validCommands = ImmutableSet.of(
	"addRectangle", "add-rectangle", "ar"
    );

    @Override
    public void initialize() {
	parser = ArgumentParsers.newFor("add-rectangle").build();
	parser.addArgument("-W", "-w", "--width")
	    .type(Float.class)
	    .setDefault(2f);
	parser.addArgument("-H", "--height")
	    .type(Float.class)
	    .setDefault(2f);
	parser.addArgument("-X", "-x", "--xcoord")
	    .type(Float.class)
	    .setDefault(0f);
	parser.addArgument("-Y", "-y", "--ycoord")
	    .type(Float.class)
	    .setDefault(0f);
	parser.addArgument("-S", "-s", "--static")
	    .action(Arguments.storeTrue());
    }
    
    @Override
    public void processSystem() {
	
    }

    @Override
    public void inserted(int id) {
	final Command command = mCommand.get(id);
	final String commandString = command.getCommandString();
	final String[] commandArr = commandString.split("\\s+");
	// Check if valid
	if (commandArr.length > 1 && validCommands.contains(commandArr[0])) {
	    final String[] args = Arrays.copyOfRange(
		commandArr, 1, commandArr.length
	    );
	    final PhysicsRectangleData c = mPhysicsRectangleData.create(world.create());
	    try {
		final Namespace res = parser.parseArgs(args);
		c.setWidth(res.getFloat("width"));
		c.setHeight(res.getFloat("height"));
		c.setX(res.getFloat("xcoord"));
		c.setY(res.getFloat("ycoord"));
		c.setStatic(res.get("static"));
	    }
	    catch (ArgumentParserException e){
		logger.error(
		    "Error parsing 'addRectangle' command: {}",
		    e.getStackTrace().toString()
		);
	    }
	    mCommand.remove(id);
	}
    }
}
