package be.viaa.fxp.amqp;

import java.text.SimpleDateFormat;
import java.util.Date;

import be.viaa.amqp.AmqpJsonConsumer;
import be.viaa.amqp.AmqpService;
import be.viaa.fxp.File;
import be.viaa.fxp.FileTransporter;
import be.viaa.fxp.FxpFileTransporter;
import be.viaa.fxp.Host;
import be.viaa.util.GsonUtil;
import com.rabbitmq.client.Channel;

/**
 * AMQP consumer for FXP messages
 *
 * @author Hannes Lowette
 *
 */
public class FxpConsumer extends AmqpJsonConsumer<FxpRequest> {

	/**
	 * The file transporter
	 */
	private final FileTransporter transporter = new FxpFileTransporter();

	/**
	 * Constructor to identify the class used in the JSON parser
	 */
	public FxpConsumer() {
		super(FxpRequest.class);
	}

	@Override
	public void accept(AmqpService service, FxpRequest message) throws Exception {
		File sourceFile = new File(message.getSourcePath(), message.getSourceFile());
		File targetFile = new File(message.getDestinationPath(), message.getDestinationFile());

		Host source = new Host(message.getSourceHost(), message.getSourceUser(), message.getSourcePassword());
		Host target = new Host(message.getDestinationHost(), message.getDestinationUser(), message.getDestinationPassword());

		transporter.transfer(sourceFile, targetFile, source, target, message.move());
	}

	@Override
	public void success(AmqpService service, FxpRequest message, Channel channel) throws Exception {
		FxpResponse response = new FxpResponse();

		response.setSourceHost(message.getSourceHost());
		response.setFilename(message.getSourceFile());
		response.setDirectory(message.getSourcePath());
		response.setDestinationHost(message.getDestinationHost());
		response.setDestinationFilename(message.getDestinationFile());
		response.setDestinationDirectory(message.getDestinationPath());
		response.setTimestamp(getTimestamp());
		response.setCorrelationId(message.getCorrelationId());
		response.setSourceFileRemoved(message.move());
		response.setOutcome("OK");

		String destqueue = (message.getDestQueue() == null || message.getDestQueue().isEmpty()) ? "fxp_responses" : message.getDestQueue();

		service.write(destqueue, GsonUtil.convert(response), channel);
	}

	@Override
	public void exception(AmqpService service, Exception exception, FxpRequest message, Channel channel) {
		FxpResponse response = new FxpResponse();

		response.setSourceHost(message.getSourceHost());
		response.setFilename(message.getSourceFile());
		response.setDirectory(message.getSourcePath());
		response.setDestinationHost(message.getDestinationHost());
		response.setDestinationFilename(message.getDestinationFile());
		response.setDestinationDirectory(message.getDestinationPath());
		response.setTimestamp(getTimestamp());
		response.setCorrelationId(message.getCorrelationId());
		response.setSourceFileRemoved(false);
		response.setOutcome("NOK");
		response.setComment(exception.getMessage());

		String destqueue = (message.getDestQueue() == null || message.getDestQueue().isEmpty()) ? "fxp_responses" : message.getDestQueue();
		exception.printStackTrace();

		try {
			service.write(destqueue, GsonUtil.convert(response), channel);
		} catch (Exception ex) {
			// TODO: This exception needs to be monitored closely and logged pretty well, it means the queue
			// TODO: is unreachable and this needs to be reported to inform that the RabbitMQ is down
			ex.printStackTrace();
		}
	}

	/**
	 *
	 * @return
	 */
	private final String getTimestamp() {
		SimpleDateFormat format = new SimpleDateFormat(FxpResponse.TIMESTAMP_FORMAT);
		return format.format(new Date());
	}

}
