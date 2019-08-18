package laurencewarne.secondspace.common.system.command;

import com.artemis.annotations.All;
import com.google.common.collect.ImmutableSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.common.component.Command;
import laurencewarne.secondspace.common.system.WorldSerializationSystem;
import lombok.NonNull;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;

@All(Command.class)
public class SaveCommandExecutorSystem extends CommandExecutorSystem {

    private final Logger logger = LoggerFactory.getLogger(
	SaveCommandExecutorSystem.class
    );
    @NonNull
    private final ImmutableSet<String> validCommands = ImmutableSet.of(
	"save", "sv"
    );
    @NonNull
    private final String name = "save";

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
	return name;
    }

    @Override
    public void addArguments(ArgumentParser parser) {

    }

    @Override
    public void executeCommand(Namespace res) {
	final WorldSerializationSystem serializationSystem = world.getSystem(
	    WorldSerializationSystem.class
	);
	if (serializationSystem != null){
	    serializationSystem.serialize();
	}
	else {
	    logger.error("No serialization system instance exists!");
	}
    }
}

