package laurencewarne.secondspace.server.component;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

/**
 * This component stores the ids of entities connected to the entity this component belongs to, and the location of those connections.
 */
@Getter @Setter @EqualsAndHashCode(callSuper=false) @ToString
public class Connections extends Component {
    /** Mapping of entity ids to {@link Array}s of coordinates which denote the location of a connection between the entity this component belongs to and the entity with the id. The coordinates are entity local.*/
    private IntMap<Array<Vector2>> entityToConnectionLocationMap =
	new IntMap<>(4);

    public Array<Vector2> getConnectionPoints(
	int id, boolean createIfNonexistant
    ) {
	if (entityToConnectionLocationMap.containsKey(id)) {
	    return entityToConnectionLocationMap.get(id);
	}
	else {
	    final Array<Vector2> emptyArr = new Array<>();
	    if (createIfNonexistant) {
		entityToConnectionLocationMap.put(id, emptyArr);
	    }
	    return emptyArr;
	}
    }

    public void put(int id, @NonNull Vector2 location) {
	if (!entityToConnectionLocationMap.containsKey(id)) {
	    entityToConnectionLocationMap.put(id, new Array<>());
	}
	entityToConnectionLocationMap.get(id).add(location);
    }

    public boolean connectionWith(int id, @NonNull Vector2 position) {
	if (entityToConnectionLocationMap.containsKey(id)) {
	    final Array<Vector2> connections =
		entityToConnectionLocationMap.get(id);
	    return connections.contains(position, false);
	}
	else {
	    return false;
	}
    }
}
