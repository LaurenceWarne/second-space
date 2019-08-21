package laurencewarne.secondspace.common.system;

import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Exclude;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.common.component.Player;
import laurencewarne.secondspace.common.component.Ship;
import laurencewarne.secondspace.common.component.SpawnRequest;
import laurencewarne.secondspace.common.component.network.NetworkConnection;

/**
 * Creates {@link Ship}s for new players.
 */
@All({Player.class, NetworkConnection.class})
@Exclude(Ship.class)
public class PlayerShipCreatorSystem extends BaseEntitySystem {

    private final Logger logger = LoggerFactory.getLogger(
	PlayerShipCreatorSystem.class
    );
    private ComponentMapper<SpawnRequest> mSpawnRequest;
    private ComponentMapper<Player> mPlayer;

    @Override
    public void inserted(int id) {
	final SpawnRequest req = mSpawnRequest.create(id);
	req.setShipOwner(id);
	req.setTemplateName("arch-hunter");
	logger.info(
	    "Spawning in ship {} for player {}",
	    req.getTemplateName(), mPlayer.get(id).getName()
	);
    }

    @Override
    public void processSystem() {
	
    }
}
