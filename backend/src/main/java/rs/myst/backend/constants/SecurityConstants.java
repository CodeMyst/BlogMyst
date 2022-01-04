package rs.myst.backend.constants;

public class SecurityConstants {
    public static final String REGISTER_URL = "/users/register";
    // TODO: This should be in the config, encrypted or something
    public static final String KEY = "-JaNcRfUjXn2r5u8x/A?D(G+KbPeSgVkYp3s6v9y$B&E)H@McQfTjWmZq4t7w!z%";
    public static final String HEADER_NAME = "Authorization";
    public static final Long EXPIRATION_TIME = 1000L * 60 * 30;
}
