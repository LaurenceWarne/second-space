package laurencewarne.secondspace.common.event;

import com.badlogic.gdx.physics.box2d.Body;

import lombok.Value;
import net.mostlyoriginal.api.event.common.Event;

/**
 * Signals contact between two entities has ended.
 */
@Value
public class BodyEndContactEvent implements Event {
    private final int entityA;
    private final int entityB;
    private final Body bodyA;
    private final Body bodyB;
}

