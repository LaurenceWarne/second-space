package laurencewarne.secondspace.server.system;

import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.physics.box2d.World;

import laurencewarne.secondspace.server.component.Physics;

/**
 * Responsible for upating the physics of the world. Essentially just updates the box2D world.
 */
@All(Physics.class) // The entity must have a physics component.
public class PhysicsSystem extends BaseEntitySystem {

    // odb injects dependencies automatically
    private ComponentMapper<Physics> mPhysics;

    @Wire
    private World box2DWorld;

    @Override
    public void processSystem() {
	box2DWorld.step(1f/60f, 6, 2);
    }

    @Override
    public void removed(int id) {
	// Remove from box2d world
	box2DWorld.destroyBody(mPhysics.get(id).getBody());
    }
}
