package laurencewarne.secondspace.common.system;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;

import laurencewarne.secondspace.common.component.PhysicsRectangleData;
import laurencewarne.secondspace.common.component.Ship;
import laurencewarne.secondspace.common.component.ShipPart;
import laurencewarne.secondspace.common.component.SpawnNotice;
import laurencewarne.secondspace.common.event.EntityCreatedEvent;
import net.fbridault.eeel.annotation.All;
import net.fbridault.eeel.annotation.Exclude;
import net.fbridault.eeel.annotation.Inserted;
import net.mostlyoriginal.api.event.common.EventSystem;

/**
 * This system initializes entities with {@link SpawnNotice} components, and removed the {@link SpawnNotice} components after initialization is complete.
 */
public class InitSpawnedEntitiesSystem extends BaseSystem {

    private ComponentMapper<SpawnNotice> mSpawnNotice;
    private ComponentMapper<Ship> mShip;
    private ComponentMapper<ShipPart> mShipPart;
    private ComponentMapper<PhysicsRectangleData> mRecData;
    private EventSystem es;

    @Override
    public void processSystem() {
		
    }

    @Inserted
    @All({ShipPart.class, PhysicsRectangleData.class, SpawnNotice.class})
    public void partInserted(int id) {
	final SpawnNotice notice = mSpawnNotice.get(id);
	if (notice.getShipOwner() == -1) {  // Not added to an existing ship
	    rectInserted(id);
	}
	else {
	    final ShipPart part = mShipPart.get(id);
	    part.shipId = notice.getShipOwner();
	    part.setLocalX((int)notice.getX());
	    part.setLocalY((int)notice.getY());
	    try {
		mShip.get(part.shipId).parts.add(id);
	    } catch (Exception e) {}
	    mSpawnNotice.remove(id);
	    es.dispatch(new EntityCreatedEvent(id, notice.getX(), notice.getY()));
	}
    }

    @Inserted
    @All({PhysicsRectangleData.class, SpawnNotice.class})
    @Exclude(ShipPart.class)
    public void rectInserted(int id) {
	final SpawnNotice notice = mSpawnNotice.get(id);
	final PhysicsRectangleData rect = mRecData.get(id);
	rect.setX(notice.getX());
	rect.setY(notice.getY());
	mSpawnNotice.remove(id);
	es.dispatch(new EntityCreatedEvent(id, notice.getX(), notice.getY()));
    }

    @Inserted
    @All(SpawnNotice.class)
    @Exclude({ShipPart.class, PhysicsRectangleData.class})
    public void genericNoticeRemover(int id) {
	mSpawnNotice.remove(id);
    }
}
