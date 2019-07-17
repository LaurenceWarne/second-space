package laurencewarne.secondspace.server.ship;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.Iterators;

import org.junit.Before;
import org.junit.Test;

public class RectanglesTest {

    private Rectangle rect;

    @Before
    public void setUp() {
	rect = new Rectangle(0f, 0f, 2f, 2f);
    }

    @Test
    public void testCanGetPointsFromSquareNoOffset() {
	List<Vector2> points = new ArrayList<>();
	Iterators.addAll(
	    points, Rectangles.getPointsOnEdge(rect, 1f, 0f).iterator()
	);
	assertThat(
	    points, hasItems(
		new Vector2(0f, 0f), new Vector2(0f, 2f), new Vector2(1f, 0f),
		new Vector2(2f, 0f), new Vector2(0f, 1f), new Vector2(2f, 1f),
		new Vector2(1f, 2f), new Vector2(2f, 2f)
	    )
	);
	assertEquals(8, points.size());
    }

    @Test
    public void testCanGetPointsFromSquareWithOffset() {
	List<Vector2> points = new ArrayList<>();
	Iterators.addAll(
	    points, Rectangles.getPointsOnEdge(rect, 1f, 0.5f).iterator()
	);
	assertThat(
	    points, hasItems(
		new Vector2(0.5f, 0f), new Vector2(1.5f, 0f), new Vector2(0f, 0.5f),
		new Vector2(0f, 1.5f), new Vector2(2f, 0.5f), new Vector2(2f, 1.5f),
		new Vector2(0.5f, 2f), new Vector2(1.5f, 2f)
	    )
	);
	assertEquals(8, points.size());
    }

    @Test
    public void testCanGetPointsReturnsEmptyOnBigOffset() {
	List<Vector2> points = new ArrayList<>();
	Iterators.addAll(
	    points, Rectangles.getPointsOnEdge(rect, 1f, 20f).iterator()
	);
	assertThat(points, is(empty()));
    }

    @Test
    public void testCanGetPointsFromSmallRectangleFarFromOrigin() {
	Rectangle rect = new Rectangle(-700f, -2235f, 1f, 1f);
	List<Vector2> points = new ArrayList<>();
	Iterators.addAll(
	    points, Rectangles.getPointsOnEdge(rect, 1f, 0.5f).iterator()
	);
	assertThat(
	    points, hasItems(
		new Vector2(-699.5f, -2235f), new Vector2(-700f, -2234.5f),
		new Vector2(-699.5f, -2234f), new Vector2(-699f, -2234.5f)
	    )
	);
	assertEquals(4, points.size());

    }

}
