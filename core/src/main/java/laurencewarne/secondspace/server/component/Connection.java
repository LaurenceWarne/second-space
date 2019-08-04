package laurencewarne.secondspace.server.component;

import com.artemis.Component;
import com.artemis.annotations.EntityId;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Connection extends Component {
    @EntityId
    public int entityAId = -1;
    @EntityId
    public int entityBId = -1;
    @Getter @Setter
    private float angle = 0f;
    @Getter
    private Array<Vector2> localACoords = new Array<>();
    @Getter
    private Array<Vector2> localBCoords = new Array<>();
}
