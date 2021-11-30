package jie.wen.doordash.assessment.springboot.services.impl;

import jie.wen.doordash.assessment.springboot.cache.RedisCache;
import jie.wen.doordash.assessment.springboot.data.dto.ParsedNumberResponseDTO;
import jie.wen.doordash.assessment.springboot.data.dto.PhoneNumberResponseDTO;
import jie.wen.doordash.assessment.springboot.data.entities.PhoneNumberData;
import jie.wen.doordash.assessment.springboot.data.repository.PhoneNumberDataRepository;
import jie.wen.doordash.assessment.springboot.enums.ProcessMethod;
import jie.wen.doordash.assessment.springboot.services.ParseNumberService;
import jie.wen.doordash.assessment.springboot.utils.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;

@Service
public class ParseNumberServiceImpl implements ParseNumberService {
    @Autowired
    private PhoneNumberDataRepository phoneNumberDataRepository;

    @Autowired
    private RedisCache redisCache;

    @Value("${process.method}")
    private String processMethod;

    @Override
    public ParsedNumberResponseDTO parseNumber(String input) {
        ParsedNumberResponseDTO parsedNumberResponseDTO = new ParsedNumberResponseDTO();

        if (processMethod.equals(ProcessMethod.DB.toValue())) {
            parsedNumberResponseDTO.setParsedNumberResponseDTOList(parseWithDB(input));
        } else if (processMethod.equals(ProcessMethod.REDIS.toValue())) {
            parsedNumberResponseDTO.setParsedNumberResponseDTOList(parseWithRedis(input));
        }

        return parsedNumberResponseDTO;
    }

    private List<PhoneNumberResponseDTO> parseWithDB(String input) {
        List<PhoneNumberResponseDTO> phoneNumberResponseDTOList = new ArrayList<>();
        List<String> searchKeys = processInput(input, phoneNumberResponseDTOList);
        operate(searchKeys, phoneNumberResponseDTOList);
        return phoneNumberResponseDTOList;
    }

    protected List<String> processInput(String input, List<PhoneNumberResponseDTO> phoneNumberResponseDTOList) {
        HashMap<String, PhoneNumberResponseDTO> responseMap = new HashMap<>();

        PhoneNumberResponseDTO phoneNumberResponseDTO = null;
        StringBuilder stringBuilder = new StringBuilder();
        List<String> searchKeys = new ArrayList<>();

        for (int i = 0; i < input.length(); i++) {
            char character = input.charAt(i);

            if (character == Constants.OPEN_BRACKET) {
                if (stringBuilder.length() > 0) {
                    if (phoneNumberResponseDTO != null) {
                        phoneNumberResponseDTO.setPhoneNumber(getNumber(stringBuilder.toString()));
                        String key = phoneNumberResponseDTO.getPhoneNumber() + Constants.UNDER_SCORE + phoneNumberResponseDTO.getPhoneType();
                        if (!searchKeys.contains(key))
                            searchKeys.add(key);
                        setDBData(phoneNumberResponseDTO, phoneNumberResponseDTOList, responseMap);
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
                String key = phoneNumberResponseDTO.getPhoneNumber() + Constants.UNDER_SCORE + phoneNumberResponseDTO.getPhoneType();
                if (!searchKeys.contains(key))
                    searchKeys.add(key);
                setDBData(phoneNumberResponseDTO, phoneNumberResponseDTOList, responseMap);
            }

            stringBuilder.setLength(0);
        }

        return searchKeys;
    }

    @Transactional
    public void operate(List<String> searchKeys, List<PhoneNumberResponseDTO> phoneNumberResponseDTOList) {
        List<PhoneNumberData> phoneNumberDataList = phoneNumberDataRepository.findBySearchKeyIn(searchKeys);
        HashMap<String, PhoneNumberData> phoneNumberDataHashMap = new HashMap<>();

        for (PhoneNumberData phoneNumberData : phoneNumberDataList) {
            phoneNumberDataHashMap.put(phoneNumberData.getSearchKey(), phoneNumberData);
        }

        for (PhoneNumberResponseDTO numberResponseDTO : phoneNumberResponseDTOList) {
            String key = numberResponseDTO.getPhoneNumber() + Constants.UNDER_SCORE + numberResponseDTO.getPhoneType();
            if (phoneNumberDataHashMap.containsKey(key)) {
                PhoneNumberData phoneNumberData = phoneNumberDataHashMap.get(key);
                phoneNumberData.setOccurrences(phoneNumberData.getOccurrences() + numberResponseDTO.getOccurrences());

                numberResponseDTO.setId(phoneNumberData.getId());
                numberResponseDTO.setOccurrences(phoneNumberData.getOccurrences());
            } else {
                PhoneNumberData phoneNumberData = new PhoneNumberData();
                phoneNumberData.setId(UUID.randomUUID().toString());
                phoneNumberData.setPhoneNumber(numberResponseDTO.getPhoneNumber());
                phoneNumberData.setPhoneType(numberResponseDTO.getPhoneType());
                phoneNumberData.setSearchKey(key);
                phoneNumberData.setOccurrences(numberResponseDTO.getOccurrences());
                phoneNumberDataList.add(phoneNumberData);

                numberResponseDTO.setId(phoneNumberData.getId());
                numberResponseDTO.setOccurrences(phoneNumberData.getOccurrences());
            }
        }

        phoneNumberDataRepository.saveAll(phoneNumberDataList);
    }

    private void setDBData(PhoneNumberResponseDTO phoneNumberResponseDTO, List<PhoneNumberResponseDTO> phoneNumberResponseDTOList, HashMap<String, PhoneNumberResponseDTO> responseMap) {
        String key = phoneNumberResponseDTO.getPhoneNumber() + Constants.UNDER_SCORE + phoneNumberResponseDTO.getPhoneType();

        if (!responseMap.containsKey(key)) {
            phoneNumberResponseDTOList.add(phoneNumberResponseDTO);
            phoneNumberResponseDTO.setOccurrences(phoneNumberResponseDTO.getOccurrences() + 1);
            responseMap.put(key, phoneNumberResponseDTO);
        } else {
            PhoneNumberResponseDTO temp = responseMap.get(key);
            temp.setOccurrences(temp.getOccurrences() + 1);
        }
    }

    protected String getNumber(String input) {
        try {
            String number = StringUtils.strip(input.toLowerCase().replace(Constants.DASH, Constants.EMPTY));

            String prefix = number.charAt(0) == Constants.PLUS_CHAR ? Constants.PLUS : Constants.EMPTY;

            if (prefix.equals(Constants.PLUS)) {
                number = number.substring(1);
            }

            NumberUtils.parseNumber(number, BigDecimal.class);
            return prefix + number;
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid phone number format");
        }
    }

    protected String getType(String input) {
        return StringUtils.strip(input.toLowerCase());
    }

    private List<PhoneNumberResponseDTO> parseWithRedis(String input) {
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
