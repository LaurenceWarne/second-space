package laurencewarne.secondspace.server.system;

import java.io.FileInputStream;
import java.io.IOException;

import com.artemis.BaseEntitySystem;
import com.artemis.annotations.Wire;
import com.artemis.io.SaveFileFormat;
import com.artemis.managers.WorldSerializationManager;

import lombok.NonNull;

/**
 * Loads the world from file
 */
public class WorldDeserializationSystem extends BaseEntitySystem {

    @Wire @NonNull
    private String worldSaveFileLocation;

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
	FileInputStream fileInputStream;
	try {
	    fileInputStream = new FileInputStream(worldSaveFileLocation);
	}
	catch (IOException e) {
	    // log some error
	    return;
	}
	final SaveFileFormat saveFileFormat = serializationManager.load(fileInputStream, SaveFileFormat.class);
    }

    @Override
    protected void processSystem() {
	// do nothing
    }
}
