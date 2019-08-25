package laurencewarne.secondspace.client.manager;

import com.artemis.BaseSystem;
import com.badlogic.gdx.utils.IntIntMap;

import lombok.NonNull;

/**
 * Translates to and from client/server ids.
 */
public class IdTranslatorManager extends BaseSystem {

    @NonNull
    private IntIntMap serverToClientIdMap = new IntIntMap();
    @NonNull
    private IntIntMap clientToServerIdMap = new IntIntMap();

    public int translate(int id) {
	if (!serverToClientIdMap.containsKey(id)) {
	    final int clientId = world.create();
	    serverToClientIdMap.put(id, clientId);
	    clientToServerIdMap.put(clientId, id);
	}
	return serverToClientIdMap.get(id, -1);
    }

    public int translateClient(int id) {
	return clientToServerIdMap.get(id, -1);
    }
    
    public void remove(int id) {
	serverToClientIdMap.remove(id, -1);
    }

    public void removeClient(int id) {
	clientToServerIdMap.remove(id, -1);
    }

    @Override
    public void processSystem() {
	
    }
}
