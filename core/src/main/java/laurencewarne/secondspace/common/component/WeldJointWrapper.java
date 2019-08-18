package laurencewarne.secondspace.common.component;

import com.artemis.Component;
import com.artemis.annotations.Transient;
import com.badlogic.gdx.physics.box2d.joints.WeldJoint;

import lombok.Getter;
import lombok.Setter;

@Transient @Getter @Setter
public class WeldJointWrapper extends Component {
    private WeldJoint weldJoint;
}
