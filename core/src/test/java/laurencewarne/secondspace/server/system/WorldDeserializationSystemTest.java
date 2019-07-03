package laurencewarne.secondspace.server.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.io.SaveFileFormat;
import com.artemis.managers.WorldSerializationManager;
import com.badlogic.gdx.files.FileHandle;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import laurencewarne.secondspace.server.component.ComponentX;

public class WorldDeserializationSystemTest {

    @Mock
    private FileHandle worldSaveFile;

    private WorldSerializationManager wsm1, wsm2;
    private World world1, world2;

    @Before
    public void setUp() {
	// Loads the first world
	MockitoAnnotations.initMocks(this);
	WorldConfiguration setup1 = new WorldConfigurationBuilder()
	    .with(
		wsm1 = new WorldSerializationManager(),
		new WorldDeserializationSystem()
	    )
	    .build();
	when(worldSaveFile.read())
	    .thenReturn(new ByteArrayInputStream("{}".getBytes(StandardCharsets.UTF_8)));
	setup1.register("worldSaveFile", worldSaveFile);
	world1 = new World(setup1);	
    }

    public void loadWorld2() {
	// Loads the second world from the serialized first world
	ByteArrayOutputStream os = new ByteArrayOutputStream();
	wsm1.save(
	    os, new SaveFileFormat(
		world1.getAspectSubscriptionManager()
		.get(Aspect.all())
		.getEntities()
	    )
	);

	WorldConfiguration setup2 = new WorldConfigurationBuilder()
	    .with(
		wsm2 = new WorldSerializationManager(),
		new WorldDeserializationSystem()
	    )
	    .build();
	when(worldSaveFile.read())
	    .thenReturn(new ByteArrayInputStream(os.toByteArray()));
	setup2.register("worldSaveFile", worldSaveFile);
	world2 = new World(setup2);	    
    }

    @Test
    public void testSystemCanLoadOneComponent() {
	int id = world1.create();
	world1.edit(id).create(ComponentX.class).text = "cool-component";
	world1.process();
	loadWorld2();

	world2.process();
	ComponentMapper<ComponentX> mX = world2.getMapper(ComponentX.class);
	assertTrue(mX.has(id));
	assertEquals("cool-component", mX.get(id).text);
    }

    @Test
    public void testSystemCanLoadTwoComponents() {
	int id1 = world1.create(), id2 = world1.create();
	world1.edit(id1).create(ComponentX.class).text = "cool-component";
	world1.edit(id2).create(ComponentX.class).text = "cooler-component";
	world1.process();
	loadWorld2();

	world2.process();
	ComponentMapper<ComponentX> mX = world2.getMapper(ComponentX.class);
	assertTrue(mX.has(id1));
	assertTrue(mX.has(id2));
	assertEquals("cool-component", mX.get(id1).text);
	assertEquals("cooler-component", mX.get(id2).text);
    }    
}
