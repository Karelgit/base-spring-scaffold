package com.cloudpioneer.demo.base.validator;

import com.cloudpioneer.demo.base.annotation.Phone;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 手机号校验类
 * @author jiangyunjun
 */
public class PhoneValidator implements ConstraintValidator<Phone, String> {

    private static int phoneNumSize = 11;

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|166|198|199|(147))\\d{8}$"
    );

    @Override
    public void initialize(Phone constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if(value == null){
            return true;
        }else if(value.length() != phoneNumSize){
            return false;
        }
        Matcher m = PHONE_PATTERN.matcher(value);
        return m.matches();
    }
}
