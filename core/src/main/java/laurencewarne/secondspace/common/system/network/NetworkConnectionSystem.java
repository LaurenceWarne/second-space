package laurencewarne.secondspace.common.system.network;

import java.io.IOException;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.TypeListener;
import com.esotericsoftware.kryonet.Server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkConnectionSystem extends BaseSystem {

    private final Logger logger = LoggerFactory.getLogger(
	NetworkConnectionSystem.class
    );
    @Wire(name="server")
    private Server server;
    private final int TCPPort = 54555;
    private final int UDPPort = 54777;

    @Override
    public void initialize() {
	server.start();
	try {
	    logger.info(
		"Attempting to start server on TCP port: {} and UDP port {}",
		TCPPort, UDPPort
	    );
	    // Starts a server on TCP port 54555 and UDP port 54777:
	    server.bind(TCPPort, UDPPort);
	} catch (IOException e) {
	    logger.error("IO error binding ports: " + e.getMessage());
	}
	logger.info(
	    "Successfully started server on TCP port: {} and UDP port {}",
	    TCPPort, UDPPort
	);
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
    protected void processSystem() {
		
    }
}
