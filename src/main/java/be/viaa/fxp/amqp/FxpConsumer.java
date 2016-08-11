package be.viaa.fxp.amqp;

import be.viaa.amqp.AmqpJsonConsumer;
import be.viaa.amqp.AmqpService;
import be.viaa.amqp.util.JsonConverter;
import be.viaa.fxp.FileTransporter;
import be.viaa.fxp.FxpFileTransporter;
import be.viaa.fxp.model.File;
import be.viaa.fxp.model.Host;

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
	public void success(AmqpService service, FxpRequest message) throws Exception {
		FxpResponse response = new FxpResponse();
		
		response.setCorrelationId(message.getPid());
		response.setPid(message.getPid());
		response.setSourceFileRemoved(message.move());
		response.setOutcome("OK");
		
		service.write("queue_name", JsonConverter.convert(response));
	}

	@Override
	public void exception(AmqpService service, Exception exception, FxpRequest message) {
		FxpResponse response = new FxpResponse();
		
		response.setCorrelationId(message.getPid());
		response.setPid(message.getPid());
		response.setSourceFileRemoved(false);
		response.setOutcome("NOK");
		response.setComment(exception.getMessage());
		
		exception.printStackTrace();
		
		try {
			service.write("queue_name", JsonConverter.convert(response));
		} catch (Exception ex) {
			// TODO: This exception needs to be monitored closely and logged pretty well, it means the queue
			// TODO: is unreachable and this needs to be reported to inform that the RabbitMQ is down
			ex.printStackTrace();
		}
	}	

}
