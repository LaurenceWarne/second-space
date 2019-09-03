package laurencewarne.secondspace.client.component;

import com.artemis.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * Indicates this player belongs to the client. Singleton.
 */
public class ClientPlayer extends Component {
    /** The id of the client player on the server.*/
    @Getter @Setter
    private int serverId = -1;
}
