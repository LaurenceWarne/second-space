package laurencewarne.secondspace.client.screen;

import java.io.IOException;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
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
import com.esotericsoftware.kryonet.Listener.TypeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.client.component.ClientPlayer;
import laurencewarne.secondspace.client.manager.IdTranslatorManager;
import laurencewarne.secondspace.common.component.network.NetworkConnection;
import laurencewarne.secondspace.common.component.network.RegistrationRequest;
import laurencewarne.secondspace.common.component.network.RegistrationResponse;
import libgdxscreencontrol.screen.ITransitionScreen;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConnectionScreen extends ScreenAdapter implements ITransitionScreen {

    private final Logger logger = LoggerFactory.getLogger(
	ConnectionScreen.class
    );    
    private Stage stage;    
    private TextField addressField;
    private TextField nameField;
    private Client client;
    @NonNull
    private final WorldConfiguration setup;
    @Getter
    private World world;
    @Getter
    private boolean isConnected = false;

    @Override
    public void show() {
	client = new Client();
	setup.register("client", client);
	setup.register("kryo", client.getKryo());
	final TypeListener listener = new TypeListener();
	client.addListener(listener);
	setup.register(listener);

	listener.addTypeHandler(RegistrationResponse.class, (conn, response) -> {
		isConnected = true;
		int playerId = response.getPlayerId();
		int clientId = world.getSystem(IdTranslatorManager.class)
		    .translate(playerId);
		world.getMapper(ClientPlayer.class).create(clientId)
		    .setServerId(playerId);
		world.getMapper(NetworkConnection.class).create(clientId)
		    .setConnection(conn);
	    }
	);
	
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
	world = new World(setup);
	client.start();
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
    public void dispose() {
	stage.dispose();
    }

    @Override
    public boolean isFinished() {
	return isConnected;
    }

    @Override
    public void reset() {
	this.isConnected = false;
    }    

    public void connect() {
	final String host = addressField.getText();
	try {
	    client.connect(5000, host, 54555, 54777);
	    RegistrationRequest req = new RegistrationRequest();
	    req.setName(nameField.getText());
	    client.sendTCP(req);
	} catch (IOException e) {
	    logger.error("Could not connect to server: " + e.getMessage());
	}
    }
}
