package laurencewarne.secondspace.server.init;

import java.io.IOException;
import java.util.Properties;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor @Getter @Setter
public class ServerConfigLoader {

    @NonNull
    private FileHandle serverConfigFile;
    // defaults
    private String defaultWorldSaveFileName = "world.json";
    private int defaultMaxClientConnections = 10;

    public ServerConfig load() {

	// Load properties
	final Properties properties = new Properties();
	try {
	    properties.load(serverConfigFile.read());
	}
	catch (GdxRuntimeException e){
	    // thrown if file is a directory
	}
	catch (IOException e){
	    // something bad happended when reading the input stream!
	}
	
	// Attempt to convert properties to correct types
	final String maxClientConnectionsStr = properties.getProperty(
	    "maxConnections", Integer.toString(defaultMaxClientConnections)
	);
	int maxClientConnections = defaultMaxClientConnections;
	try {
	    maxClientConnections = Integer.parseInt(maxClientConnectionsStr);
	}
	catch (NumberFormatException e){
	    
	}
	return new ServerConfig(
	    properties.getProperty("WorldFileName", defaultWorldSaveFileName),
	    maxClientConnections
	);
    }

    @RequiredArgsConstructor @EqualsAndHashCode @ToString @Getter
    public static class ServerConfig {

	@NonNull
	private final String worldSaveFileLocation;
	private final int maxClientConnections;
    }
}
