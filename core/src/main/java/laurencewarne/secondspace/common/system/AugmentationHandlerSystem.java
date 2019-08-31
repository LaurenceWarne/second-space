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
import laurencewarne.secondspace.common.component.AugmentationNotice;
import laurencewarne.secondspace.common.component.AugmentationRequest;
import laurencewarne.secondspace.common.component.EntityTemplate;

@All(AugmentationRequest.class)
public class AugmentationHandlerSystem extends IteratingSystem {

    private final Logger logger = LoggerFactory.getLogger(
	AugmentationHandlerSystem.class
    );
    private ComponentMapper<AugmentationRequest> mAugRequest;
    private ComponentMapper<AugmentationNotice> mAugNotice;
    private ComponentMapper<EntityTemplate> mTemplate;
    @FieldLookup(component=EntityTemplate.class, field="name")
    private ObjectIntMap<String> templateNameMap;
    private WorldSerializationManager serializationManager;

    @Override
    public void process(int id) {
	final AugmentationRequest request = mAugRequest.get(id);
	final String name = request.getTemplateName();
	if (templateNameMap.containsKey(name)){
	    final InputStream is = new ByteArrayInputStream(
		mTemplate.get(templateNameMap.get(name, -1)).getBytes()
	    );
	    final SaveFileFormat saveFileFormat = serializationManager.load(
		is, SaveFileFormat.class
	    );
	    for (int entityId : IntBags.toSet(saveFileFormat.entities)) {
		final AugmentationNotice notice = mAugNotice.create(entityId);
		notice.setFromRequest(request);
	    }
	    logger.info(
		"Added augmentation '{}' to ship with id {}",
		request.getTemplateName(), request.ship
	    );
	}
	else {
	    logger.error(
		"Declined augmentation request '{}' as their exists no" +
		" augmentation with name: '{}'",
		request, name
	    );
	}
	mAugRequest.remove(id);
    }	
    
}
