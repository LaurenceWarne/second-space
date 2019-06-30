package laurencewarne.secondspace.server.component;

import com.artemis.Component;
import com.artemis.annotations.Transient;
import com.badlogic.gdx.physics.box2d.Body;

import lombok.Getter;
import lombok.Setter;

/**
 *
 */
@Transient @Getter @Setter
public class Physics extends Component {

    private Body body;
}
