package OSS_group11.ConvoPersona.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class EncryptionService {

    private final String algorithm;
    private final String key;
    private final String iv;

    @Autowired
    public EncryptionService(@Value("${encryption.algorithm}") String algorithm,
                             @Value("${encryption.key}") String key,
                             @Value("${encryption.iv}") String iv) {
        this.algorithm = algorithm;
        this.key = key;
        this.iv = iv;
    }


    /***
     * 키 생성 (Key Generation)
     * @return
     */
    private SecretKey getSecretKey() {
        return new SecretKeySpec(key.getBytes(), "AES");
    }

    /**
     * IV 생성 (IV Generation)
     *
     * @return IvParameterSpec
     */
    private IvParameterSpec getIvSpec() {
        return new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
    }

    /***
     * 암호화 (Encryption)
     * @param plainText
     * @return
     * @throws Exception
     */
    public String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), getIvSpec());
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /***
     * 복호화 (Decryption)
     * @param encryptedText
     * @return
     * @throws Exception
     */
    public String decrypt(String encryptedText) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), getIvSpec());
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
