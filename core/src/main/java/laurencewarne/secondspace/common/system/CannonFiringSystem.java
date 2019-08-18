package laurencewarne.secondspace.common.system;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import laurencewarne.secondspace.common.component.Bullet;
import laurencewarne.secondspace.common.component.Cannon;
import laurencewarne.secondspace.common.component.Cannon.CannonActivated;
import laurencewarne.secondspace.common.component.Physics;
import laurencewarne.secondspace.common.component.PhysicsRectangleData;
import lombok.NonNull;
import net.fbridault.eeel.annotation.Inserted;

/**
 * Fires projectiles from cannons based on {@link laurencewarne.secondspace.common.component.Cannon.CannonActivated} components, and applies impulses to them.
 */
@All({CannonActivated.class, Cannon.class, Physics.class})
public class CannonFiringSystem extends IteratingSystem {

    private ComponentMapper<CannonActivated> mCannonActivated;
    private ComponentMapper<Cannon> mCannon;
    private ComponentMapper<Bullet> mBullet;
    private ComponentMapper<Physics> mPhysics;
    private ComponentMapper<PhysicsRectangleData> mRecData;

    @Override
    public void process(int id) {
	final Cannon cannon = mCannon.get(id);
	if (cannon.getCurrentCoolDown() <= 0f) {
	    final Body body = mPhysics.get(id).getBody();
	    final int bulletId = createBulletData(cannon, body);
	    mBullet.create(bulletId).sourceId = id;
	    cannon.setCurrentCoolDown(cannon.getCoolDown());
	}
	mCannonActivated.remove(id);
    }

    @Inserted
    @net.fbridault.eeel.annotation.All({Bullet.class, Physics.class})
    public void applyImpulse(int id) {
	final Bullet bullet = mBullet.get(id);
	if (mCannon.has(bullet.sourceId)) {
	    final Cannon cannon = mCannon.get(bullet.sourceId);
	    final Body body = mPhysics.get(id).getBody();
	    final Vector2 applPoint = new Vector2(
		body.getPosition().x, body.getPosition().y
	    );
	    final Vector2 localImpulse = new Vector2(0, 1).scl(cannon.getForce());
	    body.applyLinearImpulse(
		body.getWorldVector(localImpulse), applPoint, true
	    );
	}
    }

    public int createBulletData(@NonNull Cannon cannon, @NonNull Body sourceBody) {
	final int bulletId = world.create();
	final PhysicsRectangleData rec = mRecData.create(bulletId);
	rec.setAngle(sourceBody.getAngle());
	rec.setWidth(cannon.getBulletSize());
	rec.setHeight(cannon.getBulletSize());
	rec.setDensity(cannon.getBulletDensity());
	rec.setFriction(cannon.getBulletFriction());
	rec.setRestitution(cannon.getBulletRestitution());
	final Vector2 localSourcePoint = new Vector2(
	    cannon.getLocalBulletSourceX(), cannon.getLocalBulletSourceY()
	);
	final Vector2 sourcePoint = sourceBody.getWorldPoint(localSourcePoint);
	rec.setX(sourcePoint.x);
	rec.setY(sourcePoint.y);
	rec.setBullet(true);
	return bulletId;
    }
}
