package jie.wen.doordash.assessment.springboot.services;

import jie.wen.doordash.assessment.springboot.data.dto.ParsedNumberResponseDTO;

public interface ParseNumberService {
    ParsedNumberResponseDTO parseNumber(String input);
}
