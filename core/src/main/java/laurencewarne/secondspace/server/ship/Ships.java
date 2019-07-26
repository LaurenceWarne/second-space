package laurencewarne.secondspace.server.ship;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import com.artemis.ComponentMapper;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntArray;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;

import laurencewarne.secondspace.server.collect.IntBags;
import laurencewarne.secondspace.server.component.PhysicsRectangleData;
import laurencewarne.secondspace.server.component.ShipPart;
import laurencewarne.secondspace.server.component.ShipPartConnections;
import lombok.NonNull;

/**
 * Utilities for working with ships. Named in the same vain as java stdlib/guava sticking an 's' on the name of the interface/thing you want to provide utilities for.
 */
public final class Ships {

	public static final ShipPartConnections NULL_CONNECTION =
		new ShipPartConnections();

    private Ships() {
	
    }

	/**
	 * Get a {@link Rectangle} representing the entity's body in 'ship space', ie a coordinate system where (0, 0) is the ships origin, and rotation is fixed.
	 *
	 * @param shipPart {@link ShipPart} component of the entity
	 * @param rectangleData {@link PhysicsRectangleData} component of the entity
	 * @return {@link Rectangle} representing the entity's body in ship space
	 */
    public static Rectangle getRectangleInShipSpace(
		@NonNull ShipPart shipPart,
		@NonNull PhysicsRectangleData rectangleData) {
		final int x = shipPart.getLocalX();
		final int y = shipPart.getLocalY();
		final float width = rectangleData.getWidth();
		final float height = rectangleData.getHeight();
		return new Rectangle(x, y, width, height);
    }

	/**
	 * Get an {@link IntBag} of all entities (which are ship parts) which either:
	 * <pre>
	 * 1) Contain the specified point
	 * 2) Have the specified point lying on an edge of their bodies
	 * 3) Have the specified point lying on a vertex of their bodies
	 * </pre>
	 *
	 * @param entities {@link IntBag} of entities to search
	 * @param mShipPart {@link ComponentMapper} of entity {@link ShipPart}s
	 * @param mPhysicsRectangleData {@link ComponentMapper} of entity {@link PhysicsRectangleData}s
	 * @param point point to search for
	 * @return entities on the specified point
	 */
    public static IntBag getShipPartsOnPoint(
		@NonNull IntBag entities,
		@NonNull ComponentMapper<ShipPart> mShipPart,
		@NonNull ComponentMapper<PhysicsRectangleData> mRecData,
		@NonNull Vector2 point) {
		// Entities which have ship parts lying on the specified point
		final IntBag entitiesOnPoint = new IntBag();
		final Set<Integer> distinctEntities = IntBags.toSet(entities);
		// Iterate through distinct entities
		for (int entity : distinctEntities) {
			final ShipPart shipPart = mShipPart.getSafe(entity, null);
			final PhysicsRectangleData recData = mRecData.getSafe(entity, null);
			if (shipPart != null && recData != null) {
				Rectangle rectangle = getRectangleInShipSpace(shipPart, recData);
				// libgdx Rectangle objs: contains() is true if point lies on rectangle edge
				if (rectangle.contains(point)) {
					entitiesOnPoint.add(entity);
				}
			}
		}
		return entitiesOnPoint;
    }    

	/**
	 * Check if a new ship part can be added to the specified position.
	 *
	 * @param existingShipParts {@link Rectangle}s encoding the position and size of existing ship parts in ship space
	 * @param newPart {@link Rectangle} encoding the position and size of the ship part to be checked
	 * @return true: if the newPart were to be added, if it would be connected to at least on other ship part (e.g. share an edge), and not overlap with an existing part, else false
	 */
    public static boolean isAugmentable(
		@NonNull Iterable<Rectangle> existingShipParts,
		@NonNull Rectangle newPart) {
		// If rectangles share a common edge, aka if they "touch"
		boolean touch = false;
		final Vector2 originalPosition = newPart.getPosition(new Vector2());
		for (Rectangle shipPart : existingShipParts) {
			final boolean clash = shipPart.overlaps(newPart) ||
				shipPart.contains(newPart) ||
				newPart.contains(shipPart);
			if ( clash ) {
				return false;
			}
			// We check if rectangles touch by translating them slightly and seeing if they overlap. We take advantage of the fact ship parts are always of integral length
			final Vector2[] transPositions = {
				new Vector2(originalPosition).add(0.5f, 0f),
				new Vector2(originalPosition).add(-0.5f, 0f),
				new Vector2(originalPosition).add(0f, 0.5f),
				new Vector2(originalPosition).add(0f, -0.5f),
			};
			for (Vector2 transPosition : transPositions) {
				newPart.setPosition(transPosition);
				if (newPart.overlaps(shipPart)) {
					touch = true;
				}
			}
			newPart.setPosition(originalPosition);
		}
		return touch;
    }

	/**
	 * Get entities whose {@link ShipPart} has an edge touching an edge of the specified entity's {@link ShipPart}. Vertex to vertex does not count.
	 *
	 * @param id the id of the entity
	 * @param mShipPartConnections {@link ShipPartConnections} {@link ComponentMapper}
	 * @param entitiesToIgnore entities belonging to this {@link Set} will not be returned even if they are adjacent
	 * @return {@link IntBag} of entities which are adjacent
	 */
    public static IntBag getAdjacentEntities(
		int id,
		@NonNull ComponentMapper<ShipPartConnections> mShipPartConnections,
		@NonNull Set<Integer> entitiesToIgnore
    ) {
		final ShipPartConnections conns = mShipPartConnections.getSafe(
			id, NULL_CONNECTION
		);
		final IntArray adjEntities = conns
			.getEntityToConnectionLocationMapping()
			.keys()
			.toArray();
		final IntBag adjacentBag = new IntBag();
		for (int i = 0; i < adjEntities.size; i++){
			if (!entitiesToIgnore.contains(adjEntities.get(i))) {
				adjacentBag.add(adjEntities.get(i));
			}
		}
		return adjacentBag;
    }

	/**
	 * Get an {@link IntBag} of entities whose {@link ShipPart}s are 'connected' to the {@link ShipPart} of entity with the specified id. Part A is said to be connected to part B if, constructing a <a href='https://en.wikipedia.org/wiki/Graph_(discrete_mathematics)'>graph</a> with nodes as ship parts and edges between nodes if the respective ship parts are adjacent, the two nodes {Part A, Part B} are <a href='https://en.wikipedia.org/wiki/Graph_(discrete_mathematics)#Connected_graph'>connected</a>.
	 *
	 * @param id id of the entity to get connected parts for
	 * @param mShipPartConnections {@link ShipPartConnections} {@link ComponentMapper}
	 * @return {@link IntBag} of connected entities, including the specified entity itself
	 */
    public static IntBag getConnectedParts(
		int id,
		@NonNull ComponentMapper<ShipPartConnections> mShipPartConnections
    ) {
		final Set<Integer> searchedEntities = Sets.newHashSet();
		final Queue<Integer> entitiesToSearch = new LinkedList<>(Ints.asList(id));
		while (!entitiesToSearch.isEmpty()){
			final int entity = entitiesToSearch.poll();
			final IntBag nextEntities = getAdjacentEntities(
				entity, mShipPartConnections, searchedEntities
			);
			entitiesToSearch.addAll(IntBags.toList(nextEntities));
			searchedEntities.add(entity);
		}
		return IntBags.fromCollection(searchedEntities);
    }
    
}
