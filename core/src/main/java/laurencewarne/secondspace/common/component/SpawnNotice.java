package laurencewarne.secondspace.common.component;

import com.artemis.Component;
import com.artemis.annotations.Transient;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

/**
 * If an entity has this component it indicates that it has been spawned from some system (not loaded from initial world deserialization), and the entity may not have been fully initialized.
 */
@Transient @Getter @Setter @ToString
public class SpawnNotice extends Component {
    /** Name of the template entity was loaded from.*/
    private String templateName = "";
    /** Desired x position.*/
    private float x = 0f;
    /** Desired y position.*/
    private float y = 0f;
    /** The {@link Ship} owner of any {@link ShipPart}s, -1 indicates no owner exists or change of owner is required*/
    private int shipOwner = -1;

    public void setFromRequest(@NonNull SpawnRequest request) {
	this.templateName = request.getTemplateName();
	this.x = request.getX();
	this.y = request.getY();
	this.shipOwner = request.getShipOwner();
    }
}
