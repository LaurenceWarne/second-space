package laurencewarne.secondspace.client.system;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBagIterator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;

import laurencewarne.secondspace.client.component.Key;

@All(Key.class)
public class KeyActivationSystem extends IteratingSystem {

    private ComponentMapper<Key> mKey;
    @Wire(name="activation-map")
    private ObjectMap<Class<? extends Component>, Class<? extends Component>>
	activationMap;

    @Override
    public void process(int id) {
	final Key key = mKey.get(id);
	if (Gdx.input.isKeyPressed(key.getKey())) {
	    final IntBagIterator it = new IntBagIterator(key.entitiesToActivate);
	    for (int entity = -1; it.hasNext(); entity = it.next()){
		if (entity != -1) {
		    for (Class<? extends Component> cls : activationMap.keys()) {
			if (world.getMapper(cls).has(entity)) {
			    world.getMapper(activationMap.get(cls))
				.create(world.create());
			}
		    }
		}
	    }
	}
    }
}
