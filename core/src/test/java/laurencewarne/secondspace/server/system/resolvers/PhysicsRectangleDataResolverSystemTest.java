package laurencewarne.secondspace.server.system.resolvers;

import static org.junit.Assert.assertEquals;

import com.badlogic.gdx.physics.box2d.BodyDef;

import org.junit.Before;
import org.junit.Test;

import laurencewarne.secondspace.server.component.PhysicsRectangleData;

public class PhysicsRectangleDataResolverSystemTest {

    private PhysicsRectangleData data1, data2;
    private PhysicsRectangleDataResolverSystem sys;

    @Before
    public void setUp() {
	sys = new PhysicsRectangleDataResolverSystem();
	data1 = new PhysicsRectangleData();
	data1.setAngle(2.4f);
	data1.setX(324f);
	data1.setY(-24f);
	
	data1.setHeight(1f);
	data1.setWidth(2f);
	data1.setDensity(10f);
	data1.setFriction(34f);
	data1.setRestitution(3990f);
    }

    @Test
    public void testSysCreatesCorrectBodyDef() {
	BodyDef bodyDef = sys.getBodyDef(data1);
	assertEquals(2.4f, bodyDef.angle, 0.001f);
	assertEquals(324f, bodyDef.position.x, 0.001f);
	assertEquals(-24f, bodyDef.position.y, 0.001f);
    }

    @Test
    public void testSysCreatesCorrectFixtureDef() {

    }
}
