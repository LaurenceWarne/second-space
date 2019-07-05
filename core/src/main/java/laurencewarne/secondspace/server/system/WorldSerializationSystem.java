package laurencewarne.secondspace.server.system;

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

import lombok.NonNull;

/**
 *
 */
public class WorldSerializationSystem extends BaseSystem {

    @Wire(name="worldSaveFile") @NonNull
    private FileHandle worldSaveFile;

    @Override
    public void processSystem() {
	serialize();
    }

    public void serialize() {
	///////////////////////////////////////////////////////////////////////////
	// Check we can get serialization manager and its initialized correctly. //
	///////////////////////////////////////////////////////////////////////////
	WorldSerializationManager serializationManager;
	try {
	    serializationManager = world.getSystem(
		WorldSerializationManager.class
	    );
	} catch (Exception e) {
	    // WorldSerializationManager not added to world
	    return;
	}
	if (serializationManager.getSerializer() == null){
	    serializationManager.setSerializer(
		new JsonArtemisSerializer(world)
	    );
	}

	/////////////////////////////////////////////////////////////////
	// Attempt to write world to file using serialization manager. //
	/////////////////////////////////////////////////////////////////
	OutputStream os;
	try {
	    os = worldSaveFile.write(false);
	} catch (GdxRuntimeException e) {
	    // File is a directory or could just not be written to
	    return;
	}
	final IntBag allEntities = world
	    .getAspectSubscriptionManager()
	    .get(Aspect.all())
	    .getEntities();
	serializationManager.save(os, new SaveFileFormat(allEntities));
    }
}
