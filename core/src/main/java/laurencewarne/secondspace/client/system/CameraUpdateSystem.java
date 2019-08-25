package laurencewarne.secondspace.client.system;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;

import laurencewarne.secondspace.client.component.Camera;

@All(Camera.class)
public class CameraUpdateSystem extends IteratingSystem {

    private ComponentMapper<Camera> mCamera;

    @Override
    protected void process(int id) {
	final Camera camera = mCamera.get(id);
	camera.getCamera().update();
    }
    
}
