package laurencewarne.secondspace.server.system;

import java.io.InputStream;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.artemis.io.JsonArtemisSerializer;
import com.artemis.io.SaveFileFormat;
import com.artemis.managers.WorldSerializationManager;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.files.FileHandle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.NonNull;
import lombok.Value;
import net.mostlyoriginal.api.event.common.Event;
import net.mostlyoriginal.api.event.common.EventSystem;

/**
 * Loads the world from a specified file.
 */
public class WorldDeserializationSystem extends BaseSystem {

	private final Logger logger = LoggerFactory.getLogger(
		WorldDeserializationSystem.class
	);
	@Wire(name="worldSaveFile") @NonNull
    private FileHandle worldSaveFile;
	private EventSystem es;

    @Override
    public void initialize() {
		final WorldSerializationManager serializationManager = world.getSystem(
			WorldSerializationManager.class
		);
		// PR artemis-odb for better javadoc?
		if (serializationManager == null) {
			// WorldSerializationManager not added to world
			logger.error(
				"A WorldSerializationManager instance has not been" +
				" added to the world"
			);
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
		logger.info(
			"Finish loading of save file, added {} entities to the world",
			saveFileFormat.entities.size()
		);
		es.dispatch(new EntitiesDeserializedEvent(saveFileFormat.entities));
    }

    @Override
    protected void processSystem() {
		// do nothing
    }

	@Value
	public static class EntitiesDeserializedEvent implements Event {
		@NonNull
		private final IntBag entitiesReadFromFile;
	}
}
