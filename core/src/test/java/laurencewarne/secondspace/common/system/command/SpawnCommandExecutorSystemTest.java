package laurencewarne.secondspace.common.system.command;

import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.List;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import laurencewarne.secondspace.common.component.Command;
import laurencewarne.secondspace.common.component.SpawnRequest;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

public class SpawnCommandExecutorSystemTest {

    private World world;
    private SpawnCommandExecutorSystem sys;
    private ComponentMapper<Command> m;
    private ComponentMapper<SpawnRequest> mRequest;
    private TestLogger logger = TestLoggerFactory.getTestLogger(
	SpawnCommandExecutorSystem.class
    );


    @Before
    public void setUp() {
	MockitoAnnotations.initMocks(this);
	WorldConfiguration setup = new WorldConfigurationBuilder()
	    .with(
		sys = new SpawnCommandExecutorSystem(name -> true)
	    )
	    .build();
	setup.register("templates", new HashMap<String, byte[]>());
	world = new World(setup);
	m = world.getMapper(Command.class);
	mRequest = world.getMapper(SpawnRequest.class);
    }

    @Test
    public void testSystemRemovesValidCommand() {
	int id = world.create();
	Command c = m.create(id);
	c.setCommandString("spawn monstro");
	world.process();
	assertFalse(m.has(id));
    }

    @Test
    public void testSystemLogsErrorOnCommandWithTooManyArgs() {
	int id = world.create();
	Command c = m.create(id);
	c.setCommandString("spawn monstro 1 2 4 extra-arg");
	world.process();
	List<LoggingEvent> events = logger.getAllLoggingEvents();
	LoggingEvent lastLog = events.get(events.size() - 1);
	assertThat(
	    lastLog.getMessage(),
	    matchesPattern("[\\s\\S]*unrecog[\\s\\S]*arg[\\s\\S]*")
	);

    }

}
