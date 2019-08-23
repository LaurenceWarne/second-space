package laurencewarne.secondspace.client;

import com.artemis.Component;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener.TypeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.common.component.network.Networked;
import laurencewarne.secondspace.common.system.network.NetworkRegisterSystem;
import lombok.Getter;

/**
 * Start a client.
 */
@Getter
public class SecondSpaceClient extends Game {

    private final Logger logger = LoggerFactory.getLogger(
	SecondSpaceClient.class
    );
    private LoadingScreen loadingScreen;
    private ConnectionScreen connectionScreen;

    //pre game stuff
    private AssetManager assetManager;
    private boolean isConnectionScreenInitialised = false;
    private World world;
    private Client client;

    @Override
    public void create() {
	this.assetManager = new AssetManager();
	this.loadingScreen = new LoadingScreen(assetManager);
	logger.info(
	    "Starting application with resolution {}*{}",
	    Gdx.graphics.getWidth(), Gdx.graphics.getHeight()
	);
	client = new Client();
	setScreen(loadingScreen);
	final WorldConfigurationBuilder setupBuilder =
	    new WorldConfigurationBuilder();
	setupWorldConfig(setupBuilder);
	final WorldConfiguration setup = setupBuilder.build();
	setup.register("client", client);
	setup.register("kryo", client.getKryo());
	setup.register(
	   "networked-components",
	   new Array<Class<? extends Component>>()
	);
	final TypeListener listener = new TypeListener();
	listener.addTypeHandler(Networked.class, (conn, c) -> System.out.println(c));
	client.addListener(listener);
	setup.register(listener);
	world = new World(setup);
    }

    /**
     * Add systems and plugins to the artemis {@link WorldConfigurationBuilder} here.
     * 
     * @param configBuilder {@link WorldConfigurationBuilder} obj to use
     */
    protected void setupWorldConfig(WorldConfigurationBuilder configBuilder) {
	configBuilder
	    .with(
		new NetworkRegisterSystem()
	    );
    }

    @Override
    public void render() {
	float delta = Gdx.graphics.getDeltaTime();
	if ( !loadingScreen.isLoadingComplete() ){
	    loadingScreen.render(delta);
	}
	else {
	    if ( !isConnectionScreenInitialised ){
		this.connectionScreen = new ConnectionScreen(client);
		// setScreen(screen) calls screen.show()
		setScreen(connectionScreen);
		isConnectionScreenInitialised = true;
	    }
	    connectionScreen.render(delta);
	}
    }

    @Override
    public void resize( int width, int height ) {
	if ( !loadingScreen.isLoadingComplete() ){
	    loadingScreen.resize(width, height);
	}
	else {
	    if ( !isConnectionScreenInitialised ){
		this.connectionScreen = new ConnectionScreen(client);
		setScreen(connectionScreen);
		isConnectionScreenInitialised = true;
	    }
	    connectionScreen.resize(width, height);
	}
    }
	
    @Override
    public void dispose() {
	logger.info("Disposing of textures and initiating world cleanup.");
	loadingScreen.dispose();
	connectionScreen.dispose();
	assetManager.dispose();
    }
}
