package client.encryption;

import java.io.Serializable;
import java.util.List;

/**
 * Przesyłany obiekt.
 * <p>Created by rsosn on 23.05.2016.</p>
 *
 * @author rsosn
 */
public class MessageObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7123071334620763971L;

	private List<MessagePart> messageParts;

	private byte[] bigM;

	private boolean useTransform;

	public List<MessagePart> getMessageParts() {
		return messageParts;
	}

	public void setMessageParts(List<MessagePart> messageParts) {
		this.messageParts = messageParts;
	}

	public byte[] getBigM() {
		return bigM;
	}

	public void setBigM(byte[] bigM) {
		this.bigM = bigM;
	}

	public boolean isUseTransform() {
		return useTransform;
	}

	public void setUseTransform(boolean useTransform) {
		this.useTransform = useTransform;
	}
	
	
}
