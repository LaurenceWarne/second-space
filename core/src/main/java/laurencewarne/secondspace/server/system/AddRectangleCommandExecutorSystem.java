package laurencewarne.secondspace.server.system;

import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.google.common.collect.ImmutableSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.server.component.Command;
import laurencewarne.secondspace.server.component.PhysicsRectangleData;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * A {@link BaseEntitySystem} implementation which runs "addRectangle" commands from {@link Command} components. "addRectangle" commands add {@link PhysicsDataRectangle} components to the world.
 */
@All(Command.class)
public class AddRectangleCommandExecutorSystem extends CommandExecutorSystem {

    private final Logger logger = LoggerFactory.getLogger(
	AddRectangleCommandExecutorSystem.class
    );
    private ComponentMapper<PhysicsRectangleData> mPhysicsRectangleData;
    private final ImmutableSet<String> validCommands = ImmutableSet.of(
	"addRectangle", "add-rectangle", "ar"
    );
    private final String name = "add-rectangle";

    @Override
    public ImmutableSet<String> getValidCommands() {
	return validCommands;
    }

    @Override
    public String getName() {
	return name;
    }

    @Override
    public void addArguments(ArgumentParser parser) {
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
    public void executeCommand(Namespace res) {
	final PhysicsRectangleData c = mPhysicsRectangleData.create(world.create());
	c.setWidth(res.getFloat("width"));
	c.setHeight(res.getFloat("height"));
	c.setX(res.getFloat("xcoord"));
	c.setY(res.getFloat("ycoord"));
	c.setStatic(res.get("static"));
    }
}
