package laurencewarne.secondspace.server.system;

import java.util.HashSet;

import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.link.EntityLinkManager;
import com.artemis.link.LinkAdapter;
import com.artemis.utils.IntBag;
import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.server.collect.IntBags;
import laurencewarne.secondspace.server.component.ShipPart;
import laurencewarne.secondspace.server.manager.ConnectionManager;
import laurencewarne.secondspace.server.ship.Ships;

@All(ShipPart.class)
public class ShipPartRemovalSystem extends BaseEntitySystem {

    private ComponentMapper<ShipPart> mShipPart;
    private ConnectionManager connectionManager;
    private final Logger logger = LoggerFactory.getLogger(
	ShipPartRemovalSystem.class
    );
    
    @Override
    public void initialize() {
	connectionManager = world.getSystem(ConnectionManager.class);
	world.getSystem(EntityLinkManager.class).register(
	    ShipPart.class, 
	    new LinkAdapter() {

		@Override
		public void onTargetDead(int sourceId, int deadTargetId) {
		    // deadTargetId is the deleted Ship currently referenced
		    // by ShipPart, owned by entity with sourceId
		    mShipPart.remove(sourceId);
		}
	    });
    }

    @Override
    public void processSystem() {
	
    }

    @Override
    public void removed(int id) {
	// -1 for shipId => LinkAdapter above has already dealt with all ShipParts
	if (mShipPart.get(id) != null && mShipPart.get(id).shipId != -1) {
	    final IntBag adjEntities = Ships.getAdjacentEntities(
		id, connectionManager, new HashSet<>()
	    );
	    for (int adjPartId : IntBags.toSet(adjEntities)) {
		// Check the entity connected to the ship is in fact still connected
		final IntBag allConnectedEntities = Ships.getConnectedParts(
		    adjPartId, connectionManager, Sets.newHashSet(id)
		);
		
		boolean isConnectedToShip = IntBags.toSet(allConnectedEntities)
		    .stream()
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
}
