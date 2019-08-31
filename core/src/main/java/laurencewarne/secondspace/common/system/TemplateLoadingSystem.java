package laurencewarne.secondspace.common.system;

import java.util.Arrays;
import java.util.Collection;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ObjectIntMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.componentlookup.annotations.FieldLookup;
import laurencewarne.secondspace.common.component.EntityTemplate;
import lombok.NonNull;

/**
 * Loads and controls {@link EntityTemplate}s.
 */
public class TemplateLoadingSystem extends BaseSystem {

    private final Logger logger = LoggerFactory.getLogger(
	TemplateLoadingSystem.class
    );
    @Wire(name="templateFiles") @NonNull
    private Collection<FileHandle> templateFiles;
    private ComponentMapper<EntityTemplate> mEntityTemplate;
    @FieldLookup(component=EntityTemplate.class, field="name")
    private ObjectIntMap<String> templateNameMap;

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
	}
    }    

    @Override
    public void processSystem() {

    }
}
