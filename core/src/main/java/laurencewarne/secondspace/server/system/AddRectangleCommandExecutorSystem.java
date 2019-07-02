package laurencewarne.secondspace.server.system;

import java.util.Arrays;
import java.util.Set;

import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.google.common.collect.Sets;

import laurencewarne.secondspace.server.component.Command;
import laurencewarne.secondspace.server.component.PhysicsRectangleData;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * A {@link BaseEntitySystem} implementation which runs "addRectangle" commands from {@link Command} components. "addRectangle" commands add {@link PhysicsDataRectangle} components to the world.
 */
@All(Command.class)
public class AddRectangleCommandExecutorSystem extends BaseEntitySystem {

    private ComponentMapper<Command> mCommand;
    private ComponentMapper<PhysicsRectangleData> mPhysicsRectangleData;

    private ArgumentParser parser;
    private Set<String> validCommands = Sets.newHashSet(
	"addRectangle", "add-rectangle", "ar"
    );

    @Override
    public void initialize() {
	parser = ArgumentParsers.newFor("add-rectangle").build();
	parser.addArgument("-W", "--width")
	    .type(Float.class)
	    .setDefault(2f);
	parser.addArgument("-H", "--height")
	    .type(Float.class)
	    .setDefault(2f);
	parser.addArgument("-X", "--xcoord")
	    .type(Float.class)
	    .setDefault(0f);
	parser.addArgument("-Y", "--ycoord")
	    .type(Float.class)
	    .setDefault(0f);
	
    }
    
    @Override
    public void processSystem() {
	
    }

    @Override
    public void inserted(int id) {
	Command command = mCommand.get(id);
	String commandString = command.getCommandString();
	String[] commandArr = commandString.split("\\s+");
	// Check if valid
	if (commandArr.length > 1 && validCommands.contains(commandArr[0])) {
	    String[] args = Arrays.copyOfRange(
		commandArr, 1, commandArr.length
	    );
	    PhysicsRectangleData c = mPhysicsRectangleData.create(world.create());
	    try {
		Namespace res = parser.parseArgs(args);
		c.setWidth(res.getFloat("width"));
		c.setHeight(res.getFloat("height"));
		c.setX(res.getFloat("xcoord"));
		c.setY(res.getFloat("ycoord"));
	    }
	    catch (ArgumentParserException e){
		System.out.println(e);
	    }
	    mCommand.remove(id);
	}
    }

}
