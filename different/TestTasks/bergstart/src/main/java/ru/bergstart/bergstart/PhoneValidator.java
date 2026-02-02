package ru.bergstart.bergstart;

import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PhoneValidator {

    private final Pattern PATTERN = Pattern.compile("^\\+79[0-9]{9}$");
    private final String phone;
    private final Set<Character> digits = Stream.concat(Stream.of("+"), IntStream.range(0, 10).mapToObj(i -> i + "")).map(s -> s.charAt(0)).collect(Collectors.toSet());
    private final boolean valid;

    public PhoneValidator(String phone) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < phone.length(); i++) {
            char charAt = phone.charAt(i);
            if (digits.contains(charAt)) {
                sb.append(charAt);
            }
        }
        this.phone = sb.toString();
        valid = isValid(this.phone);
    }

    private boolean isValid(String phoneNumber) {
        if (phoneNumber == null) {
            return false;
        }
        return PATTERN.matcher(phoneNumber).matches();
    }

    public boolean isValid() {
        return valid;
    }

    public String getPhone() {
        return phone;
    }
}
