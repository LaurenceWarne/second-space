package laurencewarne.secondspace.client.screen;

import com.badlogic.gdx.Screen;

/**
 * A screen which is no longer needed after some process is complete.
 */
public interface IProgressScreen extends Screen {

    boolean isFinished();
}
