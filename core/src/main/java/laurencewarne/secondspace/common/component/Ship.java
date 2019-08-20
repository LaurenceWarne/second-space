package laurencewarne.secondspace.common.component;

import static com.artemis.annotations.LinkPolicy.Policy.CHECK_SOURCE_AND_TARGETS;

import com.artemis.Component;
import com.artemis.annotations.EntityId;
import com.artemis.annotations.LinkPolicy;
import com.artemis.utils.IntBag;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(callSuper=false) @ToString
public class Ship extends Component {
    @EntityId @LinkPolicy(CHECK_SOURCE_AND_TARGETS)
    public IntBag parts = new IntBag(16);
    @Getter @Setter
    private float x = 0f;
    @Getter @Setter    
    private float y = 0f;
}
