package laurencewarne.secondspace.server.component;

import com.artemis.Component;
import com.badlogic.gdx.physics.box2d.BodyDef;

import lombok.Getter;
import lombok.Setter;

/**
 * Encapsulates data corresponding to a physics component so that it can be saved to a file.
 * For this to make sense the physics component must reference a box2D rectangle.
 */
@Getter @Setter
public class PhysicsRectangleData extends Component {

    // A POJO so fine to serialize
    private BodyDef bodyDef;
    private float width;
    private float height;
    // Information about the entity specific to the world
    private float x;
    private float y;
    private float velocityX;
    private float velocityY;
    private float angle;
    
}
