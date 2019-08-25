package laurencewarne.secondspace.client.screen;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.client.component.Camera;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor @Getter
public class GameScreen implements Screen {

    private final Logger logger = LoggerFactory.getLogger(
	GameScreen.class
    );    
    @NonNull
    private final World world;
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
	world.getMapper(Camera.class).create(world.create()).setCamera(gameCamera);
	gameViewport = new FitViewport(cameraWidth, cameraHeight, gameCamera);
	inputMultiplexer = new InputMultiplexer();
	Gdx.input.setInputProcessor(inputMultiplexer);	
	
	Gdx.graphics.setTitle("Second Space");
    }

    @Override
    public void render(float delta) {
	gameViewport.apply();
	gameCamera.update();

	Gdx.gl.glClearColor(0, 0, 0, 1);
	Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	world.setDelta(delta);
	world.process();
    }

    @Override
    public void resize(int width, int height) {
     
	logger.info("Setting resolution to:{}*{}", width, height);
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
	logger.info("Disposing of textures and initiating world cleanup");
	world.dispose();
    }
}
