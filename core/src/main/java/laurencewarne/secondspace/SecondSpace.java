package laurencewarne.secondspace;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import laurencewarne.secondspace.server.component.Physics;
import laurencewarne.secondspace.server.system.PhysicsSystem;

public class SecondSpace extends ApplicationAdapter {
    SpriteBatch batch;
    Viewport viewport;
    OrthographicCamera gameCamera;
    World world;
    com.badlogic.gdx.physics.box2d.World box2dWorld;
    Box2DDebugRenderer debugRenderer;
    Body playerBody, attachedBody;
    PolygonShape square1, square2;
    PolygonShape groundBox;
	
    @Override
    public void create () {
	// Box2D stuff
	box2dWorld = new com.badlogic.gdx.physics.box2d.World(
	    new Vector2(0, -10), true
	);
	debugRenderer = new Box2DDebugRenderer();
	
	// Init Artemis stuff: register any plugins, setup the world.
	final WorldConfiguration setup = new WorldConfigurationBuilder()
	    .with(new PhysicsSystem())
	    .build();
	// Inject non-artemis dependencies
	setup.register(box2dWorld);
	// Create Artermis World
	world = new World(setup);
	
	batch = new SpriteBatch();
	gameCamera = new OrthographicCamera(100, 100);
	viewport = new FitViewport(100, 100, gameCamera);


	// First we create a body definition
	BodyDef bodyDef = new BodyDef();
	// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
	bodyDef.type = BodyType.DynamicBody;
	// Set our body's starting position in the world
	bodyDef.position.set(0, 50);
	BodyDef bodyDef2 = new BodyDef();
	bodyDef2.type = BodyType.DynamicBody;
	bodyDef2.position.set(0, 40);
 
	// Create our body in the world using our body definition
	playerBody = box2dWorld.createBody(bodyDef);
	square1 = new PolygonShape();
	square1.setAsBox(5f, 5f);
	FixtureDef fixtureDef = new FixtureDef();
	fixtureDef.shape = square1;
	fixtureDef.density = 0.5f; 
	fixtureDef.friction = 0.4f;
	fixtureDef.restitution = 0.6f; // Make it bounce a little bit

	Fixture fixture = playerBody.createFixture(fixtureDef); 

	// Create our body in the world using our body definition
	attachedBody = box2dWorld.createBody(bodyDef2);
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
	groundBox = new PolygonShape();  
	// Set the polygon shape as a box which is twice the size of our view port and 20 high
	// (setAsBox takes half-width and half-height as arguments)
	groundBox.setAsBox(viewport.getCamera().viewportWidth, 10.0f);
	// Create a fixture from our polygon shape and add it to our ground body  
	groundBody.createFixture(groundBox, 0.0f); 

	int playerID = world.create();
	world.edit(playerID).create(Physics.class).setBody(playerBody);
    }

    @Override
    public void render () {

	handleInput();
	viewport.getCamera().position.set(
	    playerBody.getPosition().x - viewport.getScreenWidth() / 2f,
	    playerBody.getPosition().y - viewport.getScreenHeight() / 2f,
	    0f
	);
	viewport.getCamera().update();

	world.setDelta(Gdx.graphics.getDeltaTime());
	world.process();

	Gdx.gl.glClearColor(1, 0, 0, 1);
	Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

	debugRenderer.render(box2dWorld, viewport.getCamera().combined);
    }

    public void handleInput() {

	Vector2 vel = playerBody.getLinearVelocity();
	Vector2 pos = playerBody.getPosition();
		
	// apply left impulse, but only if max velocity is not reached yet
	if (Gdx.input.isKeyPressed(Keys.A) && vel.x > -10f) {
	    playerBody.applyLinearImpulse(-5f, 0, pos.x, pos.y, true);
	}

	// apply right impulse, but only if max velocity is not reached yet
	if (Gdx.input.isKeyPressed(Keys.D) && vel.x < 10f) {
	    playerBody.applyLinearImpulse(5f, 0, pos.x, pos.y, true);
	}
	if (Gdx.input.isKeyPressed(Keys.W) && vel.y > -10f) {
	    playerBody.applyLinearImpulse(0f, 15f, pos.x, pos.y, true);
	}
	
    }
	
    @Override
    public void dispose () {
	batch.dispose();
	square1.dispose();
	groundBox.dispose();
	debugRenderer.dispose();
    }
}
