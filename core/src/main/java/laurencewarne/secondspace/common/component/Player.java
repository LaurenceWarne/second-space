package laurencewarne.secondspace.common.component;

import com.artemis.Component;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper=false)
public class Player extends Component {
    private String name;
}
