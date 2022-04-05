package jie.wen.doordash.assessment.springboot.component;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BaseProcessTest {

    @Test
    void getNumber() {
        BaseProcess baseProcess = new BaseProcess();
        Assertions.assertEquals("1231232123", baseProcess.getNumber("123-1232-123"));
    }

    @Test
    void getType() {
        BaseProcess baseProcess = new BaseProcess();
        Assertions.assertEquals("home", baseProcess.getType("HOME"));
    }
}