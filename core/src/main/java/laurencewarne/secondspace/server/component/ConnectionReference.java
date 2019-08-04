package laurencewarne.secondspace.server.component;

import com.artemis.Component;
import com.artemis.annotations.EntityId;
import com.artemis.utils.IntBag;

import lombok.ToString;

/**
 * Maps ids of entities the entity this component belongs to to Connection ids.
 */
@ToString
public class ConnectionReference extends Component {
    @EntityId
    public IntBag connectedEntities = new IntBag();
    @EntityId
    public IntBag links = new IntBag();
}
