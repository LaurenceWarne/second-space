package laurencewarne.secondspace.common.system;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import laurencewarne.secondspace.common.component.Command;

@All(Command.class)
public class TerminalSystem extends BaseEntitySystem {

    private final Logger logger = LoggerFactory.getLogger(TerminalSystem.class);
    private ComponentMapper<Command> mCommand;

    private final BlockingQueue<String> commandQueue= new ArrayBlockingQueue<>(16);

    @Override
    public void initialize() {
	logger.debug("Starting line reader in new thread");
	new Thread() {
	    public void run() {
		final LineReaderBuilder readerBuilder = LineReaderBuilder.builder();
		try {
		    Terminal terminal = TerminalBuilder.builder().dumb(true).build();
		    readerBuilder.terminal(terminal);
		} catch (IOException e1) {
		    // Could no build terminal, but we don't really care
		}
		final LineReader reader = readerBuilder.build();
		while (true) {
		    try {
			String line = reader.readLine("second-space> ");
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
	    final String commandString = commandQueue.poll();
	    final Command command = mCommand.create(world.create());
	    command.setCommandString(commandString);
	}
    }
}
