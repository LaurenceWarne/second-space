package laurencewarne.secondspace.common.system;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.link.EntityLinkManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import laurencewarne.secondspace.common.component.PhysicsRectangleData;
import laurencewarne.secondspace.common.component.Ship;
import laurencewarne.secondspace.common.component.ShipPart;
import laurencewarne.secondspace.common.manager.ConnectionManager;
import laurencewarne.secondspace.common.ship.ShipCoordinateLocaliser;
import net.mostlyoriginal.api.event.common.EventSystem;


public class ShipConnectionSystemTest {

    private World world;
    private ShipConnectionSystem sys;
    private ComponentMapper<ShipPart> m;
    private ConnectionManager cm;
    private int e1, e2;
    private ShipPart p1, p2;
    private PhysicsRectangleData r1, r2;

    @Before
    public void setUp() {
	WorldConfiguration setup = new WorldConfigurationBuilder()
	    .dependsOn(EntityLinkManager.class)
	    .with(new EventSystem())
	    .with(
		cm = Mockito.spy(new ConnectionManager()),
		sys = new ShipConnectionSystem()
	    )
	    .build();
	setup.register(new ShipCoordinateLocaliser());
	world = new World(setup);
	m = world.getMapper(ShipPart.class);
	e1 = world.create();
	e2 = world.create();

    }

    public void createParts() {
	int s = world.create();
	Ship ship = world.edit(s).create(Ship.class);
	ship.parts.add(e1);
	ship.parts.add(e2);

	p1 = world.edit(e1).create(ShipPart.class);
	r1 = world.edit(e1).create(PhysicsRectangleData.class);
	p1.shipId = s;

	p2 = world.edit(e2).create(ShipPart.class);
	r2 = world.edit(e2).create(PhysicsRectangleData.class);
	p2.shipId = s;
    }

    @Test
    public void testCanCreateValidConnectionWherePartsTouch() {
	createParts();
	p1.setLocalX(0); p1.setLocalY(0);
	r1.setWidth(1f); r1.setHeight(2f);
	p2.setLocalX(1); p2.setLocalY(0);
	r2.setWidth(2f); r2.setHeight(2f);
    }

    @Test
    public void noConnectionsCreatedOnPartWithNoShip() {
	ShipPart p = new ShipPart();
	world.process();
    }
}
