package laurencewarne.secondspace.server.manager;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.math.Vector2;

import laurencewarne.secondspace.server.component.Connection;
import laurencewarne.secondspace.server.component.ConnectionReference;
import lombok.NonNull;

/**
 * Provides a simplified interface to the components involved in entity connections, which are complex owing to the need to have them serializable.
 */
public class ConnectionManager extends BaseSystem {

    private static final ConnectionReference EMPTY_REF = new ConnectionReference();
    private static final Connection EMPTY_CON = new Connection();

    private ComponentMapper<ConnectionReference> mConnectionReference;
    private ComponentMapper<Connection> mConnection;

    public void processSystem() {
	
    }

    public boolean existsConnection(
	int entityA, int entityB, @NonNull Vector2 position
    ) {
	final ConnectionReference connectionA = mConnectionReference
	    .getSafe(entityA, EMPTY_REF);
	try {
	    final int index = connectionA.connectedEntities.indexOf(entityB);
	    final Connection conn = mConnection.getSafe(
		connectionA.links.get(index), EMPTY_CON
	    );
	    return conn.getLocalACoords().contains(position, false);
	}
	catch (ArrayIndexOutOfBoundsException e) {
	    return false;
	}
    }

    public void createConnection(
	int entityA, int entityB,
	@NonNull Vector2 localPositionA, @NonNull Vector2 localPositionB
    ) {
	if (!mConnectionReference.has(entityA)) {
	    mConnectionReference.create(entityA);
	}
	final ConnectionReference refA = mConnectionReference.get(entityA);
	if (!mConnectionReference.has(entityB)) {
	    mConnectionReference.create(entityB);
	}
	final ConnectionReference refB = mConnectionReference.get(entityB);	
	if (!refA.connectedEntities.contains(entityB)) {
	    refA.connectedEntities.add(entityB);
	    refB.connectedEntities.add(entityA);
	    final Connection conn = mConnection.create(world.create());
	    conn.entityAId = entityA;
	    conn.entityBId = entityB;
	}
    }

    public void removeConnections(int entity) {
	
    }

    public void removeConnections(int entityA, int entityB) {
	
    }

    public void addConnectionListener() {
	
    }
}
