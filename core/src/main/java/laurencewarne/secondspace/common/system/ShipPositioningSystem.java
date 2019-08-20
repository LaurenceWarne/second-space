package laurencewarne.secondspace.common.system;

import java.util.Optional;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import laurencewarne.secondspace.common.collect.IntBags;
import laurencewarne.secondspace.common.component.Physics;
import laurencewarne.secondspace.common.component.Ship;
import laurencewarne.secondspace.common.component.ShipPart;

/**
 * Updates positions of {@link Ship}s based on their {@link ShipPart}s.
 */
@All(Ship.class)
public class ShipPositioningSystem extends IteratingSystem {
    
    private ComponentMapper<Ship> mShip;
    private ComponentMapper<ShipPart> mShipPart;
    private ComponentMapper<Physics> mPhysics;

    @Override
    public void process(int id) {
	final Ship ship = mShip.get(id);
	final Optional<Integer> partIdOptional = IntBags.toSet(ship.parts)
	    .stream()
	    .filter(p -> p != -1)
	    .findAny();
	if (partIdOptional.isPresent()) {
	    final int partId = partIdOptional.get();
	    if (mPhysics.has(partId)) {
		final Body body = mPhysics.get(partId).getBody();
		final ShipPart part = mShipPart.get(partId);
		final Vector2 pos = body.getWorldPoint(
		    new Vector2(part.getLocalX(), part.getLocalY()).scl(-1f)
		);
		ship.setX(pos.x);
		ship.setY(pos.y);
	    }
	}
    }
}
