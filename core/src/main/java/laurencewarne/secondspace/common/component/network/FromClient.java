package laurencewarne.secondspace.common.component.network;

import com.artemis.Component;
import com.artemis.annotations.Transient;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Component representing a component sent from a client.
 */
@ToString @Transient
public class FromClient extends Component {
    @Getter @Setter
    private int id = -1;
    @Getter @Setter
    private int clientId = -1;
    @Getter @Setter
    private Component component;
}
