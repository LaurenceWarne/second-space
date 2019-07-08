package laurencewarne.secondspace.server.system;

import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Exclude;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import laurencewarne.secondspace.server.component.Physics;
import laurencewarne.secondspace.server.component.PhysicsRectangleData;

/**
 * A {@link BaseEntitySystem} implementation which creates {@link Physics} components from corresponding {@link PhysicsRectangleData} components.
 */
@All(PhysicsRectangleData.class)
@Exclude(Physics.class)
public class PhysicsRectangleDataResolverSystem extends BaseEntitySystem {

    private ComponentMapper<Physics> mPhysics;
    private ComponentMapper<PhysicsRectangleData> mPhysicsRectangleData;

    @Wire
    private World box2DWorld;

    @Override
    public void processSystem() {
	
    }    
    
    @Override
    public void inserted(int id) {
		final PhysicsRectangleData c = mPhysicsRectangleData.get(id);
		final BodyDef bodyDef = new BodyDef();
		if (!c.isStatic()) {
			bodyDef.type = BodyType.DynamicBody;
		}
		bodyDef.position.set(c.getX(), c.getY());
		PolygonShape rect = new PolygonShape();
		rect.setAsBox(c.getWidth(), c.getHeight());

		final Body body = box2DWorld.createBody(bodyDef);
		final FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = rect;
		fixtureDef.density = 0.5f; 
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.6f;
		final Fixture fixture = body.createFixture(fixtureDef); 

		mPhysics.create(id).setBody(body);
		rect.dispose();
    }
}
