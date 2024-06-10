package OSS_group11.ConvoPersona.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
public class EncryptionServiceTest {

    @Value("${encryption.algorithm}")
    private String algorithm;

    @Value("${encryption.key}")
    private String key;

    @Value("${encryption.iv}")
    private String iv;


    private EncryptionService encryptionService;

    @BeforeEach
    public void setUp() {
        encryptionService = new EncryptionService(algorithm, key, iv);
    }

    @Test
    public void testEncryptDecrypt() throws Exception {
        String originalText = "안녕";
        String encryptedText = encryptionService.encrypt(originalText);
        String decryptedText = encryptionService.decrypt("ckplaN9VHdQ0DPxI9oOQJw==");

        System.out.println(encryptedText);
        System.out.println("복호화 결과 : " + decryptedText);

        assertEquals(originalText, decryptedText);
    }

    @Test
    public void testEncryptWithEmptyString() throws Exception {
        String originalText = "";
        String encryptedText = encryptionService.encrypt(originalText);
        String decryptedText = encryptionService.decrypt(encryptedText);

        System.out.println("암호화 결과 : " + encryptedText);
        System.out.println("복호화 결과 : " + decryptedText);

        assertEquals(originalText, decryptedText);
    }

    @Test
    public void testDecryptWithInvalidKey() {
        String originalText = "안녕";
        assertThrows(Exception.class, () -> {
            EncryptionService invalidEncryptionService = new EncryptionService(algorithm, "wrongkeywrongkey", iv);
            String encryptedText = invalidEncryptionService.encrypt(originalText);
            invalidEncryptionService.decrypt(encryptedText);
        });
    }
}