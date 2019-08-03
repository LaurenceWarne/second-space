package laurencewarne.secondspace.server;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Collectors;

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
import laurencewarne.secondspace.server.ship.ShipCoordinateLocaliser;
import laurencewarne.secondspace.server.system.PhysicsRectangleSynchronizerSystem;
import laurencewarne.secondspace.server.system.PhysicsSystem;
import laurencewarne.secondspace.server.system.ShipWeldingSystem;
import laurencewarne.secondspace.server.system.TemplateSystem;
import laurencewarne.secondspace.server.system.TerminalSystem;
import laurencewarne.secondspace.server.system.WorldDeserializationSystem;
import laurencewarne.secondspace.server.system.WorldSerializationSystem;
import laurencewarne.secondspace.server.system.command.AddRectangleCommandExecutorSystem;
import laurencewarne.secondspace.server.system.command.AddWeldCommandExecutorSystem;
import laurencewarne.secondspace.server.system.command.EntityRemovalCommandExecutorSystem;
import laurencewarne.secondspace.server.system.command.SpawnCommandExecutorSystem;
import laurencewarne.secondspace.server.system.resolvers.PhysicsRectangleDataResolverSystem;
import laurencewarne.secondspace.server.system.resolvers.WeldJointDataResolverSystem;
import lombok.Getter;
import net.fbridault.eeel.EEELPlugin;
import net.mostlyoriginal.api.event.common.EventSystem;

/**
 * Headless implementation of {@link Game}, ie does no rendering and creates no
 * windows, just updates the world.
 */
@Getter
public class SecondSpaceServerBase extends Game {

    private final Logger logger = LoggerFactory.getLogger(SecondSpaceServerBase.class);
    private com.badlogic.gdx.physics.box2d.World box2dWorld;
    @Getter
    private World world;

    @Override
    public void create() {
	////////////////////////
	// Load server config //
	////////////////////////
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

	//////////////////////////////////////////////////////////////////
	// Create world configuration, add plugins and systems to world //
	//////////////////////////////////////////////////////////////////
	final WorldConfigurationBuilder setupBuilder =
	    new WorldConfigurationBuilder();
	setupWorldConfig(setupBuilder);
	final WorldConfiguration setup = setupBuilder.build();

	/////////////////////////////////////
	// Inject non-artemis dependencies //
	/////////////////////////////////////
	setup.register(new ShipCoordinateLocaliser());
	box2dWorld = new com.badlogic.gdx.physics.box2d.World(
	    new Vector2(0f, 0f), true
	);
	setup.register(box2dWorld);
	final FileHandle worldSaveFile = Gdx.files.local(
	    serverConfig.worldSaveFileLocation()
	);
	if (!worldSaveFile.exists()) {
	    logger.info("Creating empty world save file: {}", worldSaveFile.path());
	    // Minimum acceptable JSON file
	    worldSaveFile.writeString("{}", false);
	}
	setup.register("worldSaveFile", worldSaveFile);
	final FileHandle[] templateFiles = Gdx.files.local(
	    serverConfig.templatesDirectory()
	).list();
	setup.register(
	    "templateFiles",
	    Arrays.stream(templateFiles).collect(Collectors.toList())
	);

	///////////////////////////////
	// Create Artermis World obj //
	///////////////////////////////
	world = new World(setup);
    }

    /**
     * Add systems and plugins to the artemis {@link WorldConfigurationBuilder} here.
     * 
     * @param configBuilder {@link WorldConfigurationBuilder} obj to use
     */
    protected void setupWorldConfig(WorldConfigurationBuilder configBuilder) {
	configBuilder
	    .dependsOn(EntityLinkManager.class)
	    .with(new EEELPlugin())
	    .with(
		new WorldSerializationManager(),
		new WorldDeserializationSystem(),
		new TerminalSystem(),
		new AddRectangleCommandExecutorSystem(),
		new AddWeldCommandExecutorSystem(),
		new SpawnCommandExecutorSystem(),
		new EntityRemovalCommandExecutorSystem(),
		new TemplateSystem(),
		new ShipWeldingSystem(),
		new PhysicsRectangleDataResolverSystem(),
		new WeldJointDataResolverSystem(),
		new PhysicsSystem(),
		new EventSystem(),
		new PhysicsRectangleSynchronizerSystem(10f),
		new WorldSerializationSystem(10f)
	    );
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
