package jie.wen.doordash.assessment.springboot.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class PhoneNumberRequestDTO implements Serializable {
    @JsonProperty("raw_phone_numbers")
    private String rawPhoneNumbers;

    public String getRawPhoneNumbers() {
        return rawPhoneNumbers;
    }

    public void setRawPhoneNumbers(String rawPhoneNumbers) {
        this.rawPhoneNumbers = rawPhoneNumbers;
    }
}
