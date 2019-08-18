package laurencewarne.secondspace.common.manager;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Vector2;

import laurencewarne.secondspace.common.collect.IntBags;
import laurencewarne.secondspace.common.component.connection.Connection;
import laurencewarne.secondspace.common.component.connection.ConnectionReference;
import lombok.NonNull;
import lombok.Value;
import net.fbridault.eeel.annotation.All;
import net.fbridault.eeel.annotation.Inserted;
import net.fbridault.eeel.annotation.Removed;
import net.mostlyoriginal.api.event.common.Event;
import net.mostlyoriginal.api.event.common.EventSystem;

/**
 * Provides a simplified interface to the components involved in entity connections, which are complex owing to the need to have them serializable.
 */
public class ConnectionManager extends BaseSystem {

    private static final ConnectionReference EMPTY_REF = new ConnectionReference();
    private static final Connection EMPTY_CON = new Connection();

    private ComponentMapper<ConnectionReference> mConnectionReference;
    private ComponentMapper<Connection> mConnection;
    private EventSystem es;

    @Override
    public void processSystem() {
	
    }

    public IntBag getConnectedEntities(int entity) {
	if (mConnectionReference.has(entity)) {
	    final ConnectionReference ref = mConnectionReference.get(entity);
	    return IntBags.copyOf(ref.connectedEntities);
	}
	else {
	    return new IntBag();
	}
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
	    if (entityA == conn.entityAId) {
		return conn.getLocalACoords().contains(position, false);
	    }
	    else {
		return conn.getLocalBCoords().contains(position, false);
	    }
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
	boolean dispatch = true;
	if (!refA.connectedEntities.contains(entityB)) {
	    refA.connectedEntities.add(entityB);
	    refB.connectedEntities.add(entityA);
	    final int connId = world.create();
	    final Connection conn = mConnection.create(connId);
	    conn.entityAId = entityA;
	    conn.entityBId = entityB;
	    refA.links.add(connId);
	    refB.links.add(connId);
	    dispatch = false;  // Our onConnectionAdded() will take care of things
	}
	final Connection conn = mConnection.get(
	    refA.links.get(refA.connectedEntities.indexOf(entityB))
	);
	if (!conn.getLocalACoords().contains(localPositionA, false)){
	    conn.getLocalACoords().add(localPositionA);
	    conn.getLocalBCoords().add(localPositionB);
	    if (dispatch) {
		es.dispatch(new ConnectionAddedEvent(entityA, entityB, localPositionA, localPositionB));		
	    }
	}
    } 

    /**
     * Remove connections between the specified entity and all other entities, in addition to removing the entity's {@link ConnectionReference} component.
     *
     * @param entity entity to remove connections from
     */
    public void removeAllConnectivity(int entity) {
	// Calls our onRemoved() method below
	mConnectionReference.remove(entity);
    }

    /**
     * Remove connections between the specified entity and all other entities. The specified entity will still have a {@link ConnectionReference} component.
     *
     * @param entity entity to remove connections from
     */
    public void removeConnections(int entity) {
	final ConnectionReference ref = mConnectionReference.getSafe(
	    entity, EMPTY_REF
	);
	for (int connectedEntity : IntBags.toSet(ref.connectedEntities)) {
	    removeConnections(entity, connectedEntity);
	}
    }

    /**
     * Remove all forms of connection between two entities. Does not affect connectedness with the specified entities and other entities.
     *
     * @param entityA
     * @param entityB
     */
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
	    es.dispatch(new ConnectionsRemovedEvent(entityA, entityB));
	}
    }

    // When an entity is straight up destroyed, we need to clean up.
    @Removed
    @All(ConnectionReference.class)
    public void onRemoved(int id) {
	removeConnections(id);
    }

    @Inserted
    @All(Connection.class)
    public void onConnectionAdded(int id) {
	final Connection conn = mConnection.get(id);
	for (int i = 0; i < conn.getLocalACoords().size; i++){
	    final ConnectionAddedEvent evt = new ConnectionAddedEvent(
		conn.entityAId, conn.entityBId,
		conn.getLocalACoords().get(i), conn.getLocalBCoords().get(i)
	    );
	    es.dispatch(evt);
	}
    }

    /**
     * Signifies all connections between two entities have been removed.
     */
    @Value
    public static class ConnectionsRemovedEvent implements Event {
	private final int entityA;
	private final int entityB;
    }

    /**
     * Signifies a connection between two entities has been added.
     */
    @Value
    public static class ConnectionAddedEvent implements Event {
	private final int entityA;
	private final int entityB;
	private final Vector2 localPositionA;
	private final Vector2 localPositionB;
    }
}
