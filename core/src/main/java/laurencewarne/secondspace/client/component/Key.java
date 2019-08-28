package laurencewarne.secondspace.client.component;

import static com.artemis.annotations.LinkPolicy.Policy.CHECK_SOURCE_AND_TARGETS;

import com.artemis.Component;
import com.artemis.annotations.EntityId;
import com.artemis.annotations.LinkPolicy;
import com.artemis.utils.IntBag;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a keybinding, binds the key specified by "key" to the activation of the entities in "entitiesToActivate".
 */
@ToString
public class Key extends Component {
    @Getter @Setter
    private int key;
    @EntityId @LinkPolicy(CHECK_SOURCE_AND_TARGETS)
    public IntBag entitiesToActivate = new IntBag();
}
