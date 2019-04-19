package be.viaa.fxp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

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
    private static final long FILESIZE_COMPARE_INTERVAL = 10000L;

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
            if ((status = transferSingle(sourceFile, destinationFile, source, destination)) == FxpStatus.OK) {
                break;
            }
            else if (attempt + 1 >= MAX_RETRY_ATTEMPTS) {
                throw new IOException("file could not be transferred");
            }
            else {
                logger.info("RETRY {}", attempt);
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
                logger.info("SUCCESS: moved file from directory '{}' to '{}'", sourceFile.getDirectory(), destinationFile.getDirectory());
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
             * Check to see if the source file exists and whether it' a file or a directory
             */
            client.changeWorkingDirectory(file.getDirectory());
            boolean isDirectory = false;
            if (containsFile(Arrays.asList(client.listDirectories()), file.getName())) {
                // This is a directory
                isDirectory = true;
            } else {
                // This might be a file

                if (!containsFile(Arrays.asList(client.listFiles()), file.getName())) {
                    // No. Nothing exists
                    throw new FileNotFoundException("could not find file " + file.getDirectory() + "/" + file.getName() + " on the FTP server");
                }
            }
			
			/*
			 * Delete the file or directory from the remote FTP server and return the OK status when successful
			 */
            if (!isDirectory) {
                if (!client.deleteFile(file.getName())) {
                    throw new IOException("could not delete " + file.getDirectory() + "/" + file.getName());
                }
                else {
                    logger.info("SUCCESS: file {}/{} successfully deleted", file.getDirectory(), file.getName());
                }
            } else {
                // We must delete a directory. Recursively delete all files within it
                removeDirectory(client, file.getDirectory(), file.getName());
            }
        } finally {
            client.disconnect();
        }
    }

    public static boolean containsFile(List<FTPFile> files, String file) {
        for (FTPFile ftpFile : files) {
            if (ftpFile.getName().equals(file)) {
                return true;
            }
        }
        return false;
    }

    public static void removeDirectory(FTPClient ftpClient, String parentDir,
                                       String currentDir) throws IOException {
        String dirToList = parentDir;
        if (!currentDir.equals("")) {
            dirToList += "/" + currentDir;
        }

        FTPFile[] subFiles = ftpClient.listFiles(dirToList);

        if (subFiles != null && subFiles.length > 0) {
            for (FTPFile aFile : subFiles) {
                String currentFileName = aFile.getName();
                if (currentFileName.equals(".") || currentFileName.equals("..")) {
                    // skip parent directory and the directory itself
                    continue;
                }
                String filePath = parentDir + "/" + currentDir + "/"
                        + currentFileName;
                if (currentDir.equals("")) {
                    filePath = parentDir + "/" + currentFileName;
                }

                if (aFile.isDirectory()) {
                    // remove the sub directory
                    removeDirectory(ftpClient, dirToList, currentFileName);
                } else {
                    // delete the file
                    boolean deleted = ftpClient.deleteFile(filePath);
                    if (deleted) {
                        logger.info("DELETED the file: " + filePath);
                    } else {
                        logger.info("CANNOT delete the file: "
                                + filePath);
                        throw new IOException("CANNOT delete the file: "
                                + filePath);
                    }
                }
            }

            // finally, remove the directory itself
            boolean removed = ftpClient.removeDirectory(dirToList);
            if (removed) {
                logger.info("REMOVED the directory: " + dirToList);
            } else {
                logger.info("CANNOT remove the directory: " + dirToList);
                throw new IOException("CANNOT remove the directory: " + dirToList);
            }
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
            logger.info("SUCCESS: renamed file '{}/{}'", source.getDirectory(), source.getName());
        } finally {
            client.disconnect();
        }
    }

    private FxpStatus transferSingle(File sourceFile, File destinationFile, Host source, Host destination) throws IOException {
        FTPClient sourceClient = connect(source);
        FTPClient targetClient = connect(destination);
        Future<?> future_source = null;
        Future<?> future_destination = null;
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
            future_destination = executor.submit(new FxpCommandThread(targetClient, "STOR " + partFile.getName()));
            future_source = executor.submit(new FxpCommandThread(sourceClient, "RETR " + sourceFile.getName()));
			
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
                logger.warn("ERROR: could not transfer file {}/{}", sourceFile.getDirectory(), sourceFile.getName());
                return FxpStatus.ERROR;
            }
			
			/*
			 * If they aren't, we can assume the files are equal
			 */
            else {
                rename(partFile, destinationFile, destination);
                logger.info("SUCCESS: file {}/{} transferred", sourceFile.getDirectory(), sourceFile.getName());
                return FxpStatus.OK;
            }
        } catch (Exception ex) {
            logger.catching(ex);
            return FxpStatus.ERROR;
        } finally {
            sourceClient.disconnect();
            targetClient.disconnect();
            future_source.cancel(true);
            future_destination.cancel(true);
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
        Deque<String> directoryStructure = new LinkedList<>(Arrays.asList(file.getDirectory().split("/"))
            .stream()
            .filter(dir -> !dir.isEmpty())
            .collect(Collectors.toList()));
        Deque<String> directoryUnexistant = new LinkedList<>();

        logger.debug("creating directory tree for {}", file.getDirectory());
		
		/*
		 * Scans to see which directory is already present and which directories
		 * need to be created.
		 */
        while (!directoryStructure.isEmpty()) {
            // If path starts with a /, add it back when changing directory (since it was removed with the filter above)
            if (!client.changeWorkingDirectory((file.getDirectory().startsWith("/") ? "/" : "") + Strings.join("/", directoryStructure))) {
                directoryUnexistant.addFirst(directoryStructure.removeLast());
            } else {
                break;
            }
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
     * @param host
     * @return
     * @throws IOException
     */
    private FTPClient connect(Host host) throws IOException {
        FTPClient client = new FTPClient();
        client.setControlEncoding("UTF8");
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
     * @param host
     * @return
     */
    private FTPFile get(File file, Host host) throws IOException {
        FTPClient client = connect(host);
        try {
            logger.debug("looking up file {}/{} on {}:{}", file.getDirectory(), file.getName(), host.getHost(), host.getPort());
            client.changeWorkingDirectory(file.getDirectory());
            FTPFile[] files = client.listFiles(file.getName());
            if (files == null || files.length == 0) {
                throw new FileNotFoundException("file '" + file.getDirectory() + "/" + file.getName() + "' not found on '" + client.getRemoteAddress().getHostAddress() + "'");
            }
            if (files.length == 1) {
                return files[0];
            }
            throw new FileNotFoundException("ambiguous filename");
        } finally {
            client.disconnect();
        }
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