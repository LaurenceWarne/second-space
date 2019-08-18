package laurencewarne.secondspace.common.system;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

import laurencewarne.secondspace.common.event.BodyBeginContactEvent;
import laurencewarne.secondspace.common.event.BodyEndContactEvent;
import lombok.NonNull;
import net.mostlyoriginal.api.event.common.EventSystem;

public class CollisionSystem extends BaseSystem implements ContactListener {

    private EventSystem es;
    @Wire
    private World box2DWorld;

    @Override
    public void initialize() {
	box2DWorld.setContactListener(this);
    }

    @Override
    public void processSystem() {
	
    }

    @Override
    public void beginContact(@NonNull Contact contact) {
	final Body bodyA = contact.getFixtureA().getBody();
	final Body bodyB = contact.getFixtureB().getBody();
	final BodyBeginContactEvent evt = new BodyBeginContactEvent(
	    (Integer)bodyA.getUserData(), (Integer)bodyB.getUserData(),
	    bodyA, bodyB
	);
	es.dispatch(evt);
    }

    @Override
    public void endContact(@NonNull Contact contact) {
	final Body bodyA = contact.getFixtureA().getBody();
	final Body bodyB = contact.getFixtureB().getBody();
	final BodyEndContactEvent evt = new BodyEndContactEvent(
	    (Integer)bodyA.getUserData(), (Integer)bodyB.getUserData(),
	    bodyA, bodyB
	);
	es.dispatch(evt);		
    }

    @Override
    public void preSolve(@NonNull Contact contact, @NonNull Manifold oldManifold) {
		
    }

    @Override
    public void postSolve(@NonNull Contact contact, @NonNull ContactImpulse impulse) {
		
    }
}
