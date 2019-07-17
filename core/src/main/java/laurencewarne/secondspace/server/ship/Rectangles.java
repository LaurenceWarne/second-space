package laurencewarne.secondspace.server.ship;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.Lists;

public final class Rectangles {

    private Rectangles() {
	
    }

    /**
     * Get an Iterable of points lying on the edges of the specified Rectange. The
     * points will start at the vertices (+ offset) of each rectangle, then be
     * spaced 'spacing' units apart.
     *
     * @param rectangle the rectangle obj to get points from
     * @param spacing how far points along the same edge should be spaced from one antother
     * @param offset the offset of the first point on an edge from the vertex
     * @return Iterable of Vector2 objects representing points on the Rectangle
     */
    public static Iterable<Vector2> getPointsOnEdge(
	Rectangle rectangle, float spacing, float offset) {
	Collection<Vector2> points = new ArrayList<>();
	float width = rectangle.width, height = rectangle.height;
	float recX = rectangle.x, recY = rectangle.y;
	for (float x = recX + offset; x <= recX + width; x += spacing){
	    points.addAll(
		Lists.newArrayList(
		    new Vector2(x, recY),
		    new Vector2(x, recY + height)
		)
	    );
	}
	for (float y = recY + offset; y <= recY + height; y += spacing){
	    points.addAll(
		Lists.newArrayList(
		    new Vector2(recX, y),
		    new Vector2(recX + rectangle.width, y)
		)
	    );
	}
	// Remove duplicates
	return points.stream().distinct().collect(Collectors.toList());
    }
}
