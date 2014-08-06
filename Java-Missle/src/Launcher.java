import java.io.IOException;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Launcher extends Thread {

	public static final int 	MIN_REVEAL = 1;
	public static final int 	MAX_REVEAL = 5;

	private static Logger 		logger;

	private String 				id;
	private boolean 			isHidden;
	private boolean 			isRunning;
	private Vector<Missile> 	missiles;
	private FileHandler 		fileHandler;

	/**
	 * Constructor 
	 * @param id
	 * @param isHidden
	 * @param missiles
	 * @throws SecurityException
	 * @throws IOException
	 */
	public Launcher(String id, boolean isHidden, Vector<Missile> missiles)
			throws SecurityException, IOException {
		super();
		this.id = id;
		this.isHidden = true;
		this.missiles = missiles;
		this.isRunning = true;

		fileHandler = new FileHandler("Launcher_" + this.id + ".txt", false);
		fileHandler.setFilter(new ObjectFilter(this));
		fileHandler.setFormatter(new MyFormatter());
		logger = Logger.getLogger("warLogger");
		logger.addHandler(this.fileHandler);
	}

	/**
	 * Constructor 
	 * @param id
	 * @param isHidden
	 * @throws SecurityException
	 * @throws IOException
	 */
	public Launcher(String id, boolean isHidden) throws SecurityException,
	IOException {
		super();
		this.id = id;
		this.isHidden = isHidden;
		this.missiles = new Vector<Missile>();
		this.isRunning = true;

		fileHandler = new FileHandler("Launcher_" + this.id + ".txt", false);
		fileHandler.setFilter(new ObjectFilter(this));
		fileHandler.setFormatter(new MyFormatter());
		logger = Logger.getLogger("warLogger");
		logger.addHandler(this.fileHandler);	
	}

	/** Return id of launcher */ 
	public String getLauncherId() {
		return id;
	}

	/** Return the status of launcher 
	 * true = hidden
	 * false = no hidden */
	public boolean isHidden() {
		return isHidden;
	}

	/** Return the status of launcher 
	 * true = running 
	 * false = not running */
	public boolean isRunning() {
		return isRunning;
	}
	/** Return the file handler */  
	public FileHandler getFileHandler() {
		return fileHandler;
	}

	/** Return vector of missiles */
	public Vector<Missile> getMissiles() {
		return missiles;
	}

	/** The method changes status running to false */
	public void stopLauncher() {
		this.isRunning = false;
	}

	/**
	 * The method add missile to selected launcher
	 * @param id
	 * @param destination
	 * @param launchtime
	 * @param flytime
	 * @param damage
	 */
	public void addMissile(String id, String destination, int launchtime,
			int flytime, int damage) {
		Missile missile = new Missile(id, destination, launchtime, 
				flytime, damage, this.fileHandler, this);
		this.missiles.add(missile);
		synchronized (this) {
			this.notify();
		}
	}

	/**
	 * Reveals the launcher and makes it not hidden
	 */
	public void revealYourSelf() {
		this.isHidden = false;
	}
	
	/**
	 * Makes the launcher hidden
	 */
	public void hideYourSelf() {
		this.isHidden = true;
	}

	/** Run launcher */ 
	public void run() {
		try {
			int size = this.getMissiles().size();
			synchronized (this) {
				//fix for new launcher 
				if (size == 0) {
					wait();
					size = this.getMissiles().size();
				}
				for(int i = 0; i < size; i++) {
					this.getMissiles().get(i).start();		
					if (i == size-1) {
						wait();
						size = this.getMissiles().size();
					}
				}
			}
		}
		catch (InterruptedException e) {
		}
	}
}
