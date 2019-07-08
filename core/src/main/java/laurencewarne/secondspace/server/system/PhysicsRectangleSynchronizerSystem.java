package laurencewarne.secondspace.server.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IntervalIteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;

import laurencewarne.secondspace.server.component.Physics;
import laurencewarne.secondspace.server.component.PhysicsRectangleData;

public class PhysicsRectangleSynchronizerSystem extends IntervalIteratingSystem {

    private ComponentMapper<Physics> mPhysics;
    private ComponentMapper<PhysicsRectangleData> mPhysicsRectangleData;

    public PhysicsRectangleSynchronizerSystem(float interval) {
	super(Aspect.all(Physics.class, PhysicsRectangleData.class), interval);
    }

    @Override
    public void process(int id) {
	Body body = mPhysics.get(id).getBody();
	PhysicsRectangleData data = mPhysicsRectangleData.get(id);
	data.setX(body.getPosition().x);
	data.setY(body.getPosition().y);
	data.setAngle(body.getAngle());
	data.setVelocityX(body.getLinearVelocity().x);
	data.setVelocityY(body.getLinearVelocity().y);
    }
}
