package rs.myst.backend.constants;

public class RoleConstants {
    public final static String USER_NOT_BANNED = "hasAnyAuthority('USER', 'MOD', 'ADMIN')";
    public final static String USER_MIGHT_BANNED = "hasAnyAuthority('BANNED', 'USER', 'MOD', 'ADMIN')";
    public final static String MOD = "hasAnyAuthority('MOD', 'ADMIN')";
    public final static String ADMIN = "hasAuthority('ADMIN')";
}
