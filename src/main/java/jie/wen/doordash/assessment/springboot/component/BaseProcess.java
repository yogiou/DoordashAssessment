package jie.wen.doordash.assessment.springboot.component;

import jie.wen.doordash.assessment.springboot.utils.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.NumberUtils;

import java.math.BigDecimal;

public class BaseProcess {
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
}
