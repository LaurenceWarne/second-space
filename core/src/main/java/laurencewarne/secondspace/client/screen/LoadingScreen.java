package laurencewarne.secondspace.client.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import libgdxscreencontrol.screen.ITransitionScreen;
import lombok.Getter;

/**
 * {@link Screen} which loads resources needed for the game whilst showing progress.
 */
public class LoadingScreen extends ScreenAdapter implements ITransitionScreen {

    private final Logger logger = LoggerFactory.getLogger(
	LoadingScreen.class
    );
    private AssetManager assetManager;
    @Getter
    private boolean isLoadingComplete = false;

    // Stuff rendered by the screen whilst loading
    private Stage stage;
    private Actor loadingBar;
    private Image logo;
    private Image loadingFrame;
    private Image loadingBarHidden;
    private Image screenBg;
    private Image loadingBg;
    private float startX, endX;
    private float percent;
    
    @Override
    public void show() {
	assetManager = new AssetManager();
	/////////////////////////////////////////////////////
	// // Load assets for use in loading screen	   //
	/////////////////////////////////////////////////////
	logger.info("Loading assets used in loading screen");
        assetManager.load(
	    "textures/startup/loading-pack.atlas", TextureAtlas.class
	);
        // Wait until they are finished loading
        assetManager.finishLoading();

        // Initialize the stage where we will place everything
        stage = new Stage();

        // Get our textureatlas from the manager
        TextureAtlas atlas = assetManager.get(
	    "textures/startup/loading-pack.atlas", TextureAtlas.class
	);

        // Grab the regions from the atlas and create some images
        logo = new Image(atlas.findRegion("libgdx-logo"));
        loadingFrame = new Image(atlas.findRegion("loading-frame"));
        loadingBarHidden = new Image(atlas.findRegion("loading-bar-hidden"));
        screenBg = new Image(atlas.findRegion("screen-bg"));
        loadingBg = new Image(atlas.findRegion("loading-frame-bg"));

        // Add the loading bar animation
        Animation<TextureRegion> anim = new Animation<>(
	    0.05f, atlas.findRegions("loading-bar-anim")
	);
        //anim.setPlayMode(Animation.LOOP_REVERSED);
        loadingBar = new LoadingBar(anim);

        // Or if you only need a static bar, you can do
        // loadingBar = new Image(atlas.findRegion("loading-bar1"));

        // Add all the actors to the stage
        stage.addActor(screenBg);
        stage.addActor(loadingBar);
        stage.addActor(loadingBg);
        stage.addActor(loadingBarHidden);
        stage.addActor(loadingFrame);
        stage.addActor(logo);

        // Make the background fill the screen
	screenBg.setSize(stage.getWidth(), stage.getHeight());

        // Place the loading frame in the middle of the screen
        loadingFrame.setX((stage.getWidth() - loadingFrame.getWidth()) / 2);
        loadingFrame.setY((stage.getHeight() - loadingFrame.getHeight()) / 2);

        // Place the loading bar at the same spot as the frame, adjusted a few px
        loadingBar.setX(loadingFrame.getX() + 15);
        loadingBar.setY(loadingFrame.getY() + 5);

	// Place the logo in the middle of the screen and 100 px up
        logo.setX((stage.getWidth() - logo.getWidth()) / 2);
        //logo.setY((height - logo.getHeight()) / 2 + 100);
        logo.setY(loadingFrame.getY() + loadingFrame.getHeight() + 15);

        // Place the image that will hide the bar on top of the bar, adjusted a few px
        loadingBarHidden.setX(loadingBar.getX() + 35);
        loadingBarHidden.setY(loadingBar.getY() - 3);
        // The start position and how far to move the hidden loading bar
        startX = loadingBarHidden.getX();
        endX = 440;

        // The rest of the hidden bar
        loadingBg.setSize(450, 50);
        loadingBg.setX(loadingBarHidden.getX() + 30);
        loadingBg.setY(loadingBarHidden.getY() + 3);
      	
    }

    @Override
    public void render(float delta) {

        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if ( assetManager.update() ) { // Load some, will return true if done loading
	    isLoadingComplete = true;
        }
	else {
	    // Interpolate the percentage to make it more smooth
	    percent = assetManager.getProgress();
	    //Interpolation.linear.apply(percent, game.manager.getProgress(), 0.1f);

	    // Update positions (and size) to match the percentage
	    loadingBarHidden.setX(startX + endX * percent);
	    loadingBg.setX(loadingBarHidden.getX() + 30);
	    loadingBg.setWidth(450 - 450 * percent);
	    loadingBg.invalidate();

	    // Show the loading screen
	    stage.act();
	    stage.draw();	    
	}
    }

    @Override
    public void resize(int width, int height) {
	stage.getViewport().update(width, height, false);
    }

    @Override
    public boolean isFinished() {
	return isLoadingComplete;
    }

    @Override
    public void reset() {
	
    }

    @Override
    public void dispose() {
	assetManager.dispose();
    }
}
