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
public class ParamService {

	public ParamService(String secretKey, int partSize, int ratio) {
		CurrentParameters.getInstance().setSha256_HMAC(secretKey);
		CurrentParameters.getInstance().setRatio(10);
		CurrentParameters.getInstance().setPartSize(1000);
	}

	public void initMacFunction(String secretKey) {
		CurrentParameters.getInstance().setSha256_HMAC(secretKey);
	}

	public Mac getMacFunction() {
		return CurrentParameters.getInstance().getSha256_HMAC();
	}

	public Mac initMacWithKey(byte[] key) {
		Mac tmp = null;
		try {
			tmp = Mac.getInstance("HmacSHA256");
			SecretKeySpec secretKeySpec = new SecretKeySpec(key, "HmacSHA256");
			tmp.init(secretKeySpec);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		return tmp;
	}

	public void setPartSizeAndRation(int partSize, int ratio) {
		CurrentParameters.getInstance().setPartSize(partSize);
		CurrentParameters.getInstance().setRatio(ratio);
	}

	public int getPartSize() {
		return CurrentParameters.getInstance().getPartSize();
	}

	public int getRatio() {
		return CurrentParameters.getInstance().getRatio();
	}
}
