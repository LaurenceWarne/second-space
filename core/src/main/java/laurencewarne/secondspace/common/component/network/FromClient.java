package laurencewarne.secondspace.common.component.network;

import com.artemis.Component;
import com.artemis.annotations.Transient;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Component representing a component sent from a client.
 */
@ToString @Transient @Getter @Setter
public class FromClient extends Component {
    /** Server id of the entity which has the component.*/
    private int id = -1;
    /** Id of the client (entity with {@link laurencewarne.secondspace.common.component.Player}) on the server.*/
    private int clientId = -1;
    private Component component;
}
