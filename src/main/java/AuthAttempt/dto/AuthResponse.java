package AuthAttempt.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class AuthResponse {

	String requestId;
	String checkIp;
    boolean isBlocked;
}
