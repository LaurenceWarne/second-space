package laurencewarne.secondspace.server.system;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.badlogic.gdx.physics.box2d.Body;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.server.component.Physics;
import laurencewarne.secondspace.server.component.PhysicsRectangleData;
import lombok.Setter;
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
	data.setX(body.getPosition().x);
	data.setY(body.getPosition().y);
	data.setAngle(body.getAngle());
	data.setVelocityX(body.getLinearVelocity().x);
	data.setVelocityY(body.getLinearVelocity().y);
    }
}
