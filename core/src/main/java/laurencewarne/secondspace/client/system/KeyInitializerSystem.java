package laurencewarne.secondspace.client.system;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.Input.Keys;

import laurencewarne.secondspace.client.component.Key;

/**
 * Creates key components.
 */
public class KeyInitializerSystem extends BaseSystem {

    private ComponentMapper<Key> mKey;

    @Override
    public void initialize() {
	for (int i = Keys.A; i <= Keys.Z; i++){
	    mKey.create(world.create()).setKey(i);
	}
    }

    @Override
    public void processSystem() {

    }    
}
