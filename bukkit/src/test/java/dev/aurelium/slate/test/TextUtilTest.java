package dev.aurelium.slate.test;

import dev.aurelium.slate.util.TextUtil;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class TextUtilTest {

    @Test
    public void testPlaceholders() {
        assert TextUtil.substringsBetween("{test}", "{", "}")[0].equals("test");
        assert TextUtil.substringsBetween("{{test}}", "{", "}")[0].equals("{test}");
        assert Arrays.equals(TextUtil.substringsBetween("{test}{{test2}}", "{", "}"), new String[]{"test", "{test2}"});
        assert Arrays.equals(TextUtil.substringsBetween("{test}{{test2}}{{{test3}}}", "{", "}"), new String[]{"test", "{test2}", "{{test3}"});
        assert Arrays.equals(TextUtil.substringsBetween("{ value with spaces }{{a.b.c}}", "{", "}"), new String[]{" value with spaces ", "{a.b.c}"});
        assert Arrays.equals(TextUtil.substringsBetween("{{a.b.c}}", "{{", "}}"), new String[]{"a.b.c"});
    }

}
