package laurencewarne.secondspace.server.ship;

import static org.junit.Assert.assertEquals;

import com.badlogic.gdx.math.Vector2;

import org.junit.Before;
import org.junit.Test;


public class ShipCoordinateLocaliserTest {

    private ShipCoordinateLocaliser shipCoordinateLocaliser;

    @Before
    public void setUp() {
	shipCoordinateLocaliser = new ShipCoordinateLocaliser();
    }

    @Test
    public void testCanConvertToUnitCellPosXPosY() {
	Vector2 cellCoords = new Vector2(2f, 3f);
	float width = 1f;
	float height = 1f;
	Vector2 coordinate = new Vector2(0f, 0f);
	Vector2 converted = shipCoordinateLocaliser.shipToCellCoords(
	    coordinate, cellCoords, width, height
	);
	assertEquals(-2.5f, converted.x, 0.0001f);
	assertEquals(-3.5f, converted.y, 0.0001f);
    }

    @Test
    public void testCanConvertToUnitCellPosXNegY() {
	Vector2 cellCoords = new Vector2(2f, -13f);
	float width = 1f;
	float height = 1f;
	Vector2 coordinate = new Vector2(0f, 0f);
	Vector2 converted = shipCoordinateLocaliser.shipToCellCoords(
	    coordinate, cellCoords, width, height
	);
	assertEquals(-2.5f, converted.x, 0.0001f);
	assertEquals(12.5f, converted.y, 0.0001f);
    }

    @Test
    public void testCanConvertToNonUnitCellNegXNegY() {
	Vector2 cellCoords = new Vector2(-222f, -13f);
	float width = 10f;
	float height = 18f;
	Vector2 coordinate = new Vector2(9f, -7f);
	Vector2 converted = shipCoordinateLocaliser.shipToCellCoords(
	    coordinate, cellCoords, width, height
	);
	assertEquals(226f, converted.x, 0.0001f);
	assertEquals(-3f, converted.y, 0.0001f);
    }
}
