package laurencewarne.secondspace.common.system.network;

import java.io.IOException;

import com.artemis.BaseSystem;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.TypeListener;
import com.esotericsoftware.kryonet.Server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.NonNull;

public class NetworkConnectionSystem extends BaseSystem {

    private final Logger logger = LoggerFactory.getLogger(
	NetworkConnectionSystem.class
    );
    private Server server;
    private final int TCPPort = 54555;
    private final int UDPPort = 54777;

    @Override
    public void initialize() {
	server = new Server();
	server.start();
	try {
	    // Starts a server on TCP port 54555 and UDP port 54777:
	    server.bind(TCPPort, UDPPort);
	} catch (IOException e) {
	    logger.error("IO error binding ports: " + e.getMessage());
	}
	TypeListener typeListener = new TypeListener();
	server.addListener(typeListener);
	server.addListener(new Listener() {
		public void received (Connection connection, Object object) {
		    System.out.println(
			"Received from client: " + connection.getRemoteAddressTCP() +
			" object: " + object
		    );
		}
	    });
    }

    @Override
    public void processSystem() {
	for (Connection c : server.getConnections()) {
	    final SndSpaceConnection con = (SndSpaceConnection) c;
	}
    }

    public void sendClientSurroundings(@NonNull SndSpaceConnection con) {
	// Send client their ship
	// Send client surrounding entities via chunk manager
    }

    public static class SndSpaceConnection extends Connection {
	private int playerId;
    }
}
