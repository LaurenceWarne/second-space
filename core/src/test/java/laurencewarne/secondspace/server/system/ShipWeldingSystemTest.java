package laurencewarne.secondspace.server.system;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.math.Vector2;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import laurencewarne.secondspace.server.component.PhysicsRectangleData;
import laurencewarne.secondspace.server.component.ShipPart;
import laurencewarne.secondspace.server.component.WeldJointData;
import laurencewarne.secondspace.server.ship.ShipCoordinateLocaliser;

public class ShipWeldingSystemTest {

    private World world;
    private ShipWeldingSystem sys;
    private ComponentMapper<WeldJointData> mWeldJointData;
    private int entityAId, entityBId;

    @Before
    public void setUp() {
	WorldConfiguration setup = new WorldConfigurationBuilder()
	    .with(
		sys = new ShipWeldingSystem()
	    )
	    .build();
	setup.register(new ShipCoordinateLocaliser());
	world = new World(setup);
	entityAId = world.create();
	entityBId = world.create();
	ComponentMapper<WeldJointData> m = world.getMapper(
	    WeldJointData.class
	);
	mWeldJointData = Mockito.spy(m);

	ShipPart partA = world.edit(entityAId).create(ShipPart.class);
	PhysicsRectangleData dataA =
	    world.edit(entityAId).create(PhysicsRectangleData.class);
	partA.setLocalX(0); partA.setLocalY(0);
	dataA.setWidth(1f); dataA.setHeight(2f);

	ShipPart partB = world.edit(entityBId).create(ShipPart.class);
	PhysicsRectangleData dataB =
	    world.edit(entityBId).create(PhysicsRectangleData.class);
	partB.setLocalX(1); partB.setLocalY(0);
	dataB.setWidth(2f); dataB.setHeight(2f);
    }

    @Test
    public void testCanCreateValidConnectionWherePartsTouch() {
	Vector2 coordinate = new Vector2(1f, 0.5f);
	sys.createConnection(coordinate, entityAId, entityBId);

	//Mockito.verify(mWeldJointData).create(anyInt());
    }

}
