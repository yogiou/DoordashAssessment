package jie.wen.doordash.assessment.springboot.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class ParsedNumberResponseDTO implements Serializable {
    @JsonProperty("results")
    List<PhoneNumberResponseDTO> parsedNumberResponseDTOList;

    public List<PhoneNumberResponseDTO> getParsedNumberResponseDTOList() {
        return parsedNumberResponseDTOList;
    }

    public void setParsedNumberResponseDTOList(List<PhoneNumberResponseDTO> parsedNumberResponseDTOList) {
        this.parsedNumberResponseDTOList = parsedNumberResponseDTOList;
    }
}
