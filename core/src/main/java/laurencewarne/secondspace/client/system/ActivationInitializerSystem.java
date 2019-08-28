package laurencewarne.secondspace.client.system;

import com.artemis.BaseSystem;
import com.artemis.Component;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.utils.ObjectMap;

import laurencewarne.secondspace.common.component.Cannon;
import laurencewarne.secondspace.common.component.Cannon.CannonActivated;
import laurencewarne.secondspace.common.component.Thruster;
import laurencewarne.secondspace.common.component.Thruster.ThrusterActivated;

/**
 * Associates components which can be activated with their "activated" components.
 */
public class ActivationInitializerSystem extends BaseSystem {

    @Wire(name="activation-map")
    private ObjectMap<Class<? extends Component>, Class<? extends Component>>
	activationMap;

    @Override
    public void initialize() {
	activationMap.put(Thruster.class, ThrusterActivated.class);
	activationMap.put(Cannon.class, CannonActivated.class);
    }

    @Override
    public void processSystem() {

    }    
    
}
