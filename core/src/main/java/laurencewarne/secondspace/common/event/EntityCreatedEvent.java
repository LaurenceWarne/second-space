package laurencewarne.secondspace.common.event;

import lombok.Value;
import net.mostlyoriginal.api.event.common.Event;

/**
 * Signals the creation of an entity in the world at a certain position.
 */
@Value
public class EntityCreatedEvent implements Event {
    /** id of created entity*/
    private final int id;
    /** world x position entity was set to*/
    private final float x;
    /** world y position entity was set to*/    
    private final float y;
}
