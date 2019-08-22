package laurencewarne.secondspace.client;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.common.component.network.RegistrationRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConnectionScreen implements Screen {

    private final Logger logger = LoggerFactory.getLogger(
	ConnectionScreen.class
    );    
    private Stage stage;    
    private TextField addressField;
    private TextField nameField;
    @NonNull
    private final Client client;

    @Override
    public void show() {
	stage = new Stage(new FillViewport(1600f, 900f));
	Gdx.input.setInputProcessor(stage);

	Skin uiSkin = new Skin(Gdx.files.internal("skins/default/uiskin.json"));
	Table table = new Table();
	table.setFillParent(true);
	stage.addActor(table);
	table.setDebug(true);

	nameField = new TextField("Dave", uiSkin);
	table.add(nameField);
	addressField = new TextField("127.0.0.1", uiSkin);
	table.add(addressField);
	
	TextButton button1 = new TextButton("Connect", uiSkin);
	button1.addListener(new ChangeListener() {
		@Override
		public void changed (ChangeEvent event, Actor actor) {
		    connect();
		}
	});
	table.add(button1);

	client.addListener(new Listener() {
		public void received (Connection connection, Object object) {
		    System.out.println(
			"Received from server: " +
			connection.getRemoteAddressTCP() +
			" object: " + object
		    );
		}
	});
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
    public void pause() {
		
    }

    @Override
    public void resume() {
		
    }

    @Override
    public void hide() {
		
    }

    @Override
    public void dispose() {
	stage.dispose();
    }

    public boolean connect() {
	final String host = addressField.getText();
	client.start();
	try {
	    client.connect(5000, host, 54555, 54777);
	    RegistrationRequest req = new RegistrationRequest();
	    req.setName(nameField.getText());
	    client.sendTCP(req);
	} catch (IOException e) {
	    logger.error("Could not connect to server: " + e.getMessage());
	}
	return false;
    }
}
