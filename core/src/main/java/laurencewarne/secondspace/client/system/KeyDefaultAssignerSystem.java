package laurencewarne.secondspace.client.system;

import com.artemis.BaseEntitySystem;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.utils.IntBagIterator;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.componentlookup.annotations.FieldLookup;
import laurencewarne.secondspace.client.component.ClientPlayer;
import laurencewarne.secondspace.client.component.Key;
import laurencewarne.secondspace.client.manager.IdTranslatorManager;
import laurencewarne.secondspace.common.component.Ship;
import laurencewarne.secondspace.common.component.network.NetworkConnection;

@All({ClientPlayer.class, NetworkConnection.class, Ship.class})
public class KeyDefaultAssignerSystem extends BaseEntitySystem {

    private final Logger logger = LoggerFactory.getLogger(
	KeyDefaultAssignerSystem.class
    );    
    private IdTranslatorManager idTranslatorManager;
    private ComponentMapper<Ship> mShip;
    private ComponentMapper<Key> mKey;
    @Wire(name="activation-map")
    private ObjectMap<Class<? extends Component>, Class<? extends Component>>
	activationMap;
    @FieldLookup(component=Key.class, field="identifier")
    private ObjectIntMap<String> keyLookup;

    @Override
    public void inserted(int id) {
	final Ship ship = mShip.get(id);
	int keysAssigned = 0;
	final IntBagIterator it = new IntBagIterator(ship.parts);
	while (it.hasNext()) {
	    final int entity = it.next();
	    if (entity != -1) {
		final int clientId = idTranslatorManager.translate(entity);
		for (Class<? extends Component> cls : activationMap.keys()) {
		    if (world.getMapper(cls).has(clientId)) {
			assignKeyToEntity(clientId, keysAssigned++);
			break;
		    }
		}
	    }
	}
    }

    private void assignKeyToEntity(int id, int keysAssignedPrior) {
	final String key = Character.toString(
	    "qwertyuiopasdfghjklzxcvbnm".charAt(keysAssignedPrior)
	);
	System.out.println(keyLookup);
	mKey.get(keyLookup.get(key, -1)).entitiesToActivate.add(id);
	logger.info("Assigned activation of entity: {} to key '{}'", id, key);
    }

    @Override
    public void processSystem() {
	
    }
}
