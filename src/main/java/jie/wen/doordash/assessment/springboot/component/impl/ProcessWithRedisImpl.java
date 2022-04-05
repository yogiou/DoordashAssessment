package jie.wen.doordash.assessment.springboot.component.impl;

import jie.wen.doordash.assessment.springboot.cache.RedisCache;
import jie.wen.doordash.assessment.springboot.component.BaseProcess;
import jie.wen.doordash.assessment.springboot.component.ProcessWithRedis;
import jie.wen.doordash.assessment.springboot.data.dto.PhoneNumberResponseDTO;
import jie.wen.doordash.assessment.springboot.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Component
public class ProcessWithRedisImpl extends BaseProcess implements ProcessWithRedis {
    @Autowired
    private RedisCache redisCache;
    @Override
    public List<PhoneNumberResponseDTO> parseWithRedis(String input) {
        List<PhoneNumberResponseDTO> phoneNumberResponseDTOList = processWithRedis(input);

        saveRedisAll(phoneNumberResponseDTOList);

        return phoneNumberResponseDTOList;
    }

    protected List<PhoneNumberResponseDTO> processWithRedis(String input) {
        HashMap<String, PhoneNumberResponseDTO> responseMap = new HashMap<>();
        List<PhoneNumberResponseDTO> phoneNumberResponseDTOList = new ArrayList<>();
        PhoneNumberResponseDTO phoneNumberResponseDTO = null;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char character = input.charAt(i);

            if (character == Constants.OPEN_BRACKET) {
                if (stringBuilder.length() > 0) {
                    if (phoneNumberResponseDTO != null) {
                        phoneNumberResponseDTO.setPhoneNumber(getNumber(stringBuilder.toString()));
                        setRedisData(phoneNumberResponseDTO, phoneNumberResponseDTOList, responseMap);
                    }

                    stringBuilder.setLength(0);
                }

                phoneNumberResponseDTO = new PhoneNumberResponseDTO();
            } else if (character == Constants.CLOSE_BRACKET) {
                if (phoneNumberResponseDTO != null) {
                    phoneNumberResponseDTO.setPhoneType(getType(stringBuilder.toString()));
                }

                stringBuilder.setLength(0);
            } else {
                stringBuilder.append(character);
            }
        }

        if (stringBuilder.length() > 0) {
            if (phoneNumberResponseDTO != null) {
                phoneNumberResponseDTO.setPhoneNumber(getNumber(stringBuilder.toString()));
                setRedisData(phoneNumberResponseDTO, phoneNumberResponseDTOList, responseMap);
            }

            stringBuilder.setLength(0);
        }

        return phoneNumberResponseDTOList;
    }

    @Async
    public void saveRedisAll(List<PhoneNumberResponseDTO> phoneNumberResponseDTOList) {
        for (PhoneNumberResponseDTO phoneNumberResponseDTO : phoneNumberResponseDTOList) {
            String key = phoneNumberResponseDTO.getPhoneNumber() + Constants.UNDER_SCORE + phoneNumberResponseDTO.getPhoneType();
            redisCache.set(key, phoneNumberResponseDTO);
        }
    }

    private void setRedisData(PhoneNumberResponseDTO phoneNumberResponseDTO, List<PhoneNumberResponseDTO> phoneNumberResponseDTOList, HashMap<String, PhoneNumberResponseDTO> responseMap) {
        String key = phoneNumberResponseDTO.getPhoneNumber() + Constants.UNDER_SCORE + phoneNumberResponseDTO.getPhoneType();
        boolean exist = responseMap.containsKey(key);

        phoneNumberResponseDTO = responseMap.getOrDefault(key, phoneNumberResponseDTO);

        if (exist) {
            phoneNumberResponseDTO.setOccurrences(phoneNumberResponseDTO.getOccurrences() + 1);
        } else if (!redisCache.hasKey(key)) {
            phoneNumberResponseDTO.setId(UUID.randomUUID().toString());
            phoneNumberResponseDTO.setOccurrences(1);
        } else {
            phoneNumberResponseDTO = (PhoneNumberResponseDTO) redisCache.get(key);
            phoneNumberResponseDTO.setOccurrences(phoneNumberResponseDTO.getOccurrences() + 1);
        }

        if (!exist) {
            responseMap.put(key, phoneNumberResponseDTO);
            phoneNumberResponseDTOList.add(phoneNumberResponseDTO);
        }
    }
}
