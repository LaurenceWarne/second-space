package laurencewarne.secondspace.common.system.network;

import java.util.function.BiConsumer;

import com.artemis.BaseSystem;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener.TypeListener;

import laurencewarne.secondspace.common.component.network.FromClient;
import laurencewarne.secondspace.common.component.network.NetworkConnection;
import lombok.NonNull;

public class FromClientAuthenticatorSystem extends BaseSystem
    implements BiConsumer<Connection, FromClient> {

    @Wire
    private TypeListener typeListener;
    private ComponentMapper<NetworkConnection> mNetConn;
    @NonNull
    private final ObjectMap<Class<? extends Component>, ComponentAuthenticator<?>>
	authenticators = new ObjectMap<>(8);

    public interface ComponentAuthenticator<T extends Component> {

	boolean isLegal(int clientId, T component);
    }

    @Override
    public void initialize() {
	typeListener.addTypeHandler(FromClient.class, this);
    }

    @Override @SuppressWarnings("unchecked")
    public void accept(
	@NonNull Connection connection, @NonNull FromClient fromClient
    ) {
	// Perform authentication
	final Component clientComponent = fromClient.getComponent();
	// Check if the client is who they say they are:
	final boolean clientAuth = mNetConn.has(fromClient.getClientId()) &&
	    mNetConn.get(fromClient.getClientId()).getConnection() == connection;
	if (!clientAuth || clientComponent == null) {
	    return;
	}
	if (authenticators.containsKey(clientComponent.getClass())) {
	    final ComponentAuthenticator authenticator = authenticators.get(
		clientComponent.getClass()
	    );
	    if (authenticator.isLegal(fromClient.getClientId(), clientComponent)) {
		world.edit(fromClient.getId()).add(clientComponent);
	    }
	}

    }

    public <T extends Component> void setAuthenticator(
	@NonNull Class<T> clazz,
	@NonNull ComponentAuthenticator<T> authenticator
    ) {
	authenticators.put(clazz, authenticator);
    }

    public void removeAuthenticator(
	@NonNull Class<? extends Component> clazz
    ) {
	authenticators.remove(clazz);
    }
    

    @Override
    public void processSystem() {
	
    }
}
