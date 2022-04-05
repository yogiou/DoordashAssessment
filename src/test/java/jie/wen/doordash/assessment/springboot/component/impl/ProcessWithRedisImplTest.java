package jie.wen.doordash.assessment.springboot.component.impl;

import jie.wen.doordash.assessment.springboot.data.dto.PhoneNumberResponseDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProcessWithRedisImplTest {
    @Autowired
    private ProcessWithRedisImpl processWithRedis;



    @Test
    void testProcessWithRedis() {
        String input = "(Home) 415-415-4155 (Cell) 415-123-4567(Home) 415-415-4154 (Home) 415-415-4155 ";
        List<PhoneNumberResponseDTO> actual = processWithRedis.processWithRedis(input);

        List<PhoneNumberResponseDTO> expected = new ArrayList<>();
        PhoneNumberResponseDTO phoneNumberResponseDTO1 = new PhoneNumberResponseDTO();
        phoneNumberResponseDTO1.setPhoneNumber("4154154155");
        phoneNumberResponseDTO1.setPhoneType("home");
        expected.add(phoneNumberResponseDTO1);

        PhoneNumberResponseDTO phoneNumberResponseDTO2 = new PhoneNumberResponseDTO();
        phoneNumberResponseDTO2.setPhoneNumber("4151234567");
        phoneNumberResponseDTO2.setPhoneType("cell");
        expected.add(phoneNumberResponseDTO2);

        PhoneNumberResponseDTO phoneNumberResponseDTO3 = new PhoneNumberResponseDTO();
        phoneNumberResponseDTO3.setPhoneNumber("4154154154");
        phoneNumberResponseDTO3.setPhoneType("home");
        expected.add(phoneNumberResponseDTO3);

        for (int i = 0; i < expected.size(); i++) {
            PhoneNumberResponseDTO expect = expected.get(i);
            PhoneNumberResponseDTO act = actual.get(i);
            Assertions.assertEquals(expect.getPhoneNumber(), act.getPhoneNumber());
            Assertions.assertEquals(expect.getPhoneType(), act.getPhoneType());
        }
    }
}