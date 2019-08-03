package laurencewarne.secondspace.server.component;

import com.artemis.Component;
import com.artemis.annotations.EntityId;
import com.artemis.annotations.Transient;
import com.badlogic.gdx.math.Vector2;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Encapsulates a request to weld two entities together.
 */
@Transient @Getter @Setter @ToString
public class WeldRequest extends Component {
    /** Entity id of the entity corresponding to body A in the weld joint.*/
    @EntityId
    public int cellAID = -1;
    /** Entity id of the entity corresponding to body B in the weld joint.*/
    @EntityId
    public int cellBID = -1;
    /** Local coordinate of body A denoting where the joint is.*/
    private Vector2 localAnchorA = new Vector2();
    /** Local coordinate of body B denoting where the joint is.*/
    private Vector2 localAnchorB = new Vector2();
    /** Difference in angle of each body.*/
    private float referenceAngle = 0f;
}
