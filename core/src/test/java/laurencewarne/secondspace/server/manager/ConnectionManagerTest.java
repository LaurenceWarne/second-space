package laurencewarne.secondspace.server.manager;

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

public class ConnectionManagerTest {

    private World world;
    private ConnectionManager cm;
    private ComponentMapper<ConnectionReference> mConnRef;
    private ComponentMapper<Connection> mConn;

    @Before
    public void setUp() {
	WorldConfiguration setup = new WorldConfigurationBuilder()
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

}
