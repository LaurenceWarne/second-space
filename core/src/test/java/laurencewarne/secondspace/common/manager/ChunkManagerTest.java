package laurencewarne.secondspace.common.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Vector2;

import org.junit.Before;
import org.junit.Test;

import laurencewarne.secondspace.common.component.chunk.Chunk;
import laurencewarne.secondspace.common.manager.ChunkManager;

public class ChunkManagerTest {

    private World world;
    private ChunkManager manager;
    private ComponentMapper<Chunk> m;

    @Before
    public void setUp() {
	WorldConfiguration setup = new WorldConfigurationBuilder()
	    .with(
		manager = new ChunkManager()
	    )
	    .build();
	world = new World(setup);
	m = world.getMapper(Chunk.class);
	world.process();
    }

    @Test
    public void testCanGetEntityPreviouslyAdded() {
	int id = world.create();
	manager.put(id, 100f, 100f);
	IntBag bag = manager.getEntitiesInChunk(100f, 100f);
	assertTrue(bag.contains(id));
    }

    @Test
    public void testCanMoveEntityBetweenChunks() {
	int id = world.create();
	manager.put(id, 100f, 100f);
	manager.put(id, -1023, 10034f);
	IntBag bag1 = manager.getEntitiesInChunk(100f, 100f);
	IntBag bag2 = manager.getEntitiesInChunk(-1023f, 10034f);
	assertFalse(bag1.contains(id));
	assertTrue(bag2.contains(id));
    }

    @Test
    public void testCanConvertPositiveCoordToChunkCoord() {
	Vector2 coord = manager.worldToChunkCoordinate(100, 50);
	Vector2 target = new Vector2(
	    100/manager.getChunkWidth(), 50/manager.getChunkHeight()
	);
	assertEquals(target, coord);
    }

    @Test
    public void testCanConvertNegativeCoordToChunkCoord() {
	Vector2 coord = manager.worldToChunkCoordinate(-1000, -502);
	Vector2 target = new Vector2(
	    -1000/manager.getChunkWidth() - 1, -502/manager.getChunkHeight() - 1
	);
	assertEquals(target, coord);
    }

    @Test
    public void testCanConvertBoundaryCoordToChunkCoord() {
	Vector2 coord = manager.worldToChunkCoordinate(
	    -10*manager.getChunkWidth(), 5*manager.getChunkHeight()
	);
	assertEquals(new Vector2(-10, 5), coord);
    }

    @Test
    public void testIsEntityInChunkReturnsFalseOnEntityNotAdded() {
	int id = world.create();
	assertFalse(manager.isEntityInChunk(id, 0, 0));
    }

    @Test
    public void testIsEntityInChunkReturnsFalseOnWrongChunk() {
	int id = world.create();
	manager.put(id, 1000f, 1000f);
	assertFalse(manager.isEntityInChunk(id, 0, 0));
    }

    @Test
    public void testGetEntityInChunkReturnsTrueWhenEntityInChunk() {
	int id = world.create();
	manager.put(id, 1000f, 1000f);
	assertTrue(
	    manager.isEntityInChunk(
		id, 1000/manager.getChunkWidth(), 1000/manager.getChunkHeight()
	    )
	);
    }

    @Test
    public void testGetEntitiesInChunkForMultipleEntities() {
	int id1 = world.create(), id2 = world.create();
	manager.put(id1, 101, 34);
	manager.put(id2, 100, 39);
	IntBag bag = manager.getEntitiesInChunk(
	    100/manager.getChunkWidth(), 34/manager.getChunkHeight()
	);
	assertTrue(bag.contains(id1));
	assertTrue(bag.contains(id2));
    }

    @Test
    public void testGetEntitiesIngoresEntityInOtherChunk() {
	int id1 = world.create(), id2 = world.create();
	manager.put(id1, 101, 34);
	manager.put(id2, 133, 39);
	IntBag bag = manager.getEntitiesInChunk(
	    100/manager.getChunkWidth(), 34/manager.getChunkHeight()
	);
	assertTrue(bag.contains(id1));
	assertFalse(bag.contains(id2));
    }

    @Test
    public void testGetEntitiesInChunksReturnsEntityInChunk() {
	int id1 = world.create();
	manager.put(id1, 101, 34);
	IntBag bag = manager.getEntitiesInChunks(
	    100, 34, 0
	);
	assertTrue(bag.contains(id1));
    }

    @Test
    public void testGetEntitiesInChunksReturnsEntitiesInRightChunk() {
	int id1 = world.create(), id2 = world.create();
	manager.put(id1, 101, 34);
	manager.put(id2, 133, 39);
	IntBag bag = manager.getEntitiesInChunks(
	    100, 34, 1
	);
	assertTrue(bag.contains(id1));
	assertTrue(bag.contains(id2));	
    }

    @Test
    public void testGetEntitiesInChunksReturnsEntitiesInLeftChunk() {
	int id1 = world.create(), id2 = world.create();
	manager.put(id1, 101, 34);
	manager.put(id2, 69, 39);
	IntBag bag = manager.getEntitiesInChunks(
	    100, 34, 1
	);
	assertTrue(bag.contains(id1));
	assertTrue(bag.contains(id2));	
    }

    @Test
    public void testGetEntitiesInChunksReturnsEntitiesInBottomChunk() {
	int id1 = world.create(), id2 = world.create();
	manager.put(id1, 101, 34);
	manager.put(id2, 101, 3);
	IntBag bag = manager.getEntitiesInChunks(
	    100, 34, 1
	);
	assertTrue(bag.contains(id1));
	assertTrue(bag.contains(id2));	
    }

    @Test
    public void testGetEntitiesInChunksIgnoresEntityOutOfRange() {
	int id1 = world.create(), id2 = world.create();
	manager.put(id1, 101, 34);
	manager.put(id2, 165, 36);
	IntBag bag = manager.getEntitiesInChunks(
	    100, 34, 1
	);
	assertTrue(bag.contains(id1));
	assertFalse(bag.contains(id2));	
    }
    
}
