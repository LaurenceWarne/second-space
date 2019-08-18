package laurencewarne.secondspace.common.component;

import com.artemis.Component;
import com.artemis.annotations.EntityId;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper=false)
public class Bullet extends Component {
    @EntityId
    public int sourceId = -1;
}
