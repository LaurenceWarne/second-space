package laurencewarne.secondspace.server.system;

import java.util.Arrays;
import java.util.HashSet;

import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.utils.IntBag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.server.collect.IntBags;
import laurencewarne.secondspace.server.component.Physics;
import laurencewarne.secondspace.server.component.Ship;
import laurencewarne.secondspace.server.component.ShipPart;
import laurencewarne.secondspace.server.manager.ConnectionManager;
import laurencewarne.secondspace.server.ship.Ships;

@All({ShipPart.class, Physics.class})
public class ShipPartRemovalSystem extends BaseEntitySystem {

    private ComponentMapper<Ship> mShip;
    private ComponentMapper<ShipPart> mShipPart;
    private ConnectionManager connectionManager;
    private final Logger logger = LoggerFactory.getLogger(
	ShipPartRemovalSystem.class
    );
    
    @Override
    public void processSystem() {
	connectionManager = world.getSystem(ConnectionManager.class);
    }

    @Override
    public void removed(int id) {
	// Does EntityLinkManager remove this for us?
	mShip.get(mShipPart.get(id).shipId).parts.removeValue(id);
	final IntBag adjEntities = Ships.getAdjacentEntities(
		id, connectionManager, new HashSet<>()
	);
	for (int adjPartId : IntBags.toSet(adjEntities)) {
	    // Check the entity connected to the ship is in fact still connected
	    final IntBag allConnectedEntities = Ships.getConnectedParts(
		adjPartId, connectionManager
	    );
	    boolean isConnectedToShip = Arrays
		.stream(allConnectedEntities.getData())
		.boxed()
		.map(e -> mShipPart.get(e))
		.anyMatch(part -> part.isController());
	    if (!isConnectedToShip) {
		// destroy ShipPart since not connected to ship
		for (int entity : IntBags.toList(allConnectedEntities)) {
		    // Calls this method again in turn
		    mShipPart.remove(entity);
		}
	    }
	}
    }
}
