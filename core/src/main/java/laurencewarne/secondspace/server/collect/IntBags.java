package laurencewarne.secondspace.server.collect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.IntArray;

import lombok.NonNull;

/**
 * Utility class for working with {@link IntBag}s.
 */
public final class IntBags {

    private IntBags() {
	
    }

    public static IntBag of(int... integers) {
	final IntBag bag = new IntBag(integers.length);
	Arrays.stream(integers).forEach(n -> bag.add(n));
	return bag;
    }

    public static List<Integer> toList(@NonNull IntBag bag) {
	final List<Integer> list = new ArrayList<>();
	for (int i = 0; i < bag.size(); i++){
	    list.add(bag.get(i));
	}
	return list;
    }

    public static Set<Integer> toSet(@NonNull IntBag bag) {
	final Set<Integer> set = new HashSet<>();
	for (int i = 0; i < bag.size(); i++){
	    set.add(bag.get(i));
	}
	return set;
    }

    public static IntArray toIntArray(@NonNull IntBag bag) {
	final IntArray array = new IntArray(bag.size());
	for (int i = 0; i < bag.size(); i++){
	    array.add(bag.get(i));
	}
	return array;
    }

    public static IntBag fromCollection(
	@NonNull Collection<? extends Integer> collection) {
	final IntBag bag = new IntBag(collection.size());
	collection.stream().forEach(n -> bag.add(n));
	return bag;
    }

    public static IntBag fromIntArray(@NonNull IntArray intArray) {
	final IntBag bag = new IntBag(intArray.size);
	for (int i = 0; i < intArray.size; i++){
	    bag.add(intArray.get(i));
	}
	return bag;
    }
}
