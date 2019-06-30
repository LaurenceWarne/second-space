package laurencewarne.secondspace.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

import lombok.Getter;

@Getter
public class SecondSpaceServerUI extends SecondSpaceServerBase {

    private OrthographicCamera camera;
    private Box2DDebugRenderer debugRenderer;    
    /** x component of the coordinate the camera is currently looking at.*/
    private float cameraX = 0f;
    /** y component of the coordinate the camera is currently looking at.*/
    private float cameraY = 0f;

    @Override
    public void create() {

	super.create();
	debugRenderer = new Box2DDebugRenderer();
	camera = new OrthographicCamera(100, 100);	
    }

    @Override
    public void render() {

	handleInput();
	// Centre camera around player position
	camera.position.set(cameraX, cameraY, 0f);
	camera.update();

	super.render();

	Gdx.gl.glClearColor(1, 0, 0, 1);
	Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

	debugRenderer.render(getBox2dWorld(), camera.combined);
    }

    public void handleInput() {
	if (Gdx.input.isKeyPressed(Keys.D)) {
	    cameraX += 3f;
	}
	if (Gdx.input.isKeyPressed(Keys.A)) {
	    cameraX -= 3f;
	}
	if (Gdx.input.isKeyPressed(Keys.W)) {
	    cameraY += 3f;
	}
	if (Gdx.input.isKeyPressed(Keys.S)) {
	    cameraY -= 3f;
	}
    }

    @Override
    public void dispose () {
	super.dispose();
	debugRenderer.dispose();
    }
    
}
