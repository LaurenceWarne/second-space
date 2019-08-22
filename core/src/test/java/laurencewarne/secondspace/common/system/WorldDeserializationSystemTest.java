package laurencewarne.secondspace.common.system;

import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.io.SaveFileFormat;
import com.artemis.managers.WorldSerializationManager;
import com.badlogic.gdx.files.FileHandle;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import laurencewarne.secondspace.common.component.ComponentX;
import laurencewarne.secondspace.common.component.ComponentY;
import net.mostlyoriginal.api.event.common.EventSystem;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

public class WorldDeserializationSystemTest {

    @Mock
    private FileHandle worldSaveFile;

    private WorldSerializationManager wsm1, wsm2;
    private World world1, world2;
    private TestLogger logger = TestLoggerFactory.getTestLogger(
	WorldDeserializationSystem.class
    );

    @Before
    public void setUp() {
	// Loads the first world
	MockitoAnnotations.initMocks(this);
	WorldConfiguration setup1 = new WorldConfigurationBuilder()
	    .with(
		new EventSystem(),
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
		new EventSystem(),
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

    @Test
    public void testSystemCanLoadTwoDifferentComponents() {
	int id = world1.create();
	world1.edit(id).create(ComponentX.class).text = "cool-component";
	world1.edit(id).create(ComponentY.class).setF(45f);
	world1.process();
	loadWorld2();

	world2.process();
	ComponentMapper<ComponentX> mX = world2.getMapper(ComponentX.class);
	ComponentMapper<ComponentY> mY = world2.getMapper(ComponentY.class);
	assertTrue(mX.has(id));
	assertTrue(mY.has(id));
	assertEquals("cool-component", mX.get(id).text);
	assertEquals(45f, mY.get(id).getF(), 0.0001f);
    }

    @Test
    public void testSystemLoadsNothingOnEmptyFile() {
	WorldConfiguration setup2 = new WorldConfigurationBuilder()
	    .with(
		new EventSystem(),
		wsm2 = new WorldSerializationManager(),
		new WorldDeserializationSystem()
	    )
	    .build();
	when(worldSaveFile.read())
	    .thenReturn(
		new ByteArrayInputStream("{}".getBytes(StandardCharsets.UTF_8))
	    );
	setup2.register("worldSaveFile", worldSaveFile);
	world2 = new World(setup2);
	world2.process();
	assertTrue(
	    world2
	    .getAspectSubscriptionManager()
	    .get(Aspect.all())
	    .getEntities()
	    .isEmpty()		   
	);
    }

    @Test @Ignore
    public void testSystemLogsManagerNotFoundWhenManagerNotAdded() {
	WorldConfiguration setup = new WorldConfigurationBuilder()
	    .with(
		new EventSystem(),
		new WorldDeserializationSystem()
	    )
	    .build();
	setup.register("worldSaveFile", worldSaveFile);
	World world = new World(setup);
	List<LoggingEvent> events = logger.getAllLoggingEvents();
	LoggingEvent lastLog = events.get(events.size() - 1);
	assertThat(
	    lastLog.getMessage(),
	    matchesPattern(".*WorldSerializationManager.*not.*added.*")
	);
    }

}
