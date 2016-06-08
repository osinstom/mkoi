package client.encryption;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Mac;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomUtils;

/**
 * <p>Created by rsosn on 23.05.2016.</p>
 *
 * @author rsosn
 */
public class MessageObjectService {

	private ParamService paramService;

	public MessageObjectService(ParamService pService) {
		this.paramService = pService;
	}
	
	/**
	 * Tworzy i zwraca MessageObject do przesłania. Przed wywołaniem tej metody warto wypełnić CurrentParameters poprzez ParamService.
	 * @param fileBytes
	 * @param useTransform
	 * @return
	 */
	public MessageObject createMessageObject(byte[] fileBytes, boolean useTransform) {
		MessageObject res = createMessageObject(fileBytes);
		if (!useTransform) {
			res.setUseTransform(false);
			return res;
		}
		byte[] keyBytes = RandomUtils.nextBytes(128);
		List<MessagePart> tmp = produceTransformedMessages(res.getMessageParts(), keyBytes);
		res.setMessageParts(tmp);
		byte[] bigM = produceBigMForTransform(res.getMessageParts(), keyBytes);
		res.setBigM(bigM);
		res.setUseTransform(true);
		return res;
	}

	/**
	 * Zwraca bajty pliku z odebranego MessageObject.
	 * @param messageObject
	 * @return
	 */
	public byte[] getFileBytes(MessageObject messageObject) {
		MessageObject msgObject = winnowMessage(messageObject);
		if (messageObject.isUseTransform()) {
			detransformMessageObject(messageObject);
		}
		byte[] res = new byte[0];
		for (MessagePart msgPart : msgObject.getMessageParts()) {
			res = concatByteArrays(res, msgPart.getMsg());
		}
		return res;
	}

	private MessageObject createMessageObject(byte[] fileBytes) {
		MessageObject res = new MessageObject();
		List<MessagePart> list = createMessageParts(fileBytes
				, paramService.getPartSize()
				, paramService.getRatio());
		list.forEach(messagePart -> authenticateMsg(messagePart));
		res.setMessageParts(list);
		return res;
	}

	private List<MessagePart> createMessageParts(byte[] msgBytes, int partSize, int ratio) {
		int arrayLength = msgBytes.length;
		int sequenceNumber = 1;
		List<MessagePart> list = new ArrayList<>((int) arrayLength / partSize);
		for (int i = 0; i < arrayLength; i += partSize) {
			MessagePart msgPart = new MessagePart();
			byte[] array;
			int lastIndex = i + partSize - 1;
			if (lastIndex >= arrayLength) {
				array = Arrays.copyOfRange(msgBytes, i, arrayLength - 1);
			} else {
				array = Arrays.copyOfRange(msgBytes, i, i + partSize - 1);
			}
			int seq = sequenceNumber++;
			msgPart.setSequenceNumber(seq);
			msgPart.setMsg(array);
			msgPart.setChaff(false);
			list.add(msgPart);
			for (int j = 0; j < ratio; j++) {
				createChaffMsg(seq, partSize);
				list.add(msgPart);
			}
		}
		return list;
	}

	private MessagePart createChaffMsg(int seq, int partSize) {
		MessagePart msgPart = new MessagePart();
		msgPart.setSequenceNumber(seq);
		msgPart.setChaff(true);
		msgPart.setMsg(RandomUtils.nextBytes(partSize));
		msgPart.setMac(RandomUtils.nextBytes(256)); //długość HMAC SHA 256
		return msgPart;
	}

	private byte[] concatByteArrays(byte[] first, byte[] second) {
		int firstLength = first.length;
		int secondLength = second.length;
		byte[] res = new byte[firstLength + secondLength];
		System.arraycopy(first, 0, res, 0, firstLength);
		System.arraycopy(second, 0, res, firstLength, secondLength);
		return res;
	}

