package laurencewarne.secondspace.server.component;

import com.artemis.Component;
import com.artemis.annotations.DelayedComponentRemoval;
import com.artemis.annotations.EntityId;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * If an entity has this component, then the entity is part of a ship.
 */
@Getter @Setter @EqualsAndHashCode(callSuper=false) @ToString @DelayedComponentRemoval
public class ShipPart extends Component {
    /** The id of the ship this component belongs to*/
    @EntityId
    public int shipId = -1;
    /** Whether this ship part is a 'controller'*/
    private boolean isController = false;
    /** Bottom right x coordinate of this part in local ship coordinate axes*/
    private int localX = 0;
    /** Bottom right y coordinate of this part in local ship coordinate axes*/
    private int localY = 0;
}
