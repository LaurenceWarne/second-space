package laurencewarne.secondspace.common.component;

import com.artemis.Component;
import com.artemis.annotations.DelayedComponentRemoval;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@DelayedComponentRemoval @Getter @Setter @ToString
public class ComponentY extends Component {
    public float f;
}
