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
import com.badlogic.gdx.utils.Array;
import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import laurencewarne.secondspace.server.component.PhysicsRectangleData;
import laurencewarne.secondspace.server.component.ShipPart;
import laurencewarne.secondspace.server.component.Connections;

@RunWith(ParameterizedTestCaseRunner.class)
public class ShipsTest {

    private ComponentMapper<ShipPart> mShipPart;
    private ComponentMapper<PhysicsRectangleData> mData;
    private ComponentMapper<Connections> mConns;

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
	mConns = Mockito.mock(ComponentMapper.class);
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
	Connections conns = new Connections();
	when(mConns.get(0)).thenReturn(conns);
	when(mConns.getSafe(eq(0), any())).thenReturn(conns);
	IntBag b = Ships.getAdjacentEntities(0, mConns, new HashSet<>());
	assertTrue(b.isEmpty());
    }

    @Test
    public void testGetAdjacentEntitiesWithAdjacentEntities() {
	Connections conns = new Connections();
	conns.getEntityToConnectionLocationMap().put(1, new Array<>());
	conns.getEntityToConnectionLocationMap().put(2, new Array<>());
	when(mConns.get(0)).thenReturn(conns);
	when(mConns.getSafe(eq(0), any())).thenReturn(conns);
	IntBag b = Ships.getAdjacentEntities(0, mConns, new HashSet<>());
	assertEquals(2, b.size());
	assertTrue(b.contains(1));
	assertTrue(b.contains(2));
    }
 
    @Test
    public void testGetAdjacentEntitiesWithAdjacentEntitiesWithIgnores() {
	Connections conns = new Connections();
	conns.getEntityToConnectionLocationMap().put(1, new Array<>());
	conns.getEntityToConnectionLocationMap().put(2, new Array<>());
	when(mConns.get(0)).thenReturn(conns);
	when(mConns.getSafe(eq(0), any())).thenReturn(conns);
	Set<Integer> ignores = new HashSet<>(Lists.newArrayList(1));
	IntBag b = Ships.getAdjacentEntities(0, mConns, ignores);
	assertEquals(1, b.size());
	assertTrue(b.contains(2));
    }
 
    @Test
    public void testGetAdjacentEntitiesWithAdjacentEntitiesWithAllIgnores() {
	Connections conns = new Connections();
	conns.getEntityToConnectionLocationMap().put(1, new Array<>());
	conns.getEntityToConnectionLocationMap().put(2, new Array<>());
	when(mConns.get(0)).thenReturn(conns);
	when(mConns.getSafe(eq(0), any())).thenReturn(conns);
	Set<Integer> ignores = new HashSet<>(Lists.newArrayList(1, 2));
	IntBag b = Ships.getAdjacentEntities(0, mConns, ignores);
	assertTrue(b.isEmpty());
    }
 
    @Test
    public void testGetConnectedPartsWithNoConnections() {
	Connections conns = new Connections();
	when(mConns.get(0)).thenReturn(conns);
	when(mConns.getSafe(eq(0), any())).thenReturn(conns);
	IntBag b = Ships.getConnectedParts(0, mConns);
	assertEquals(1, b.size());
	assertTrue(b.contains(0));
    }

    @Test
    public void testGetConnectedPartsWithOnlyAdjacentConnections() {
	Connections conns = new Connections();
	conns.getEntityToConnectionLocationMap().put(1, new Array<>());
	conns.getEntityToConnectionLocationMap().put(2, new Array<>());
	when(mConns.get(0)).thenReturn(conns);
	when(mConns.getSafe(eq(0), any())).thenReturn(conns);
	
	Connections conns1 = new Connections();	
	when(mConns.get(1)).thenReturn(conns1);
	when(mConns.getSafe(eq(1), any())).thenReturn(conns1);
	
	Connections conns2 = new Connections();	
	when(mConns.get(2)).thenReturn(conns2);
	when(mConns.getSafe(eq(2), any())).thenReturn(conns2);

	IntBag b = Ships.getConnectedParts(0, mConns);
	assertEquals(3, b.size());
	assertTrue(b.contains(0));
	assertTrue(b.contains(1));
	assertTrue(b.contains(2));
    }

    @Test
    public void testGetConnectedPartsWithAdjacentAndFurtherConnections() {
	Connections conns = new Connections();
	conns.getEntityToConnectionLocationMap().put(1, new Array<>());
	conns.getEntityToConnectionLocationMap().put(2, new Array<>());
	when(mConns.get(0)).thenReturn(conns);
	when(mConns.getSafe(eq(0), any())).thenReturn(conns);
	
	Connections conns1 = new Connections();	
	when(mConns.get(1)).thenReturn(conns1);
	when(mConns.getSafe(eq(1), any())).thenReturn(conns1);
	
	Connections conns2 = new Connections();	
	conns2.getEntityToConnectionLocationMap().put(3, new Array<>());
	when(mConns.get(2)).thenReturn(conns2);
	when(mConns.getSafe(eq(2), any())).thenReturn(conns2);
	
	Connections conns3 = new Connections();	
	when(mConns.get(3)).thenReturn(conns3);
	when(mConns.getSafe(eq(3), any())).thenReturn(conns3);

	IntBag b = Ships.getConnectedParts(0, mConns);
	assertEquals(4, b.size());
	assertTrue(b.contains(0));
	assertTrue(b.contains(1));
	assertTrue(b.contains(2));
	assertTrue(b.contains(3));
    }

    @Test
    public void testGetConnectedPartsWithAdjacentAndFarConnections() {
	Connections conns = new Connections();
	conns.getEntityToConnectionLocationMap().put(1, new Array<>());
	when(mConns.get(0)).thenReturn(conns);
	when(mConns.getSafe(eq(0), any())).thenReturn(conns);
	
	Connections conns1 = new Connections();
	conns1.getEntityToConnectionLocationMap().put(2, new Array<>());
	when(mConns.get(1)).thenReturn(conns1);
	when(mConns.getSafe(eq(1), any())).thenReturn(conns1);
	
	Connections conns2 = new Connections();	
	conns2.getEntityToConnectionLocationMap().put(3, new Array<>());
	when(mConns.get(2)).thenReturn(conns2);
	when(mConns.getSafe(eq(2), any())).thenReturn(conns2);
	
	Connections conns3 = new Connections();	
	when(mConns.get(3)).thenReturn(conns3);
	when(mConns.getSafe(eq(3), any())).thenReturn(conns3);

	IntBag b = Ships.getConnectedParts(0, mConns);
	assertEquals(4, b.size());
	assertTrue(b.contains(0));
	assertTrue(b.contains(1));
	assertTrue(b.contains(2));
	assertTrue(b.contains(3));
    }


    @Test
    public void testGetConnectedPartsWithUnonnectedParts() {
	Connections conns = new Connections();
	conns.getEntityToConnectionLocationMap().put(1, new Array<>());
	when(mConns.get(0)).thenReturn(conns);
	when(mConns.getSafe(eq(0), any())).thenReturn(conns);
	
	Connections conns1 = new Connections();	
	when(mConns.get(1)).thenReturn(conns1);
	when(mConns.getSafe(eq(1), any())).thenReturn(conns1);

	Connections conns2 = new Connections();	
	conns2.getEntityToConnectionLocationMap().put(3, new Array<>());
	when(mConns.get(2)).thenReturn(conns2);
	when(mConns.getSafe(eq(2), any())).thenReturn(conns2);
	
	Connections conns3 = new Connections();	
	when(mConns.get(3)).thenReturn(conns3);
	when(mConns.getSafe(eq(3), any())).thenReturn(conns3);

	IntBag b = Ships.getConnectedParts(0, mConns);
	assertEquals(2, b.size());
	assertTrue(b.contains(0));
	assertTrue(b.contains(1));
    }
    
}

