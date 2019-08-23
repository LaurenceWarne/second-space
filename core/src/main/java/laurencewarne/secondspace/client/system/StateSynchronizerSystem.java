package laurencewarne.secondspace.client.system;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.esotericsoftware.kryonet.Listener.TypeListener;

import laurencewarne.secondspace.common.component.network.Networked;

public class StateSynchronizerSystem extends BaseSystem {

    @Wire
    private TypeListener typeListener;

    @Override
    public void initialize() {
	typeListener.addTypeHandler(
	    Networked.class, (conn, networked) -> {
		System.out.println(networked.getComponent().getClass().cast(networked.getComponent()));
	    }
	);
    }

    @Override
    public void processSystem() {
	
    }
}
