package laurencewarne.secondspace.server.component.ship;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * A simple implementation of {@link IShipAugmentationHelper}.
 */
public class ShipAugmentationHelper implements IShipAugmentationHelper {

    @Override
    public boolean isAugmentable(
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
