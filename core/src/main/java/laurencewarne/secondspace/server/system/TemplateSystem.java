package laurencewarne.secondspace.server.system;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.io.SaveFileFormat;
import com.artemis.managers.WorldSerializationManager;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.files.FileHandle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.server.collect.IntBags;
import laurencewarne.secondspace.server.component.EntityTemplate;
import laurencewarne.secondspace.server.component.PhysicsRectangleData;
import laurencewarne.secondspace.server.component.ShipPart;
import laurencewarne.secondspace.server.component.SpawnRequest;
import lombok.NonNull;
import net.fbridault.eeel.annotation.Inserted;
import net.fbridault.eeel.annotation.Removed;

@All(SpawnRequest.class)
public class TemplateSystem extends IteratingSystem {

    private final Logger logger = LoggerFactory.getLogger(TemplateSystem.class);
    @Wire(name="templateFiles") @NonNull
    private Collection<FileHandle> templateFiles;
    private ComponentMapper<EntityTemplate> mEntityTemplate;
    private ComponentMapper<SpawnRequest> mSpawnRequest;
    private ComponentMapper<ShipPart> mShipPart;
    private ComponentMapper<PhysicsRectangleData> mRecData;
    @NonNull
    private final Map<String, byte[]> entityNameToBytesMap = new HashMap<>();

    public boolean templateExists(@NonNull String templateName) {
	return entityNameToBytesMap.containsKey(templateName);
    }

    @Override
    public void initialize() {
	for (FileHandle file : templateFiles) {
	    EntityTemplate template = mEntityTemplate.create(world.create());
	    template.setName(file.nameWithoutExtension());
	    template.setBytes(file.readBytes());
	    // Apparently @Inserted doesn't get called from initialize()
	    entityNameToBytesMap.put(template.getName(), template.getBytes());
	}
    }

    @Inserted
    @net.fbridault.eeel.annotation.All(EntityTemplate.class)    
    public void templateInserted(int id) {
	final EntityTemplate template = mEntityTemplate.get(id);
	entityNameToBytesMap.put(template.getName(), template.getBytes());
    }

    @Removed
    @net.fbridault.eeel.annotation.All(EntityTemplate.class)
    public void templateRemoved(int id) {
	final EntityTemplate template = mEntityTemplate.get(id);
	entityNameToBytesMap.remove(template.getName());
    }

    @Override
    public void process(int id) {
	final SpawnRequest request = mSpawnRequest.get(id);
	final String name = request.getTemplateName();
	if (entityNameToBytesMap.containsKey(name)){
	    final InputStream is = new ByteArrayInputStream(
		entityNameToBytesMap.get(name)
	    );
	    final WorldSerializationManager serializationManager = world.getSystem(
		WorldSerializationManager.class
	    );
	    final SaveFileFormat saveFileFormat = serializationManager.load(
		is, SaveFileFormat.class
	    );
	    for (int entityId : IntBags.toSet(saveFileFormat.entities)) {
		// Set positions of entities if appropriate
		float x = request.getX(), y = request.getY();
		if (mShipPart.has(entityId)){
		    final ShipPart part = mShipPart.get(entityId);
		    x = x + part.getLocalX();
		    y = y + part.getLocalY();
		}
		if (mRecData.has(entityId)){
		    final PhysicsRectangleData rect = mRecData.get(entityId);
		    rect.setX(x);
		    rect.setY(y);
		}
	    }
	}
	mSpawnRequest.remove(id);
    }
}
