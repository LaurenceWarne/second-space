package laurencewarne.secondspace.server.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.math.Vector2;

import org.junit.Before;
import org.junit.Test;

import laurencewarne.secondspace.server.component.connection.Connection;
import laurencewarne.secondspace.server.component.connection.ConnectionReference;
import lombok.NonNull;
import net.fbridault.eeel.EEELPlugin;
import net.mostlyoriginal.api.event.common.EventSystem;

public class ConnectionManagerTest {

    private World world;
    private ConnectionManager cm;
    private ComponentMapper<ConnectionReference> mConnRef;
    private ComponentMapper<Connection> mConn;

    @Before
    public void setUp() {
	WorldConfiguration setup = new WorldConfigurationBuilder()
	    .with(new EEELPlugin())
	    .with(new EventSystem())
	    .with(cm = new ConnectionManager())
	    .build();
	world = new World(setup);
	mConnRef = world.getMapper(ConnectionReference.class);
	mConn = world.getMapper(Connection.class);
	world.process();
    }

    public void createConnection(
	 int entityA, int entityB, int connEntity,
	 @NonNull Vector2 localPositionA, @NonNull Vector2 localPositionB
    ) {
	if (!mConnRef.has(entityA)) {
	    mConnRef.create(entityA);
	    mConnRef.get(entityA).connectedEntities.add(entityB);
	    mConnRef.get(entityA).links.add(connEntity);
	}
	if (!mConnRef.has(entityB)) {
	    mConnRef.create(entityB);
	    mConnRef.get(entityB).connectedEntities.add(entityA);
	    mConnRef.get(entityB).links.add(connEntity);
	}
	if (!mConn.has(connEntity)){
	    System.out.println("Creating new");
	    mConn.create(connEntity);	    
	}
	Connection conn = mConn.get(connEntity);
	conn.entityAId = entityA;
	conn.entityBId = entityB;
	conn.getLocalACoords().add(localPositionA);
	conn.getLocalBCoords().add(localPositionB);
    }

    @Test
    public void testExistsConnectionReturnsFalseOnNonExistantEntities() {
	assertFalse(cm.existsConnection(123, 3412, new Vector2()));
    }

    @Test
    public void testExistsConnectionReturnsFalseOnUnconnectedEntities() {
	int id1 = world.create(), id2 = world.create();
	mConnRef.create(id1);
	mConnRef.create(id2);
	assertFalse(cm.existsConnection(id1, id2, new Vector2()));
    }

    @Test
    public void testExistsConnectionReturnsTrueOnConnectedEntities() {
	int id1 = world.create(), id2 = world.create();
	int idc = world.create();
	createConnection(id1, id2, idc, new Vector2(), new Vector2());
	assertTrue(cm.existsConnection(id1, id2, new Vector2()));
	assertTrue(cm.existsConnection(id2, id1, new Vector2()));
    }

    @Test
    public void testExistsConnectionReturnsTrueOnManyConnectionPoints() {
	int id1 = world.create(), id2 = world.create();
	int idc = world.create();
	for (int i = 0; i < 10; i++){
	    createConnection(id1, id2, idc, new Vector2(i, 0f), new Vector2(0f, i));
	    assertTrue(cm.existsConnection(id1, id2, new Vector2(i, 0f)));
	    assertTrue(cm.existsConnection(id2, id1, new Vector2(0f, i)));
	}
    }

    @Test
    public void testExistsConnectionReturnsTrueOnManyConnectedEntities() {
	int id1 = world.create(), id2 = world.create();
	int idc = world.create();
	createConnection(id1, id2, idc, new Vector2(), new Vector2());
	for (int i = 0; i < 10; i++){
	    int id3 = world.create();
	    int idc2 = world.create();
	    createConnection(id1, id3, idc2, new Vector2(i, 0f), new Vector2(0f, i));
	}
	assertTrue(cm.existsConnection(id1, id2, new Vector2()));
	assertTrue(cm.existsConnection(id2, id1, new Vector2()));	
    }

