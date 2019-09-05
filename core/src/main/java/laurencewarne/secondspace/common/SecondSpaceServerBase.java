package laurencewarne.secondspace.common;

import java.io.IOException;
import java.util.Arrays;
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

import laurencewarne.componentlookup.ComponentLookupPlugin;
import laurencewarne.secondspace.common.component.Cannon;
import laurencewarne.secondspace.common.component.PhysicsRectangleData;
import laurencewarne.secondspace.common.component.Ship;
import laurencewarne.secondspace.common.component.ShipPart;
import laurencewarne.secondspace.common.component.Thruster;
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
import laurencewarne.secondspace.common.system.network.FromClientAuthenticatorSystem;
import laurencewarne.secondspace.common.system.network.NetworkInitializationSystem;
import laurencewarne.secondspace.common.system.network.NetworkRegisterSystem;
import laurencewarne.secondspace.common.system.network.NewClientConnectionSystem;
import laurencewarne.secondspace.common.system.network.RegisterAuthSystem;
import laurencewarne.secondspace.common.system.network.StateSenderSystem;
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

	int shipId = world.create();
	Ship ship = world.edit(shipId).create(Ship.class);
	int id1 = world.create();
	int id2 = world.create();
	int id3 = world.create();

	PhysicsRectangleData r1 = world.edit(id1)
	    .create(PhysicsRectangleData.class);
	r1.setWidth(4f); r1.setHeight(4f);
	ShipPart s1 = world.edit(id1).create(ShipPart.class);
	s1.setLocalX(0); s1.setLocalY(0); s1.shipId = shipId;
	ship.parts.add(id1);

	PhysicsRectangleData r2 = world.edit(id2)
	    .create(PhysicsRectangleData.class);
	r2.setWidth(2f); r2.setHeight(2f);
	ShipPart s2 = world.edit(id2).create(ShipPart.class);
	s2.setLocalX(4); s2.setLocalY(2); s2.shipId = shipId;
	Cannon c2 = world.edit(id2).create(Cannon.class);
	c2.setLocalBulletSourceY(-1f);
	ship.parts.add(id2);

	PhysicsRectangleData r3 = world.edit(id3)
	    .create(PhysicsRectangleData.class);
	r3.setWidth(2f); r3.setHeight(2f);
	ShipPart s3 = world.edit(id3).create(ShipPart.class);
	s3.setLocalX(6); s3.setLocalY(2); s3.shipId = shipId;
	Thruster t3 = world.edit(id3).create(Thruster.class);
	t3.setLocalApplY(-1f);
	ship.parts.add(id3);

	int id4 = world.create();       
	PhysicsRectangleData r4 = world.edit(id4)
	    .create(PhysicsRectangleData.class);
	r4.setWidth(2f); r4.setHeight(2f);
	ShipPart s4 = world.edit(id4).create(ShipPart.class);
	s4.setLocalX(8); s4.setLocalY(2); s4.shipId = shipId;
	Cannon c4 = world.edit(id4).create(Cannon.class);
	c4.setLocalBulletSourceY(-1f);
	ship.parts.add(id4);

	int id5 = world.create();       
	PhysicsRectangleData r5 = world.edit(id5)
	    .create(PhysicsRectangleData.class);
	r5.setWidth(4f); r5.setHeight(4f);
	ShipPart s5 = world.edit(id5).create(ShipPart.class);
	s5.setLocalX(10); s5.setLocalY(0); s5.shipId = shipId;
	ship.parts.add(id5);
	
	int id6 = world.create();       
	PhysicsRectangleData r6 = world.edit(id6)
	    .create(PhysicsRectangleData.class);
	r6.setWidth(2f); r6.setHeight(2f);
	ShipPart s6 = world.edit(id6).create(ShipPart.class);
	s6.setLocalX(2); s6.setLocalY(4); s6.shipId = shipId;
	Cannon c6 = world.edit(id6).create(Cannon.class);
	c6.setLocalBulletSourceX(-1f);
	ship.parts.add(id6);

	int id7 = world.create();       
	PhysicsRectangleData r7 = world.edit(id7)
	    .create(PhysicsRectangleData.class);
	r7.setWidth(2f); r7.setHeight(2f);
	ShipPart s7 = world.edit(id7).create(ShipPart.class);
	s7.setLocalX(2); s7.setLocalY(6); s7.shipId = shipId;
	Thruster t7 = world.edit(id7).create(Thruster.class);
	t7.setLocalApplX(-1f);	
	ship.parts.add(id7);

	int id8 = world.create();       
	PhysicsRectangleData r8 = world.edit(id8)
	    .create(PhysicsRectangleData.class);
	r8.setWidth(2f); r8.setHeight(2f);
	ShipPart s8 = world.edit(id8).create(ShipPart.class);
	s8.setLocalX(2); s8.setLocalY(8); s8.shipId = shipId;
	Cannon c8 = world.edit(id8).create(Cannon.class);
	c8.setLocalBulletSourceX(-1f);
	ship.parts.add(id8);

	int id9 = world.create();       
	PhysicsRectangleData r9 = world.edit(id9)
	    .create(PhysicsRectangleData.class);
	r9.setWidth(4f); r9.setHeight(4f);
	ShipPart s9 = world.edit(id9).create(ShipPart.class);
	s9.setLocalX(0); s9.setLocalY(10); s9.shipId = shipId;
	ship.parts.add(id9);

	int id10 = world.create();       
	PhysicsRectangleData r10 = world.edit(id10)
	    .create(PhysicsRectangleData.class);
	r10.setWidth(2f); r10.setHeight(2f);
	ShipPart s10 = world.edit(id10).create(ShipPart.class);
	s10.setLocalX(4); s10.setLocalY(10); s10.shipId = shipId;
	Cannon c10 = world.edit(id10).create(Cannon.class);
	c10.setLocalBulletSourceY(1f);
	ship.parts.add(id10);

	int id11 = world.create();       
	PhysicsRectangleData r11 = world.edit(id11)
	    .create(PhysicsRectangleData.class);
	r11.setWidth(2f); r11.setHeight(2f);
	ShipPart s11 = world.edit(id11).create(ShipPart.class);
	s11.setLocalX(6); s11.setLocalY(10); s11.shipId = shipId;
	Thruster t11 = world.edit(id11).create(Thruster.class);
	t11.setLocalApplY(1f);
	ship.parts.add(id11);

	int id12 = world.create();
	PhysicsRectangleData r12 = world.edit(id12)
	    .create(PhysicsRectangleData.class);
	r12.setWidth(2f); r12.setHeight(2f);
	ShipPart s12 = world.edit(id12).create(ShipPart.class);
	s12.setLocalX(8); s12.setLocalY(10); s12.shipId = shipId;
	Cannon c12 = world.edit(id12).create(Cannon.class);
	c12.setLocalBulletSourceY(1f);	
	ship.parts.add(id12);

	int id13 = world.create();       
	PhysicsRectangleData r13 = world.edit(id13)
	    .create(PhysicsRectangleData.class);
	r13.setWidth(4f); r13.setHeight(4f);
	ShipPart s13 = world.edit(id13).create(ShipPart.class);
	s13.setLocalX(10); s13.setLocalY(10); s13.shipId = shipId;
	ship.parts.add(id13);

	int id14 = world.create();
	PhysicsRectangleData r14 = world.edit(id14)
	    .create(PhysicsRectangleData.class);
	r14.setWidth(2f); r14.setHeight(2f);
	ShipPart s14 = world.edit(id14).create(ShipPart.class);
	s14.setLocalX(10); s14.setLocalY(8); s14.shipId = shipId;
	Cannon c14 = world.edit(id14).create(Cannon.class);
	c14.setLocalBulletSourceX(1f);	
	ship.parts.add(id14);

	int id15 = world.create();
	PhysicsRectangleData r15 = world.edit(id15)
	    .create(PhysicsRectangleData.class);
	r15.setWidth(2f); r15.setHeight(2f);
	ShipPart s15 = world.edit(id15).create(ShipPart.class);
	s15.setLocalX(10); s15.setLocalY(6); s15.shipId = shipId;
	Thruster t15 = world.edit(id15).create(Thruster.class);
	t15.setLocalApplX(1f);
	ship.parts.add(id15);

	int id16 = world.create();
	PhysicsRectangleData r16 = world.edit(id16)
	    .create(PhysicsRectangleData.class);
	r16.setWidth(2f); r16.setHeight(2f);
	ShipPart s16 = world.edit(id16).create(ShipPart.class);
	s16.setLocalX(10); s16.setLocalY(4); s16.shipId = shipId;
	Cannon c16 = world.edit(id16).create(Cannon.class);
	c16.setLocalBulletSourceX(1f);	
	ship.parts.add(id16);
	
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
	    .with(new ComponentLookupPlugin())
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
		new NetworkRegisterSystem(),          // Registers classes for Kryo
		new NetworkInitializationSystem(),    // Starts server
		new NewClientConnectionSystem(),      // Handles new clients
		new StateSenderSystem(),              // Sends world state to clients
		new FromClientAuthenticatorSystem(),  // Handles client requests
		new RegisterAuthSystem()              // Handles specific requests
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
	setup.register("server", server);
	setup.register("kryo", server.getKryo());
	final TypeListener listener = new TypeListener();
	server.addListener(listener);
	setup.register(listener);
	setup.register(
	   "server-to-client-components",
	   new Array<Class<? extends Component>>()
	);
	setup.register(
	   "client-to-server-components",
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
