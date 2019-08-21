package laurencewarne.secondspace.common.system;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.io.SaveFileFormat;
import com.artemis.managers.WorldSerializationManager;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.files.FileHandle;

import org.hamcrest.collection.IsEmptyCollection;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import laurencewarne.secondspace.common.component.EntityTemplate;
import laurencewarne.secondspace.common.component.SpawnRequest;
import net.fbridault.eeel.EEELPlugin;

public class TemplateLoadingSystemTest {

    private World world;
    private TemplateLoadingSystem sys;
    private Collection<FileHandle> templateFiles = new ArrayList<>();
    private ComponentMapper<EntityTemplate> m;
    private ComponentMapper<SpawnRequest> ms;
    private WorldSerializationManager s;
    private Map<String, byte[]> entityNameToBytesMap = new HashMap<>();

    @Mock
    private FileHandle t1, t2, t3;
    @Mock
    private SaveFileFormat save;
    private IntBag loadedEntities;

    @Before
    public void setUp() {
	MockitoAnnotations.initMocks(this);
	templateFiles = new ArrayList<>();
	loadedEntities = new IntBag();
	save.entities = loadedEntities;
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
	    .with(new EEELPlugin())
	    .with(
		sys = new TemplateLoadingSystem()
	    )
	    .build();
	setup.register("templateFiles", templateFiles);
	setup.register("templates", entityNameToBytesMap);
	world = new World(setup);
	m = world.getMapper(EntityTemplate.class);
	ms = world.getMapper(SpawnRequest.class);
	world.process();
    }

    @Test
    public void testCanLoadOneTemplate() {
	templateFiles.add(t1);
	createWorld();
	assertThat(entityNameToBytesMap.keySet(), hasItems(t1.nameWithoutExtension()));
    }

    @Test
    public void testCanLoadMultipleTemplates() {
	templateFiles.addAll(Arrays.asList(t1, t2, t3));
	createWorld();
	assertThat(entityNameToBytesMap.keySet(), hasItems(t1.nameWithoutExtension()));
	assertThat(entityNameToBytesMap.keySet(), hasItems(t2.nameWithoutExtension()));
	assertThat(entityNameToBytesMap.keySet(), hasItems(t3.nameWithoutExtension()));
    }

    @Test
    public void testSystemTracksTemplateAddedByComponent() {
	createWorld();
	EntityTemplate t = m.create(world.create());
	t.setName("brilliant-template");
	world.process();
	assertThat(entityNameToBytesMap.keySet(), hasItems(t.getName()));
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
	assertThat(entityNameToBytesMap.keySet(), is(IsEmptyCollection.empty()));
    }
    
}

