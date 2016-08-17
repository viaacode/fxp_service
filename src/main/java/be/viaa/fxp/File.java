package be.viaa.fxp;

/**
 * Represents a file on a remote FTP host
 * @author Hannes Lowette
 *
 */
public class File {

	/**
	 * The directory of the file
	 */
	private final String directory;
	
	/**
	 * The name of the file
	 */
	private final String name;

	/**
	 * @param directory
	 * @param name
	 */
	public File(String directory, String name) {
		this.directory = directory;
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "File [directory=" + directory + ", name=" + name + "]";
	}

	/**
	 * Gets a new file in the same directory
	 * 
	 * @param filename
	 * @return
	 */
	public File derive(String filename) {
		return new File(directory, filename);
	}

	/**
	 * @return the directory
	 */
	public String getDirectory() {
		return directory;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

}
