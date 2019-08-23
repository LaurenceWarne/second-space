package laurencewarne.secondspace.common;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.stream.Collectors;

import com.artemis.Component;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.link.EntityLinkManager;
import com.artemis.managers.WorldSerializationManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.esotericsoftware.kryonet.Listener.TypeListener;
import com.esotericsoftware.kryonet.Server;

import org.aeonbits.owner.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.common.init.ServerConfig;
import laurencewarne.secondspace.common.manager.ChunkManager;
import laurencewarne.secondspace.common.manager.ConnectionManager;
import laurencewarne.secondspace.common.ship.ShipCoordinateLocaliser;
import laurencewarne.secondspace.common.system.AugmentationHandlerSystem;
import laurencewarne.secondspace.common.system.CannonCooldownSystem;
import laurencewarne.secondspace.common.system.CannonFiringSystem;
import laurencewarne.secondspace.common.system.CollisionSystem;
import laurencewarne.secondspace.common.system.InitSpawnedEntitiesSystem;
import laurencewarne.secondspace.common.system.PhysicsRectangleSynchronizerSystem;
import laurencewarne.secondspace.common.system.PhysicsSystem;
import laurencewarne.secondspace.common.system.PlayerShipCreatorSystem;
import laurencewarne.secondspace.common.system.ShipConnectionSystem;
import laurencewarne.secondspace.common.system.ShipPartRemovalSystem;
import laurencewarne.secondspace.common.system.ShipPositioningSystem;
import laurencewarne.secondspace.common.system.SpawnFromTemplateSystem;
import laurencewarne.secondspace.common.system.TemplateLoadingSystem;
import laurencewarne.secondspace.common.system.TerminalSystem;
import laurencewarne.secondspace.common.system.ThrusterSystem;
import laurencewarne.secondspace.common.system.WeldControllerSystem;
import laurencewarne.secondspace.common.system.WorldDeserializationSystem;
import laurencewarne.secondspace.common.system.WorldSerializationSystem;
import laurencewarne.secondspace.common.system.command.AddRectangleCommandExecutorSystem;
import laurencewarne.secondspace.common.system.command.AddWeldCommandExecutorSystem;
import laurencewarne.secondspace.common.system.command.AugmentationCommandExecutorSystem;
import laurencewarne.secondspace.common.system.command.EntityRemovalCommandExecutorSystem;
import laurencewarne.secondspace.common.system.command.SaveCommandExecutorSystem;
import laurencewarne.secondspace.common.system.command.SpawnCommandExecutorSystem;
import laurencewarne.secondspace.common.system.network.NetworkConnectionSystem;
import laurencewarne.secondspace.common.system.network.NetworkRegisterSystem;
import laurencewarne.secondspace.common.system.network.NewClientConnectionSystem;
import laurencewarne.secondspace.common.system.resolvers.ConnectionToWeldSystem;
import laurencewarne.secondspace.common.system.resolvers.PhysicsRectangleDataResolverSystem;
import lombok.Getter;
import lombok.NonNull;
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
    private World world;
    private Server server;

    @Override
    public void create() {
	////////////////////////
	// Load server config //
	////////////////////////
	FileHandle serverConfigFile = Gdx.files.local("server.properties");
	logger.info("Loading server config file");
	final Properties serverProperties = new Properties();
	try {
	    serverProperties.load(serverConfigFile.read());
	    logger.info("Loaded server config file: " + serverConfigFile.path());
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
	server = new Server();

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
	injectDependencies(setup, serverConfig);

	///////////////////////////////
	// Create Artermis World obj //
	///////////////////////////////
	logger.info("Initializing world systems");
	world = new World(setup);
	logger.info("Finished intializing world systems");
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
	    .with(  // Deserialization and events
		new EventSystem(),
		new WorldSerializationManager(),
		new WorldDeserializationSystem()
	    )
	    .with(  // Managers
		new ConnectionManager(),
		new ChunkManager()
	    )
	    .with(  // Network
		new NetworkRegisterSystem(),
		new NetworkConnectionSystem(),
		new NewClientConnectionSystem()
	    )
	    .with(  // Terminal and command systems
		new TerminalSystem(),
		new AddRectangleCommandExecutorSystem(),
		new AddWeldCommandExecutorSystem(),
		new SpawnCommandExecutorSystem(),
		new AugmentationCommandExecutorSystem(),
		new EntityRemovalCommandExecutorSystem(),
		new SaveCommandExecutorSystem()
	    )
	    .with(  // Entity creation and initialization
		new TemplateLoadingSystem(),
		new SpawnFromTemplateSystem(),
		new AugmentationHandlerSystem(),
		new InitSpawnedEntitiesSystem()
	    )
	    .with(  // Create fron-end components from back-end components
		new PhysicsRectangleDataResolverSystem(),		
		new ConnectionToWeldSystem()
	    )
	    .with(  // Ship and ShipPart handling
		// Adds welds in box2d world
		new WeldControllerSystem(),
		// Sends WeldRequests when ShipParts are added
		new ShipConnectionSystem(),
		new ShipPositioningSystem(),
		new ShipPartRemovalSystem()
	    )
	    .with(  // 'vanity' systems
		new PhysicsSystem(1f/40f),
		new CollisionSystem(),
		new ThrusterSystem(),
		new CannonFiringSystem(),
		new CannonCooldownSystem(),
		new PlayerShipCreatorSystem()
	    )
	    .with(  // Synchronizes front-end components and back-end components
		new PhysicsRectangleSynchronizerSystem()
	    )
	    .with(  // Serialization
		new WorldSerializationSystem()
	    );
    }

    /**
     * Inject system dependencies or perform any other config on the {@link WorldConfiguration} object prior to {@link World} creation.
     *
     * @param setup 
     * @param serverConfig loaded server configuration file
     */
    protected void injectDependencies(
	@NonNull WorldConfiguration setup, @NonNull ServerConfig serverConfig
    ) {
	setup.register(new ShipCoordinateLocaliser());
	box2dWorld = new com.badlogic.gdx.physics.box2d.World(
	    new Vector2(0f, -0.1f), true
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
	setup.register("server", server);
	setup.register("kryo", server.getKryo());
	final TypeListener listener = new TypeListener();
	server.addListener(listener);
	setup.register(listener);
	setup.register("templates", new HashMap<String, byte[]>());
	setup.register(
	   "networked-components",
	   new Array<Class<? extends Component>>()
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
