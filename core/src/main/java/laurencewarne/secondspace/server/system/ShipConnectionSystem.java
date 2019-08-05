package laurencewarne.secondspace.server.system;

import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import laurencewarne.secondspace.server.component.Connections;
import laurencewarne.secondspace.server.component.PhysicsRectangleData;
import laurencewarne.secondspace.server.component.Ship;
import laurencewarne.secondspace.server.component.ShipPart;
import laurencewarne.secondspace.server.component.WeldJointData;
import laurencewarne.secondspace.server.manager.ConnectionManager;
import laurencewarne.secondspace.server.ship.Rectangles;
import laurencewarne.secondspace.server.ship.ShipCoordinateLocaliser;
import laurencewarne.secondspace.server.ship.Ships;
import lombok.NonNull;

/**
 * This system connects entities which have {@link ShipPart} components to other components on their ship when their {@link ShipPart} component has just been added. This system is not responsible for adding welds.
 */
@All({ShipPart.class, PhysicsRectangleData.class})
public class ShipConnectionSystem extends BaseEntitySystem {

    private ComponentMapper<ShipPart> mShipPart;
    private ComponentMapper<WeldJointData> mWeldJointData;
    private ComponentMapper<PhysicsRectangleData> mRecData;
    private ComponentMapper<Ship> mShip;
    private ComponentMapper<Connections> mConnections;
    private ConnectionManager connectionManager;

    @Wire
    private ShipCoordinateLocaliser shipCoordLocaliser;

    @Override
    public void initialize() {
	connectionManager = world.getSystem(ConnectionManager.class);
    }

    @Override
    public void processSystem() {
	
    }    

    // This method will also be called when ShipParts are loaded from file, but we
    // ignore these cases by not adding connections between entities which are
    // already connected
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
	final Rectangle shipRectangle = Ships.getRectangleInShipSpace(
	    newPart, recData
	);
	final Iterable<Vector2> connectionPoints = Rectangles.getPointsOnEdge(
	    shipRectangle, 0.5f, 1f
	);
	// Don't add any welds between already connected entities
	final IntBag alreadyConnected = connectionManager.getConnectedEntities(id);

	////////////////////////////////
	// Add correct weld positions //
	////////////////////////////////
	for (Vector2 connectionCoordinate : connectionPoints) {
	    // Check if there's an existing ship part where the connection is
	    final IntBag entitiesOnConnection = Ships.getShipPartsOnPoint(
		otherShipParts, mShipPart,
		mRecData, connectionCoordinate
	    );
	    // Just to make sure, there should be max 2 values in the bag
	    entitiesOnConnection.removeValue(id);
	    // We check an adjacent part exists
	    final boolean createConnection = !entitiesOnConnection.isEmpty() &&
		!alreadyConnected.contains(entitiesOnConnection.get(0));
	    if (createConnection){
		final int adjEntityId = entitiesOnConnection.get(0);
		connectParts(id, adjEntityId, connectionCoordinate);
	    }
	}
    }

    public void connectParts(
	int entityAId, int entityBId, @NonNull Vector2 location
    ) {
	final ShipPart partA = mShipPart.get(entityAId);
	final PhysicsRectangleData recDataA = mRecData.get(entityAId);
	final Vector2 localA = shipCoordLocaliser.shipToCellCoords(
	    location, new Vector2(partA.getLocalX(), partA.getLocalY()), 
	    recDataA.getWidth(), recDataA.getHeight()
	);
	final ShipPart partB = mShipPart.get(entityBId);
	final PhysicsRectangleData recDataB = mRecData.get(entityBId);
	final Vector2 localB = shipCoordLocaliser.shipToCellCoords(
	    location, new Vector2(partB.getLocalX(), partB.getLocalY()), 
	    recDataB.getWidth(), recDataB.getHeight()
	);
	connectionManager.createConnection(
	    entityAId, entityBId, localA, localB
	);
    }
}
