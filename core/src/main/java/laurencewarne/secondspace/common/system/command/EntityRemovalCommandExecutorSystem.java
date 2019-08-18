package laurencewarne.secondspace.common.system.command;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.google.common.collect.ImmutableSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.common.collect.IntBags;
import laurencewarne.secondspace.common.component.Command;
import laurencewarne.secondspace.common.component.Ship;
import laurencewarne.secondspace.common.component.ShipPart;
import lombok.NonNull;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;

@All(Command.class)
public class EntityRemovalCommandExecutorSystem extends CommandExecutorSystem {

    private final Logger logger = LoggerFactory.getLogger(
	EntityRemovalCommandExecutorSystem.class
    );
    private ComponentMapper<Ship> mShip;
    private ComponentMapper<ShipPart> mShipPart;
    @NonNull
    private final ImmutableSet<String> validCommands = ImmutableSet.of(
	"remove", "rm"
    );
    @NonNull
    private final String name = "remove";
    @NonNull
    private final ShipPart failSafe = new ShipPart();

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
	parser.addArgument("id")
	    .type(Integer.class);
	parser.addArgument("-s", "--ship")
	    .action(Arguments.storeTrue());
	parser.addArgument("shipX")
	    .type(Integer.class)
	    .nargs("?")
	    .setDefault(0f);
	parser.addArgument("shipY")
	    .type(Integer.class)
	    .nargs("?")
	    .setDefault(0f);
    }

    @Override
    public void executeCommand(Namespace res) {
	if (res.getBoolean("ship")) {
	    final int shipId = res.getInt("id");
	    final int shipX = res.getInt("shipX");
	    final int shipY = res.getInt("shipY");
	    removeFromShip(shipId, shipX, shipY);
	}
	else {
	    world.delete(res.getInt("id"));
	}
    }

    private void removeFromShip(int shipId, int shipX, int shipY) {
	final Ship ship = mShip.get(shipId);
	failSafe.setLocalX(shipX - 1);  // Ensure wrong
	boolean foundPart = false;
	for (int partId : IntBags.toSet(ship.parts)) {
	    final ShipPart part = mShipPart.getSafe(partId, failSafe);
	    if (part.getLocalX() == shipX && part.getLocalY() == shipY){
		world.delete(partId);
		foundPart = true;
		break;
	    }
	}
	if (!foundPart){
	    logger.error(
		"No ShipPart found for ship {} at ship coordinates {} {}",
		shipX, shipY
	    );
	}
    }
}
