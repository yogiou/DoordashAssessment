package jie.wen.doordash.assessment.springboot.component.impl;

import jie.wen.doordash.assessment.springboot.component.BaseProcess;
import jie.wen.doordash.assessment.springboot.component.ProcessWithDB;
import jie.wen.doordash.assessment.springboot.data.dto.PhoneNumberResponseDTO;
import jie.wen.doordash.assessment.springboot.data.entities.PhoneNumberData;
import jie.wen.doordash.assessment.springboot.data.repository.PhoneNumberDataRepository;
import jie.wen.doordash.assessment.springboot.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Component
public class ProcessWithDBImpl extends BaseProcess implements ProcessWithDB {
    @Autowired
    private PhoneNumberDataRepository phoneNumberDataRepository;

    @Override
    public List<PhoneNumberResponseDTO> parseWithDB(String input) {
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
}
