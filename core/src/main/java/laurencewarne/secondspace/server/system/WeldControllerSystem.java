package laurencewarne.secondspace.server.system;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.WeldJoint;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.server.component.Physics;
import laurencewarne.secondspace.server.component.WeldRequest;

@All(WeldRequest.class)
public class WeldControllerSystem extends IteratingSystem {

    private ComponentMapper<WeldRequest> mWeldRequest;
    private ComponentMapper<Physics> mPhysics;

    private final Logger logger = LoggerFactory.getLogger(
	WeldControllerSystem.class
    );    

    @Wire
    private World box2DWorld;    

    @Override
    public void process(int id) {
	final WeldRequest weldRequest = mWeldRequest.get(id);
	final WeldJointDef weldJointDef = new WeldJointDef();
	world.delete(id);
	// Set data from weldRequest component
	try {
	    weldJointDef.bodyA = mPhysics.get(weldRequest.cellAID).getBody();
	    weldJointDef.bodyB = mPhysics.get(weldRequest.cellBID).getBody();
	} catch (NullPointerException e) {
	    logger.error(
		"Could not create weld joint, error getting physics" +
		" bodies from supplied ids."
	    );
	    return;
	}
	weldJointDef.localAnchorA.set(weldRequest.getLocalAnchorA());
	weldJointDef.localAnchorB.set(weldRequest.getLocalAnchorB());
	weldJointDef.referenceAngle = weldRequest.getReferenceAngle();
	// Finally create the weld in the world
	final WeldJoint weldJoint = (WeldJoint) box2DWorld.createJoint(weldJointDef);
    }
}
