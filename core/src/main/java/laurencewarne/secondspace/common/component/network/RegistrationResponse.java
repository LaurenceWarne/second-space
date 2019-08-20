package laurencewarne.secondspace.common.component.network;

import com.artemis.Component;
import com.artemis.annotations.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Sent from server to client on receiving and processing a {@link RegistrationRequest}.
 */
@Data @EqualsAndHashCode(callSuper=false) @AllArgsConstructor @Transient
public class RegistrationResponse extends Component {
    /**Entity id assigned to the player.*/
    private int playerId = -1;

    public RegistrationResponse() {
	
    }
}
