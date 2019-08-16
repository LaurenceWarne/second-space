package laurencewarne.secondspace.server.component;

import com.artemis.Component;
import com.artemis.annotations.EntityId;

public class Bullet extends Component {
    @EntityId
    public int sourceId = -1;
}
