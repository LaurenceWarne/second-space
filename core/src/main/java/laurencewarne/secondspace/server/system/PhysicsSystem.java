package laurencewarne.secondspace.server.system;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.World;

import laurencewarne.secondspace.server.component.Physics;

@All(Physics.class) // The entity must have a physics component.
public class PhysicsSystem extends IteratingSystem {
    // odb injects dependencies automatically
    private ComponentMapper<Physics> mPhysics;

    @Wire
    private World box2DWorld;

    // Called for each matching entity
    @Override
    public void process(int id) {
	// Check if in box2d world:
	System.out.println(mPhysics.get(id));
    }
}
