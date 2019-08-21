package laurencewarne.secondspace.common.system;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.io.SaveFileFormat;
import com.artemis.managers.WorldSerializationManager;
import com.artemis.systems.IteratingSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.common.collect.IntBags;
import laurencewarne.secondspace.common.component.SpawnNotice;
import laurencewarne.secondspace.common.component.SpawnRequest;

@All(SpawnRequest.class)
public class SpawnFromTemplateSystem extends IteratingSystem {

    private final Logger logger = LoggerFactory.getLogger(
	SpawnFromTemplateSystem.class
    );
    private ComponentMapper<SpawnRequest> mSpawnRequest;
    private ComponentMapper<SpawnNotice> mSpawnNotice;
    @Wire(name="templates")
    private Map<String, byte[]> entityNameToBytesMap;

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
		final SpawnNotice notice = mSpawnNotice.create(entityId);
		notice.setFromRequest(request);
	    }
	}
	mSpawnRequest.remove(id);
    }	
}
