package laurencewarne.secondspace.server.ship;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;

import com.andrewjamesjohnson.junit.ParameterGenerator;
import com.andrewjamesjohnson.junit.ParameterizedTest;
import com.andrewjamesjohnson.junit.ParameterizedTestCaseRunner;
import com.andrewjamesjohnson.junit.tuple.Tuple1;
import com.badlogic.gdx.math.Rectangle;
import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ParameterizedTestCaseRunner.class)
public class ShipsTest {

    public static class RecParameterGenerator
	implements ParameterGenerator<Tuple1<Rectangle>> {

        @Override
        public Collection<Tuple1<Rectangle>> generate() {
            return Lists.newArrayList(
		Tuple1.of(new Rectangle(0f, 1f, 1f, 1f)),
		Tuple1.of(new Rectangle(1f, 0f, 1f, 1f)),
		Tuple1.of(new Rectangle(0f, -1f, 1f, 1f)),
		Tuple1.of(new Rectangle(-1f, 0f, 1f, 1f))
	    );
        }
    }    

    @Before
    public void setUp() {

    }    

    @ParameterizedTest(generator = RecParameterGenerator.class)
    public void test1PartShipIsAugmentableWith1StaticTouchingPart(Tuple1<Rectangle> param) {
	List<Rectangle> existingParts = Lists.newArrayList(
	    param._1
	);
	Rectangle newPart = new Rectangle(0f, 0f, 1f, 1f);
	assertTrue(Ships.isAugmentable(existingParts, newPart));
    }

    @ParameterizedTest(generator = RecParameterGenerator.class)
    public void test1PartShipIsAugmentableWith1MovingTouchingPart(Tuple1<Rectangle> param) {
	List<Rectangle> existingParts = Lists.newArrayList(
	    new Rectangle(0f, 0f, 1f, 1f)
	);
	Rectangle newPart = param._1;
	assertTrue(Ships.isAugmentable(existingParts, newPart));
    }

    @Test
    public void test1PartShipIsNotAugmentableWithOverlappingPart() {
	List<Rectangle> existingParts = Lists.newArrayList(
	    new Rectangle(0f, 0f, 2f, 2f)
	);
	Rectangle newPart = new Rectangle(1f, 0f, 1f, 1f);
	assertFalse(Ships.isAugmentable(existingParts, newPart));
    }

    @Test
    public void test1PartShipIsNotAugmentableWithNonTouchingPart() {
	List<Rectangle> existingParts = Lists.newArrayList(
	    new Rectangle(0f, 0f, 2f, 2f)
	);
	Rectangle newPart = new Rectangle(-2f, 0f, 1f, 1f);
	assertFalse(Ships.isAugmentable(existingParts, newPart));
    }

    @Test
    public void test5PartShipAugmentableWithNewPartTouching3Parts() {
	List<Rectangle> existingParts = Lists.newArrayList(
	    new Rectangle(0f, 0f, 1f, 1f),
	    new Rectangle(0f, 1f, 1f, 1f),
	    new Rectangle(1f, 1f, 1f, 1f),
	    new Rectangle(2f, 1f, 1f, 1f),
	    new Rectangle(2f, 0f, 1f, 1f)
	);
	Rectangle newPart = new Rectangle(1f, 0f, 1f, 1f);
	assertTrue(Ships.isAugmentable(existingParts, newPart));
    }

    @Test
    public void testNewShipPartNotAugmentableWith0ExistingParts() {
	List<Rectangle> existingParts = Lists.newArrayList();
	Rectangle newPart = new Rectangle(1f, 0f, 1f, 1f);
	assertFalse(Ships.isAugmentable(existingParts, newPart));	
    }

    @Test
    public void testNewShipPartNotAugmentableWhenInsideExistingPart() {
	List<Rectangle> existingParts = Lists.newArrayList(
	    new Rectangle(2f, 2f, 4f, 4f),
	    new Rectangle(1f, 2f, 1f, 1f)
	);
	Rectangle newPart = new Rectangle(3f, 3f, 1f, 1f);
	assertFalse(Ships.isAugmentable(existingParts, newPart));	
    }

    @Test
    public void testNewShipPartNotAugmentableWhenTouchingAndOverlapping() {
	List<Rectangle> existingParts = Lists.newArrayList(
	    new Rectangle(3f, 1f, 1f, 1f),
	    new Rectangle(1f, 1f, 1f, 1f)
	);
	Rectangle newPart = new Rectangle(2f, 1f, 2f, 1f);
	assertFalse(Ships.isAugmentable(existingParts, newPart));	
    }

    @Test
    public void testNewShipPartNotAugmentableWhenSameAsExistingPart() {
	List<Rectangle> existingParts = Lists.newArrayList(
	    new Rectangle(1f, 1f, 1f, 1f)
	);
	Rectangle newPart = new Rectangle(1f, 1f, 1f, 1f);
	assertFalse(Ships.isAugmentable(existingParts, newPart));	
    }

}
