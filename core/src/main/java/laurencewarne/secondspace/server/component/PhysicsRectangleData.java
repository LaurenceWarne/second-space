package laurencewarne.secondspace.server.component;

import com.artemis.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Encapsulates data corresponding to a physics component so that it can be saved to a file.
 * For this to make sense the physics component must reference a box2D rectangle.
 */
@Getter @Setter @ToString
public class PhysicsRectangleData extends Component {

    // A POJO so fine to serialize
    private float width = 2f;
    private float height = 2f;
    private boolean isStatic = false;
    // Information about the entity specific to the world
    private float x = 0f;
    private float y = 0f;
    private float velocityX = 0f;
    private float velocityY = 0f;
    private float angle = 0f;
}
