package laurencewarne.secondspace.common.manager;

import java.util.HashMap;
import java.util.Map;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.common.collect.IntBags;
import laurencewarne.secondspace.common.component.Physics;
import laurencewarne.secondspace.common.component.chunk.Chunk;
import laurencewarne.secondspace.common.event.EntityCreatedEvent;
import laurencewarne.secondspace.common.event.EntityMovedEvent;
import laurencewarne.secondspace.common.system.WorldDeserializationSystem;
import lombok.Getter;
import lombok.NonNull;
import net.fbridault.eeel.annotation.All;
import net.fbridault.eeel.annotation.Removed;
import net.mostlyoriginal.api.event.common.Subscribe;

/**
 * Manages the addition and removal of entities in chunks.
 */
public class ChunkManager extends BaseSystem {
    private final Logger logger = LoggerFactory.getLogger(ChunkManager.class);
    private ComponentMapper<Chunk> mChunk;
    @NonNull
    private final Map<Integer, Chunk> idToChunkMap = new HashMap<>();
    private final Table<Integer, Integer, Chunk> chunkTable =
	HashBasedTable.create();
    @Getter
    private final int chunkWidth = 32;
    @Getter
    private final int chunkHeight = 32;

    @Override
    public void initialize() {
	final WorldDeserializationSystem deserializationSystem = world.getSystem(
	    WorldDeserializationSystem.class
	);
	if (deserializationSystem == null) {
	    return;
	}
	int chunkCount = 0;
	for (int entity : deserializationSystem.getLoadedEntities()) {
	    if (mChunk.has(entity)) {
		final Chunk chunk = mChunk.get(entity);
		chunkTable.put(
		    chunk.getOriginX(), chunk.getOriginY(), chunk
		);
		for (int entityInChunk : IntBags.toSet(chunk.entities)) {
		    idToChunkMap.put(entityInChunk, chunk);
		}
		chunkCount++;
	    }
	}
	logger.info("Loaded {} chunks from file", chunkCount);
    }

    @Override
    public void processSystem() {

    }

    /**
     * Return the chunk coordinate of the chunk the position resides in.
     *
     * @param x
     * @param y
     * @return chunk coordinate of the chunk (x, y) resides in
     */
    public Vector2 worldToChunkCoordinate(float x, float y) {
	return new Vector2(
	    MathUtils.floor(x / chunkWidth), MathUtils.floor(y / chunkWidth)
	);
    }

    public void put(int entity, float x, float y) {
	final int chunkX = MathUtils.floor(x / chunkWidth);
	final int chunkY = MathUtils.floor(y / chunkHeight);
	// Small optimisation, most movement occurs in same chunk
	if (!isEntityInChunk(entity, chunkX, chunkY)){
	    if (!chunkTable.contains(chunkX, chunkY)) {
		final Chunk chunk = mChunk.create(world.create());
		chunk.setOriginX(chunkX);
		chunk.setOriginY(chunkY);
		chunkTable.put(chunkX, chunkY, chunk);
	    }
	    final Chunk chunk = chunkTable.get(chunkX, chunkY);
	    chunk.entities.add(entity);
	    if (idToChunkMap.containsKey(entity)){
		idToChunkMap.get(entity).entities.removeValue(entity);
	    }
	    idToChunkMap.put(entity, chunk);
	}
    }

    public boolean isEntityInChunk(int entity, int chunkX, int chunkY) {
	if (idToChunkMap.containsKey(entity)) {
	    final Chunk chunk = idToChunkMap.get(entity);
	    return chunk.getOriginX() == chunkX && chunk.getOriginY() == chunkY;
	}
	else {
	    return false;
	}
    }

    public IntBag getEntitiesInChunk(float x, float y) {
	final int chunkX = MathUtils.floor(x / chunkWidth);
	final int chunkY = MathUtils.floor(y / chunkWidth);
	return getEntitiesInChunk(chunkX, chunkY);
    }

    public IntBag getEntitiesInChunk(int chunkX, int chunkY) {
	if (chunkTable.contains(chunkX, chunkY)) {
	    return IntBags.copyOf(chunkTable.get(chunkX, chunkY).entities);
	}
	else {
	    return new IntBag();
	}	
    }

    /**
     * Return entities in chunks whose <a href="https://en.wikipedia.org/wiki/Chebyshev_distance">Chebyshev Distance</a> from the chunk containing (x, y) is less than or equal to distance.
     *
     * @param x
     * @param y
     * @param distance
     * @return entities in chunks which are in range
     */
    public IntBag getEntitiesInChunks(float x, float y, int distance) {
	final int chunkX = MathUtils.floor(x / chunkWidth);
	final int chunkY = MathUtils.floor(y / chunkWidth);
	final IntBag allEntities = new IntBag();
	for (int i = chunkX - distance; i <= chunkX + distance; i++){
	    for (int j = chunkY - distance; j <= chunkY + distance; j++){
		allEntities.addAll(getEntitiesInChunk(i, j));
	    }
	}
	return allEntities;
    }

    @Subscribe
    public void onEntityCreatedEvent(@NonNull EntityCreatedEvent evt) {
	put(evt.getId(), evt.getX(), evt.getY());	
    }

    @Subscribe
    public void onEntityMovedEvent(@NonNull EntityMovedEvent evt) {
	put(evt.getId(), evt.getPostX(), evt.getPostY());
    }

    @Removed
    @All(Physics.class)
    public void onPhysicsRemoved(int id) {
	if (idToChunkMap.containsKey(id)){
	    final Chunk chunk = idToChunkMap.get(id);
	    chunk.entities.removeValue(id);
	    idToChunkMap.remove(id);
	}
    }
}
