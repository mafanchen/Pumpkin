package com.video.test.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Enoch Created on 2019/1/29.
 */
public class UnicodeUtils {

    public static String unicodeToString(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{2,4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");
        }
        return str;
    }


}
