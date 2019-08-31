package laurencewarne.secondspace.common.system.command;

import java.util.function.Predicate;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.google.common.collect.ImmutableSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.componentlookup.annotations.FieldLookup;
import laurencewarne.secondspace.common.component.Command;
import laurencewarne.secondspace.common.component.EntityTemplate;
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
    private Predicate<String> templateExistenceChecker;
    @FieldLookup(component=EntityTemplate.class, field="name")
    private ObjectIntMap<String> templateNameMap;

    public SpawnCommandExecutorSystem(
	@NonNull Predicate<String> templateExistenceChecker) {
	super();
	this.templateExistenceChecker = templateExistenceChecker;
    }

    public SpawnCommandExecutorSystem() {
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
	if (templateExistenceChecker.test(templateName)) {
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
