package laurencewarne.secondspace.common.system.network;

import java.util.Set;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Connection;

import laurencewarne.secondspace.common.collect.IntBags;
import laurencewarne.secondspace.common.component.Player;
import laurencewarne.secondspace.common.component.Ship;
import laurencewarne.secondspace.common.component.network.NetworkConnection;
import laurencewarne.secondspace.common.component.network.Networked;
import laurencewarne.secondspace.common.manager.ChunkManager;
import lombok.NonNull;

/**
 * Sends the state of the world near a player to the player.
 */
@All({Player.class, Ship.class, NetworkConnection.class})
public class StateSenderSystem extends IteratingSystem {

    private ComponentMapper<Ship> mShip;
    private ComponentMapper<NetworkConnection> mNetworkConnection;
    private ChunkManager chunkManager;
    @Wire(name="networked-components")
    private Array<Class<? extends Component>> typesToSend;

    @Override
    protected void process(int id) {
	final Ship playerShip = mShip.get(id);
	final float playerX = playerShip.getX();
	final float playerY = playerShip.getY();
	final Set<Integer> entitiesToSend = IntBags.toSet(
	    chunkManager.getEntitiesInChunks(playerX, playerY, 2)
	);
	final Connection conn = mNetworkConnection.get(id).getConnection();
	for (int entity : entitiesToSend) {
	    for (Class<? extends Component> cls : typesToSend) {
		if (world.getMapper(cls).has(entity)) {
		    sendComponent(id, world.getMapper(cls).get(entity), conn);
		}
	    }
	}
    }

    public <T extends Component> void sendComponent(
	int id, @NonNull T component, @NonNull Connection conn
    ) {
	final Networked<T> networked = new Networked<>();
	networked.setId(id);
	networked.setComponent(component);
	conn.sendTCP(networked);
    }
}
