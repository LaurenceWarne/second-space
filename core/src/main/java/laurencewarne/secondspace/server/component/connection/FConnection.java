package laurencewarne.secondspace.server.component.connection;

import com.artemis.Component;
import com.artemis.annotations.EntityId;
import com.artemis.annotations.Transient;
import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString @Transient
public class FConnection extends Component {
    @EntityId
    public int entityAId = -1;
    @EntityId
    public int entityBId = -1;
    @Getter @Setter
    private float angle = 0f;
    @Getter
    private BiMap<Vector2, Vector2> localCoordMap = HashBiMap.create();
}
