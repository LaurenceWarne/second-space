package laurencewarne.secondspace.common.system.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener.TypeListener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import laurencewarne.secondspace.common.component.Player;
import laurencewarne.secondspace.common.component.network.NetworkConnection;
import laurencewarne.secondspace.common.component.network.RegistrationRequest;
import laurencewarne.secondspace.common.component.network.RegistrationResponse;

public class NewClientConnectionSystemTest {

    private World world;
    private ComponentMapper<Player> mP;
    private ComponentMapper<NetworkConnection> mC;
    private NewClientConnectionSystem sys;
    private TypeListener typeListener;
    @Mock
    private Connection conn;

    @Before
    public void setUp() {
	MockitoAnnotations.initMocks(this);
	WorldConfiguration setup = new WorldConfigurationBuilder()
	    .with(
		  sys = new NewClientConnectionSystem()
	    )
	    .build();
	setup.register(typeListener = new TypeListener());
	world = new World(setup);
	world.process();
	mP = world.getMapper(Player.class);
	mC = world.getMapper(NetworkConnection.class);
    }

    @Test
    public void testServerSendsResponseToClientOnNewConnection() {
	RegistrationRequest req = new RegistrationRequest();
	req.setName("Bob");
	typeListener.received(conn, req);
	Mockito.verify(conn, times(1)).sendTCP(any());
    }

    @Test
    public void testNewPlayerCreatedOnNewConnection() {
	RegistrationRequest req = new RegistrationRequest();
	req.setName("Bob");
	typeListener.received(conn, req);
	ArgumentCaptor<RegistrationResponse> argument = ArgumentCaptor
	    .forClass(RegistrationResponse.class);
	Mockito.verify(conn).sendTCP(argument.capture());
	int id = argument.getValue().getPlayerId();
	assertTrue(mP.has(id));
	assertEquals("Bob", mP.get(id).getName());
    }

    @Test
    public void testNoPlayerCreatedOnRequestToExistingConnection() {
	RegistrationRequest req = new RegistrationRequest();
	req.setName("Bob");
	typeListener.received(conn, req);
	world.process();
	typeListener.received(conn, req);
	ArgumentCaptor<RegistrationResponse> argument = ArgumentCaptor
	    .forClass(RegistrationResponse.class);
	Mockito.verify(conn, times(2)).sendTCP(argument.capture());
	int id = argument.getAllValues().get(1).getPlayerId();
	assertEquals(-1, id);
    }

    @Test
    public void testPlayerReturnedOnKnownPlayerNotConnected() {
	RegistrationRequest req = new RegistrationRequest();
	req.setName("Bob");
	typeListener.received(conn, req);
	world.process();
	ArgumentCaptor<RegistrationResponse> argument = ArgumentCaptor
	    .forClass(RegistrationResponse.class);
	Mockito.verify(conn, times(1)).sendTCP(argument.capture());
	int id = argument.getValue().getPlayerId();
	mC.remove(id);
	
	typeListener.received(conn, req);
	world.process();
	ArgumentCaptor<RegistrationResponse> argument2 = ArgumentCaptor
	    .forClass(RegistrationResponse.class);
	Mockito.verify(conn, times(2)).sendTCP(argument2.capture());
	assertEquals(id, argument2.getAllValues().get(1).getPlayerId());
    }
}
