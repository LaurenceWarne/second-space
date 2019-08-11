package laurencewarne.secondspace.server.system;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import laurencewarne.secondspace.server.component.Physics;
import laurencewarne.secondspace.server.component.Thruster;
import laurencewarne.secondspace.server.component.Thruster.ThrusterActivated;

/**
 * Applies impulses to entities whose thruster has been activated.
 */
@All({ThrusterActivated.class, Thruster.class, Physics.class})
public class ThrusterSystem extends IteratingSystem {

    private ComponentMapper<ThrusterActivated> mThrusterActivated;
    private ComponentMapper<Thruster> mThruster;
    private ComponentMapper<Physics> mPhysics;

    @Override
    public void process(int id) {
	final Thruster thruster = mThruster.get(id);
	final Body body = mPhysics.get(id).getBody();
	final Vector2 applPoint = body.getWorldPoint(
	    new Vector2(thruster.getLocalApplX(), thruster.getLocalApplY())
	);
	// Apply along normal vector from appl point to body centre (ie negate)
	final Vector2 localImpulse = new Vector2(
	    thruster.getLocalApplX(), thruster.getLocalApplY()
	).nor().scl(-thruster.getPower());
	body.applyLinearImpulse(body.getWorldPoint(localImpulse), applPoint, true);
	mThrusterActivated.remove(id);
    }
}
