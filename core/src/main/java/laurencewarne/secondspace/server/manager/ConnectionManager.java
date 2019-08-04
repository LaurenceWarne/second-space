package laurencewarne.secondspace.server.manager;

import com.artemis.BaseSystem;
import com.badlogic.gdx.math.Vector2;

import lombok.NonNull;

public class ConnectionManager extends BaseSystem {

    public void processSystem() {
	
    }

    public boolean existsConnection(
	int entityA, int entityB, @NonNull Vector2 position
    ) {
	return true;
    }

    public void createConnection(
	int entityA, int entityB,
	@NonNull Vector2 localPositionA, @NonNull Vector2 localPositionB
    ) {
	
    }

    public void removeConnections(int entity) {
	
    }

    public void removeConnections(int entityA, int entityB) {
	
    }

    public void addConnectionListener() {
	
    }
}
