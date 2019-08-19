package laurencewarne.secondspace.common.component.network;

import com.artemis.Component;
import com.artemis.annotations.Transient;
import com.esotericsoftware.kryonet.Connection;

import lombok.Getter;
import lombok.Setter;

/**
 * Encapsulates a connection between the server and a client.
 */
@Transient @Getter @Setter
public class NetworkConnection extends Component {
    private Connection connection;
}
