package laurencewarne.secondspace.client;

import com.artemis.Component;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener.TypeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.client.manager.IdTranslatorManager;
import laurencewarne.secondspace.client.screen.ConnectionScreen;
import laurencewarne.secondspace.client.screen.GameScreen;
import laurencewarne.secondspace.client.screen.LoadingScreen;
import laurencewarne.secondspace.client.screen.ScreenController;
import laurencewarne.secondspace.client.screen.ScreenControllerBuilder;
import laurencewarne.secondspace.client.screen.ScreenFactory;
import laurencewarne.secondspace.client.system.StateSynchronizerSystem;
import laurencewarne.secondspace.common.component.network.Networked;
import laurencewarne.secondspace.common.system.network.NetworkRegisterSystem;
import lombok.Getter;
import lombok.NonNull;

/**
 * Start a client.
 */
@Getter
public class SecondSpaceClient extends Game {

    private final Logger logger = LoggerFactory.getLogger(
	SecondSpaceClient.class
    );
    private ScreenController screenController;

    //pre game stuff
    private boolean isConnectionScreenInitialised = false;
    private World world;
    private Client client;

    @Override
    public void create() {
	client = new Client();
	final WorldConfigurationBuilder setupBuilder =
	    new WorldConfigurationBuilder();
	setupWorldConfig(setupBuilder);
	final WorldConfiguration setup = setupBuilder.build();
	final ConnectionScreen connectionScreen;
	screenController = new ScreenControllerBuilder()
	    .withScreen(new LoadingScreen())
	    .withScreen(connectionScreen = new ConnectionScreen(setup))
	    .withFinalScreenFactory(new ScreenFactory(){
		@Override
		public Screen create() {
		    return new GameScreen(connectionScreen.getWorld());
		}
	     })
	    .build();
	setScreen(screenController.getActiveScreen());
	logger.info(
	    "Starting application with resolution {}*{}",
	    Gdx.graphics.getWidth(), Gdx.graphics.getHeight()
	);
	injectDependencies(setup);
    }

    /**
     * Add systems and plugins to the artemis {@link WorldConfigurationBuilder} here.
     * 
     * @param configBuilder {@link WorldConfigurationBuilder} obj to use
     */
    protected void setupWorldConfig(WorldConfigurationBuilder configBuilder) {
	configBuilder
	    .with(
		new IdTranslatorManager(),
		new NetworkRegisterSystem(),
		new StateSynchronizerSystem()
	    );
    }

    /**
     * Inject system dependencies or perform any other config on the {@link WorldConfiguration} object prior to {@link World} creation.
     *
     * @param setup 
     * @param serverConfig loaded server configuration file
     */
    protected void injectDependencies(
	@NonNull WorldConfiguration setup
    ) {
	setup.register("client", client);
	setup.register("kryo", client.getKryo());
	setup.register(new SpriteBatch());
	setup.register(
	   "networked-components",
	   new Array<Class<? extends Component>>()
	);
	final TypeListener listener = new TypeListener();
	listener.addTypeHandler(Networked.class, (conn, c) -> System.out.println(c.getComponent().getClass()));
	client.addListener(listener);
	setup.register(listener);	
    }    

    @Override
    public void render() {
	float delta = Gdx.graphics.getDeltaTime();
	if (screenController.checkForScreenChange()){
	    setScreen(screenController.getActiveScreen());
	}
	screenController.getActiveScreen().render(delta);
    }

    @Override
    public void resize(int width, int height) {
	screenController.getActiveScreen().resize(width, height);
    }
	
    @Override
    public void dispose() {
	logger.info("Disposing of textures and initiating world cleanup.");
	screenController.dipose();
    }
}
