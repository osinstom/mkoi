package client.encryption;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * <p>Created by rsosn on 28.05.2016.</p>
 *
 * @author rsosn
 */
public class CurrentParameters {

	private static CurrentParameters ourInstance = new CurrentParameters();

	public static CurrentParameters getInstance() {
		return ourInstance;
	}

	private Mac sha256_HMAC;

	private int partSize;

	private int ratio;

	private CurrentParameters() {
		//defaultowe wartości coby nullami nie waliło po ryju
		setSha256_HMAC("defaultSecretKey");
		setPartSize(1000);
		setRatio(10);

	}

	public Mac getSha256_HMAC() {
		return sha256_HMAC;
	}

	public void setSha256_HMAC(String secretKey) {
		try {
			Mac tmp = Mac.getInstance("HmacSHA256");
			SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
			tmp.init(secretKeySpec);
			sha256_HMAC = tmp;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
	}

	public int getPartSize() {
		return partSize;
	}

	public void setPartSize(int partSize) {
		this.partSize = partSize;
	}

	public int getRatio() {
		return ratio;
	}

	public void setRatio(int ratio) {
		this.ratio = ratio;
	}
}
