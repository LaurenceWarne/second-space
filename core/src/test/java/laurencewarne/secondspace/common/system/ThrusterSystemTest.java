package laurencewarne.secondspace.common.system;

import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.physics.box2d.Body;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import laurencewarne.secondspace.common.component.Physics;
import laurencewarne.secondspace.common.component.Thruster;
import laurencewarne.secondspace.common.component.Thruster.ThrusterActivated;

public class ThrusterSystemTest {

    private World world;
    private ThrusterSystem sys;
    private ComponentMapper<ThrusterActivated> m;
    private ComponentMapper<Thruster> ms;
    private ComponentMapper<Physics> mp;

    private int id1, id2;
    private Thruster t;
    private Physics p;
    @Mock
    private Body b;

    @Before
    public void setUp() {
	MockitoAnnotations.initMocks(this);
    }

    public void createWorld() {
	WorldConfiguration setup = new WorldConfigurationBuilder()
	    .with(
		sys = new ThrusterSystem()
	    )
	    .build();
	world = new World(setup);
	m = world.getMapper(ThrusterActivated.class);
	ms = world.getMapper(Thruster.class);
	mp = world.getMapper(Physics.class);
	id1 = world.create();
	id2 = world.create();
	t = ms.create(id1);
	p = mp.create(id1);
	p.setBody(b);
	world.process();
    }

    @Test
    public void testSystemRemovesProcessedThrusterActivatedComponent() {
	createWorld();
	m.create(id1);
	world.process();
	assertFalse(m.has(id1));
    }

    @Test
    public void testSystemAppliesLinearImpulseOnThruster() {
	createWorld();
	m.create(id1);
	world.process();
	Mockito.verify(b).applyLinearImpulse(any(), any(), anyBoolean());;
    }
}
