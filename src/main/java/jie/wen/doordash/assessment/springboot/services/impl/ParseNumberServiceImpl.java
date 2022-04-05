package jie.wen.doordash.assessment.springboot.services.impl;

import jie.wen.doordash.assessment.springboot.component.ProcessWithDB;
import jie.wen.doordash.assessment.springboot.component.ProcessWithRedis;
import jie.wen.doordash.assessment.springboot.data.dto.ParsedNumberResponseDTO;
import jie.wen.doordash.assessment.springboot.enums.ProcessMethod;
import jie.wen.doordash.assessment.springboot.services.ParseNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ParseNumberServiceImpl implements ParseNumberService {
    @Value("${process.method}")
    private String processMethod;

    @Autowired
    private ProcessWithDB processWithDB;

    @Autowired
    private ProcessWithRedis processWithRedis;

    @Override
    public ParsedNumberResponseDTO parseNumber(String input) {
        ParsedNumberResponseDTO parsedNumberResponseDTO = new ParsedNumberResponseDTO();

        if (processMethod.equals(ProcessMethod.DB.toValue())) {
            parsedNumberResponseDTO.setParsedNumberResponseDTOList(processWithDB.parseWithDB(input));
        } else if (processMethod.equals(ProcessMethod.REDIS.toValue())) {
            parsedNumberResponseDTO.setParsedNumberResponseDTOList(processWithRedis.parseWithRedis(input));
        }

        return parsedNumberResponseDTO;
    }
}
