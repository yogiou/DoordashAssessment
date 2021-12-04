package jie.wen.doordash.assessment.springboot.component.impl;

import jie.wen.doordash.assessment.springboot.component.ProcessWithDB;
import jie.wen.doordash.assessment.springboot.data.dto.PhoneNumberResponseDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProcessWithDBImplTest {
    @Autowired
    private ProcessWithDBImpl processWithDB;

    @Test
    void testProcessWithDB() {
        List<PhoneNumberResponseDTO> phoneNumberResponseDTOList = new ArrayList<>();
        String input = "(Home) 415-415-4155 (Cell) 415-123-4567(Home) 415-415-4154 ";

        List<String> expected = new ArrayList<>();
        expected.add("4154154155_home");
        expected.add("4151234567_cell");
        expected.add("4154154154_home");

        List<String> actual = processWithDB.processInput(input, phoneNumberResponseDTOList);

        for (int i = 0; i < expected.size(); i++) {
            String expect = expected.get(i);
            String act = actual.get(i);
            Assertions.assertEquals(expect, act);
        }
    }
}