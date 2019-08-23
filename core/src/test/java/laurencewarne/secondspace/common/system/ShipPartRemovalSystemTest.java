package laurencewarne.secondspace.common.system;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.link.EntityLinkManager;
import com.badlogic.gdx.math.Vector2;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import laurencewarne.secondspace.common.component.Ship;
import laurencewarne.secondspace.common.component.ShipPart;
import laurencewarne.secondspace.common.manager.ConnectionManager;
import net.fbridault.eeel.EEELPlugin;
import net.mostlyoriginal.api.event.common.EventSystem;

public class ShipPartRemovalSystemTest {

    private ComponentMapper<Ship> mShip;
    private ComponentMapper<ShipPart> ms;
    private World world;
    private ConnectionManager cm;
    private EventSystem es;

    private int shipId, part1Id, part2Id;
    private Ship ship;
    private ShipPart part1, part2;

    @Before
    public void setUp() {
	WorldConfiguration setup = new WorldConfigurationBuilder()
	    .dependsOn(EntityLinkManager.class)
	    .with(new EEELPlugin())
	    .with(es = Mockito.spy(new EventSystem()))
	    .with(
		cm = new ConnectionManager(),
		new ShipPartRemovalSystem()
	    )
	    .build();
	world = new World(setup);
	ms = world.getMapper(ShipPart.class);
	mShip = world.getMapper(Ship.class);

	shipId = world.create();
	ship = mShip.create(shipId);
	part1Id = world.create();
	part1 = ms.create(part1Id);
	ship.parts.add(part1Id);
	part1.shipId = shipId;
	part2Id = world.create();
	part2 = ms.create(part2Id);
	part2.shipId = shipId;
	ship.parts.add(part2Id);
	world.process();
    }

    @Test
    public void testShipPartRemovedOnShipDeletion() {
	world.delete(shipId);
	world.process();
	assertFalse(ms.has(part1Id));
    }

    @Test
    public void testControllerPartNotRemovedOnOtherRemoval() {
	part1.setController(true);
	world.process();

	cm.createConnection(part1Id, part2Id, new Vector2(), new Vector2());
	world.delete(part2Id);
	world.process();
	assertTrue(ms.has(part1Id));
    }

    @Test
    public void testCorrectPartsRemovedOnConnectedShip() {
	int part3Id = world.create();
	ShipPart part3 = ms.create(part3Id);
	ship.parts.add(part3Id);
	part3.shipId = shipId;
	int part4Id = world.create();
	ShipPart part4 = ms.create(part4Id);
	part4.shipId = shipId;
	ship.parts.add(part4Id);
	part1.setController(true);
	world.process();

	cm.createConnection(part1Id, part2Id, new Vector2(), new Vector2());
	cm.createConnection(part2Id, part3Id, new Vector2(), new Vector2());
	cm.createConnection(part3Id, part4Id, new Vector2(), new Vector2());
	world.process();

	world.delete(part3Id);
	world.process();
	assertFalse(ms.has(part3Id));
	assertFalse(ms.has(part4Id));
	assertTrue(ms.has(part1Id));
	assertTrue(ms.has(part2Id));
    }
}
