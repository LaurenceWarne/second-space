package laurencewarne.secondspace.server.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IntervalIteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.server.component.Physics;
import laurencewarne.secondspace.server.component.PhysicsRectangleData;

/**
 * Synchronizes {@link PhysicsRectangleData} components from {@link Physics} components. ie sets position, angle, etc of the {@link PhysicsRectangleData} components from the {@link Body} instances wrapped by {@link Physics} components.
 */
public class PhysicsRectangleSynchronizerSystem extends IntervalIteratingSystem {

    private final Logger logger = LoggerFactory.getLogger(
	PhysicsRectangleSynchronizerSystem.class
    );
    private ComponentMapper<Physics> mPhysics;
    private ComponentMapper<PhysicsRectangleData> mPhysicsRectangleData;

    public PhysicsRectangleSynchronizerSystem(float interval) {
	super(Aspect.all(Physics.class, PhysicsRectangleData.class), interval);
    }

    @Override
    public void process(int id) {
	final Body body = mPhysics.get(id).getBody();
	final PhysicsRectangleData data = mPhysicsRectangleData.get(id);
	data.setX(body.getPosition().x);
	data.setY(body.getPosition().y);
	data.setAngle(body.getAngle());
	data.setVelocityX(body.getLinearVelocity().x);
	data.setVelocityY(body.getLinearVelocity().y);
    }

    @Override
    public void end() {
	logger.debug("Synchronized physics and physics rectangle data components");
    }
}
