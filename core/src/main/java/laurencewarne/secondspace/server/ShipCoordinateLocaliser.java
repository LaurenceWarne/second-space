package laurencewarne.secondspace.server;

import com.badlogic.gdx.math.Vector2;

/**
 * Class to convert from local ship coordinates to local cell coordinates.
 */
public class ShipCoordinateLocaliser {

    /**
     * Converts from ship local coordinates to cell local coordinates. The origin of the cell coordinate is its centre point.
     *
     * @param coordinateInShipCoords The coordinate to be converted from ship local to cell local coordinates
     * @param cellInShipCoords The (ship local) coordinate (bottom left corner) of the cell (ship part) whose local coordinates are to be converted to
     * @param cellWidth The width of the cell
     * @param cellHeight The height of the cell
     * @return coordinateInShipCoordinates in cell local coordinates
     */
    public Vector2 shipToCellCoords(
	Vector2 coordinateInShipCoords, Vector2 cellInShipCoords,
	float cellWidth, float cellHeight) {
	float originX = cellInShipCoords.x + cellWidth / 2f;
	float originY = cellInShipCoords.y + cellHeight / 2f;
	return new Vector2(
	    coordinateInShipCoords.x - originX,
	    coordinateInShipCoords.y - originY
	);
    }
}
