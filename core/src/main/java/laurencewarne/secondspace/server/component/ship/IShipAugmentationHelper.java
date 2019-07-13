package laurencewarne.secondspace.server.component.ship;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Classes implementing this interface help with ship augmentation.
 */
public interface IShipAugmentationHelper {
    boolean isAugmentable(
	Iterable<Rectangle> existingShipParts, Rectangle newPart);

    Iterable<Vector2> getAugmentationPoints(
	Iterable<Rectangle> existingShipParts, Rectangle newPart);
}

