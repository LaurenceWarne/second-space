package laurencewarne.secondspace.server.manager;

import java.util.HashMap;
import java.util.Map;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.MathUtils;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import laurencewarne.secondspace.server.collect.IntBags;
import laurencewarne.secondspace.server.component.chunk.Chunk;
import lombok.Getter;
import lombok.NonNull;

/**
 * Manages the addition and removal of entities in chunks.
 */
public class ChunkManager extends BaseSystem {
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
	// populate maps based off chunks which have just been deserialized
    }

    @Override
    public void processSystem() {

    }

    public void put(int entity, float x, float y) {
	final int chunkX = MathUtils.floor(x / chunkWidth);
	final int chunkY = MathUtils.floor(y / chunkWidth);
	if (!chunkTable.contains(chunkX, chunkY)) {
	    chunkTable.put(chunkX, chunkY, mChunk.create(world.create()));
	}
	final Chunk chunk = chunkTable.get(chunkX, chunkY);
	chunk.entities.add(entity);
	if (idToChunkMap.containsKey(entity)){
	    idToChunkMap.get(entity).entities.remove(entity);
	}
	idToChunkMap.put(entity, chunk);
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
}
