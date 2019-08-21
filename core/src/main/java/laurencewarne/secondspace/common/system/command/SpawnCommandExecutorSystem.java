package laurencewarne.secondspace.common.system.command;

import java.util.Map;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.google.common.collect.ImmutableSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.common.component.Command;
import laurencewarne.secondspace.common.component.SpawnRequest;
import lombok.NonNull;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;

@All(Command.class)
public class SpawnCommandExecutorSystem extends CommandExecutorSystem {

    private final Logger logger = LoggerFactory.getLogger(
	SpawnCommandExecutorSystem.class
    );
    private ComponentMapper<SpawnRequest> mSpawnRequest;
    @NonNull
    private final ImmutableSet<String> validCommands = ImmutableSet.of(
	"spawn", "spwn", "spn"
    );
    @NonNull
    private ITemplateExistenceChecker templateExistenceChecker;
    @Wire(name="templates")
    private Map<String, byte[]> entityNameToBytesMap;    

    public interface ITemplateExistenceChecker {
	boolean exists(String templateName);
    }

    public SpawnCommandExecutorSystem(
	@NonNull ITemplateExistenceChecker templateExistenceChecker) {
	super();
	this.templateExistenceChecker = templateExistenceChecker;
    }

    public SpawnCommandExecutorSystem() {
	super();
	this.templateExistenceChecker =
	    name -> entityNameToBytesMap.containsKey(name);
    }

    @Override
    public ImmutableSet<String> getValidCommands() {
	return validCommands;
    }

    @Override
    public void handleParsingError(
	@NonNull String command, @NonNull String errorString
    ) {
	logger.error("Error parsing command: " + errorString);
    }    

    @Override
    public String getName() {
	return "spawn";
    }

    @Override
    public void addArguments(ArgumentParser parser) {
	parser.addArgument("template");
	parser.addArgument("x")
	    .type(Float.class)
	    // Makes optional
	    .nargs("?")
	    .setDefault(0f);
	parser.addArgument("y")
	    .type(Float.class)
	    .nargs("?")
	    .setDefault(0f);
	parser.addArgument("ship")
	    .type(Integer.class)
	    .nargs("?")
	    .setDefault(-1);
    }

    @Override
    public void executeCommand(Namespace res) {
	final String templateName = res.get("template");
	if (templateExistenceChecker.exists(templateName)) {
	    final SpawnRequest request = mSpawnRequest.create(world.create());
	    request.setTemplateName(templateName);
	    request.setX(res.getFloat("x"));
	    request.setY(res.getFloat("y"));
	    request.setShipOwner(res.getInt("ship"));
	}
	else {
	    logger.error("No template exists named '{}'", templateName);
	}
    }
}
