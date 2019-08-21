package laurencewarne.secondspace.common.system;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.files.FileHandle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.common.component.EntityTemplate;
import lombok.NonNull;

/**
 * Loads and controls {@link EntityTemplate}s.
 */
@All(EntityTemplate.class)
public class TemplateLoadingSystem extends BaseEntitySystem {

    private final Logger logger = LoggerFactory.getLogger(
	TemplateLoadingSystem.class
    );
    @Wire(name="templateFiles") @NonNull
    private Collection<FileHandle> templateFiles;
    private ComponentMapper<EntityTemplate> mEntityTemplate;
    @Wire(name="templates")
    private Map<String, byte[]> entityNameToBytesMap;

    @Override
    public void initialize() {
	for (FileHandle file : templateFiles) {
	    loadTemplate(file);
	}
    }

    /**
     * Load a template from a file or if the file is a directory, load all files
     * in the directory.
     *
     * @param file file to load
     */
    public void loadTemplate(@NonNull FileHandle file) {
	if (file.isDirectory()) {
	    Arrays.stream(file.list()).forEach(f -> loadTemplate(f));;
	}
	else if (file.exists()) {
	    EntityTemplate template = mEntityTemplate.create(world.create());
	    template.setName(file.nameWithoutExtension());
	    template.setBytes(file.readBytes());
	    // Apparently @Inserted doesn't get called from initialize()
	    entityNameToBytesMap.put(template.getName(), template.getBytes());
	}
    }    

    @Override
    public void inserted(int id) {
	final EntityTemplate template = mEntityTemplate.get(id);
	entityNameToBytesMap.put(template.getName(), template.getBytes());
    }

    @Override
    public void removed(int id) {
	final EntityTemplate template = mEntityTemplate.get(id);
	entityNameToBytesMap.remove(template.getName());
    }

    @Override
    public void processSystem() {
	
    }
}
