package laurencewarne.secondspace.server.system;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.IntArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.server.collect.IntBags;
import laurencewarne.secondspace.server.component.Physics;
import laurencewarne.secondspace.server.component.PhysicsRectangleData;
import laurencewarne.secondspace.server.component.ShipPart;
import laurencewarne.secondspace.server.component.ShipPartConnections;
import laurencewarne.secondspace.server.ship.Ships;

@All({ShipPart.class, Physics.class})
public class ShipPartRemovalSystem extends BaseEntitySystem {

    private ComponentMapper<ShipPart> mShipPart;
    private ComponentMapper<PhysicsRectangleData> mRecData;
    private ComponentMapper<ShipPartConnections> mShipPartConnections;    
    private final Logger logger = LoggerFactory.getLogger(
	ShipPartRemovalSystem.class
    );
    
    @Override
    public void processSystem() {
	
    }

    @Override
    public void removed(int id) {
	if (mShipPartConnections.has(id)){
	    final Collection<Integer> adjEntities = IntBags.toList(
		Ships.getAdjacentEntities(
		    id, mShipPartConnections, new HashSet<>()
		)
	    );
	    for (int adjPartId : adjEntities) {
		// Check the entity connected to the ship is in fact still connected
		final IntBag allConnectedEntities = Ships.getConnectedParts(
		    adjPartId, mShipPartConnections
		);
		boolean isConnectedToShip = Arrays
		    .stream(allConnectedEntities.getData())
		    .boxed()
		    .map(e -> mShipPart.get(e))
		    .anyMatch(part -> part.isController());
		if (!isConnectedToShip) {
		    // destroy ShipPart and ShipPartConnections
		    for (int entity : IntBags.toList(allConnectedEntities)) {
			mShipPartConnections.remove(entity);
			mShipPart.remove(entity);
		    }
		}

	    }
	}
	else {
	    logger.error(
		"A ship part was removed, but the ship part had no" +
		" ShipPartConnection component, this is strange..."
	    );
	}
    }
}
