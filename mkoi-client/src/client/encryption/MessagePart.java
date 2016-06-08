package client.encryption;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Części wiadomości (m).
 * <p>Created by rsosn on 22.05.2016.</p>
 *
 * @author rsosn
 */
public class MessagePart implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6598643260557863936L;

	private int sequenceNumber;

	private byte[] msg;

	private byte[] mac;

	private transient boolean isChaff;

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public byte[] getMsg() {
		return msg;
	}

	public void setMsg(byte[] msg) {
		this.msg = msg;
	}

	public byte[] getMac() {
		return mac;
	}

	public void setMac(byte[] mac) {
		this.mac = mac;
	}

	public boolean isChaff() {
		return isChaff;
	}

	public void setChaff(boolean chaff) {
		isChaff = chaff;
	}
	
	@Override
	public String toString() {
		return new String(msg);
	}
}
