package laurencewarne.secondspace.server.system;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;

import laurencewarne.secondspace.server.component.Command;

@All(Command.class)
public class TerminalSystem extends BaseEntitySystem {

    private ComponentMapper<Command> mCommand;

    private final BlockingQueue<String> commandQueue= new ArrayBlockingQueue<>(16);

    @Override
    public void initialize() {
	new Thread() {
	    public void run() {
		final LineReader reader = LineReaderBuilder.builder()
		    .build();
		String line = null;
		while (true) {
		    try {
			line = reader.readLine("second-space> ");
			commandQueue.put(line);
		    } catch (UserInterruptException e) {
			// Ignore
		    } catch (EndOfFileException e) {
			return;
		    } catch (InterruptedException e){
			// Thread interrupted whilst waiting
			return;
		    }
		}
	    }
	}.start();
    }

    @Override
    public void processSystem() {
	if (!commandQueue.isEmpty()) {
	    String commandString = commandQueue.poll();
	    Command command = mCommand.create(world.create());
	    command.setCommandString(commandString);
	}
    }
}
