package laurencewarne.secondspace.server.init;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.aeonbits.owner.ConfigFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

public class ServerConfigTest {

    private Properties props;
    private String fileContents = "worldSaveFileLocation=name\nmaxClientConnections=100";

    private ServerConfig config;

    @Before
    public void setUp() {
	MockitoAnnotations.initMocks(this);

	props = new Properties();
    }

    public void loadFileContentsIntoProperties() {
	// For obtaining a Stream from a string see:
	// https://stackoverflow.com/questions/782178/how-do-i-convert-a-string-to-an-inputstream-in-java
	try {	    
	    props.load(
		new ByteArrayInputStream(
		    fileContents.getBytes(StandardCharsets.UTF_8)
		)
	    );
	} catch (Exception e){
	    // fail test
	    fail("Can't wrap string in InputStream");
	}
    }

    @Test
    public void testConfigLoadsWorldSaveFileNameCorrectly() {
	fileContents = "worldSaveFileLocation=my-world.json";
	loadFileContentsIntoProperties();
	ServerConfig serverConfig = ConfigFactory.create(
	    ServerConfig.class, props
	);
	assertEquals("my-world.json", serverConfig.worldSaveFileLocation());
    }

    @Test
    public void testConfigLoadsMaxConnectionsCorrectly() {
	fileContents = "maxClientConnections=5";
	loadFileContentsIntoProperties();
	ServerConfig serverConfig = ConfigFactory.create(
	    ServerConfig.class, props
	);
	assertEquals(5, serverConfig.maxClientConnections());
    }

    @Test
    public void testConfigLoadsDefaultConnectionsAndFileNameOnEmptyConfig() {
	fileContents = "";
	loadFileContentsIntoProperties();
	ServerConfig serverConfig = ConfigFactory.create(
	    ServerConfig.class, props
	);
	assertEquals(10, serverConfig.maxClientConnections());
	assertEquals("world.json", serverConfig.worldSaveFileLocation());
    }
}
