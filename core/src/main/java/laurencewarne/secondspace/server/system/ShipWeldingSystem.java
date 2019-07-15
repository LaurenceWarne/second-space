package laurencewarne.secondspace.server.system;

import java.util.ArrayList;
import java.util.List;

import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.badlogic.gdx.math.Vector2;

import laurencewarne.secondspace.server.component.PhysicsRectangleData;
import laurencewarne.secondspace.server.component.Ship;
import laurencewarne.secondspace.server.component.ShipPart;
import laurencewarne.secondspace.server.component.WeldJointData;

@All({ShipPart.class, PhysicsRectangleData.class})
public class ShipWeldingSystem extends BaseEntitySystem {

    private ComponentMapper<ShipPart> mShipPart;
    private ComponentMapper<WeldJointData> mWeldJointData;
    private ComponentMapper<PhysicsRectangleData> mPhysicsRectangleData;
    private ComponentMapper<Ship> mShip;

    @Override
    public void processSystem() {
	
    }    
    
    @Override
    public void inserted(int id) {
	final ShipPart newPart = mShipPart.get(id);
	////////////////////////////////////////////////////////
	// Get ship parts from ship the new part was added to //
	////////////////////////////////////////////////////////
	final List<ShipPart> shipParts = new ArrayList<>();
	for (int shipPartID : mShip.get(newPart.getShipID()).parts.getData()) {
	    shipParts.add(mShipPart.get(shipPartID));
	}

	/////////////////////////////////
	// Get possible weld positions //
	/////////////////////////////////
	final PhysicsRectangleData recData = mPhysicsRectangleData.get(id);
	final List<Vector2> connections = new ArrayList<>();
	final int x = newPart.getLocalX(), y = newPart.getLocalY();
	final int width = (int) recData.getWidth();
	final int height = (int) recData.getHeight();
	for (int i = x - 1; i < x + width + 1; i++){
	    for (int j = y - 1; j < y + height + 1; j++){
		if (i < x && y <= j && j < y + height){
		    connections.add(new Vector2(i + 1f, j + 0.5f));
		}
		else if (i == x + width && y <= j && j < y + height){
		    connections.add(new Vector2(i, j + 0.5f));
		}
		else if (j < y && x <= i && i< x + width){
		    connections.add(new Vector2(i + 1f, j + 0.5f));
		}
		else if (j == y + height && x <= i && i < x + width){
		    connections.add(new Vector2(i + 0.5f, j + 1f));
		}
	    }
	}

	////////////////////////////////
	// Add correct weld positions //
	////////////////////////////////
	for (Vector2 shipCoordinateVector2 : connections) {
	    // Check if there's an existing ship part where the connection is
	    List<ShipPart> adjShipParts = null;
	    if (!adjShipParts.isEmpty()){
		/*
		  Vector2 newPartCoord = shipCoordinateLocaliser.shipToLocalCoords(
		      connection.x, connection.y, newPart.id
		  )				
		  Vector2 adjCoord = shipCoordinateLocaliser.shipToLocalCoords(
		      connection.x, connection.y, adjShipPart.id
		  )
		  WeldJointData weldJointData = mWeldJointData.create()
		  weldJointData.setCellAID(newPart.id)
		  weldJointData.setCellBID(adjCoord.id)
		  weldJointData.setLocalAnchorA(newPartCoord)
		  weldJointData.setLocalAnchorB(adjPartCoord)
		*/
	    }
	}
    }    
}