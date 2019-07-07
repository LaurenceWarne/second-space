package laurencewarne.secondspace.server;

import java.io.IOException;
import java.util.Properties;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.managers.WorldSerializationManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.utils.GdxRuntimeException;

import org.aeonbits.owner.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.server.component.Physics;
import laurencewarne.secondspace.server.init.ServerConfig;
import laurencewarne.secondspace.server.system.AddRectangleCommandExecutorSystem;
import laurencewarne.secondspace.server.system.PhysicsRectangleDataResolverSystem;
import laurencewarne.secondspace.server.system.PhysicsSystem;
import laurencewarne.secondspace.server.system.TerminalSystem;
import laurencewarne.secondspace.server.system.WorldDeserializationSystem;
import lombok.Getter;

/**
 * Headless implementation of @link{Game}, ie does no rendering, just updates.
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
	    .with(
		new WorldSerializationManager(),
		new WorldDeserializationSystem(),
		new TerminalSystem(),
		new AddRectangleCommandExecutorSystem(),
		new PhysicsRectangleDataResolverSystem(),
		new PhysicsSystem()
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
	
	BodyDef bodyDef = new BodyDef();
	bodyDef.type = BodyType.DynamicBody;
	bodyDef.position.set(0, 50);
	BodyDef bodyDef2 = new BodyDef();
	bodyDef2.type = BodyType.DynamicBody;
	bodyDef2.position.set(0, 40);
 
	// Create our body in the world using our body definition
	Body playerBody = box2dWorld.createBody(bodyDef);
	PolygonShape square1 = new PolygonShape();
	square1.setAsBox(5f, 5f);
	FixtureDef fixtureDef = new FixtureDef();
	fixtureDef.shape = square1;
	fixtureDef.density = 0.5f; 
	fixtureDef.friction = 0.4f;
	fixtureDef.restitution = 0.6f; // Make it bounce a little bit
	Fixture fixture = playerBody.createFixture(fixtureDef); 

	// Create our body in the world using our body definition
	Body attachedBody = box2dWorld.createBody(bodyDef2);
	FixtureDef fixtureDef2 = new FixtureDef();
	fixtureDef2.shape = square1;
	fixtureDef2.density = 0.5f; 
	fixtureDef2.friction = 0.4f;
	fixtureDef2.restitution = 0.6f; // Make it bounce a little bit
	Fixture fixture2 = attachedBody.createFixture(fixtureDef2);

	WeldJointDef jointDef = new WeldJointDef ();
	jointDef.initialize(playerBody, attachedBody, new Vector2(0f, 45f));
	box2dWorld.createJoint(jointDef);

	// Create our body definition
	BodyDef groundBodyDef = new BodyDef();  
	// Set its world position
	groundBodyDef.position.set(new Vector2(0, -50));
	// Create a body from the defintion and add it to the world
	Body groundBody = box2dWorld.createBody(groundBodyDef);  
	// Create a polygon shape
	PolygonShape groundBox = new PolygonShape();  
	// Set the polygon shape as a box which is twice the size of our view port and 20 high
	// (setAsBox takes half-width and half-height as arguments)
	groundBox.setAsBox(100f, 10.0f);
	// Create a fixture from our polygon shape and add it to our ground body  
	groundBody.createFixture(groundBox, 0.0f); 

	int playerID = world.create();
	world.edit(playerID).create(Physics.class).setBody(playerBody);

	square1.dispose();
	groundBox.dispose();
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
