package laurencewarne.secondspace.server.manager;

import java.util.LinkedList;
import java.util.List;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.math.Vector2;

import laurencewarne.secondspace.server.collect.IntBags;
import laurencewarne.secondspace.server.component.connection.Connection;
import laurencewarne.secondspace.server.component.connection.ConnectionReference;
import lombok.NonNull;
import net.fbridault.eeel.annotation.All;
import net.fbridault.eeel.annotation.Removed;

/**
 * Provides a simplified interface to the components involved in entity connections, which are complex owing to the need to have them serializable.
 */
public class ConnectionManager extends BaseSystem {

    private static final ConnectionReference EMPTY_REF = new ConnectionReference();
    private static final Connection EMPTY_CON = new Connection();

    private ComponentMapper<ConnectionReference> mConnectionReference;
    private ComponentMapper<Connection> mConnection;
    @NonNull
    private List<IConnectionListener> listeners = new LinkedList<>();

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
	final Connection conn = mConnection.get(
	    refA.links.get(refA.connectedEntities.indexOf(entityB))
	);
	if (!conn.getLocalACoords().contains(localPositionA, false)){
	    conn.getLocalACoords().add(localPositionA);
	    conn.getLocalBCoords().add(localPositionB);
	    // alert listeners
	    listeners.forEach(l -> l.onConnectionAdded(entityA, entityB, localPositionA, localPositionB));
	}
    } 

    public void removeAllConnectivity(int entity) {
	// Calls our onRemoved() method below
	mConnection.remove(entity);
    }

    public void removeConnections(int entity) {
	final ConnectionReference ref = mConnectionReference.getSafe(
	    entity, EMPTY_REF
	);
	for (int connectedEntity : IntBags.toSet(ref.connectedEntities)) {
	    removeConnections(entity, connectedEntity);
	}
    }

    public void removeConnections(int entityA, int entityB) {
	final ConnectionReference refA = mConnectionReference.getSafe(
	    entityA, EMPTY_REF
	);
	if (refA.connectedEntities.contains(entityB)){
	    final int index = refA.connectedEntities.indexOf(entityB);
	    final int connId = refA.links.get(index);
	    mConnection.remove(connId);
	    refA.connectedEntities.removeValue(entityB);
	    refA.links.removeValue(connId);
	    final ConnectionReference refB = mConnectionReference.getSafe(
		entityB, EMPTY_REF
	    );
	    refB.connectedEntities.removeValue(entityA);
	    refB.links.removeValue(connId);
	    listeners.forEach(l -> l.onConnectionsRemoved(entityA, entityB));
	}
    }

    public void addConnectionListener(@NonNull IConnectionListener listener) {
	listeners.add(listener);
    }

    public void removeConnectionListener(@NonNull IConnectionListener listener) {
	listeners.remove(listener);
    }

    // When an entity is straight up destroyed, we need to clean up.
    @Removed
    @All(ConnectionReference.class)
    public void onRemoved(int id) {
	removeConnections(id);
    }

    public interface IConnectionListener {
	void onConnectionAdded(
	    int entityA, int entityB,
	    Vector2 localPositionA, Vector2 localPositionB
	);

	void onConnectionsRemoved(
	    int entityA, int entityB
	);
    }
}
