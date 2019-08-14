package laurencewarne.secondspace.server.manager;

import java.util.HashMap;
import java.util.Map;

import com.artemis.BaseSystem;
import com.artemis.utils.IntBag;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import laurencewarne.secondspace.server.component.chunk.Chunk;
import lombok.Getter;
import lombok.NonNull;

public class ChunkManager extends BaseSystem {
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
	chunkTable.containsValue(world);
    }

    public void put(int entity, float x, float y) {
	
    }

    public IntBag getEntitiesInChunk(float x, float y) {
	return null;
    }

    public IntBag getEntitiesInChunksSurrounding(float x, float y, int distance) {
	return null;
    }
}
