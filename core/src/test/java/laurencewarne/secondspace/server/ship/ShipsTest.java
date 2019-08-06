package laurencewarne.secondspace.server.ship;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.andrewjamesjohnson.junit.ParameterGenerator;
import com.andrewjamesjohnson.junit.ParameterizedTest;
import com.andrewjamesjohnson.junit.ParameterizedTestCaseRunner;
import com.andrewjamesjohnson.junit.tuple.Tuple1;
import com.artemis.ComponentMapper;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import laurencewarne.secondspace.server.collect.IntBags;
import laurencewarne.secondspace.server.component.PhysicsRectangleData;
import laurencewarne.secondspace.server.component.ShipPart;
import laurencewarne.secondspace.server.manager.ConnectionManager;

@RunWith(ParameterizedTestCaseRunner.class)
public class ShipsTest {

    private ComponentMapper<ShipPart> mShipPart;
    private ComponentMapper<PhysicsRectangleData> mData;
    private ConnectionManager cm;

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

    public static class RecParameterGeneratorOrigin
	implements ParameterGenerator<Tuple1<Rectangle>> {

        @Override
        public Collection<Tuple1<Rectangle>> generate() {
            return Lists.newArrayList(
		Tuple1.of(new Rectangle(0f, -1f, 1f, 1f)),
		Tuple1.of(new Rectangle(-1f, -1f, 1f, 1f)),
		Tuple1.of(new Rectangle(0f, 0f, 1f, 1f)),
		Tuple1.of(new Rectangle(-1f, 0f, 1f, 1f))
	    );
        }
    }    