	private MessageObject winnowMessage(MessageObject messageObject) {
		List<MessagePart> msgParts = new ArrayList<>();
		messageObject.getMessageParts().stream()
				.filter(messagePart -> validateMacOfMsgPart(messagePart))
				.forEach(filteredMsgPart -> msgParts.add(filteredMsgPart));
		msgParts.sort((o1, o2) -> Integer.compare(o1.getSequenceNumber(), o2.getSequenceNumber()));
		MessageObject winnowedMsgObject = new MessageObject();
		winnowedMsgObject.setMessageParts(msgParts);
		return winnowedMsgObject;
	}

	private void authenticateMsg(MessagePart msgPart) {
		if (msgPart.isChaff()) {
			return; // jeśli CHAFF to nie ma co oznaczać.
		}
		byte[] tmp = msgPart.getMsg();
		byte[] mac = computeHMAC(tmp);
		msgPart.setMac(mac);
	}

	private byte[] computeHMAC(byte[] arg) {
		return paramService.getMacFunction().doFinal(arg);
		//return CurrentParameters.getInstance().getSha256_HMAC().doFinal(arg);
	}

	private boolean validateMacOfMsgPart(MessagePart msgPart) {
		byte[] tmp = computeHMAC(msgPart.getMsg());
		String computed = Base64.encodeBase64String(tmp);
		String received = Base64.encodeBase64String(msgPart.getMac());
		return computed.equalsIgnoreCase(received);
	}

	private List<MessagePart> produceTransformedMessages(List<MessagePart> messageParts, byte[] keyBytes) {
		Mac mac = paramService.initMacWithKey(keyBytes);
		List<MessagePart> res = new ArrayList<>(messageParts.size());
		messageParts.forEach(messagePart -> {
			MessagePart tmp = new MessagePart();
			int seq = messagePart.getSequenceNumber();
			tmp.setSequenceNumber(seq);
			byte[] bytes = mac.doFinal(getIntBytes(seq));
			tmp.setMsg(xorByteArrays(messagePart.getMsg(), bytes));
			authenticateMsg(tmp);
			res.add(tmp);
		});
		return res;
	}

	/**
	 * Metoda odwracająca transformatę all-or-nothing.
	 * @param msgObject
	 */
	private void detransformMessageObject(MessageObject msgObject) {
		byte[] tempKey = msgObject.getBigM();
		List<MessagePart> transformedMsgs = msgObject.getMessageParts();
		for (MessagePart msg : transformedMsgs) { //wyliczenie klucza transformacji
			tempKey = xorByteArrays(tempKey, paramService.getMacFunction().doFinal(xorByteArrays(msg.getMsg()
					, getIntBytes(msg.getSequenceNumber()))));
		}
		List<byte[]> res = new ArrayList<>(transformedMsgs.size());
		Mac mac = paramService.initMacWithKey(tempKey);
		transformedMsgs.forEach(messagePart -> {
			byte[] tmp = xorByteArrays(messagePart.getMsg()
					, mac.doFinal(getIntBytes(messagePart.getSequenceNumber()))); //wyliczenie bloków wiadomości
			messagePart.setMsg(tmp);
		});
	}

	private byte[] produceBigMForTransform(List<MessagePart> messageParts, byte[] keyBytes) {
		byte[] res = keyBytes;
		for (MessagePart messagePart : messageParts) {
			byte[] tmp = paramService.getMacFunction().doFinal(xorByteArrays(messagePart.getMsg()
					, getIntBytes(messagePart.getSequenceNumber())));
			res = xorByteArrays(res, tmp);
		}
		return res;
	}

	private byte[] xorByteArrays(byte[] a, byte[] b) {
		int greaterLength = a.length >= b.length ? a.length : b.length;
		if (a.length > b.length) {
			byte[] tmp = new byte[a.length];
			System.arraycopy(b, 0, tmp, a.length - b.length, b.length);
			b = tmp;
		} else if (b.length > a.length) {
			byte[] tmp = new byte[b.length];
			System.arraycopy(a, 0, tmp, b.length - a.length, a.length);
			a = tmp;
		}
		byte[] res =  new byte[greaterLength];
		int i = 0;
		for (byte x : a) {
			res[i] = (byte) (x ^ b[i++]);
		}
		return res;
	}

	private byte[] getIntBytes(int i) {
		return BigInteger.valueOf(i).toByteArray();
	}
}
