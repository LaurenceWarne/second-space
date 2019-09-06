package laurencewarne.secondspace.common.component;

import com.artemis.Component;
import com.artemis.annotations.Transient;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Encapsulates a request to spawn an entity (or collection of entities - determined by the template) at a position in the world.
 */
@Transient @Getter @Setter @ToString
public class SpawnRequest extends Component {
    /** Name of the template to load*/
    private String templateName = "";
    /** Desired x position*/
    private float x = 0f;
    /** Desired y position*/
    private float y = 0f;
}
