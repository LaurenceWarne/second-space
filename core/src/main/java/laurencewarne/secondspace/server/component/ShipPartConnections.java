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
 * This component stores the ids of the ship parts an entity is connected to, and the location of those connections. Hence this component does not make sense if the entity does not have a {@link ShipPart} component also.
 */
@Getter @Setter @EqualsAndHashCode(callSuper=false) @ToString
public class ShipPartConnections extends Component {
    /** Mapping of entity ids to {@link Array}s of coordinates which denote the location of a connection between the entity this component belongs to and the entity with the id.*/
    private IntMap<Array<Vector2>> entityToConnectionLocationMapping =
	new IntMap<>();
}
