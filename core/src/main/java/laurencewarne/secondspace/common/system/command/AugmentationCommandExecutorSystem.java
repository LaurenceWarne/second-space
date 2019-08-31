package laurencewarne.secondspace.common.system.command;

import java.util.function.Predicate;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.google.common.collect.ImmutableSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.componentlookup.annotations.FieldLookup;
import laurencewarne.secondspace.common.component.AugmentationRequest;
import laurencewarne.secondspace.common.component.Command;
import laurencewarne.secondspace.common.component.EntityTemplate;
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
    private Predicate<String> templateExistenceChecker;
    @FieldLookup(component=EntityTemplate.class, field="name")
    private ObjectIntMap<String> templateNameMap;

    public AugmentationCommandExecutorSystem(
	@NonNull Predicate<String> templateExistenceChecker) {
	super();
	this.templateExistenceChecker = templateExistenceChecker;
    }

    public AugmentationCommandExecutorSystem() {
	super();
	this.templateExistenceChecker =
	    name -> templateNameMap.containsKey(name);
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
	parser.addArgument("ship")
	    .type(Integer.class);
	parser.addArgument("shipX")
	    .type(Integer.class);
	parser.addArgument("shipY")
	    .type(Integer.class);
    }

    @Override
    public void executeCommand(Namespace res) {
	final String templateName = res.get("template");
	if (templateExistenceChecker.test(templateName)) {
	    final AugmentationRequest request = mAugRequest.create(world.create());
	    request.setTemplateName(templateName);
	    request.setShip(res.getInt("ship"));
	    request.setShipX(res.getInt("shipX"));
	    request.setShipY(res.getInt("shipY"));
	}
	else {
	    logger.error("No template exists named '{}'", templateName);
	}
    }
}
