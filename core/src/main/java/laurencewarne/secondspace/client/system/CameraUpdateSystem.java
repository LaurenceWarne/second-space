package laurencewarne.secondspace.client.system;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;

import laurencewarne.secondspace.client.component.Camera;
import laurencewarne.secondspace.client.component.ClientPlayer;
import laurencewarne.secondspace.common.component.Ship;
import net.fbridault.eeel.annotation.Inserted;

@All({ClientPlayer.class, Ship.class})
public class CameraUpdateSystem extends IteratingSystem {

    private ComponentMapper<Camera> mCamera;
    private ComponentMapper<Ship> mShip;
    private Camera camera;

    // temporary hack before odb contrib's singleton plugin is released
    @Inserted
    @net.fbridault.eeel.annotation.All(Camera.class)
    public void cameraInserted(int id) {
	camera = mCamera.get(id);
    }

    @Override
    protected void process(int id) {
	if (camera != null){
	    final Ship ship = mShip.get(id);
	    camera.getCamera().position.set(ship.getX(), ship.getY(), 0f);
	    camera.getCamera().update();
	}
    }
    
}
