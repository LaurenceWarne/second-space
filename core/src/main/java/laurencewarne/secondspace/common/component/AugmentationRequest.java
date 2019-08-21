package laurencewarne.secondspace.common.component;

import com.artemis.Component;
import com.artemis.annotations.EntityId;
import com.artemis.annotations.Transient;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Encapsulates a request to augment an existing {@link Ship} with a {@link ShipPart}.
 */
@Transient @Getter @Setter @ToString
public class AugmentationRequest extends Component {
    /** Name of the augmentation to load.*/
    private String templateName = "";
    /** Desired x position of the augment in ship local coordinates*/
    private int shipX = 0;
    /** Desired y position of the augment in ship local coordinates*/
    private int shipY = 0;
    /** Id of the ship to augment.*/
    @EntityId
    public int ship = -1;
    
}
