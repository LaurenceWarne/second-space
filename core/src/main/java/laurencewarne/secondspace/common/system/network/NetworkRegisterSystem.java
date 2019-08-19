package laurencewarne.secondspace.common.system.network;

import java.util.Set;

import com.artemis.BaseSystem;
import com.artemis.Component;
import com.artemis.annotations.Wire;
import com.esotericsoftware.kryo.Kryo;

import laurencewarne.secondspace.common.component.network.Networked;

public class NetworkRegisterSystem extends BaseSystem {

    @Wire
    private Set<? extends Component> registeredClasses;
    @Wire
    private Kryo kryo;

    @Override
    public void initialize() {
	kryo.register(Networked.class);
    }

    @Override
    public void processSystem() {
	
    }
}
