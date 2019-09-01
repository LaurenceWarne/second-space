package laurencewarne.secondspace.client.system;

import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;

import laurencewarne.secondspace.client.component.ClientPlayer;
import laurencewarne.secondspace.client.manager.IdTranslatorManager;
import laurencewarne.secondspace.common.component.Ship;
import laurencewarne.secondspace.common.component.network.NetworkConnection;

@All({ClientPlayer.class, NetworkConnection.class, Ship.class})
public class KeyDefaultAssignerSystem extends BaseEntitySystem {

    private IdTranslatorManager idTranslatorManager;
    private ComponentMapper<Ship> mShip;

    @Override
    public void inserted(int id) {
	final Ship ship = mShip.get(id);
    }

    @Override
    public void processSystem() {
	
    }
}
