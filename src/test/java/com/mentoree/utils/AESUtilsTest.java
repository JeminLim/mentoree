package com.mentoree.utils;

import com.mentoree.config.security.util.AESUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import javax.inject.Inject;

@ActiveProfiles("test")
public class AESUtilsTest {

    @Test
    @DisplayName("AES encrypt test")
    public void AESEncryptTest() throws Exception {

        AESUtils aesUtils = new AESUtils();

        //given
        String msg = "test message";
        //when
        String encryptedMsg = aesUtils.encrypt(msg);
        System.out.println("encryptedMsg = " + encryptedMsg);
        String decryptedMsg = aesUtils.decrypt(encryptedMsg);
        System.out.println("decryptedMsg = " + decryptedMsg);
        //then
        Assertions.assertThat(encryptedMsg).isNotEqualTo(msg);
        Assertions.assertThat(decryptedMsg).isEqualTo(msg);
    }

}
