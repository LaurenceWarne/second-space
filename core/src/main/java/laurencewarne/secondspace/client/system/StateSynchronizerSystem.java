package laurencewarne.secondspace.client.system;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.esotericsoftware.kryonet.Listener.TypeListener;

import laurencewarne.secondspace.client.IdTranslator;
import laurencewarne.secondspace.common.component.network.Networked;

/**
 * Synchronizes the client world from received {@link Networked} components from the server.
 */
public class StateSynchronizerSystem extends BaseSystem {

    @Wire
    private TypeListener typeListener;
    @Wire
    private IdTranslator idTranslator;

    @Override
    public void initialize() {
	typeListener.addTypeHandler(
	    Networked.class, (conn, networked) -> {
		int clientId = idTranslator.translate(networked.getId());
 		world.edit(clientId).add(networked.getComponent());
	    }
	);
    }

    @Override
    public void processSystem() {
	
    }
}
