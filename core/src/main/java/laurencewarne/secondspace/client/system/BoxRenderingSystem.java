package laurencewarne.secondspace.client.system;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;

import laurencewarne.secondspace.client.component.Camera;
import laurencewarne.secondspace.common.component.PhysicsRectangleData;
import lombok.NonNull;
import net.fbridault.eeel.annotation.Inserted;

@All(PhysicsRectangleData.class)
public class BoxRenderingSystem extends IteratingSystem {

    private ComponentMapper<PhysicsRectangleData> mData;
    private ComponentMapper<Camera> mCamera;
    @NonNull
    private ShapeRenderer shapeRenderer;
    private Camera camera;

    // temporary hack before odb contrib's singleton plugin is released
    @Inserted
    @net.fbridault.eeel.annotation.All(Camera.class)
    public void cameraInserted(int id) {
	camera = mCamera.get(id);
    }

    @Override
    public void initialize() {
	shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void begin() {
	shapeRenderer.setProjectionMatrix(camera.getCamera().combined);
	shapeRenderer.begin(ShapeType.Filled);
    }

    @Override
    protected void process(int id) {
	final PhysicsRectangleData data = mData.get(id);
	shapeRenderer.identity();
	shapeRenderer.translate(data.getX(), data.getY(), 0f);
	shapeRenderer.rotate(0, 0, 1, data.getAngle() * MathUtils.radiansToDegrees);
	shapeRenderer.box(  // it's relative to the current point
	    -data.getWidth() / 2f,
	    -data.getHeight() / 2f,
	    0f,
	    data.getWidth(), data.getHeight(), 0f
	);
	// undo
	shapeRenderer.translate(-data.getX(), -data.getY(), 0f);
	shapeRenderer.rotate(0, 0, 1, -data.getAngle() * MathUtils.radiansToDegrees);
    }

    @Override
    public void end() {
	shapeRenderer.end();
    }
}
