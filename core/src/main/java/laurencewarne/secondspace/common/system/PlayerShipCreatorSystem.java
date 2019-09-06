package laurencewarne.secondspace.common.system;

import java.util.Random;

import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Exclude;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.common.component.Player;
import laurencewarne.secondspace.common.component.Ship;
import laurencewarne.secondspace.common.component.SpawnRequest;
import laurencewarne.secondspace.common.component.network.NetworkConnection;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Creates {@link Ship}s for new players.
 */
@All({Player.class, NetworkConnection.class})
@Exclude(Ship.class)
public class PlayerShipCreatorSystem extends BaseEntitySystem {

    private static final Random RANDOM = new Random();
    private final Logger logger = LoggerFactory.getLogger(
	PlayerShipCreatorSystem.class
    );
    private ComponentMapper<SpawnRequest> mSpawnRequest;
    private ComponentMapper<Player> mPlayer;
    @Wire
    private World box2dWorld;

    @Override
    public void inserted(int id) {
	final SpawnRequest req = mSpawnRequest.create(id);
	//req.setShipOwner(id);
	req.setTemplateName("deban-rosette");
	final Vector2 spawnLocation = findSpawnLocation(20f, 20);
	req.setX(spawnLocation.x);
	req.setY(spawnLocation.y);
	logger.info(
	    "Spawning in ship {} for player {} at ({}, {})",
	    req.getTemplateName(), mPlayer.get(id).getName(), req.getX(), req.getY()
	);
    }

    private Vector2 findSpawnLocation(float size, int attempts) {
	float scope = 64f;
	int currentAttempts = 0;
	final Array<Fixture> fixturesInRange = new Array<>();
	final QueryCallback callback = new SimpleCallback(fixturesInRange);
	while (currentAttempts <= attempts) {
	    fixturesInRange.clear();
	    final float tryX = RANDOM.nextFloat() * scope - scope/2f;
	    final float tryY = RANDOM.nextFloat() * scope - scope/2f;
	    box2dWorld.QueryAABB(
		callback, tryX - size/2f, tryX - size/2f, tryX + size/2f, tryY + size/2f
	    );
	    if (fixturesInRange.isEmpty()){
		return new Vector2(tryX, tryY);
	    }
	    else {
		currentAttempts++;
		scope += 4f;
	    }
	}
	return new Vector2(0f, 0f);
    }

    @Override
    public void processSystem() {
	
    }

    @RequiredArgsConstructor
    private static class SimpleCallback implements QueryCallback {

	@NonNull
	private Array<Fixture> fixturesInRange;

	@Override
	public boolean reportFixture(Fixture fixture) {
	    fixturesInRange.add(fixture);
	    return false;
	}
    }
}
