package laurencewarne.secondspace.common.system;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.io.SaveFileFormat;
import com.artemis.managers.WorldSerializationManager;
import com.artemis.systems.IteratingSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.common.collect.IntBags;
import laurencewarne.secondspace.common.component.AugmentationNotice;
import laurencewarne.secondspace.common.component.AugmentationRequest;

public class AugmentationHandlerSystem extends IteratingSystem {

    private final Logger logger = LoggerFactory.getLogger(
	AugmentationHandlerSystem.class
    );
    private ComponentMapper<AugmentationRequest> mAugRequest;
    private ComponentMapper<AugmentationNotice> mAugNotice;    
    @Wire(name="templates")
    private Map<String, byte[]> entityNameToBytesMap;
    private WorldSerializationManager serializationManager;

    @Override
    public void process(int id) {
	final AugmentationRequest request = mAugRequest.get(id);
	final String name = request.getTemplateName();
	if (entityNameToBytesMap.containsKey(name)){
	    final InputStream is = new ByteArrayInputStream(
		entityNameToBytesMap.get(name)
	    );
	    final SaveFileFormat saveFileFormat = serializationManager.load(
		is, SaveFileFormat.class
	    );
	    for (int entityId : IntBags.toSet(saveFileFormat.entities)) {
		final AugmentationNotice notice = mAugNotice.create(entityId);
		notice.setFromRequest(request);
	    }
	}
	mAugRequest.remove(id);
    }	
    
}
