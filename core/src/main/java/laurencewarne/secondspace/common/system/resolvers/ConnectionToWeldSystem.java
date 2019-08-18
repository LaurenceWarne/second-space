package laurencewarne.secondspace.common.system.resolvers;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.physics.box2d.World;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.common.component.WeldRequest;
import laurencewarne.secondspace.common.manager.ConnectionManager.ConnectionAddedEvent;
import lombok.NonNull;
import net.mostlyoriginal.api.event.common.Subscribe;

/**
 * A {@link BaseSystem} implementation which creates {@link WeldRequest}s when connections are created between entities.
 */
public class ConnectionToWeldSystem extends BaseSystem {

    private final Logger logger = LoggerFactory.getLogger(
	ConnectionToWeldSystem.class
    );
    private ComponentMapper<WeldRequest> mWeldRequest;

    @Wire
    private World box2DWorld;

    @Override
    public void processSystem() {
	
    }

    @Subscribe
    public void onConnectionAddedEvent(@NonNull ConnectionAddedEvent evt) {
	final WeldRequest request = mWeldRequest.create(world.create());
	request.cellAID = evt.getEntityA();
	request.cellBID = evt.getEntityB();
	request.setLocalAnchorA(evt.getLocalPositionA());
	request.setLocalAnchorB(evt.getLocalPositionB());
    }
}
