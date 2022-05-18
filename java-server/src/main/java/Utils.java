import org.apache.commons.lang3.RandomStringUtils;

import java.util.HashSet;
import java.util.Set;

public class Utils {

    public static final String LOCAL_HOST = "tcp://localhost:1883";

    private static final int TOKEN_LENGTH = 2;
    private static final Set<String> EXISTING_TOKENS = new HashSet<>();

    static public String generateToken() {
        String newToken = RandomStringUtils.randomAlphanumeric(TOKEN_LENGTH);

        while (EXISTING_TOKENS.contains(newToken)) {
            newToken = RandomStringUtils.randomAlphanumeric(TOKEN_LENGTH);
        }

        EXISTING_TOKENS.add(newToken);
        return newToken;
    }
}