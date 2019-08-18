package laurencewarne.secondspace.common.system;

import java.io.OutputStream;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.artemis.io.JsonArtemisSerializer;
import com.artemis.io.SaveFileFormat;
import com.artemis.managers.WorldSerializationManager;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.NonNull;
import lombok.Value;
import net.mostlyoriginal.api.event.common.Event;
import net.mostlyoriginal.api.event.common.EventSystem;

/**
 * A system which writes the world to an injected file.
 */
public class WorldSerializationSystem extends BaseSystem {

    @Wire(name="worldSaveFile") @NonNull
    private FileHandle worldSaveFile;
    private final Logger logger = LoggerFactory.getLogger(
	WorldSerializationSystem.class
    );
    private EventSystem es;

    @Override
    public void processSystem() {

    }

    /**
     * Serialize world to the file referenced by getWorldSavefile().
     */
    public void serialize() {
	///////////////////////////////////////////////////////////////////////////
	// Check we can get serialization manager and its initialized correctly. //
	///////////////////////////////////////////////////////////////////////////
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
	es.dispatch(new WorldSerializationEvent());

	/////////////////////////////////////////////////////////////////
	// Attempt to write world to file using serialization manager. //
	/////////////////////////////////////////////////////////////////
	OutputStream os;
	try {
	    os = worldSaveFile.write(false);
	} catch (GdxRuntimeException e) {
	    // File is a directory or could just not be written to
	    logger.error(
		"The specified save file '{}' is either a directory or" +
		" could not be written to", worldSaveFile.name()
	    );
	    return;
	}
	final IntBag allEntities = world
	    .getAspectSubscriptionManager()
	    .get(Aspect.all())
	    .getEntities();
	serializationManager.save(os, new SaveFileFormat(allEntities));
	logger.info("Completed world serialization successfully");
	logger.debug("Saved {} entities to file", allEntities.size());
    }

    @Value
    public static class WorldSerializationEvent implements Event {
	
    }
}
