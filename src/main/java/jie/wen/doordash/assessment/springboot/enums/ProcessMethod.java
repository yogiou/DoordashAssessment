package jie.wen.doordash.assessment.springboot.enums;

public enum ProcessMethod {
    DB("db"), REDIS("redis");

    private String value;

    ProcessMethod(String value) {
        this.value = value;
    }

    public String toValue() {
        return value;
    }
}
