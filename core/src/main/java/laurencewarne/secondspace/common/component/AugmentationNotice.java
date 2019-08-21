package laurencewarne.secondspace.common.component;

import com.artemis.Component;
import com.artemis.annotations.EntityId;
import com.artemis.annotations.Transient;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 */
@Transient @Getter @Setter @ToString
public class AugmentationNotice extends Component {
    /** Name of the template entity was loaded from.*/
    private String templateName = "";
    /** Desired x position.*/
    private int shipX = 0;
    /** Desired y position.*/
    private int shipY = 0;
    /** Owner of any ShipPart components in the template, -1 to ignore.*/
    @EntityId
    public int ship = 1;

    public void setFromRequest(@NonNull AugmentationRequest request) {
	this.templateName = request.getTemplateName();
	this.shipX = request.getShipX();
	this.shipY = request.getShipY();
	this.ship = request.ship;
    }
}
