package laurencewarne.secondspace.server.system;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;

import laurencewarne.secondspace.server.component.Cannon;

/**
 * Sets the cooldowns of cannons based on the world delta.
 */
@All(Cannon.class)
public class CannonCooldownSystem extends IteratingSystem {

    private ComponentMapper<Cannon> mCannon;

    @Override
    public void process(int id) {
	final Cannon cannon = mCannon.get(id);
	final float currentCoolDown = cannon.getCurrentCoolDown();
	if (currentCoolDown > 0f) {
	    cannon.setCurrentCoolDown(currentCoolDown - world.delta);
	}
    }
}
