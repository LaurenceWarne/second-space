package laurencewarne.secondspace.common.system.network;

import java.util.HashMap;
import java.util.Map;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.esotericsoftware.kryonet.Listener.TypeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.common.component.Player;
import laurencewarne.secondspace.common.component.network.NetworkConnection;
import laurencewarne.secondspace.common.component.network.RegistrationRequest;


public class NewClientConnectionSystem extends BaseSystem {

    private final Logger logger = LoggerFactory.getLogger(
	NewClientConnectionSystem.class
    );
    @Wire
    private TypeListener typeListener;
    private ComponentMapper<Player> mPlayer;
    private ComponentMapper<NetworkConnection> mNetworkConnection;
    private Map<String, Integer> nameToPlayerMap = new HashMap<>();

    @Override
    public void initialize() {
	typeListener.addTypeHandler(
	    RegistrationRequest.class,
	    (conn, req) -> {
		final String name = req.getName();
		int playerId = -1;
		final boolean isExistingPlayer = nameToPlayerMap.containsKey(name);
		final boolean isAlreadyConnected = isExistingPlayer &&
		    !mNetworkConnection.has(nameToPlayerMap.get(name));
		if (isExistingPlayer && !isAlreadyConnected) {
		    playerId = nameToPlayerMap.get(name);
		    mNetworkConnection.create(playerId).setConnection(conn);
		    logger.info(
			"Known player '{}' has logged on from ip {}",
			name, conn.getRemoteAddressTCP()
		    );
		}
		else if (!isExistingPlayer) {
		    playerId = world.create();
		    mPlayer.create(playerId).setName(name);
		    mNetworkConnection.create(playerId).setConnection(conn);
		    logger.info(
			"New player '{}' has logged on from ip {}",
			name, conn.getRemoteAddressTCP()
		    );
		}
		else {
		    logger.info(
			"Rejected request from {} to log on as existing player {}",
			conn.getRemoteAddressTCP(), name
		    );
		}
		// Send response
	    }
	);
	
    }

    @Override
    public void processSystem() {
	
    }
}
