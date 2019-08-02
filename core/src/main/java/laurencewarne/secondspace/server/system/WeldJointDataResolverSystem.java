package laurencewarne.secondspace.server.system;

import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Exclude;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.WeldJoint;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.server.component.Physics;
import laurencewarne.secondspace.server.component.PhysicsRectangleData;
import laurencewarne.secondspace.server.component.WeldJointData;
import laurencewarne.secondspace.server.component.WeldJointWrapper;

/**
 * A {@link BaseEntitySystem} implementation which creates {@link WeldJointWrapper} components for entities which have a {@link PhysicsRectangleData} component but no {@link WeldJointWrapper} component.
 */
@All(WeldJointData.class)
@Exclude(WeldJointWrapper.class)
public class WeldJointDataResolverSystem extends BaseEntitySystem {

    private final Logger logger = LoggerFactory.getLogger(
	WeldJointDataResolverSystem.class
    );
    private ComponentMapper<WeldJointData> mWeldJointData;
    private ComponentMapper<WeldJointWrapper> mWeldJointWrapper;
    private ComponentMapper<Physics> mPhysics;

    @Wire
    private World box2DWorld;

    @Override
    public void processSystem() {
	
    }    
    
    @Override
    public void inserted(int id) {
	////////////////////////////////////////////////////////////////////////////
	// Create a weld joint in the box2d world using a WeldJointData component //
	////////////////////////////////////////////////////////////////////////////
	final WeldJointData weldJointData = mWeldJointData.get(id);
	final WeldJointDef weldJointDef = new WeldJointDef();
	// Set data from weldJointData component
	try {
	    weldJointDef.bodyA = mPhysics.get(weldJointData.cellAID).getBody();
	    weldJointDef.bodyB = mPhysics.get(weldJointData.cellBID).getBody();
	} catch (NullPointerException e) {
	    logger.error(
		"Could not create weld joint, error getting physics" +
		" bodies from supplied ids."
	    );
	    return;
	}
	weldJointDef.localAnchorA.set(weldJointData.getLocalAnchorA());
	weldJointDef.localAnchorB.set(weldJointData.getLocalAnchorB());
	weldJointDef.referenceAngle = weldJointData.getReferenceAngle();
	// Finally create the weld in the world
	final WeldJoint weldJoint = (WeldJoint) box2DWorld.createJoint(weldJointDef);
	mWeldJointWrapper.create(id).setWeldJoint(weldJoint);
    }
}
