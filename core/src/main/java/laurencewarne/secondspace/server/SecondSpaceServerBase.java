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

import laurencewarne.secondspace.server.component.PhysicsRectangleData;
import laurencewarne.secondspace.server.component.Ship;
import laurencewarne.secondspace.server.component.ShipPart;
import laurencewarne.secondspace.server.init.ServerConfig;
import laurencewarne.secondspace.server.ship.ShipCoordinateLocaliser;
import laurencewarne.secondspace.server.system.AddRectangleCommandExecutorSystem;
import laurencewarne.secondspace.server.system.AddWeldCommandExecutorSystem;
import laurencewarne.secondspace.server.system.PhysicsRectangleDataResolverSystem;
import laurencewarne.secondspace.server.system.PhysicsRectangleSynchronizerSystem;
import laurencewarne.secondspace.server.system.PhysicsSystem;
import laurencewarne.secondspace.server.system.ShipWeldingSystem;
import laurencewarne.secondspace.server.system.TerminalSystem;
import laurencewarne.secondspace.server.system.WeldJointDataResolverSystem;
import laurencewarne.secondspace.server.system.WorldDeserializationSystem;
import laurencewarne.secondspace.server.system.WorldSerializationSystem;
import lombok.Getter;
import net.fbridault.eeel.EEELPlugin;

/**
 * Headless implementation of {@link Game}, ie does no rendering, just updates.
 */
@Getter
public class SecondSpaceServerBase extends Game {

    private final Logger logger = LoggerFactory.getLogger(SecondSpaceServerBase.class);
    private com.badlogic.gdx.physics.box2d.World box2dWorld;
    @Getter
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
	final WorldConfigurationBuilder setupBuilder =
	    new WorldConfigurationBuilder();
	setupWorldConfig(setupBuilder);
	final WorldConfiguration setup = setupBuilder.build();

	// Inject non-artemis dependencies
	setup.register(new ShipCoordinateLocaliser());
	box2dWorld = new com.badlogic.gdx.physics.box2d.World(
	    new Vector2(0, -5), true
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

	int shipId = world.create();
	Ship ship = world.edit(shipId).create(Ship.class);
	int id1 = world.create();
	int id2 = world.create();
	int id3 = world.create();

	PhysicsRectangleData r1 = world.edit(id1)
	    .create(PhysicsRectangleData.class);
	r1.setWidth(10f); r1.setHeight(4f);
	ShipPart s1 = world.edit(id1).create(ShipPart.class);
	s1.setLocalX(0); s1.setLocalY(0); s1.shipId = shipId;

	PhysicsRectangleData r2 = world.edit(id2)
	    .create(PhysicsRectangleData.class);
	r2.setWidth(2f); r2.setHeight(15f);
	ShipPart s2 = world.edit(id2).create(ShipPart.class);
	s2.setLocalX(-2); s2.setLocalY(0); s2.shipId = shipId;

	PhysicsRectangleData r3 = world.edit(id3)
	    .create(PhysicsRectangleData.class);
	r3.setWidth(2f); r3.setHeight(15f);
	ShipPart s3 = world.edit(id3).create(ShipPart.class);
	s3.setLocalX(10); s3.setLocalY(0); s3.shipId = shipId;

	int id4 = world.create();	
	PhysicsRectangleData r4 = world.edit(id4)
	    .create(PhysicsRectangleData.class);
	r4.setWidth(3f); r4.setHeight(7f);
	ShipPart s4 = world.edit(id4).create(ShipPart.class);
	s4.setLocalX(2); s4.setLocalY(4); s4.shipId = shipId;
	ship.parts.add(id4);

	int id5 = world.create();	
	PhysicsRectangleData r5 = world.edit(id5)
	    .create(PhysicsRectangleData.class);
	r5.setWidth(3f); r5.setHeight(7f);
	ShipPart s5 = world.edit(id5).create(ShipPart.class);
	s5.setLocalX(5); s5.setLocalY(4); s5.shipId = shipId;
	ship.parts.add(id5);

	int id6 = world.create();	
	PhysicsRectangleData r6 = world.edit(id6)
	    .create(PhysicsRectangleData.class);
	r6.setWidth(4f); r6.setHeight(2f);
	ShipPart s6 = world.edit(id6).create(ShipPart.class);
	s6.setLocalX(3); s6.setLocalY(11); s6.shipId = shipId;
	ship.parts.add(id6);

	ship.parts.add(id1);
	ship.parts.add(id2);
	ship.parts.add(id3);
    }

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
		new ShipWeldingSystem(),
		new PhysicsRectangleDataResolverSystem(),
		new WeldJointDataResolverSystem(),
		new PhysicsSystem(),
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
