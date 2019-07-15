package laurencewarne.secondspace.server.ship;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Classes implementing this interface help with ship augmentation.
 */
public interface IShipAugmentationHelper {
    /**
     * Von Neumann neighbourhood.
     */
    enum Neighbourhood {
	RIGHT (new Vector2(1f, 0f)),
	LEFT  (new Vector2(-1f, 0f)),
	ABOVE (new Vector2(0f, 1f)),
	BELOW(new Vector2(0f, -1f));

	private final Vector2 basis;

	Neighbourhood(Vector2 basis) {
	    this.basis = basis;
	}

	public Vector2 getBasis() {
	    return new Vector2(basis).nor();
	}
    }

    boolean isAugmentable(
	Iterable<Rectangle> existingShipParts, Rectangle newPart);
}

