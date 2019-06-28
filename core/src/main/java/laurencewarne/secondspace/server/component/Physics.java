package laurencewarne.secondspace.server.component;

import com.artemis.Component;
import com.badlogic.gdx.physics.box2d.Body;

/**
 
 */
public class Physics extends Component {

    private Body body;

    public Body getBody() {
	return body;
    }

    public void setBody(Body body) {
	this.body = body;
    }
}
