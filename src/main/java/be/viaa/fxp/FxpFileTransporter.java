package be.viaa.fxp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import be.viaa.FileTransporter;
import be.viaa.fxp.amqp.FxpException;
import be.viaa.fxp.model.File;
import be.viaa.fxp.model.Host;

/**
 * FXP implementation of a file transporter
 * 
 * @author Hannes Lowette
 *
 */
public class FxpFileTransporter implements FileTransporter {

	/**
	 * The maximum amount of files that can be present on the given location
	 */
	private static final int MAX_FILES_LOCATION = 10;
	
	/**
	 * The maximum amount of retry attempts
	 */
	private static final int MAX_RETRY_ATTEMPTS = 10;
	
	/**
	 * The amount of times the file size equal check needs to verify
	 */
	private static final int FILESIZE_EQUAL_CHECKS = 10;
	
	/**
	 * The interval in between 2 file size checks
	 */
	private static final long FILESIZE_COMPARE_INTERVAL = 30000L;
	
	/**
	 * The executor service
	 */
	private final ExecutorService executor = Executors.newCachedThreadPool();

	@Override
	public void transfer(File sourceFile, File destinationFile, Host source, Host destination, boolean move) throws IOException {
		FxpStatus status = FxpStatus.RUNNING;
		
		/*
		 * Attempt 10 times to transfer the file correctly
		 */
		for (int attempt = 0; attempt < MAX_RETRY_ATTEMPTS; attempt++) {
			if (transfer(sourceFile, destinationFile, source, destination) == FxpStatus.OK) {
				break;
			}
			else if (attempt + 1 >= MAX_RETRY_ATTEMPTS) {
				throw new IOException("file could not be transferred successfully");
			}
		}
		
		/*
		 * If move is set to true, the source file needs to be deleted
		 */
		if (FxpStatus.OK.equals(status) && move) {
			delete(sourceFile, source);
		}
	}

	@Override
	public void move(File source, File destination, Host host) throws IOException {
		
	}

	@Override
	public void delete(File file, Host host) throws IOException {
		
	}

	/**
	 * Renames a file on the remote FTP client
	 * 
	 * @param source
	 * @param destination
	 * @param client
	 * @throws IOException
	 */
	public void rename(File source, File destination, FTPClient client) throws IOException {
		
	}
	
