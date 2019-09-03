package laurencewarne.secondspace.common.system.network;

import com.artemis.BaseSystem;

import laurencewarne.secondspace.common.component.Cannon.CannonActivated;
import laurencewarne.secondspace.common.component.Thruster.ThrusterActivated;

/**
 * Registers client component authenticators used by the server.
 */
public class RegisterAuthSystem extends BaseSystem {

    private FromClientAuthenticatorSystem clientAuthSystem;

    @Override
    public void initialize() {
	clientAuthSystem.setAuthenticator(
	    ThrusterActivated.class,
	    (clientId, component) -> {
		return true;
	    }
	);
	clientAuthSystem.setAuthenticator(
	    CannonActivated.class,
	    (clientId, component) -> {
		return true;
	    }
	);
	
    }

    @Override
    public void processSystem() {
	
    }
}
