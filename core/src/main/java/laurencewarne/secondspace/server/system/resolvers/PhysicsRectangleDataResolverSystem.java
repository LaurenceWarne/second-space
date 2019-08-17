package laurencewarne.secondspace.server.system.resolvers;

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
import laurencewarne.secondspace.server.component.SpawnNotice;
import lombok.NonNull;

/**
 * A {@link BaseEntitySystem} implementation which creates {@link Physics} components for entities which have a  {@link PhysicsRectangleData} component but no {@link Physics} component.
 */
@All(PhysicsRectangleData.class)
@Exclude({Physics.class, SpawnNotice.class})
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
	final PhysicsRectangleData data = mPhysicsRectangleData.get(id);
	final BodyDef bodyDef = getBodyDef(data);

	final Body body = box2DWorld.createBody(bodyDef);
	final FixtureDef fixtureDef = getFixtureDef(data);
	final Fixture fixture = body.createFixture(fixtureDef); 
	body.setUserData(id);
	body.setBullet(data.isBullet());

	mPhysics.create(id).setBody(body);
	fixtureDef.shape.dispose();;
    }

    public BodyDef getBodyDef(@NonNull PhysicsRectangleData data) {
	final BodyDef bodyDef = new BodyDef();
	if (!data.isStatic()) {
	    bodyDef.type = BodyType.DynamicBody;
	}
	// Sets position of the origin
	bodyDef.position.set(data.getX(), data.getY());
	bodyDef.angle = data.getAngle();
	return bodyDef;
    }

    public FixtureDef getFixtureDef(@NonNull PhysicsRectangleData data) {
	final FixtureDef fixtureDef = new FixtureDef();
	PolygonShape rect = new PolygonShape();
	// Sets shapes local origin to the centre of the box
	rect.setAsBox(data.getWidth() / 2f, data.getHeight() / 2f);
	fixtureDef.shape = rect;
	fixtureDef.density = data.getDensity();
	fixtureDef.friction = data.getFriction();
	fixtureDef.restitution = data.getRestitution();
	return fixtureDef;
    }
}
