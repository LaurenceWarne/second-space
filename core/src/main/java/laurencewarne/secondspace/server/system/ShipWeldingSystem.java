package laurencewarne.secondspace.server.system;

import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

import laurencewarne.secondspace.server.component.PhysicsRectangleData;
import laurencewarne.secondspace.server.component.Ship;
import laurencewarne.secondspace.server.component.ShipPart;
import laurencewarne.secondspace.server.component.ShipPartConnections;
import laurencewarne.secondspace.server.component.WeldJointData;
import laurencewarne.secondspace.server.ship.Rectangles;
import laurencewarne.secondspace.server.ship.ShipCoordinateLocaliser;
import laurencewarne.secondspace.server.ship.Ships;
import lombok.NonNull;

@All({ShipPart.class, PhysicsRectangleData.class})
public class ShipWeldingSystem extends BaseEntitySystem {

    private ComponentMapper<ShipPart> mShipPart;
    private ComponentMapper<WeldJointData> mWeldJointData;
    private ComponentMapper<PhysicsRectangleData> mRecData;
    private ComponentMapper<Ship> mShip;
    private ComponentMapper<ShipPartConnections> mShipPartConnections;

    @Wire
    private ShipCoordinateLocaliser shipCoordLocaliser;

    @Override
    public void processSystem() {
	
    }    

    public void createConnection(
	@NonNull Vector2 shipConnectionCoordinate,
	int entityAId, int entityBId
    ) {
	final ShipPart aPart = mShipPart.get(entityAId);
	final PhysicsRectangleData aRecData = mRecData.get(entityAId);
	// validation
	final Vector2 aPartCoord = shipCoordLocaliser.shipToCellCoords(
	    shipConnectionCoordinate,
	    new Vector2(aPart.getLocalX(), aPart.getLocalY()),
	    aRecData.getWidth(),
	    aRecData.getHeight()
	);
	final ShipPart bPart = mShipPart.get(entityBId);
	final PhysicsRectangleData bRecData = mRecData.get(entityBId);
	final Vector2 bPartCoord = shipCoordLocaliser.shipToCellCoords(
	    shipConnectionCoordinate,
	    new Vector2(bPart.getLocalX(), bPart.getLocalY()),
	    bRecData.getWidth(),
	    bRecData.getHeight()
	);
	final WeldJointData weldJointData = mWeldJointData.create(
	    world.create()
	);
	weldJointData.setCellAID(entityAId);
	weldJointData.setCellBID(entityBId);
	weldJointData.setLocalAnchorA(aPartCoord);
	weldJointData.setLocalAnchorB(bPartCoord);	
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
	if (!mShipPartConnections.has(id)){
	    mShipPartConnections.create(id);
	}
	final IntMap<Array<Vector2>> allConnections = mShipPartConnections
	    .get(id)
	    .getEntityToConnectionLocationMapping();
	final Iterable<Vector2> connectionPoints = Rectangles.getPointsOnEdge(
	    shipRectangle, 0.5f, 1f
	);

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
	    // We check a adjacent part exists
	    if (!entitiesOnConnection.isEmpty()){
		final int adjEntityId = entitiesOnConnection.get(0);
		// Don't want to make the connection if it already exists
		final Array<Vector2> existingConnsWithAdj =
		    allConnections.get(adjEntityId, new Array<>());
		if (!existingConnsWithAdj.contains(connectionCoordinate, false)){
		    // TODO add try/catches here
		    // Here we add the PHYSICAL connection
		    createConnection(connectionCoordinate, id, adjEntityId);
		    // Add connection to conn component of entity with the new part
		    final Array<Vector2> connectionsArr = allConnections
			.get(adjEntityId, new Array<>());
		    connectionsArr.add(connectionCoordinate);
		    allConnections.put(adjEntityId, connectionsArr);
		    // Add connection to conn component of adj entityu
		    if (!mShipPartConnections.has(adjEntityId)) {
			mShipPartConnections.create(adjEntityId);
		    }		    
		    final IntMap<Array<Vector2>> adjConnectionsMap =
			mShipPartConnections
			.get(adjEntityId)
			.getEntityToConnectionLocationMapping();
		    final Array<Vector2> adjConnectionsArr = adjConnectionsMap
			.get(id, new Array<>());
		    adjConnectionsArr.add(connectionCoordinate);
		    adjConnectionsMap.put(id, adjConnectionsArr);
		}
	    }
	}
    }    
}
