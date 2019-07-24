package laurencewarne.secondspace.server.collect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.IntArray;
import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;

public class IntBagsTest {

    private IntBag bag = new IntBag();

    @Before
    public void setUp() {
	bag.add(0);
	bag.add(1);
	bag.add(2);
    }

    @Test
    public void testCanUseIntBagOfToCreateIntBagOf3Elems() {
	IntBag b = IntBags.of(0, 1, 2);
	assertEquals(bag, b);
    }

    @Test
    public void testCanUseIntBagsOfToCreateIntBagOfNoElements() {
	IntBag b = IntBags.of();
	assertEquals(new IntBag(), b);
    }

    @Test
    public void testToListCreatesAppropriateListWith3ElementIntBag() {
	List<Integer> l = IntBags.toList(bag);
	assertEquals(3, l.size());
	assertEquals(0, l.get(0).intValue());
	assertEquals(1, l.get(1).intValue());
	assertEquals(2, l.get(2).intValue());
    }

    @Test
    public void testToListCreatesAppropriateListWith2ElementsTheSame() {
	bag.add(0);
	List<Integer> l = IntBags.toList(bag);
	assertEquals(4, l.size());
	assertEquals(0, l.get(0).intValue());
	assertEquals(1, l.get(1).intValue());
	assertEquals(2, l.get(2).intValue());
	assertEquals(0, l.get(3).intValue());
    }

    @Test
    public void testToListCreatesEmptyListOnEmptyIntBag() {
	IntBag b = new IntBag();
	List<Integer> l = IntBags.toList(b);
	assertTrue(l.isEmpty());
    }

    @Test
    public void testToListIgnoresUnfilledSpaceInIntBag() {
	IntBag b = new IntBag(64);
	b.add(12);
	List<Integer> l = IntBags.toList(b);
	assertEquals(1, l.size());
	assertEquals(12, l.get(0).intValue());
    }

    @Test
    public void testToSetCreatesAppropriateSetWith3ElementIntBag() {
	Set<Integer> s = IntBags.toSet(bag);
	assertEquals(3, s.size());
	assertTrue(s.contains(0));
	assertTrue(s.contains(1));
	assertTrue(s.contains(2));
    }

    @Test
    public void testToSetCreatesAppropriateSetWith2ElementsTheSame() {
	bag.add(0);
	Set<Integer> s = IntBags.toSet(bag);
	assertEquals(3, s.size());
	assertTrue(s.contains(0));
	assertTrue(s.contains(1));
	assertTrue(s.contains(2));
    }

    @Test
    public void testToSetCreatesEmptySetOnEmptyIntBag() {
	IntBag b = new IntBag();
	Set<Integer> s = IntBags.toSet(b);
	assertTrue(s.isEmpty());
    }

    @Test
    public void testToSetIgnoresUnfilledSpaceInIntBag() {
	IntBag b = new IntBag(64);
	b.add(12);
	Set<Integer> s = IntBags.toSet(b);
	assertEquals(1, s.size());
	assertTrue(s.contains(12));
    }

    @Test
    public void testFromCollectionWorksWithEmptyCollection() {
	List<Integer> l = new ArrayList<>();
	IntBag b = IntBags.fromCollection(l);
	assertTrue(b.isEmpty());
    }

    @Test
    public void testFromCollectionWith3Elements() {
	List<Integer> l = Lists.newArrayList(1, 2, 3);
	IntBag b = IntBags.fromCollection(l);
	assertEquals(3, b.size());
	assertEquals(1, b.get(0));
	assertEquals(2, b.get(1));
	assertEquals(3, b.get(2));
    }

    @Test
    public void testToIntArrayWith3Elements() {
	IntArray a = IntBags.toIntArray(bag);
	assertEquals(3, a.size);
	assertEquals(0, a.get(0));
	assertEquals(1, a.get(1));
	assertEquals(2, a.get(2));
    }

    @Test
    public void testToIntArrayIgnoreEmptySpaceInBag() {
	IntBag b = new IntBag(3212);
	b.add(-4);
	IntArray a = IntBags.toIntArray(b);
	assertEquals(1, a.size);
	assertEquals(-4, a.get(0));
    }

    @Test
    public void testFromIntArrayWithUnfilledSpaceInArray() {
	IntArray a = new IntArray(128);
	a.add(45);
	IntBag b = IntBags.fromIntArray(a);
	assertEquals(1, b.size());
	assertEquals(45, b.get(0));
    }

}
