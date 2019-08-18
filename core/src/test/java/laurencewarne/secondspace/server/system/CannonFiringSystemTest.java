package laurencewarne.secondspace.server.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import laurencewarne.secondspace.server.component.Bullet;
import laurencewarne.secondspace.server.component.Cannon;
import laurencewarne.secondspace.server.component.Cannon.CannonActivated;
import laurencewarne.secondspace.server.component.Physics;
import laurencewarne.secondspace.server.component.PhysicsRectangleData;
import net.fbridault.eeel.EEELPlugin;

public class CannonFiringSystemTest {

    private CannonFiringSystem sys;
    private World world;
    private ComponentMapper<Cannon> mCannon;
    private ComponentMapper<CannonActivated> mCannonA;
    private ComponentMapper<Physics> mPhysics;
    private ComponentMapper<PhysicsRectangleData> mRec;
    private ComponentMapper<Bullet> mBullet;
    private int id;
    @Mock
    private Body body;

    @Before
    public void setUp() {
	MockitoAnnotations.initMocks(this);
	WorldConfiguration setup = new WorldConfigurationBuilder()
	    .with(new EEELPlugin())
	    .with(
		sys = Mockito.spy(new CannonFiringSystem())
	    )
	    .build();
	world = new World(setup);
	mCannon = world.getMapper(Cannon.class);
	mCannonA = world.getMapper(CannonActivated.class);
	mPhysics = world.getMapper(Physics.class);
	mRec = world.getMapper(PhysicsRectangleData.class);
	mBullet = world.getMapper(Bullet.class);
	id = world.create();
	Mockito.when(body.getWorldPoint(any())).thenReturn(new Vector2(10f, 10f));
	Mockito.when(body.getPosition()).thenReturn(new Vector2(-10f, -10f));
	world.process();
    }

    @Test
    public void testCreateBulletDataCreatesCorrectObj() {
	Cannon cannon = mCannon.create(id);
	cannon.setBulletDensity(1f);
	cannon.setBulletFriction(2f);
	cannon.setBulletSize(3f);
	mPhysics.create(id).setBody(body);
	int bulletId = sys.createBulletData(cannon, body);
	PhysicsRectangleData expected = new PhysicsRectangleData();
	expected.setWidth(cannon.getBulletSize());
	expected.setHeight(cannon.getBulletSize());
	expected.setDensity(cannon.getBulletDensity());
	expected.setFriction(cannon.getBulletFriction());
	expected.setRestitution(cannon.getBulletRestitution());
	expected.setX(10f);
	expected.setY(10f);
	expected.setBullet(true);
	assertEquals(expected, mRec.get(bulletId));
    }

    @Test
    public void testSystemResetsCooldownOnValidActivation() {
	Cannon cannon = mCannon.create(id);
	mCannonA.create(id);
	mPhysics.create(id).setBody(body);
	world.process();
	assertEquals(cannon.getCoolDown(), cannon.getCurrentCoolDown(), 0.001f);
    }

    @Test
    public void testBulletCreatedOnValidActivation() {
	Cannon cannon = mCannon.create(id);
	mCannonA.create(id);
	mPhysics.create(id).setBody(body);
	world.process();
	Mockito.verify(sys, times(1)).createBulletData(cannon, body);
    }

    @Test
    public void testCannonActivateComponentRemovedOnValidActivation() {
	Cannon cannon = mCannon.create(id);
	mCannonA.create(id);
	mPhysics.create(id).setBody(body);
	world.process();
	assertFalse(mCannonA.has(id));
    }

    @Test
    public void testCannonActivateComponentRemovedOnInvalidActivation() {
	Cannon cannon = mCannon.create(id);
	cannon.setCurrentCoolDown(1f);
	mCannonA.create(id);
	mPhysics.create(id).setBody(body);
	world.process();
	assertFalse(mCannonA.has(id));
    }

    @Test
    public void testBulletNotCreatedOnInvalidActivation() {
	Cannon cannon = mCannon.create(id);
	cannon.setCurrentCoolDown(1f);
	mCannonA.create(id);
	mPhysics.create(id).setBody(body);
	world.process();
	Mockito.verify(sys, never()).createBulletData(cannon, body);
    }

    @Test
    public void testCooldownNotResetOnInvalidActivation() {
	Cannon cannon = mCannon.create(id);
	cannon.setCurrentCoolDown(1f);
	mCannonA.create(id);
	mPhysics.create(id).setBody(body);
	world.process();
	assertEquals(1f, cannon.getCurrentCoolDown(), 0.001f);
    }

    @Test
    public void testImpulseAppliedToBullet() {
	Cannon cannon = mCannon.create(id);
	int idB = world.create();
	Bullet bullet = mBullet.create(idB);
	bullet.sourceId = id;
	mPhysics.create(idB).setBody(body);
	world.process();
	Mockito.verify(body, times(1)).applyLinearImpulse(any(), any(), anyBoolean());
    }

}
