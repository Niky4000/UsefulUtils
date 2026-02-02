package ru.bergstart.bergstart;

import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class PhoneValidatorTest {

    @Test
    public void testPhone() {
        List<Object[]> params = Arrays.asList(new Object[]{"8925-555-22-22", false}, new Object[]{"+7G925-&555_22^22", true}, new Object[]{"+7G925-&555_22^2 2", true});
        for (Object[] param : params) {
            PhoneValidator phoneValidator = new PhoneValidator((String) param[0]);
            Assert.assertTrue(phoneValidator.isValid() == (boolean) param[1]);
        }
    }
}
