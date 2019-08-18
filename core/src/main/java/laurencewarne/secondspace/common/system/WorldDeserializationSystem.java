package laurencewarne.secondspace.common.system;

import java.io.InputStream;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.artemis.io.JsonArtemisSerializer;
import com.artemis.io.SaveFileFormat;
import com.artemis.managers.WorldSerializationManager;
import com.badlogic.gdx.files.FileHandle;
import com.google.common.collect.ImmutableSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.common.collect.IntBags;
import lombok.Getter;
import lombok.NonNull;
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
	@Getter
	private ImmutableSet<Integer> loadedEntities;

    @Override
    public void initialize() {
		final WorldSerializationManager serializationManager = world.getSystem(
			WorldSerializationManager.class
		);
		// PR artemis-odb for better javadoc?
		if (serializationManager == null) {
			// WorldSerializationManager not added to world
			logger.error(
				"Can't load entities from world save file because a" +
				" WorldSerializationManager instance has not been" +
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
			"Finished loading of save file, added {} entities to the world",
			saveFileFormat.entities.size()
		);
		loadedEntities = ImmutableSet.<Integer>builder().addAll(
			IntBags.toSet(saveFileFormat.entities)
		).build();
    }

    @Override
    protected void processSystem() {
		// do nothing
    }
}
