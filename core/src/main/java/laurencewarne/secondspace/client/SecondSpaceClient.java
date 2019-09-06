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
import com.badlogic.gdx.utils.ObjectMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.componentlookup.ComponentLookupPlugin;
import laurencewarne.secondspace.client.manager.IdTranslatorManager;
import laurencewarne.secondspace.client.screen.ConnectionScreen;
import laurencewarne.secondspace.client.screen.GameScreen;
import laurencewarne.secondspace.client.screen.LoadingScreen;
import laurencewarne.secondspace.client.screen.MenuScreen;
import laurencewarne.secondspace.client.screen.MenuScreen;
import laurencewarne.secondspace.client.screen.ScreenController;
import laurencewarne.secondspace.client.screen.ScreenControllerBuilder;
import laurencewarne.secondspace.client.screen.ScreenFactory;
import laurencewarne.secondspace.client.system.ActivationInitializerSystem;
import laurencewarne.secondspace.client.system.BoxRenderingSystem;
import laurencewarne.secondspace.client.system.CameraUpdateSystem;
import laurencewarne.secondspace.client.system.KeyActivationSystem;
import laurencewarne.secondspace.client.system.KeyDefaultAssignerSystem;
import laurencewarne.secondspace.client.system.KeyInitializerSystem;
import laurencewarne.secondspace.client.system.StateSynchronizerSystem;
import laurencewarne.secondspace.client.system.network.ActivationSenderSystem;
import laurencewarne.secondspace.common.system.network.NetworkRegisterSystem;
import lombok.Getter;
import lombok.NonNull;
import net.fbridault.eeel.EEELPlugin;

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

    @Override
    public void create() {
	final WorldConfigurationBuilder setupBuilder =
	    new WorldConfigurationBuilder();
	setupWorldConfig(setupBuilder);
	final WorldConfiguration setup = setupBuilder.build();
	injectDependencies(setup);
	final ConnectionScreen connectionScreen;
	screenController = new ScreenControllerBuilder()
	    .withScreen(new LoadingScreen())
	    .withScreen(new MenuScreen())
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
    }

    /**
     * Add systems and plugins to the artemis {@link WorldConfigurationBuilder} here.
     * 
     * @param configBuilder {@link WorldConfigurationBuilder} obj to use
     */
    protected void setupWorldConfig(WorldConfigurationBuilder configBuilder) {
	configBuilder
	    .with(new EEELPlugin())
	    .with(new ComponentLookupPlugin())
	    .with(
		new IdTranslatorManager(),
		new NetworkRegisterSystem(),
		new KeyInitializerSystem(),
		new ActivationInitializerSystem(),
		new StateSynchronizerSystem(),
		new KeyActivationSystem(),
		new KeyDefaultAssignerSystem(),
		new ActivationSenderSystem(),
		new CameraUpdateSystem(),
		new BoxRenderingSystem()
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
	setup.register(new SpriteBatch());
	setup.register(
	   "server-to-client-components",
	   new Array<Class<? extends Component>>()
	);
	setup.register(
	   "client-to-server-components",
	   new Array<Class<? extends Component>>()
	);
	setup.register(
	    "activation-map",
	    new ObjectMap<Class<? extends Component>, Class<? extends Component>>(8)
	);
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
