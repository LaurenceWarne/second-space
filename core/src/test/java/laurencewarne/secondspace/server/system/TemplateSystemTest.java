package laurencewarne.secondspace.server.system;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.io.SaveFileFormat;
import com.artemis.managers.WorldSerializationManager;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.files.FileHandle;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import laurencewarne.secondspace.server.component.EntityTemplate;
import laurencewarne.secondspace.server.component.SpawnRequest;
import net.fbridault.eeel.EEELPlugin;

public class TemplateSystemTest {

    private World world;
    private TemplateSystem sys;
    private Collection<FileHandle> templateFiles = new ArrayList<>();
    private ComponentMapper<EntityTemplate> m;
    private ComponentMapper<SpawnRequest> ms;
    private WorldSerializationManager s;

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
	when(t2.nameWithoutExtension()).thenReturn("another-cool-template");
	when(t2.readBytes())
	    .thenReturn("{}".getBytes(StandardCharsets.UTF_8));
	when(t3.nameWithoutExtension()).thenReturn("cool-template-the-third");
	when(t3.readBytes())
	    .thenReturn("{}".getBytes(StandardCharsets.UTF_8));
    }

    public void createWorld() {
	WorldConfiguration setup = new WorldConfigurationBuilder()
	    .with(new EEELPlugin())
	    .with(
		sys = new TemplateSystem()
	    )
	    .build();
	setup.register("templateFiles", templateFiles);
	world = new World(setup);
	System.out.println(world.getSystem(WorldSerializationManager.class));
	m = world.getMapper(EntityTemplate.class);
	ms = world.getMapper(SpawnRequest.class);
	world.process();
    }

    @Test
    public void testCanLoadOneTemplate() {
	templateFiles.add(t1);
	createWorld();
	assertTrue(sys.templateExists(t1.nameWithoutExtension()));
    }

    @Test
    public void testCanLoadMultipleTemplates() {
	templateFiles.addAll(Arrays.asList(t1, t2, t3));
	createWorld();
	assertTrue(sys.templateExists(t1.nameWithoutExtension()));
	assertTrue(sys.templateExists(t2.nameWithoutExtension()));
	assertTrue(sys.templateExists(t3.nameWithoutExtension()));
    }

    @Test
    public void testSystemTracksTemplateAddedByComponent() {
	createWorld();
	EntityTemplate t = m.create(world.create());
	t.setName("brilliant-template");
	world.process();
	assertTrue(sys.templateExists(t.getName()));
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
	assertFalse(sys.templateExists(t.getName()));
    }

}

