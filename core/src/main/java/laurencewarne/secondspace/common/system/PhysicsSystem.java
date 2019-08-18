package laurencewarne.secondspace.common.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.systems.IntervalIteratingSystem;
import com.badlogic.gdx.physics.box2d.World;

import laurencewarne.secondspace.common.component.Physics;

/**
 * Responsible for upating the physics of the world. Essentially just updates the box2D world.
 */
public class PhysicsSystem extends IntervalIteratingSystem {
    
	// odb injects dependencies automatically
    private ComponentMapper<Physics> mPhysics;
    @Wire
    private World box2DWorld;

    public PhysicsSystem(float interval) {
	super(Aspect.all(Physics.class), interval);
    }    

    @Override
    public void begin() {
	box2DWorld.step(1/40f, 6, 2);
    }

    @Override
    public void removed(int id) {
	// Remove from box2d world
	box2DWorld.destroyBody(mPhysics.get(id).getBody());
    }

    @Override
    protected void process(int arg0) {

    }
}
