package laurencewarne.secondspace.server.system;

import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;

import laurencewarne.secondspace.server.component.Command;
import laurencewarne.secondspace.server.component.PhysicsRectangleData;

/**
 * A {@link BaseEntitySystem} implementation which runs "addRectangle" commands from {@link Command} components. "addRectangle" commands add {@link PhysicsDataRectangle} components to the world.
 */
@All(Command.class)
public class AddRectangleCommandExecutorSystem extends BaseEntitySystem {

    private ComponentMapper<Command> mCommand;
    private ComponentMapper<PhysicsRectangleData> mPhysicsRectangleData;


    @Override
    public void processSystem() {
	
    }

    @Override
    public void inserted(int id) {
	Command command = mCommand.get(id);
	String commandString = command.getCommandString();
	mPhysicsRectangleData.create(world.create());
	mCommand.remove(id);
    }

}
