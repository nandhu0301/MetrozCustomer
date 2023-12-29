package com.smiligenceUAT1.metrozcustomer.common;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.smiligenceUAT1.metrozcustomer.common.Constant.EMAIL_PATTERN;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.LAST_NAME_PATTERN;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.MADURAI_PINCODE_PATTERN;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.PASSWORD_LENGTH;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.PASSWORD_PATTERN;

public class TextUtils {

    public static <T> List<T> removeDuplicatesList(List<T> list) {

        Set<T> set = new LinkedHashSet<>();
        set.addAll(list);
        list.clear();
        list.addAll(set);
        return list;
    }

    public static boolean isValidlastName(final String lastName) {
        Pattern pattern = Pattern.compile(LAST_NAME_PATTERN);
        Matcher matcher = pattern.matcher(lastName);
        return matcher.matches();
    }

    public static boolean validPinCode(final String firstName) {
        Pattern pattern = Pattern.compile(MADURAI_PINCODE_PATTERN);
        Matcher matcher = pattern.matcher(firstName);

        return matcher.matches();

    }


    public static boolean isValidEmail(final String email) {

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    public static boolean isValidPassword(final String password) {
        if (password.length() >= PASSWORD_LENGTH) {
            Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
            Matcher matcher = pattern.matcher(password);
            return matcher.matches();
        } else {
            return Constant.BOOLEAN_FALSE;
        }
    }


    public static boolean validatePhoneNumber(String phoneNo) {

        if (phoneNo.matches("\\d{10}")) return true;
        else return false;
    }

}
