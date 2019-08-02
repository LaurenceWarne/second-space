package laurencewarne.secondspace.server.component;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * This component stores the ids of entities connected to the entity this component belongs to, and the location of those connections.
 */
@Getter @Setter @EqualsAndHashCode(callSuper=false) @ToString
public class Connections extends Component {
    /** Mapping of entity ids to {@link Array}s of coordinates which denote the location of a connection between the entity this component belongs to and the entity with the id.*/
    private IntMap<Array<Vector2>> entityToConnectionLocationMap =
	new IntMap<>(4);
}
