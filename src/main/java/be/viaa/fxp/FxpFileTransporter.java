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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import be.viaa.util.Strings;

/**
 * FXP implementation of a file transporter
 * 
 * @author Hannes Lowette
 *
 */
public class FxpFileTransporter implements FileTransporter {
	
	/**
	 * The logger for this class
	 */
	private static final Logger logger = LogManager.getLogger(FxpFileTransporter.class);
	
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
		 * Debug information
		 */
		logger.debug("Attempting file transfer");
		logger.debug("Source Host: {}:{}", source.getHost(), source.getPort());
		logger.debug("Target Host: {}:{}", source.getHost(), source.getPort());
		logger.debug("Source file: {}/{}", sourceFile.getDirectory(), sourceFile.getName());
		logger.debug("Target file: {}/{}", destinationFile.getDirectory(), destinationFile.getName());
		
		/*
		 * Attempt 10 times to transfer the file correctly
		 */
		for (int attempt = 0; attempt < MAX_RETRY_ATTEMPTS; attempt++) {
			if (transferSingle(sourceFile, destinationFile, source, destination) == FxpStatus.OK) {
				break;
			}
			else if (attempt + 1 >= MAX_RETRY_ATTEMPTS) {
				throw new IOException("file could not be transferred");
			}
			try {
				Thread.sleep(10000L);
			} catch (Exception ex) {
				ex.printStackTrace(); // This should never happen
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
	public void move(File sourceFile, File destinationFile, Host host) throws IOException {
		FTPClient client = connect(host);
		try {
			logger.debug("attempting to move file");
			logger.debug("Host: {}", host.getHost());
			logger.debug("Directory: {}", sourceFile.getDirectory());
			logger.debug("Source filename: {}", sourceFile.getName());
			logger.debug("Target filename: {}", destinationFile.getName());
			
//			/*
//			 * Send the commands that indicate a transaction needs to happen
//			 */
//			client.sendCommand("TYPE I");
//
//			/*
//			 * Send the STOR and RETR commands to the corresponding FTP servers
//			 * TODO: Find out why this needs to be on a different thread.
//			 */
//			executor.submit(new FxpCommandThread(client, "RNFR " + sourceFile.getDirectory() + "/" + sourceFile.getName()));
//			executor.submit(new FxpCommandThread(client, "RNTO " + destinationFile.getDirectory() + "/" + destinationFile.getName()));
			if (client.rename(sourceFile.getDirectory() + "/" + sourceFile.getName(), destinationFile.getDirectory() + "/" + destinationFile.getName())) {
				logger.info("successfully moved file from directory '{}' to '{}'", sourceFile.getDirectory(), destinationFile.getDirectory());
			}
			else {
				throw new IOException("could not move file '" + sourceFile.getDirectory() + "/" + sourceFile.getName() + "' - " + client.getReplyString());
			}
		} catch (IOException ex) {
			// TODO: Connection to the FTP server has gone wrong
			logger.catching(ex);
		} finally {
			client.disconnect();
		}
	}

	@Override
	public void delete(File file, Host host) throws IOException {
		FTPClient client = connect(host);

		try {
			logger.debug("attempting to delete file");
			logger.debug("Host: {}:{}", host.getHost(), host.getPort());
			logger.debug("File: {}/{}", file.getDirectory(), file.getName());
			
            /*
             * Check to see if the source file exists
             */
			client.changeWorkingDirectory(file.getDirectory());
			if (!Arrays.asList(client.listNames()).contains(file.getName())) {
				throw new FileNotFoundException("could not find file " + file.getDirectory() + "/" + file.getName() + " on the FTP server");
			}
			
			/*
			 * Delete the file from the remote FTP server and return the OK status when successful
			 */
			if (!client.deleteFile(file.getName())) {
				throw new IOException("could not delete " + file.getDirectory() + "/" + file.getName());
			}
		} catch (IOException ex) {
			// TODO: Connection to the FTP server has gone wrong
			logger.catching(ex);
		} finally {
			client.disconnect();
		}
	}

	@Override
	public void rename(File source, File destination, Host host) throws IOException {
		FTPClient client = connect(host);
		
		logger.info("attempting to rename file '{}/{}'", source.getDirectory(), source.getName());
		logger.debug("Host: {}:{}", host.getHost(), host.getPort());
		logger.debug("New: {}/{}", destination.getDirectory(), destination.getName());

		try {
			client.changeWorkingDirectory(source.getDirectory());
			client.rename(source.getName(), destination.getName());
			logger.info("successfully renamed file '{}/{}'", source.getDirectory(), source.getName());
		} finally {
			client.disconnect();
		}
	}

	private FxpStatus transferSingle(File sourceFile, File destinationFile, Host source, Host destination) throws IOException {
		FTPClient sourceClient = connect(source);
		FTPClient targetClient = connect(destination);
		File partFile = destinationFile.derive(destinationFile.getName() + ".part");
		
		try {
			/*
			 * Send the commands that indicate a transaction needs to happen
			 * TODO: Rework to use short commands?
			 */
			targetClient.sendCommand("TYPE I");
			sourceClient.sendCommand("TYPE I");
			targetClient.sendCommand("PASV");
			sourceClient.sendCommand("PORT " + filterPortNumber(targetClient.getReplyString()));
			
			/*
			 * Transfer the file data
			 */
			createDirectoryTree(destinationFile, targetClient);
			createDirectoryTree(sourceFile, sourceClient);
			
			/*
			 * Send the STOR and RETR commands to the corresponding FTP servers.
			 */
			logger.debug("sending store and retreive commands");
			executor.submit(new FxpCommandThread(targetClient, "STOR " + partFile.getName()));
			executor.submit(new FxpCommandThread(sourceClient, "RETR " + sourceFile.getName()));
			
			/*
			 * Periodically check the size of the partfile and compare it to the size of the file that
			 * is being transferred.
			 */
			AtomicInteger counter = new AtomicInteger();
			AtomicLong currentSize = new AtomicLong();
			AtomicLong expectedSize = new AtomicLong(get(sourceFile, source).getSize());
			while (currentSize.get() != expectedSize.get() && counter.get() <= FILESIZE_EQUAL_CHECKS) {
				Thread.sleep(FILESIZE_COMPARE_INTERVAL);
				long filesize = get(partFile, destination).getSize();
				if (filesize == currentSize.get()) {
					// If these are equal, the file hasn't progressed at all in 30 seconds
					counter.incrementAndGet();
				}
				
				currentSize.set(filesize);
				logger.info("Transfer progress: {}", ((float) currentSize.get() * 100) / ((float) expectedSize.get()));
			}
			
			/*
			 * Check to see if the file sizes are different
			 */
			if (expectedSize.get() != get(partFile, destination).getSize()) {
				// TODO: the files have not successfully transferred
				logger.warn("could not transfer file {}/{}", sourceFile.getDirectory(), sourceFile.getName());
				return FxpStatus.ERROR;
			}
			
			/*
			 * If they aren't, we can assume the files are equal
			 */
			else {
				rename(partFile, destinationFile, destination);
				logger.info("file transferred successfully");
				return FxpStatus.OK;
			}
		} catch (Exception ex) {
			logger.catching(ex);
			return FxpStatus.ERROR;
		} finally {
			sourceClient.disconnect();
			targetClient.disconnect();
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
		Deque<String> directoryStructure = new LinkedList<>(Arrays.asList(file.getDirectory().split("/")));
		Deque<String> directoryUnexistant = new LinkedList<>();

		logger.debug("creating directory tree for {}", file.getDirectory());
		
		/*
		 * Scans to see which directory is already present and which directories
		 * need to be created.
		 */
		while (!directoryStructure.isEmpty()) {
			if (!client.changeWorkingDirectory(Strings.join("/", directoryStructure))) {
				directoryUnexistant.addFirst(directoryStructure.removeLast());
			} else break;
		}
		
		logger.debug("folders to be created: {}", Strings.join("/", directoryUnexistant));

		/*
		 * Creates the directories that need to be created
		 */
		for (Iterator<String> iterator = directoryUnexistant.iterator(); iterator.hasNext();) {
			String directory = iterator.next();

			if (!client.makeDirectory(directory) || !client.changeWorkingDirectory(directory)) {
				throw new IOException("could not create directory tree");
			}
		}
		return true;
	}

	/**
	 * 
	 * @param client
	 * @param host
	 * @return
	 * @throws IOException
	 */
	private FTPClient connect(Host host) throws IOException {
		FTPClient client = new FTPClient();
		try {
			logger.debug("connecting to {}:{}", host.getHost(), host.getPort());
			
			/*
			 * Attempt to establish a connection
			 */
			client.connect(host.getHost(), host.getPort());
			client.enterLocalPassiveMode();
			logger.debug("made connection with {}:{}", host.getHost(), host.getPort());
			
			/*
			 * Attempt to authenticate on the remote server
			 */
			if (!client.login(host.getUsername(), host.getPassword())) {
				throw new IOException("invalid credentials for the source FTP");
			}
			logger.debug("successfully authenticated with {}:{}", host.getHost(), host.getPort());
	
			/*
			 * Send both of the FTP hosts to see if they are reachable
			 */
			if (!client.isConnected() || !client.isAvailable()) {
				throw new IOException("ftp connection not available");
			}
			return client;
		} catch (Exception ex) {
			if (client.isConnected())
				client.disconnect();
			throw new IOException("could not connect to " + host.getHost() + ":" + host.getPort(), ex);
		}
	}
	
	/**
	 * Gets a remote FTP file by its name
	 * 
	 * @param file
	 * @param client
	 * @return
	 */
	private FTPFile get(File file, Host host) throws IOException {
		logger.debug("looking up file {}/{} on {}:{}", file.getDirectory(), file.getName(), host.getHost(), host.getPort());
		FTPClient client = connect(host);
		client.changeWorkingDirectory(file.getDirectory());
		FTPFile[] files = client.listFiles(file.getName());
		if (files == null || files.length == 0) {
			throw new FileNotFoundException("file '" + file.getDirectory() + "/" + file.getName() + "' not found on '" + client.getRemoteAddress().getHostAddress() + "'");
		}
		if (files.length == 1) {
			return files[0];
		}
		throw new FileNotFoundException("ambiguous filename");
	}
	
	/**
	 * Support method that is used to retrieve the port number that is received
	 * in a string in between 2 parentheses
	 * 
	 * @param input
	 * @return
	 */
	private final String filterPortNumber(String input) {
		return input.substring(input.indexOf("(") + 1, input.indexOf(")"));
	}

}
