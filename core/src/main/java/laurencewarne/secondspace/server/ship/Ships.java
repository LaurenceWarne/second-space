package laurencewarne.secondspace.server.ship;

import com.artemis.ComponentMapper;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import laurencewarne.secondspace.server.component.PhysicsRectangleData;
import laurencewarne.secondspace.server.component.ShipPart;

public final class Ships {

    private Ships() {
	
    }

    public static Rectangle getRectangleInShipSpace(
	ShipPart shipPart, PhysicsRectangleData rectangleData) {
	final int x = shipPart.getLocalX();
	final int y = shipPart.getLocalY();
	final float width = rectangleData.getWidth();
	final float height = rectangleData.getHeight();
	return new Rectangle(x, y, width, height);
    }

    public static IntBag getShipPartsOnPoint(
	IntBag entities, ComponentMapper<ShipPart> mShipPart,
	ComponentMapper<PhysicsRectangleData> mRecData, Vector2 point) {
	// Entities which have ship parts lieing on the specified point
	final IntBag entitiesOnPoint = new IntBag();
	for (int entity : entities.getData()) {
	    final ShipPart shipPart = mShipPart.getSafe(entity, null);
	    final PhysicsRectangleData recData = mRecData.getSafe(entity, null);
	    if (shipPart != null && recData != null) {
		Rectangle rectangle = getRectangleInShipSpace(shipPart, recData);
		// libgdx Rectangle objs: contains() is true if point lies on rectangle edge
		if (rectangle.contains(point)) {
		    entitiesOnPoint.add(entity);
		}
	    }
	}
	return entitiesOnPoint;
    }    

    public static boolean isAugmentable(
	Iterable<Rectangle> existingShipParts, Rectangle newPart) {
	// If rectangles share a common edge, aka if they "touch"
	boolean touch = false;
	final Vector2 originalPosition = newPart.getPosition(new Vector2());
	for (Rectangle shipPart : existingShipParts) {
	    final boolean clash = shipPart.overlaps(newPart) ||
		shipPart.contains(newPart) ||
		newPart.contains(shipPart);
	    if ( clash ) {
		return false;
	    }
	    // We check if rectangles touch by translating them slightly and seeing if they overlap. We take advantage of the fact ship parts are always of integral length
	    final Vector2[] transPositions = {
		new Vector2(originalPosition).add(0.5f, 0f),
		new Vector2(originalPosition).add(-0.5f, 0f),
		new Vector2(originalPosition).add(0f, 0.5f),
		new Vector2(originalPosition).add(0f, -0.5f),
	    };
	    for (Vector2 transPosition : transPositions) {
		newPart.setPosition(transPosition);
		if (newPart.overlaps(shipPart)) {
		    touch = true;
		}
	    }
	    newPart.setPosition(originalPosition);
	}
	return touch;
    }
    
}
