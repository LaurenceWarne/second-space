package laurencewarne.secondspace.server.event;

import lombok.Value;
import net.mostlyoriginal.api.event.common.Event;

/**
 * Signals the movement of an entity from one position to another.
 */
@Value
public class EntityMovedEvent implements Event {
    /** id of created entity*/
    private final int id;
    /** world x position of the entity prior to movement*/
    private final float prevX;
    /** world y position of the entity prior to movement*/
    private final float prevY;
    /** world x position of the entity after movement*/
    private final float postX;
    /** world y position of the entity after movement*/
    private final float postY;
}
