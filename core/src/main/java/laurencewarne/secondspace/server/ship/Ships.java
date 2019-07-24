package laurencewarne.secondspace.server.ship;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import com.artemis.ComponentMapper;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntArray;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import laurencewarne.secondspace.server.component.PhysicsRectangleData;
import laurencewarne.secondspace.server.component.ShipPart;
import laurencewarne.secondspace.server.component.ShipPartConnections;

/**
 * Utilities for working with ships. Named in the same vain as java stdlib/guava sticking an 's' on the name of the interface/thing you want to provide utilities for.
 */
public final class Ships {

	public static final ShipPartConnections NULL_CONNECTION =
		new ShipPartConnections();

    private Ships() {
	
    }

    public static Rectangle getRectangleInShipSpace(
		ShipPart shipPart, PhysicsRectangleData rectangleData) {
		final int x = shipPart.getLocalX();
		final int y = shipPart.getLocalY();
		final float width = rectangleData.getWidth();
		final float height = rectangleData.getHeight();
		return new Rectangle(x, y, width, height);
    }

    public static IntBag getShipPartsOnPoint(
		IntBag entities, ComponentMapper<ShipPart> mShipPart,
		ComponentMapper<PhysicsRectangleData> mRecData, Vector2 point) {
		// Entities which have ship parts lying on the specified point
		final IntBag entitiesOnPoint = new IntBag();
		// Intbag data adds extra 0s
		final Set<Integer> entitiesDistinct = Arrays
			.stream(entities.getData())
			.boxed()
			.collect(Collectors.toSet());
		// Iterate through distinct entities
		for (int entity : entitiesDistinct) {
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

    public static boolean isAugmentable(
		Iterable<Rectangle> existingShipParts, Rectangle newPart) {
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

    public static IntBag getAdjacentEntities(
		int id, ComponentMapper<ShipPartConnections> mShipPartConnections,
		Set<Integer> entitiesToIgnore
    ) {
		final IntBag adjacentBag = new IntBag();
		final ShipPartConnections conns = mShipPartConnections.getSafe(
			id, NULL_CONNECTION
		);
		final IntArray adjEntities = conns
			.getEntityToConnectionLocationMapping()
			.keys()
			.toArray();
		Arrays.stream(adjEntities.items)
			.filter(e -> !entitiesToIgnore.contains(e))
			.forEach(e -> adjacentBag.add(e));
		return adjacentBag;
    }

    public static IntBag getConnectedParts(
		int id, ComponentMapper<ShipPartConnections> mShipPartConnections
    ) {
		final Set<Integer> searchedEntities = Sets.newHashSet();
		final Queue<Integer> entitiesToSearch = Lists.newLinkedList();
		entitiesToSearch.add(id);
		while (!entitiesToSearch.isEmpty()){
			final int entity = entitiesToSearch.poll();
			final IntBag nextEntities = getAdjacentEntities(
				entity, mShipPartConnections, searchedEntities
			);
			final Collection<Integer> nextEntitiesColl = Arrays
				.stream(nextEntities.getData())
				.boxed()
				.collect(Collectors.toList());
			entitiesToSearch.addAll(nextEntitiesColl);
			searchedEntities.add(entity);
		}
		final IntBag result = new IntBag();
		searchedEntities.stream().forEach(e -> result.add(e));
		return result;
    }
    
}
