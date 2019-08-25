package laurencewarne.secondspace.client.screen;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * @author Mats Svensson
 */
public class LoadingBar extends Actor {

    Animation<TextureRegion> animation;
    TextureRegion reg;
    float stateTime;

    public LoadingBar(Animation<TextureRegion> animation) {

        this.animation = animation;
        reg = animation.getKeyFrame(0);
    }

    @Override
    public void act( float delta ) {

        stateTime += delta;
        reg = animation.getKeyFrame(stateTime);
    }

    @Override
    public void draw( Batch batch, float parentAlpha ) {

        batch.draw(reg, getX(), getY());
    }
}
