package jie.wen.doordash.assessment.springboot.component;

import jie.wen.doordash.assessment.springboot.data.dto.PhoneNumberResponseDTO;

import java.util.List;

public interface ProcessWithRedis {
    List<PhoneNumberResponseDTO> parseWithRedis(String input);
}
