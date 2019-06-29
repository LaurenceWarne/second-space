package laurencewarne.secondspace.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor @Getter
public class GameScreen implements Screen {

    // Constructor args
    @NonNull
    private SpriteBatch batch;

    //game loop stuff
    private static final String TAG = GameScreen.class.getName();

    private final int cameraWidth = 32;
    private final int cameraHeight = 18;
    private InputMultiplexer inputMultiplexer;

    //graphics stuff
    private Viewport gameViewport;
    private OrthographicCamera gameCamera;
    
    @Override
    public void show() {

	// the parameters here are void right?
	gameCamera = new OrthographicCamera(100, 100);
	// initial ratio 16:9
	gameViewport = new FitViewport(cameraWidth, cameraHeight, gameCamera);
	inputMultiplexer = new InputMultiplexer();
	Gdx.input.setInputProcessor(inputMultiplexer);	
	
	Gdx.graphics.setTitle("Second Space");
    }

    @Override
    public void render( float delta ) {
    
	handleInput();
	gameViewport.apply();
	gameCamera.update();
	batch.setProjectionMatrix(gameCamera.combined);

	Gdx.gl.glClearColor(1, 0, 0, 1);
	Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void handleInput() {
	
    }    

    @Override
    public void resize( int width, int height ) {
     
	Gdx.app.log(TAG, "Setting resolution to:" + width + "*" + height);
	// use true here to center the camera
        // that's what you probably want in case of a UI
        gameViewport.update(width, height, false);
    }

    @Override
    public void pause() {
		
    }

    @Override
    public void resume() {
		
    }

    @Override
    public void hide() {
		
    }

    @Override
    public void dispose() {

	Gdx.app.log(TAG, "Disposing of textures and initiating world cleanup.");
    }
}
