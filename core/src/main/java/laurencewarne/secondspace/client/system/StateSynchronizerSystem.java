package laurencewarne.secondspace.client.system;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.esotericsoftware.kryonet.Listener.TypeListener;

import laurencewarne.secondspace.client.manager.IdTranslatorManager;
import laurencewarne.secondspace.common.component.network.Networked;

/**
 * Synchronizes the client world from received {@link Networked} components from the server.
 */
public class StateSynchronizerSystem extends BaseSystem {

    private IdTranslatorManager idTranslatorManager;
    @Wire
    private TypeListener typeListener;
    private Queue<Networked> networkQueue = new LinkedBlockingQueue<>();

    @Override
    public void initialize() {
	typeListener.addTypeHandler(
	    Networked.class, (conn, networked) -> {
		// Called on Client thread
		networkQueue.add(networked);
	    }
	);
    }

    @Override
    public void processSystem() {
	while (!networkQueue.isEmpty()){
	    Networked networked = networkQueue.poll();
	    int clientId = idTranslatorManager.translate(networked.getId());
	    world.edit(clientId).add(networked.getComponent());	    
	}
    }
}
