package jie.wen.doordash.assessment.springboot.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class PhoneNumberResponseDTO implements Serializable {
    @JsonProperty("id")
    private String id;
    @JsonProperty("phone_number")
    private String phoneNumber;
    @JsonProperty("phone_type")
    private String phoneType;
    @JsonProperty("occurrences")
    private int occurrences;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }

    public int getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(int occurrences) {
        this.occurrences = occurrences;
    }
}
