package laurencewarne.secondspace.server.system;

import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Exclude;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.WeldJoint;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;

import laurencewarne.secondspace.server.component.Physics;
import laurencewarne.secondspace.server.component.PhysicsRectangleData;
import laurencewarne.secondspace.server.component.WeldJointData;

/**
 * A {@link BaseEntitySystem} implementation which creates {@link WeldJointWrapper} components for entities which have a {@link PhysicsRectangleData} component but no {@link WeldJointWrapper} component.
 */
@All({WeldJointData.class, Physics.class})
@Exclude(WeldJointWrapper.class)
public class WeldJointDataResolverSystem extends BaseEntitySystem {

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
	weldJointDef.bodyA = mPhysics.get(weldJointData.cellAID).getBody();
	weldJointDef.bodyB = mPhysics.get(weldJointData.cellBID).getBody();
	weldJointDef.localAnchorA.set(weldJointData.getLocalAnchorA());
	weldJointDef.localAnchorB.set(weldJointData.getLocalAnchorB());
	weldJointDef.referenceAngle = weldJointData.getReferenceAngle();
	// Finally create the weld in the world
	final WeldJoint weldJoint = (WeldJoint) box2DWorld.createJoint(weldJointDef);
	mWeldJointWrapper.create(id).setWeldJoint(weldJoint);
    }
    
}
