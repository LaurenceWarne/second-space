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

    @Override
    public void begin() {
	box2DWorld.step(1/60f, 6, 2);
    }

    // Called for each matching entity
    @Override
    public void process(int id) {
	
    }

    @Override
    public void end() {
	
    }

    // When an entity which "matches" this system is created
    // or has components added so that it matches
    @Override
    public void inserted(int id) {

	// Add to box2d world
    }

    @Override
    public void removed(int id) {

	// Remove from box2d world
    }
}
