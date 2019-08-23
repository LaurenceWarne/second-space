package laurencewarne.secondspace.common.system.network;

import com.artemis.BaseSystem;
import com.artemis.Component;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;

import laurencewarne.secondspace.common.component.Cannon;
import laurencewarne.secondspace.common.component.PhysicsRectangleData;
import laurencewarne.secondspace.common.component.Ship;
import laurencewarne.secondspace.common.component.ShipPart;
import laurencewarne.secondspace.common.component.Thruster;
import laurencewarne.secondspace.common.component.network.Networked;
import laurencewarne.secondspace.common.component.network.RegistrationRequest;
import laurencewarne.secondspace.common.component.network.RegistrationResponse;

/**
 * Registers objects that need to be sent over a network connection with kryonet, see <a href="https://github.com/EsotericSoftware/kryonet/#registering-classes">this</a> link for more information.
 */
public class NetworkRegisterSystem extends BaseSystem {

    @Wire(name="kryo")
    private Kryo kryo;
    @Wire(name="networked-components")
    private Array<Class<? extends Component>> typesToSend;    

    @Override
    public void initialize() {
	kryo.register(RegistrationRequest.class);
	kryo.register(RegistrationResponse.class);
	kryo.register(Networked.class);

	typesToSend.addAll(
	    Ship.class, PhysicsRectangleData.class, ShipPart.class,
	    Thruster.class, Cannon.class
	);
    }

    @Override
    public void processSystem() {
	
    }
}
