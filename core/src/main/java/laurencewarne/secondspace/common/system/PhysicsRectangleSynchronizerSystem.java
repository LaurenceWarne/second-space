package laurencewarne.secondspace.common.system;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.badlogic.gdx.physics.box2d.Body;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.common.component.Physics;
import laurencewarne.secondspace.common.component.PhysicsRectangleData;
import laurencewarne.secondspace.common.event.EntityMovedEvent;
import lombok.Setter;
import net.mostlyoriginal.api.event.common.EventSystem;
import net.mostlyoriginal.api.system.core.TimeboxedProcessingSystem;

/**
 * Synchronizes {@link PhysicsRectangleData} components from {@link Physics} components. ie sets position, angle, etc of the {@link PhysicsRectangleData} components from the {@link Body} instances wrapped by {@link Physics} components.
 */
@All({Physics.class, PhysicsRectangleData.class})
public class PhysicsRectangleSynchronizerSystem extends TimeboxedProcessingSystem {

    private final Logger logger = LoggerFactory.getLogger(
	PhysicsRectangleSynchronizerSystem.class
    );
    private ComponentMapper<Physics> mPhysics;
    private ComponentMapper<PhysicsRectangleData> mPhysicsRectangleData;
    private EventSystem es;
    @Setter
    private float allottedTime = 0.01f;

    @Override
    protected float getAllottedTime() {
	return allottedTime;
    }

    @Override
    public void process(int id) {
	final Body body = mPhysics.get(id).getBody();
	final PhysicsRectangleData data = mPhysicsRectangleData.get(id);
	float bodyX = body.getPosition().x;
	float bodyY = body.getPosition().y;
	if (data.getX() != bodyX || data.getY() != bodyY) {
	    final EntityMovedEvent entityMovedEvent = new EntityMovedEvent(
		id, data.getX(), data.getY(), bodyX, bodyY
	    );
	    data.setX(body.getPosition().x);
	    data.setY(body.getPosition().y);
	    es.dispatch(entityMovedEvent);
	}	
	data.setAngle(body.getAngle());
	data.setVelocityX(body.getLinearVelocity().x);
	data.setVelocityY(body.getLinearVelocity().y);
    }
}
