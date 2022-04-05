package jie.wen.doordash.assessment.springboot.component;

import jie.wen.doordash.assessment.springboot.data.dto.PhoneNumberResponseDTO;

import java.util.List;

public interface ProcessWithDB {
    List<PhoneNumberResponseDTO> parseWithDB(String input);
}
