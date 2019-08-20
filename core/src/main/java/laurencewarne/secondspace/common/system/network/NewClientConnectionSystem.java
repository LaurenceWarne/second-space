package laurencewarne.secondspace.common.system.network;

import java.util.HashMap;
import java.util.Map;

import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener.TypeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.common.component.Player;
import laurencewarne.secondspace.common.component.network.NetworkConnection;
import laurencewarne.secondspace.common.component.network.RegistrationRequest;
import laurencewarne.secondspace.common.component.network.RegistrationResponse;
import lombok.NonNull;

/**
 * This system handles new client connections using player names.
 */
@All(Player.class)
public class NewClientConnectionSystem extends BaseEntitySystem {

    private final Logger logger = LoggerFactory.getLogger(
	NewClientConnectionSystem.class
    );
    @Wire
    private TypeListener typeListener;
    private ComponentMapper<Player> mPlayer;
    private ComponentMapper<NetworkConnection> mNetworkConnection;
    private Map<String, Integer> nameToPlayerMap = new HashMap<>();

    @Override
    public void inserted(int id) {
	final Player player = mPlayer.get(id);
	nameToPlayerMap.put(player.getName(), id);
    }

    @Override
    public void initialize() {
	typeListener.addTypeHandler(
	    RegistrationRequest.class,
	    (conn, req) -> {
		final String name = req.getName();
		RegistrationResponse response;
		final boolean isExistingPlayer = nameToPlayerMap.containsKey(name);
		final boolean isAlreadyConnected = isExistingPlayer &&
		    !mNetworkConnection.has(nameToPlayerMap.get(name));
		if (isExistingPlayer && !isAlreadyConnected) {
		    response = handleExistingPlayerConnection(name, conn);
		}
		else if (!isExistingPlayer) {
		    response = handleNewPlayerConnection(name, conn);
		}
		else {
		    response = handleAlreadyLoggedInConnection(name, conn);
		}
		// Send response
		conn.sendTCP(response);
	    }
	);
    }

    public RegistrationResponse handleNewPlayerConnection(
	@NonNull String playerName, @NonNull Connection conn
    ) {
	int playerId = nameToPlayerMap.get(playerName);
	mNetworkConnection.create(playerId).setConnection(conn);
	logger.info(
	    "Known player '{}' has logged on from ip {}",
	    playerName, conn.getRemoteAddressTCP()
	);	
	return new RegistrationResponse(playerId);
    }

    public RegistrationResponse handleExistingPlayerConnection(
	@NonNull String playerName, @NonNull Connection conn
    ) {
	int playerId = world.create();
	mPlayer.create(playerId).setName(playerName);
	mNetworkConnection.create(playerId).setConnection(conn);
	logger.info(
	    "New player '{}' has logged on from ip {}",
	    playerName, conn.getRemoteAddressTCP()
	);
	return new RegistrationResponse(playerId);	
    }

    public RegistrationResponse handleAlreadyLoggedInConnection(
	@NonNull String playerName, @NonNull Connection conn
    ) {
	logger.info(
	    "Rejected request from {} to log on as existing player {}",
	    conn.getRemoteAddressTCP(), playerName
	);
	return new RegistrationResponse(-1);
    }

    @Override
    public void processSystem() {
	
    }
}
