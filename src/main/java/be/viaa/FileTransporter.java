package be.viaa;

import java.io.IOException;

import be.viaa.fxp.model.File;
import be.viaa.fxp.model.Host;

/**
 * 
 * @author Hannes Lowette
 *
 */
public interface FileTransporter {

	/**
	 * Transfer a file from the source host to the destination host, removing the source file if required
	 * 
	 * @param file
	 * @param source
	 * @param destination
	 * @param move
	 */
	void transfer(File sourceFile, File destinationFile, Host source, Host destination, boolean move) throws IOException;

	/**
	 * Moves a file from one place to the other on the given host
	 * 
	 * @param source
	 * @param destination
	 * @param host
	 */
	void move(File source, File destination, Host host) throws IOException;
	
	/**
	 * Deletes a file from the given host
	 * 
	 * @param file
	 * @param host
	 */
	void delete(File file, Host host) throws IOException;

}
