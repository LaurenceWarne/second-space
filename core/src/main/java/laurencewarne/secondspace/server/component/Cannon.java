package laurencewarne.secondspace.server.component;

import com.artemis.Component;
import com.artemis.annotations.Transient;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * If an entity has this component, it can shoot bullets.
 */
@Data @EqualsAndHashCode(callSuper=false)
public class Cannon extends Component {
    /** Cooldown between consecutive shots.*/
    private float coolDown = 1f;
    private float currentCoolDown = 0f;
    private float force = 10f;
    private float localBulletSourceX = 0f;
    private float localBulletSourceY = 0f;
    private float bulletDamage = 1f;
    /** Side length of the bullet (all bullets are square).*/
    private float bulletSize = 0.1f;
    private float bulletDensity = 1f;
    private float bulletFriction = 0.4f;
    private float bulletRestitution = 0.6f;

    /**
     * Indicates cannon has been activated.
     */
    @Transient
    public static class CannonActivated extends Component {
	
    }
}
						 