    @Before
    public void setUp() {
	mShipPart = Mockito.mock(ComponentMapper.class);
	mData = Mockito.mock(ComponentMapper.class);
	cm = Mockito.mock(ConnectionManager.class);
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

    @Test
    public void testGetShipPartsOnPointReturnsShipPartWithPointAtCentre() {
	IntBag entities = new IntBag();
	entities.add(0);
	ShipPart part = new ShipPart();
	part.setLocalX(0); part.setLocalY(0);
	PhysicsRectangleData data = new PhysicsRectangleData();
	data.setWidth(1f); data.setHeight(1f);

	when(mShipPart.get(0)).thenReturn(part);
	when(mShipPart.getSafe(eq(0), any())).thenReturn(part);
	when(mData.get(0)).thenReturn(data);
	when(mData.getSafe(eq(0), any())).thenReturn(data);
	Vector2 point = new Vector2(0.5f, 0.5f);

	IntBag ret = Ships.getShipPartsOnPoint(
	    entities, mShipPart, mData, point
	);
	assertEquals(1, ret.size());
	assertEquals(0, ret.get(0));
    }

    @ParameterizedTest(generator = RecParameterGeneratorOrigin.class)
    public void testGetShipPartsOnPointReturnsShipPartWithPointOnVertex(
	Tuple1<Rectangle> param) {
	Rectangle rect = param._1;
	IntBag entities = new IntBag();
	entities.add(0);
	ShipPart part = new ShipPart();
	part.setLocalX((int) rect.x); part.setLocalY((int) rect.y);
	PhysicsRectangleData data = new PhysicsRectangleData();
	data.setWidth(rect.width); data.setHeight(rect.height);

	when(mShipPart.get(0)).thenReturn(part);
	when(mShipPart.getSafe(eq(0), any())).thenReturn(part);
	when(mData.get(0)).thenReturn(data);
	when(mData.getSafe(eq(0), any())).thenReturn(data);
	Vector2 point = new Vector2(0f, 0f);

	IntBag ret = Ships.getShipPartsOnPoint(
	    entities, mShipPart, mData, point
	);
	assertEquals(1, ret.size());
	assertEquals(0, ret.get(0));
    }

    @Test
    public void testGetAdjacentEntitiesReturnsNothingOnShipWithOneEntity() {
	when(cm.getConnectedEntities(0)).thenReturn(new IntBag());
	IntBag b = Ships.getAdjacentEntities(0, cm, new HashSet<>());
	assertTrue(b.isEmpty());
    }

    @Test
    public void testGetAdjacentEntitiesWithAdjacentEntities() {
	when(cm.getConnectedEntities(0)).thenReturn(IntBags.of(1, 2));
	IntBag b = Ships.getAdjacentEntities(0, cm, new HashSet<>());
	assertEquals(2, b.size());
	assertTrue(b.contains(1));
	assertTrue(b.contains(2));
    }
 
    @Test
    public void testGetAdjacentEntitiesWithAdjacentEntitiesWithIgnores() {
	when(cm.getConnectedEntities(0)).thenReturn(IntBags.of(1, 2));
	Set<Integer> ignores = new HashSet<>(Lists.newArrayList(1));
	IntBag b = Ships.getAdjacentEntities(0, cm, ignores);
	assertEquals(1, b.size());
	assertTrue(b.contains(2));
    }
 
    @Test
    public void testGetAdjacentEntitiesWithAdjacentEntitiesWithAllIgnores() {
	when(cm.getConnectedEntities(0)).thenReturn(IntBags.of(1, 2));
	Set<Integer> ignores = new HashSet<>(Lists.newArrayList(1, 2));
	IntBag b = Ships.getAdjacentEntities(0, cm, ignores);
	assertTrue(b.isEmpty());
    }
 
    @Test
    public void testGetConnectedPartsWithNoConnections() {
	when(cm.getConnectedEntities(0)).thenReturn(new IntBag());
	IntBag b = Ships.getConnectedParts(0, cm);
	assertEquals(1, b.size());
	assertTrue(b.contains(0));
    }

    @Test
    public void testGetConnectedPartsWithOnlyAdjacentConnections() {
	when(cm.getConnectedEntities(0)).thenReturn(IntBags.of(1, 2));
	when(cm.getConnectedEntities(1)).thenReturn(IntBags.of(0));
	when(cm.getConnectedEntities(2)).thenReturn(IntBags.of(0));

	IntBag b = Ships.getConnectedParts(0, cm);
	assertEquals(3, b.size());
	assertTrue(b.contains(0));
	assertTrue(b.contains(1));
	assertTrue(b.contains(2));
    }

    @Test
    public void testGetConnectedPartsWithAdjacentAndFurtherConnections() {
	when(cm.getConnectedEntities(0)).thenReturn(IntBags.of(1, 2));
	when(cm.getConnectedEntities(1)).thenReturn(IntBags.of(0));
	when(cm.getConnectedEntities(2)).thenReturn(IntBags.of(0, 3));
	when(cm.getConnectedEntities(3)).thenReturn(IntBags.of(2));

	IntBag b = Ships.getConnectedParts(0, cm);
	assertEquals(4, b.size());
	assertTrue(b.contains(0));
	assertTrue(b.contains(1));
	assertTrue(b.contains(2));
	assertTrue(b.contains(3));
    }

    @Test
    public void testGetConnectedPartsWithAdjacentAndFarConnections() {
	when(cm.getConnectedEntities(0)).thenReturn(IntBags.of(1));
	when(cm.getConnectedEntities(1)).thenReturn(IntBags.of(0, 2));
	when(cm.getConnectedEntities(2)).thenReturn(IntBags.of(1, 3));
	when(cm.getConnectedEntities(3)).thenReturn(IntBags.of(2));

	IntBag b = Ships.getConnectedParts(0, cm);
	assertEquals(4, b.size());
	assertTrue(b.contains(0));
	assertTrue(b.contains(1));
	assertTrue(b.contains(2));
	assertTrue(b.contains(3));
    }

    @Test
    public void testGetConnectedPartsWithUnonnectedParts() {
	when(cm.getConnectedEntities(0)).thenReturn(IntBags.of(1));
	when(cm.getConnectedEntities(1)).thenReturn(IntBags.of(0));
	when(cm.getConnectedEntities(2)).thenReturn(IntBags.of(3));
	when(cm.getConnectedEntities(3)).thenReturn(IntBags.of(2));

	IntBag b = Ships.getConnectedParts(0, cm);
	assertEquals(2, b.size());
	assertTrue(b.contains(0));
	assertTrue(b.contains(1));
    }
    
}

