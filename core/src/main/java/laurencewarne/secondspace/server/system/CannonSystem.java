package laurencewarne.secondspace.server.system;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import laurencewarne.secondspace.server.component.Bullet;
import laurencewarne.secondspace.server.component.Cannon;
import laurencewarne.secondspace.server.component.Cannon.CannonActivated;
import laurencewarne.secondspace.server.component.Physics;
import laurencewarne.secondspace.server.component.PhysicsRectangleData;
import lombok.NonNull;
import net.fbridault.eeel.annotation.Inserted;

@All({CannonActivated.class, Cannon.class, Physics.class})
public class CannonSystem extends IteratingSystem {

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
    @All({Bullet.class, Physics.class})
    public void applyImpulse(int id) {
	final Bullet bullet = mBullet.get(id);
	if (mCannon.has(bullet.sourceId)) {
	    final Cannon cannon = mCannon.get(bullet.sourceId);
	    final Body body = mPhysics.get(id).getBody();
	    final Vector2 applPoint = new Vector2(
		body.getPosition().x, body.getPosition().y
	    );
	    final Vector2 localImpulse = new Vector2(0, 1).scl(-cannon.getForce());
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
