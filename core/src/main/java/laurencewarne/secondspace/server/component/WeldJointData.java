package laurencewarne.secondspace.server.component;

import com.artemis.Component;
import com.artemis.annotations.EntityId;
import com.badlogic.gdx.math.Vector2;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Holds all information necessary to recreate a weld joint, for serialization. Can be thought of as a backend to {@link laurencewarne.secondspace.server.system.WeldJointWrapper}.
 */
@Getter @Setter @EqualsAndHashCode(callSuper=true) @ToString
public class WeldJointData extends Component {
	/** Entity id of the entity corresponding to body A in the weld joint.*/
    @EntityId
    public int cellAID = -1;
	/** Entity id of the entity corresponding to body B in the weld joint.*/
    @EntityId
	public int cellBID = -1;
	/** Local coordinate of body A denoting where the joint is.*/
	private Vector2 localAnchorA = new Vector2();
	/** Local coordinate of body A denoting where the joint is.*/
	private Vector2 localAnchorB = new Vector2();
	/** Difference in angle of each body.*/
	private float referenceAngle = 0f;
}
