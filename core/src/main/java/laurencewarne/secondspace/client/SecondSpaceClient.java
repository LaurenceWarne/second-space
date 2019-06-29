package laurencewarne.secondspace.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import lombok.Getter;

/**
Start a client.
 */
@Getter
public class SecondSpaceClient extends Game {

    private static final String TAG = SecondSpaceClient.class.getName();

    private LoadingScreen loadingScreen;
    private GameScreen gameScreen;

    private SpriteBatch batch;

    //pre game stuff
    private AssetManager assetManager;
    private boolean isGameScreenInitialised = false;

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
	    if ( !isGameScreenInitialised ){
		this.gameScreen = new GameScreen(batch);
		// setScreen(screen) calls screen.show()
		setScreen(gameScreen);
		isGameScreenInitialised = true;
	    }
	    gameScreen.render(delta);
	}
    }

    @Override
    public void resize( int width, int height ) {

	if ( !loadingScreen.isLoadingComplete() ){
	    loadingScreen.resize(width, height);
	}
	else {
	    if ( !isGameScreenInitialised ){
		this.gameScreen = new GameScreen(batch);
		setScreen(gameScreen);
		isGameScreenInitialised = true;
	    }
	    gameScreen.resize(width, height);
	}
    }
	
    @Override
    public void dispose() {

	Gdx.app.log(TAG, "Disposing of textures and initiating world cleanup.");
	batch.dispose();
	loadingScreen.dispose();
	gameScreen.dispose();
	assetManager.dispose();
    }
}
