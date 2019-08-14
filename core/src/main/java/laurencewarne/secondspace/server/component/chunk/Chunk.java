package laurencewarne.secondspace.server.component.chunk;

import static com.artemis.annotations.LinkPolicy.Policy.CHECK_SOURCE_AND_TARGETS;

import com.artemis.Component;
import com.artemis.annotations.EntityId;
import com.artemis.annotations.LinkPolicy;
import com.artemis.utils.IntBag;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Stores references to a collection of entities in a specified rectangular zone in the world.
 */
@EqualsAndHashCode(callSuper=false) @ToString
public class Chunk extends Component {
    @EntityId @LinkPolicy(CHECK_SOURCE_AND_TARGETS)
    public IntBag parts = new IntBag(32);
    @Getter
    private final int width = 32;
    @Getter
    private final int height = 32;
    /** Where in the world the x coord of the bottom-left corner of this chunk is located.*/
    @Getter @Setter
    private int originX = 0;
    /** Where in the world the y coord of the bottom-left corner of this chunk is located.*/
    @Getter @Setter
    private int originY = 0;
}
