package laurencewarne.secondspace.server.system;

import java.util.Arrays;

import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.badlogic.gdx.utils.IntArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.server.component.Physics;
import laurencewarne.secondspace.server.component.PhysicsRectangleData;
import laurencewarne.secondspace.server.component.ShipPart;
import laurencewarne.secondspace.server.component.ShipPartConnections;

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
	    final ShipPartConnections connections = mShipPartConnections.get(id);
	    final IntArray adjEntities = connections
		.getEntityToConnectionLocationMapping()
		.keys()
		.toArray();
	    for (int adjPartId : adjEntities.items) {
		// Check the entity connected to the ship is in fact still connected
		IntArray allConnectedEntities = null;
		boolean isConnectedToShip = Arrays.stream(allConnectedEntities.items)
		    .boxed()
		    .map(e -> mShipPart.get(e))
		    .anyMatch(part -> part.isController());
		if (!isConnectedToShip) {
		    // destroy 
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
