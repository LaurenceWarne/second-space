package laurencewarne.secondspace.common.system.command;

import java.util.Map;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.google.common.collect.ImmutableSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.common.component.AugmentationRequest;
import laurencewarne.secondspace.common.component.Command;
import lombok.NonNull;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;

@All(Command.class)
public class AugmentationCommandExecutorSystem extends CommandExecutorSystem {

    private final Logger logger = LoggerFactory.getLogger(
	AugmentationCommandExecutorSystem.class
    );
    private ComponentMapper<AugmentationRequest> mAugRequest;
    @NonNull
    private final ImmutableSet<String> validCommands = ImmutableSet.of(
	"augment", "aug"
    );
    @NonNull
    private ITemplateExistenceChecker templateExistenceChecker;
    @Wire(name="templates")
    private Map<String, byte[]> entityNameToBytesMap;    

    public interface ITemplateExistenceChecker {
	boolean exists(String templateName);
    }

    public AugmentationCommandExecutorSystem(
	@NonNull ITemplateExistenceChecker templateExistenceChecker) {
	super();
	this.templateExistenceChecker = templateExistenceChecker;
    }

    public AugmentationCommandExecutorSystem() {
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
	return "augment";
    }

    @Override
    public void addArguments(ArgumentParser parser) {
	parser.addArgument("template");
	parser.addArgument("shipX")
	    .type(Integer.class)
	    .nargs("?")
	    .setDefault(0f);
	parser.addArgument("shipY")
	    .type(Integer.class)
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
	    final AugmentationRequest request = mAugRequest.create(world.create());
	    request.setTemplateName(templateName);
	    request.setShipX(res.getInt("shipX"));
	    request.setShipY(res.getInt("shipY"));
	    request.setShip(res.getInt("ship"));
	}
	else {
	    logger.error("No template exists named '{}'", templateName);
	}
    }
}
