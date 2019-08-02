package laurencewarne.secondspace.server.system.command;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.ImmutableSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.server.component.Command;
import laurencewarne.secondspace.server.component.WeldJointData;
import lombok.NonNull;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;

@All(Command.class)
public class AddWeldCommandExecutorSystem extends CommandExecutorSystem {

    private final Logger logger = LoggerFactory.getLogger(
	AddWeldCommandExecutorSystem.class
    );
    private ComponentMapper<WeldJointData> mWeldJointData;
    private final ImmutableSet<String> validCommands = ImmutableSet.of(
	"addWelds", "add-welds", "aw"
    );
    private final String name = "add-weld";

    @Override
    public ImmutableSet<String> getValidCommands() {
	return validCommands;
    }

    @Override
    public void handleParsingError(@NonNull String command, @NonNull String errorString) {
	logger.error("Error parsing command: " + errorString);
    }    

    @Override
    public String getName() {
	return name;
    }

    @Override
    public void addArguments(ArgumentParser parser) {
	parser.addArgument("-A", "-a", "--entityAId")
	    .type(Integer.class)
	    .setDefault(-1);
	parser.addArgument("-B", "-b", "--entityBId")
	    .type(Integer.class)
	    .setDefault(-1);
	parser.addArgument("-AX", "-ax", "--entityALocalX")
	    .type(Float.class)
	    .setDefault(0f);
	parser.addArgument("-BX", "-bx", "--entityBLocalX")
	    .type(Float.class)
	    .setDefault(0f);
	parser.addArgument("-AY", "-ay", "--entityALocalY")
	    .type(Float.class)
	    .setDefault(0f);
	parser.addArgument("-BY", "-by", "--entityBLocalY")
	    .type(Float.class)
	    .setDefault(0f);
	parser.addArgument("-RA", "-ra", "--referenceAngle")
	    .type(Float.class)
	    .setDefault(0f);	
    }

    @Override
    public void executeCommand(Namespace res) {
	final WeldJointData weldJointData = mWeldJointData.create(world.create());
	weldJointData.setCellAID(res.getInt("entityAId"));
	weldJointData.setCellBID(res.getInt("entityBId"));
	weldJointData.setLocalAnchorA(
	    new Vector2(res.getFloat("entityALocalX"), res.getFloat("entityALocalY"))
	);
	weldJointData.setLocalAnchorB(
	    new Vector2(res.getFloat("entityBLocalX"), res.getFloat("entityBLocalY"))
	);
	weldJointData.setReferenceAngle(res.getFloat("referenceAngle"));
    }
    

}
