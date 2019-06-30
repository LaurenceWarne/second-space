package laurencewarne.secondspace.server.init;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import com.badlogic.gdx.files.FileHandle;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import laurencewarne.secondspace.server.init.ServerConfigLoader.ServerConfig;

public class ServerConfigLoaderTest {

    @Mock
    private FileHandle serverConfigFile;
    private String fileContents = "worldFileName=name\na=b";

    private ServerConfigLoader configLoader;

    @Before
    public void setUp() {
	MockitoAnnotations.initMocks(this);
	// See:
	when(serverConfigFile.read())
	    .thenReturn(
		new ByteArrayInputStream(
		    fileContents.getBytes(StandardCharsets.UTF_8)
		)
	    );
	configLoader = new ServerConfigLoader(serverConfigFile);
    }

    @Test
    public void testConfigLoadsWorldSaveFileNameCorrectly() {
	final String config = "worldFileName=my-world.json";
	when(serverConfigFile.read())
	    .thenReturn(
		new ByteArrayInputStream(
		    config.getBytes(StandardCharsets.UTF_8)
		)
	    );
	configLoader = new ServerConfigLoader(serverConfigFile);	
	ServerConfig serverConfig = configLoader.load();
	assertEquals("my-world.json", serverConfig.getWorldSaveFileLocation());
    }
}
