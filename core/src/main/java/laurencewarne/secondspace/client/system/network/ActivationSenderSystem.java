package laurencewarne.secondspace.client.system.network;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBagIterator;
import com.badlogic.gdx.utils.ObjectMap;

import laurencewarne.secondspace.client.component.ClientPlayer;
import laurencewarne.secondspace.client.manager.IdTranslatorManager;
import laurencewarne.secondspace.common.component.Ship;
import laurencewarne.secondspace.common.component.network.NetworkConnection;
import laurencewarne.secondspace.common.component.network.Networked;

@All({ClientPlayer.class, NetworkConnection.class, Ship.class})
public class ActivationSenderSystem extends IteratingSystem {

    private ComponentMapper<Ship> mShip;
    private ComponentMapper<NetworkConnection> mNetworkConnection;
    @Wire(name="activation-map")
    private ObjectMap<Class<? extends Component>, Class<? extends Component>>
	activationMap;
    private IdTranslatorManager idTranslatorManager;

    @Override
    public void process(int id) {
	final Ship ship = mShip.get(id);
	final NetworkConnection con = mNetworkConnection.get(id);
	final IntBagIterator it = new IntBagIterator(ship.parts);
	while (it.hasNext()) {
	    final int clientId = idTranslatorManager.translate(it.next());
	    if (clientId != -1) {
		for (Class<? extends Component> cls : activationMap.values()) {
		    if (world.getMapper(cls).has(clientId)) {
			final Networked networked = new Networked();
			networked.setId(clientId);
			networked.setComponent(world.getMapper(cls).get(clientId));
			con.getConnection().sendTCP(networked);
			world.getMapper(cls).remove(clientId);
		    }		
		}
	    }
	}
    }
}
