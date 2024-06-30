package AuthAttempt.validation.processor;

import java.util.regex.Pattern;

import AuthAttempt.validation.ValidIpAddress;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IpAddressValidator implements ConstraintValidator<ValidIpAddress, String> {

	private static final String IP_V4_PATTERN = "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])$";
//	private static final String IP_V6_PATTERN =
//           "(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:))";

	private static final Pattern ipv4Pattern = Pattern.compile(IP_V4_PATTERN);
//    private static final Pattern ipv6Pattern = Pattern.compile(IP_V6_PATTERN);

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return value != null && ipv4Pattern.matcher(value).matches();
//		return value != null && (ipv4Pattern.matcher(value).matches() || ipv6Pattern.matcher(value).matches());
	}

	@Override
	public void initialize(ValidIpAddress constraintAnnotation) {
	}

}