    @Test
    public void testCanCreateConnectionBetweenExistingEntities() {
	int id1 = world.create(), id2 = world.create();
	cm.createConnection(id1, id2, new Vector2(), new Vector2());
	world.process();
	assertTrue(mConnRef.has(id1));
	assertTrue(mConnRef.has(id2));
	ConnectionReference ref1 = mConnRef.get(id1);
	assertTrue(ref1.connectedEntities.contains(id2));
	ConnectionReference ref2 = mConnRef.get(id2);
	assertTrue(ref2.connectedEntities.contains(id1));
    } 

    @Test
    public void testCreatedConnectionInCorrectPlace() {
	int id1 = world.create(), id2 = world.create();
	cm.createConnection(id1, id2, new Vector2(1f, 0f), new Vector2(0f, 10f));
	world.process();
	ConnectionReference ref = mConnRef.get(id1);
	Connection conn = mConn.get(ref.links.get(0));
	assertEquals(new Vector2(1f, 0f), conn.getLocalACoords().get(0));
	assertEquals(new Vector2(0f, 10f), conn.getLocalBCoords().get(0));
    } 

    @Test
    public void testMultipleCreatedConnectionsInCorrectPlace() {
	int id1 = world.create(), id2 = world.create();
	for (int i = 0; i < 10; i++){
	    cm.createConnection(id1, id2, new Vector2(i, 0), new Vector2(0, i));
	}
	world.process();
	ConnectionReference ref = mConnRef.get(id1);
	Connection conn = mConn.get(ref.links.get(0));
	for (int i = 0; i < 10; i++){
	    assertEquals(new Vector2(i, 0f), conn.getLocalACoords().get(i));
	    assertEquals(new Vector2(0f, i), conn.getLocalBCoords().get(i));
	}
    }

    @Test
    public void testDuplicateConnectionNotCreated() {
	int id1 = world.create(), id2 = world.create();
	cm.createConnection(id1, id2, new Vector2(1f, 0f), new Vector2(0f, 10f));
	world.process();
	cm.createConnection(id1, id2, new Vector2(1f, 0f), new Vector2(0f, 10f));
	world.process();
	ConnectionReference ref = mConnRef.get(id1);
	Connection conn = mConn.get(ref.links.get(0));
	assertEquals(1, conn.getLocalACoords().size);
	assertEquals(1, conn.getLocalBCoords().size);
    }

    @Test
    public void testRemovingEntityRemovesConnections() {
	int id1 = world.create(), id2 = world.create();
	for (int i = 0; i < 10; i++){
	    cm.createConnection(id1, id2, new Vector2(i, 0), new Vector2(0, i));
	}
	world.process();
	ConnectionReference ref = mConnRef.get(id2);
	int connId = ref.links.get(0);
	world.delete(id1);
	world.process();
	assertFalse(mConn.has(connId));
	assertFalse(ref.connectedEntities.contains(id1));
	assertFalse(ref.links.contains(connId));
    }

    @Test
    public void testRemoveConnectionsRemovesConnectionsBetweenEntities() {
	int id1 = world.create(), id2 = world.create();
	for (int i = 0; i < 10; i++){
	    cm.createConnection(id1, id2, new Vector2(i, 0), new Vector2(0, i));
	}
	world.process();
	ConnectionReference ref1 = mConnRef.get(id1);
	ConnectionReference ref2 = mConnRef.get(id2);
	int connId = ref1.links.get(0);
	cm.removeConnections(id1, id2);
	world.process();
	assertTrue(mConnRef.has(id1));
	assertTrue(mConnRef.has(id2));
	assertFalse(mConn.has(connId));
	assertFalse(ref1.connectedEntities.contains(id2));
	assertFalse(ref1.links.contains(connId));
	assertFalse(ref2.connectedEntities.contains(id1));
	assertFalse(ref2.links.contains(connId));
    }

    @Test
    public void testRemoveConnectionsDoesNotAffectOtherConnections() {
	int id1 = world.create(), id2 = world.create(), id3 = world.create();
	for (int i = 0; i < 10; i++){
	    cm.createConnection(id1, id2, new Vector2(i, 0), new Vector2(0, i));
	}
	for (int i = 0; i < 10; i++){
	    cm.createConnection(id1, id3, new Vector2(0, i), new Vector2(0, i));
	}
	world.process();
	cm.removeConnections(id1, id2);
	ConnectionReference ref1 = mConnRef.get(id1);
	assertEquals(id3, ref1.connectedEntities.get(0));
    }

}
