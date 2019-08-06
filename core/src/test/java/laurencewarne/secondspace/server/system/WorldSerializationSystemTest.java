package laurencewarne.secondspace.server.system;

import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.io.JsonArtemisSerializer;
import com.artemis.io.SaveFileFormat;
import com.artemis.managers.WorldSerializationManager;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import laurencewarne.secondspace.server.component.ComponentX;
import laurencewarne.secondspace.server.component.ComponentY;
import net.mostlyoriginal.api.event.common.EventSystem;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

public class WorldSerializationSystemTest {

    @Mock
    private FileHandle worldSaveFile;

    private TestLogger logger = TestLoggerFactory.getTestLogger(
		WorldSerializationSystem.class
    );    
    private World world;
    private WorldSerializationManager wsm;
    private WorldSerializationSystem sys;
    private ByteArrayOutputStream os;

    @Before
    public void setUp() {
		initWorld();
    }

    public void initWorld() {
		MockitoAnnotations.initMocks(this);
		WorldConfiguration setup = new WorldConfigurationBuilder()
			.with(
				new EventSystem(),
				wsm = new WorldSerializationManager(),
				sys = new WorldSerializationSystem()
			)
			.build();
		setup.register("worldSaveFile", worldSaveFile);
		world = new World(setup);
		wsm.setSerializer(new JsonArtemisSerializer(world));
		doAnswer(new Answer<OutputStream>() {
				@Override
				public OutputStream answer(InvocationOnMock invocation) throws Throwable {
					return os = new ByteArrayOutputStream();
				}
			}).when(worldSaveFile).write(false);
		doAnswer(new Answer<InputStream>() {
				@Override
				public InputStream answer(InvocationOnMock invocation) throws Throwable {
					return new ByteArrayInputStream(os.toByteArray());
				}
			}).when(worldSaveFile).read();	    
    }

    public void reloadWorld() {
		initWorld();
		wsm.load(worldSaveFile.read(), SaveFileFormat.class);
		world.process();
    }

    @Test
    public void testCanSerializeSingleEntityWithSingleComponent() {
		int id = world.create();
		world.edit(id).create(ComponentX.class).text = "cool-component";
		world.process();
		// Just to make sure serialization has occurred
		sys.serialize();
		// create a new world, and load entities from file
		reloadWorld();

		ComponentMapper<ComponentX> mX = world.getMapper(ComponentX.class);
		assertTrue(mX.has(id));
		assertEquals("cool-component", mX.get(id).text);
    }

    @Test
    public void testCanSerializeEmptyWorldAndGetEmptyWorld() {
		world.process();
		sys.serialize();
		reloadWorld();
		final IntBag allEntities = world
			.getAspectSubscriptionManager()
			.get(Aspect.all())
			.getEntities();
		assertTrue(allEntities.isEmpty());
    }

    @Test
    public void testCanSerializeWorldWithMultipleEntitiesAndComponents() {
		int id1 = world.create(), id2 = world.create();
		world.edit(id1).create(ComponentX.class).text = "cool-component";
		world.edit(id2).create(ComponentX.class).text = "another-cool-component";
		world.edit(id1).create(ComponentY.class).f = 46f;
		world.process();
		sys.serialize();
		reloadWorld();
		ComponentMapper<ComponentX> mX = world.getMapper(ComponentX.class);
		ComponentMapper<ComponentY> mY = world.getMapper(ComponentY.class);
		assertTrue(mX.has(id1));
		assertTrue(mX.has(id2));
		assertEquals("cool-component", mX.get(id1).text);
		assertEquals("another-cool-component", mX.get(id2).text);
		assertEquals(46f, mY.get(id1).f, 0.0001f);
    }

    @Test
    public void testErrorLoggedWithInvalidFile() {
		WorldConfiguration setup = new WorldConfigurationBuilder()
			.with(
				new EventSystem(),
				wsm = new WorldSerializationManager(),
				sys = new WorldSerializationSystem()
			)
			.build();
		setup.register("worldSaveFile", worldSaveFile);
		when(worldSaveFile.write(anyBoolean()))
			.thenThrow(new GdxRuntimeException(""));
		world = new World(setup);
		sys.serialize();
		List<LoggingEvent> events = logger.getAllLoggingEvents();
		LoggingEvent lastLog = events.get(events.size() - 1);
		assertThat(
			lastLog.getMessage(),
			matchesPattern(".*file.*not.*written.*")
		);
    }
}
