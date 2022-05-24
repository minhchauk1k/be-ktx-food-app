package springboot.enums;

public class MConst {
	// String
	public static final String BEARER = "Bearer ";

	// Role
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String ROLE_USER = "ROLE_USER";
	public static final String ROLE_MANAGER = "ROLE_MANAGER";
	public static final String ROLE_OWNER = "ROLE_OWNER";
	public static final String ROLE_STAFF = "ROLE_STAFF";
	public static final String ANONYMOUS = "ANONYMOUS";

	// System Parameter
	public static final String SERECT_KEY = "SERECT_KEY";
	public static final String OPEN_TIME = "OPEN_TIME";
	public static final String CLOSE_TIME = "CLOSE_TIME";
	public static final String LOT_CONTROL = "LOT_CONTROL";

	// Date & Time
	public static final int HOUR = 60 * 60 * 1000;
	public static final int DAY = HOUR * 24;
	public static final int WEEK = DAY * 7;

	// Type
	public static final String FOOD = "FOOD";
	public static final String SERVICE = "SERVICE";
	public static final String ALL = "ALL";
	public static final String DISCOUNT = "DISCOUNT";
	public static final String NUMBER = "NUMBER";
	public static final String PERCENT = "PERCENT";
	public static final String YES = "YES";
	public static final String NO = "NO";

	// PayType
	public static final String MONEY = "MONEY";
	public static final String MOMO = "MOMO";

	// Status of Order
	public static final String ORDER = "ORDER";
	public static final String WAITFORPAY = "WAITFORPAY";
	public static final String PAID = "PAID";
	public static final String PREPARING = "PREPARING";
	public static final String DELIVERY = "DELIVERY";
	public static final String COMPLETED = "COMPLETED";
	public static final String CANCEL = "CANCEL";

}
