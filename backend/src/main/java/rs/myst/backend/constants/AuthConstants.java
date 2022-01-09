package rs.myst.backend.constants;

public class AuthConstants {
    public final static String USER_AUTH = "hasAnyAuthority('USER', 'MOD', 'ADMIN')";
    public final static String MOD_AUTH = "hasAnyAuthority('MOD', 'ADMIN')";
    public final static String ADMIN_AUTH = "hasAuthority('ADMIN')";
}
