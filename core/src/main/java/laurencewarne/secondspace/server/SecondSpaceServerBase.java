package laurencewarne.secondspace.server;

import java.io.IOException;
import java.util.Properties;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.link.EntityLinkManager;
import com.artemis.managers.WorldSerializationManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;

import org.aeonbits.owner.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.server.init.ServerConfig;
import laurencewarne.secondspace.server.system.AddRectangleCommandExecutorSystem;
import laurencewarne.secondspace.server.system.PhysicsRectangleDataResolverSystem;
import laurencewarne.secondspace.server.system.PhysicsRectangleSynchronizerSystem;
import laurencewarne.secondspace.server.system.PhysicsSystem;
import laurencewarne.secondspace.server.system.TerminalSystem;
import laurencewarne.secondspace.server.system.WorldDeserializationSystem;
import laurencewarne.secondspace.server.system.WorldSerializationSystem;
import lombok.Getter;

/**
 * Headless implementation of {@link Game}, ie does no rendering, just updates.
 */
@Getter
public class SecondSpaceServerBase extends Game {

    private final Logger logger = LoggerFactory.getLogger(SecondSpaceServerBase.class);
    private com.badlogic.gdx.physics.box2d.World box2dWorld;
    private World world;

    @Override
    public void create() {
	// Load config
	FileHandle serverConfigFile = Gdx.files.local("server.properties");
	final Properties serverProperties = new Properties();
	try {
	    serverProperties.load(serverConfigFile.read());
	}
	catch (GdxRuntimeException e){
	    logger.info(
		"Server properties file is not a valid file or missing," +
		" using default server configuration"
	    );
	}
	catch (IOException|IllegalArgumentException e){
	    logger.error(
		"Error occurred whilst reading the server properties file," +
		" using default server configuration."
	    );
	}
	ServerConfig serverConfig = ConfigFactory.create(
	    ServerConfig.class, serverProperties
	);

	// Init Artemis stuff: register any plugins, setup the world.
	final WorldConfiguration setup = new WorldConfigurationBuilder()
	    .dependsOn(EntityLinkManager.class)
	    .with(
		new WorldSerializationManager(),
		new WorldDeserializationSystem(),
		new TerminalSystem(),
		new AddRectangleCommandExecutorSystem(),
		new PhysicsRectangleDataResolverSystem(),
		new PhysicsSystem(),
		new PhysicsRectangleSynchronizerSystem(10f),
		new WorldSerializationSystem(10f)
	    )
	    .build();

	// Inject non-artemis dependencies
	box2dWorld = new com.badlogic.gdx.physics.box2d.World(
	    new Vector2(0, -10), true
	);
	setup.register(box2dWorld);
	FileHandle worldSaveFile = Gdx.files.local(
	    serverConfig.worldSaveFileLocation()
	);
	if (!worldSaveFile.exists()) {
	    logger.info("Creating empty world save file: {}", worldSaveFile.path());
	    // Minimum acceptable JSON file
	    worldSaveFile.writeString("{}", false);
	}
	setup.register("worldSaveFile", worldSaveFile);
	// Create Artermis World
	world = new World(setup);
    }

    @Override
    public void render() {
	world.setDelta(Gdx.graphics.getDeltaTime());
	world.process();
    }

    @Override
    public void dispose() {
	world.dispose();	
    }
}
