package laurencewarne.secondspace.common.system;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.annotations.All;
import com.badlogic.gdx.files.FileHandle;

import org.hamcrest.collection.IsEmptyCollection;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import laurencewarne.componentlookup.ComponentLookupPlugin;
import laurencewarne.secondspace.common.component.EntityTemplate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class TemplateLoadingSystemTest {

    private World world;
    private TemplateLoadingSystem sys;
    private Collection<FileHandle> templateFiles = new ArrayList<>();
    private ComponentMapper<EntityTemplate> m;
    private List<String> loadedTemplateNames;

    @Mock
    private FileHandle t1, t2, t3;

    @Before
    public void setUp() {
	MockitoAnnotations.initMocks(this);
	templateFiles = new ArrayList<>();
	loadedTemplateNames = new ArrayList<>();
	
	when(t1.nameWithoutExtension()).thenReturn("cool-template");
	when(t1.readBytes())
	    .thenReturn("{}".getBytes(StandardCharsets.UTF_8));
	when(t1.exists())
	    .thenReturn(true);
	when(t2.nameWithoutExtension()).thenReturn("another-cool-template");
	when(t2.readBytes())
	    .thenReturn("{}".getBytes(StandardCharsets.UTF_8));
	when(t2.exists())
	    .thenReturn(true);
	when(t3.nameWithoutExtension()).thenReturn("cool-template-the-third");
	when(t3.readBytes())
	    .thenReturn("{}".getBytes(StandardCharsets.UTF_8));
	when(t3.exists())
	    .thenReturn(true);
    }

    public void createWorld() {
	WorldConfiguration setup = new WorldConfigurationBuilder()
	    .with(new ComponentLookupPlugin())
	    .with(
		sys = new TemplateLoadingSystem(),
		new TemplateTrackingSystem(loadedTemplateNames)
	    )
	    .build();
	setup.register("templateFiles", templateFiles);
	world = new World(setup);
	m = world.getMapper(EntityTemplate.class);
	world.process();
    }

    @Test
    public void testCanLoadOneTemplate() {
	templateFiles.add(t1);
	createWorld();
	assertThat(loadedTemplateNames, hasItems(t1.nameWithoutExtension()));
    }

    @Test
    public void testCanLoadMultipleTemplates() {
	templateFiles.addAll(Arrays.asList(t1, t2, t3));
	createWorld();
	assertThat(loadedTemplateNames, hasItems(t1.nameWithoutExtension()));
	assertThat(loadedTemplateNames, hasItems(t2.nameWithoutExtension()));
	assertThat(loadedTemplateNames, hasItems(t3.nameWithoutExtension()));
    }

    @Test
    public void testSystemTracksTemplateAddedByComponent() {
	createWorld();
	EntityTemplate t = m.create(world.create());
	t.setName("brilliant-template");
	world.process();
	assertThat(loadedTemplateNames, hasItems(t.getName()));
    }
    
    @Test
    public void testSystemRemovesTemplateAddedByComponentWhenRemoved() {
	createWorld();
	int id = world.create();
	EntityTemplate t = m.create(id);
	t.setName("brilliant-template");
	world.process();
	world.delete(id);
	world.process();
	assertThat(loadedTemplateNames, is(IsEmptyCollection.empty()));
    }

    @All(EntityTemplate.class) @RequiredArgsConstructor
    private static class TemplateTrackingSystem extends BaseEntitySystem {

	private ComponentMapper<EntityTemplate> m;
	@NonNull
	private final List<String> templateNamesSeen;

	@Override
	public void inserted(int id) {
	    templateNamesSeen.add(m.get(id).getName());
	}

	@Override
	public void removed(int id) {
	    templateNamesSeen.remove(m.get(id).getName());
	}
	
	@Override
	public void processSystem() {
	    
	}
    }
}

