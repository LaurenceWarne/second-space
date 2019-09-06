package laurencewarne.secondspace.client.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;

import lombok.NonNull;

public class MenuScreen extends ScreenAdapter implements IProgressScreen {

    @NonNull
    private Stage stage;

    @Override
    public void show() {
	//stage = new Stage(new FillViewport(800f, 450f));
	stage = new Stage(new FitViewport(960f, 540f));
	Gdx.input.setInputProcessor(stage);

	final Skin skin = new Skin(Gdx.files.internal("skins/star-soldier/uiskin.json"));
	final Table rootTable = new Table(skin);
	//rootTable.setDebug(true);
	rootTable.setFillParent(true);
	final Table table = new Table(skin);
	table.background(skin.getDrawable("window"));
	rootTable.add(table).center();
	final Label titleLabel = new Label("Second Space", skin);
	titleLabel.setScale(2f);
	table.add(titleLabel).padTop(30f);
	table.row();
	table.add(new TextButton("Single Player", skin)).expandX();
	table.row();
	table.add(new TextButton("Multi Player", skin)).expandX();
	table.row();
	table.add(new TextButton("Options", skin)).expandX();
	table.row();
	table.add(new TextButton("Quit", skin)).expandX();

	stage.addActor(rootTable);
    }    

    @Override
    public void render(float delta) {
	Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	stage.act(delta);
	stage.draw();	    
    }

    @Override
    public void resize(int width, int height) {
	stage.getViewport().update(width, height, false);		
    }
    

    @Override
    public boolean isFinished() {
	return false;
    }

    @Override
    public void dispose() {

    }
}
