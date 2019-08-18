package laurencewarne.secondspace.common.component.chunk;

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
    public IntBag entities = new IntBag(32);
    @Getter @Setter
    private int width = 32;
    @Getter @Setter
    private int height = 32;
    /** x coord of the bottom-left corner of this chunk is located (in chunk coordinats)*/
    @Getter @Setter
    private int originX = 0;
    /** y coord of the bottom-left corner of this chunk is located (in chunk coordinates)*/
    @Getter @Setter
    private int originY = 0;
}