	/**
	 * A single attempt at transferring a file between 2 FTP endpoints using the FXP protocol
	 * 
	 * @param sourceFile
	 * @param destinationFile
	 * @param source
	 * @param destination
	 * @return
	 * @throws Exception
	 */
	private FxpStatus transfer(File sourceFile, File destinationFile, Host source, Host destination) throws IOException {
		FTPClient client_source = new FTPClient();
		FTPClient client_target = new FTPClient();
		
		File partFile = destinationFile.derive(destinationFile.getName() + ".part");
		
		try {
			/*
			 * Connect to the FTP server that hosts the source file
			 */
			client_source.connect(source.getHost(), source.getPort());
			if (client_source.login(source.getUsername(), source.getPassword())) {
				throw new IOException("invalid credentials for the source FTP");
			}
			
			/*
			 * Connect to the FTP server that hosts the destination file
			 */
			client_target.connect(destination.getHost(), destination.getPort());
			if (!client_target.login(destination.getUsername(), destination.getPassword())) {
				throw new IOException("invalid credentials for the destination FTP");
			}
	
			/*
			 * Send both of the FTP hosts to see if they are reachable
			 */
			if (!client_source.isConnected() || !client_source.isAvailable() || !client_target.isConnected() || !client_target.isAvailable()) {
				throw new IOException("ftp connection not available");
			}
            
            /*
             * Check to see if the source file exists
             */
			client_source.changeWorkingDirectory(sourceFile.getDirectory());
			if (!Arrays.asList(client_source.listNames()).contains(sourceFile.getName())) {
				throw new FileNotFoundException("could not find file " + sourceFile.getDirectory() + "/" + sourceFile.getName() + " on the FTP server");
			}
			
			/*
			 * Check to see if the destination has enough free space
			 */
			client_target.changeWorkingDirectory(destinationFile.getDirectory());
            if (client_target.listFiles().length >= MAX_FILES_LOCATION) {
                throw new FxpException("too many files in destination directory");
            }
			
			/*
			 * Transfer the file data
			 */
			createDirectoryTree(destinationFile, client_target);
			
			/*
			 * Send the commands that indicate a transaction needs to happen
			 */
			client_target.sendCommand("TYPE I");
			client_source.sendCommand("TYPE I");
			client_target.sendCommand("PASV");
			client_source.sendCommand("PORT " + filter(client_target.getReplyString()));

			/*
			 * Send the STOR and RETR commands to the corresponding FTP servers
			 * TODO: Find out why this needs to be on a different thread.
			 */
			executor.submit(new FxpCommandThread(client_target, "STOR " + partFile.getName()));
			executor.submit(new FxpCommandThread(client_source, "RETR " + sourceFile.getName()));
			
			/*
			 * Periodically check the size of the partfile and compare it to the size of the file that
			 * is being transferred.
			 */
			AtomicInteger counter = new AtomicInteger();
			AtomicLong current_size = new AtomicLong();
			AtomicLong expected_size = new AtomicLong(get(sourceFile, client_source).getSize());
			while (current_size.get() != expected_size.get() && counter.get() >= FILESIZE_EQUAL_CHECKS) {
				Thread.sleep(FILESIZE_COMPARE_INTERVAL);
				long filesize = get(partFile, client_target).getSize();
				if (filesize == current_size.get()) {
					// If these are equal, the file isn't transferring correctly
					counter.incrementAndGet();
				}
				current_size.set(filesize);
			}
			
			if (expected_size.get() != get(partFile, client_target).getSize()) {
				// TODO: the files have not successfully transferred
				return FxpStatus.ERROR;
			}
			else {
				rename(partFile, destinationFile, client_target);
			}
			return FxpStatus.OK;
		} catch (FxpException ex) {
			// TODO: There was something wrong with the FXP protocol
			ex.printStackTrace();
			return FxpStatus.ERROR;
		} catch (IOException ex) {
			// TODO: Connection to the FTP server has gone wrong
			ex.printStackTrace();
			return FxpStatus.ERROR;
		} catch (InterruptedException ex) {
			// TODO: This exception should never occur!
			ex.printStackTrace();
			return FxpStatus.ERROR;
		} finally {
			client_source.disconnect();
			client_target.disconnect();
		}
	}

	/**
	 * Attempts to create the directory structure of the given file
	 * 
	 * @param file
	 * @param client
	 * @return
	 * @throws IOException
	 */
	private boolean createDirectoryTree(File file, FTPClient client) throws IOException {
		Deque<String> directory_structure = new LinkedList<>(Arrays.asList(file.getDirectory().split("/")));
		Deque<String> directory_unexistant = new LinkedList<>();

		/*
		 * Scans to see which directory is already present and which directories
		 * need to be created.
		 */
		while (!directory_structure.isEmpty()) {
			if (!client.changeWorkingDirectory(String.join("/", directory_structure))) {
				directory_unexistant.addFirst(directory_structure.removeLast());
			} else
				break;
		}

		/*
		 * Creates the directories that need to be created
		 */
		for (Iterator<String> iterator = directory_unexistant.iterator(); iterator.hasNext();) {
			String directory = iterator.next();

			if (!client.makeDirectory(directory) || !client.changeWorkingDirectory(directory)) {
				throw new IOException("could not create directory tree");
			}
		}
		return true;
	}
	
	/**
	 * Support method that is used to retrieve the port number that is received
	 * in a string in between 2 parentheses
	 * 
	 * @param input
	 * @return
	 */
	private String filter(String input) {
		return input.substring(input.indexOf("(") + 1, input.indexOf(")"));
	}
	
	/**
	 * Gets a remote FTP file by its name
	 * 
	 * @param file
	 * @param client
	 * @return
	 */
	private FTPFile get(File file, FTPClient client) throws IOException {
		client.changeWorkingDirectory(file.getDirectory());
		FTPFile[] files = client.listFiles(file.getName());
		if (files == null || files.length == 0) {
			throw new FileNotFoundException();
		}
		if (files.length == 1) {
			return files[0];
		}
		throw new FileNotFoundException("ambiguous filename");
	}

}
