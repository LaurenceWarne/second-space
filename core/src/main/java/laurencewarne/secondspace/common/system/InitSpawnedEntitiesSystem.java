package laurencewarne.secondspace.common.system;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;

import laurencewarne.secondspace.common.component.AugmentationNotice;
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
    private ComponentMapper<AugmentationNotice> mAugNotice;
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
	final ShipPart part = mShipPart.get(id);
	final PhysicsRectangleData recData = mRecData.get(id);
	recData.setX(notice.getX() + part.getLocalX());
	recData.setY(notice.getY() + part.getLocalY());
	if (notice.getShipOwner() != -1) {
	    part.shipId = notice.getShipOwner();
	    if (!mShip.has(notice.getShipOwner())) {
		Ship ship = mShip.create(notice.getShipOwner());
		ship.setX(notice.getX()); ship.setY(notice.getY());
	    }
	    mShip.get(notice.getShipOwner()).parts.add(id);
	}
	mSpawnNotice.remove(id);
	es.dispatch(new EntityCreatedEvent(id, notice.getX(), notice.getY()));
    }

    @Inserted
    @All({ShipPart.class, PhysicsRectangleData.class, AugmentationNotice.class})
    public void augmentationInserted(int id) {
	final AugmentationNotice notice = mAugNotice.get(id);
	if (notice.getShip() != -1) {  // Not added to an existing ship
	    final ShipPart part = mShipPart.get(id);
	    part.shipId = notice.getShip();
	    part.setLocalX(notice.getShipX());
	    part.setLocalY(notice.getShipY());
	}
	mAugNotice.remove(id);
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
    @All({Ship.class, SpawnNotice.class})
    public void shipInserted(int id) {
	final Ship ship = mShip.get(id);
	final SpawnNotice notice = mSpawnNotice.get(id);
	if (notice.getShipOwner() != -1) {
	    mShip.remove(id);
	}
	else {
	    ship.setX(notice.getX());
	    ship.setY(notice.getY());	    
	}
	mSpawnNotice.remove(id);
    }

    @Inserted
    @All(SpawnNotice.class)
    @Exclude({ShipPart.class, PhysicsRectangleData.class, Ship.class})
    public void genericNoticeRemover(int id) {
	mSpawnNotice.remove(id);
    }
}
