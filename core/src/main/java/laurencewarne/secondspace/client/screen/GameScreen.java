package laurencewarne.secondspace.client.screen;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.client.component.Camera;
import libgdxscreencontrol.screen.ITransitionScreen;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor @Getter
public class GameScreen implements ITransitionScreen {

    private final Logger logger = LoggerFactory.getLogger(
	GameScreen.class
    );    
    @NonNull
    private final World world;
    private final int worldWidth = 16 * 10;
    private final int worldHeight = 9 * 10;
    private InputMultiplexer inputMultiplexer;

    //graphics stuff
    private Viewport gameViewport;
    
    @Override
    public void show() {
	// the parameters here are void right?
	gameViewport = new FitViewport(worldWidth, worldHeight);
	gameViewport.apply();
	world.getMapper(Camera.class).create(world.create())
	    .setCamera((OrthographicCamera) gameViewport.getCamera());
	inputMultiplexer = new InputMultiplexer();
	Gdx.input.setInputProcessor(inputMultiplexer);	
	
	Gdx.graphics.setTitle("Second Space");
    }

    @Override
    public void render(float delta) {
	Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
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

    @Override
    public void reset() {
	
    }

    @Override
    public boolean isFinished() {
	return false;
    }
}
