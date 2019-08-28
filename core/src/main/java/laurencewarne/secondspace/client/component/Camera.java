package laurencewarne.secondspace.client.component;

import com.artemis.Component;
import com.badlogic.gdx.graphics.OrthographicCamera;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Wraps a camera. Singleton.
 */
@ToString @Getter @Setter
public class Camera extends Component {
    private OrthographicCamera camera;
}
