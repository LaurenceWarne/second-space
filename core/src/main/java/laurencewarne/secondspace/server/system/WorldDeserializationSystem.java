package laurencewarne.secondspace.server.system;

import java.io.InputStream;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.artemis.io.JsonArtemisSerializer;
import com.artemis.io.SaveFileFormat;
import com.artemis.managers.WorldSerializationManager;
import com.badlogic.gdx.files.FileHandle;

import lombok.NonNull;

/**
 * Loads the world from file
 */
public class WorldDeserializationSystem extends BaseSystem {

	@Wire(name="worldSaveFile") @NonNull
    private FileHandle worldSaveFile;

    @Override
    public void initialize() {
		WorldSerializationManager serializationManager;
		try {
			serializationManager = world.getSystem(
				WorldSerializationManager.class
			);
		}
		catch (Exception e) {
			// WorldSerializationManager not added to world
			return;
		}
		if (serializationManager.getSerializer() == null){
			serializationManager.setSerializer(
				new JsonArtemisSerializer(world)
			);
		}
		InputStream inputStream = worldSaveFile.read();
		final SaveFileFormat saveFileFormat = serializationManager.load(
			inputStream, SaveFileFormat.class
		);
    }

    @Override
    protected void processSystem() {
		// do nothing
    }
}
