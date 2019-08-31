package laurencewarne.secondspace.common.system;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.io.SaveFileFormat;
import com.artemis.managers.WorldSerializationManager;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.utils.ObjectIntMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.componentlookup.annotations.FieldLookup;
import laurencewarne.secondspace.common.collect.IntBags;
import laurencewarne.secondspace.common.component.EntityTemplate;
import laurencewarne.secondspace.common.component.SpawnNotice;
import laurencewarne.secondspace.common.component.SpawnRequest;

@All(SpawnRequest.class)
public class SpawnFromTemplateSystem extends IteratingSystem {

    private final Logger logger = LoggerFactory.getLogger(
	SpawnFromTemplateSystem.class
    );
    private ComponentMapper<SpawnRequest> mSpawnRequest;
    private ComponentMapper<SpawnNotice> mSpawnNotice;
    private ComponentMapper<EntityTemplate> mTemplate;
    @FieldLookup(component=EntityTemplate.class, field="name")
    private ObjectIntMap<String> templateNameMap;
    private WorldSerializationManager sm;

    @Override
    public void process(int id) {
	final SpawnRequest request = mSpawnRequest.get(id);
	final String name = request.getTemplateName();
	if (templateNameMap.containsKey(name)){
	    final EntityTemplate template = mTemplate.get(
		templateNameMap.get(name, -1)
	    );
	    final InputStream is = new ByteArrayInputStream(template.getBytes());
	    final SaveFileFormat saveFileFormat;
	    try {
		 saveFileFormat = sm.load(is, SaveFileFormat.class);
	    } catch (Exception e) {
		logger.error(
		    "Could not load template '{}' due to error: '{}'",
		    name, e.getMessage()
		);
		mSpawnRequest.remove(id);
		return;
	    }
	    for (int entityId : IntBags.toSet(saveFileFormat.entities)) {
		// Set positions of entities if appropriate
		final SpawnNotice notice = mSpawnNotice.create(entityId);
		notice.setFromRequest(request);
	    }
	}
	mSpawnRequest.remove(id);
    }	
}
