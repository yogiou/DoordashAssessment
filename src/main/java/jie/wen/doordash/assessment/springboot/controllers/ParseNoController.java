package jie.wen.doordash.assessment.springboot.controllers;

import jie.wen.doordash.assessment.springboot.data.dto.ParsedNumberResponseDTO;
import jie.wen.doordash.assessment.springboot.data.dto.PhoneNumberRequestDTO;
import jie.wen.doordash.assessment.springboot.data.error.NoPhoneNumberRequestException;
import jie.wen.doordash.assessment.springboot.services.ParseNumberService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ParseNoController {
    @Autowired
    private ParseNumberService parseNumberService;

    @PostMapping("/phone-numbers")
    public ParsedNumberResponseDTO parseNumber(@RequestBody PhoneNumberRequestDTO phoneNumberRequestDTO) {
        valid(phoneNumberRequestDTO);
        return parseNumberService.parseNumber(phoneNumberRequestDTO.getRawPhoneNumbers());
    }

    private void valid(PhoneNumberRequestDTO phoneNumberRequestDTO) {
        if (phoneNumberRequestDTO == null || StringUtils.isBlank(phoneNumberRequestDTO.getRawPhoneNumbers())) {
            throw new NoPhoneNumberRequestException();
        }
    }
}
