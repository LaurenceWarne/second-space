package laurencewarne.secondspace.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import lombok.Getter;

/**
 * Start a client.
 */
@Getter
public class SecondSpaceClient extends Game {

    private static final String TAG = SecondSpaceClient.class.getName();

    private LoadingScreen loadingScreen;
    private ConnectionScreen connectionScreen;

    private SpriteBatch batch;

    //pre game stuff
    private AssetManager assetManager;
    private boolean isConnectionScreenInitialised = false;

    @Override
    public void create() {

	this.batch = new SpriteBatch();
	this.assetManager = new AssetManager();
	this.loadingScreen = new LoadingScreen(assetManager);
	Gdx.app.log(
	    TAG, "Starting app with resolution: " + Gdx.graphics.getWidth() +
	    "*" + Gdx.graphics.getHeight()
	);	
	setScreen(loadingScreen);
    }

    @Override
    public void render() {

	float delta = Gdx.graphics.getDeltaTime();
	if ( !loadingScreen.isLoadingComplete() ){
	    loadingScreen.render(delta);
	}
	else {
	    if ( !isConnectionScreenInitialised ){
		this.connectionScreen = new ConnectionScreen();
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
		this.connectionScreen = new ConnectionScreen();
		setScreen(connectionScreen);
		isConnectionScreenInitialised = true;
	    }
	    connectionScreen.resize(width, height);
	}
    }
	
    @Override
    public void dispose() {

	Gdx.app.log(TAG, "Disposing of textures and initiating world cleanup.");
	batch.dispose();
	loadingScreen.dispose();
	connectionScreen.dispose();
	assetManager.dispose();
    }
}
