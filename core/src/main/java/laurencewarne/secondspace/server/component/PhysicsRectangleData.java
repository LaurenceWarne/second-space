package laurencewarne.secondspace.server.component;

import com.artemis.Component;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Encapsulates data corresponding to a physics component so that it can be saved to a file.
 * For this to make sense the physics component must reference a box2D rectangle.
 */
@Getter @Setter @ToString @EqualsAndHashCode(callSuper=false)
public class PhysicsRectangleData extends Component {
    // A POJO so fine to serialize
    private float width = 2f;
    private float height = 2f;
    private boolean isStatic = false;
    private boolean isBullet = false;
    private float density = 0.5f;
    private float friction = 0.4f;
    private float restitution = 0.6f;
    // Information about the entity specific to the world
    private float x = 0f;
    private float y = 0f;
    private float velocityX = 0f;
    private float velocityY = 0f;
    private float angle = 0f;
}
