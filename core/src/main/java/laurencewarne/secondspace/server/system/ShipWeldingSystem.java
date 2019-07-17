package laurencewarne.secondspace.server.system;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import laurencewarne.secondspace.server.component.PhysicsRectangleData;
import laurencewarne.secondspace.server.component.Ship;
import laurencewarne.secondspace.server.component.ShipPart;
import laurencewarne.secondspace.server.component.WeldJointData;
import laurencewarne.secondspace.server.ship.Rectangles;
import laurencewarne.secondspace.server.ship.ShipCoordinateLocaliser;
import laurencewarne.secondspace.server.ship.Ships;

@All({ShipPart.class, PhysicsRectangleData.class})
public class ShipWeldingSystem extends BaseEntitySystem {

    private ComponentMapper<ShipPart> mShipPart;
    private ComponentMapper<WeldJointData> mWeldJointData;
    private ComponentMapper<PhysicsRectangleData> mRecData;
    private ComponentMapper<Ship> mShip;

    @Wire
    private ShipCoordinateLocaliser shipCoordLocaliser;
    private final Set<Integer> entitiesProcessedThisRound = new HashSet<>();

    @Override
    public void processSystem() {
	
    }    
    
    @Override
    public void inserted(IntBag entities) {
	/* Triggers right after a system finishes processing. Adding and 
	 * immediately removing a component does not permanently change the
	 * composition and will prevent this method from being called.
	 */
	entitiesProcessedThisRound.clear();
	super.inserted(entities);
    }

    @Override
    public void inserted(int id) {
	final ShipPart newPart = mShipPart.get(id);
	////////////////////////////////////////////////////////
	// Get ship parts from ship the new part was added to //
	////////////////////////////////////////////////////////
	final IntBag otherShipParts = mShip.get(newPart.shipId).parts;

	/////////////////////////////////
	// Get possible weld positions //
	/////////////////////////////////
	final PhysicsRectangleData recData = mRecData.get(id);
	final Rectangle shipRectangle = new Rectangle(
	    newPart.getLocalX(), newPart.getLocalY(),
	    recData.getWidth(), recData.getHeight()
	);
	final Iterable<Vector2> connections = Rectangles.getPointsOnEdge(
	    shipRectangle, 0.5f, 1f
	);

	////////////////////////////////
	// Add correct weld positions //
	////////////////////////////////
	for (Vector2 connectionCoordinate : connections) {
	    // Check if there's an existing ship part where the connection is
	    final IntBag entitiesOnConnection = Ships.getShipPartsOnPoint(
		otherShipParts, mShipPart,
		mRecData, connectionCoordinate
	    );
	    // Just to make sure
	    entitiesOnConnection.removeValue(id);
	    // We check a adjacent part exists and that the adj part isn't already
	    // welded to the new part in the exact same position
	    boolean shouldAddWeld = !entitiesOnConnection.isEmpty() &&
		!entitiesProcessedThisRound.contains(entitiesOnConnection.get(0));
	    if (shouldAddWeld){
		final int adjEntityId = entitiesOnConnection.get(0);
		final Vector2 newPartCoord = shipCoordLocaliser.shipToCellCoords(
		    connectionCoordinate,
		    new Vector2(newPart.getLocalX(), newPart.getLocalY()),
		    recData.getWidth(),
		    recData.getHeight()
		);
		final ShipPart adjPart = mShipPart.get(adjEntityId);
		final PhysicsRectangleData adjRecData = mRecData.get(adjEntityId);
		final Vector2 adjPartCoord = shipCoordLocaliser.shipToCellCoords(
		    connectionCoordinate,
		    new Vector2(adjPart.getLocalX(), adjPart.getLocalY()),
		    adjRecData.getWidth(),
		    adjRecData.getHeight()
		);
		final WeldJointData weldJointData = mWeldJointData.create(
		    world.create()
		);
		weldJointData.setCellAID(id);
		weldJointData.setCellBID(adjEntityId);
		weldJointData.setLocalAnchorA(newPartCoord);
		weldJointData.setLocalAnchorB(adjPartCoord);
	    }
	}
	entitiesProcessedThisRound.add(id);
    }    
}
