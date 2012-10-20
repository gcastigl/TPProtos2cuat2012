package model;

public enum StatusCodes {
	OK_CONFIGURER_READY(0, "Configurer ready"),
	OK_COMMANDS_LISTED(1, "Commands listed"),
	OK_LOGGED_OUT(2, "Logged out successfully"), 
	OK_FILE_UPDATED(3, "File successfully updated"),
	OK_PASSWORD_ACCEPTED(4, "Password accepted"),
	OK_GOOD_BYE(5, "Good bye!"),
	OK_FILE_PRINTED(6, "File printed"),
	OK_STATISTICS_READY(7, "Statistics service ready"),
	OK_SERVER_STATS_DISPLAYED(8, "Server stats displayed"),
	OK_USER_STATS_DISPLAYED(9, "User stats displayed"),
	OK_ALL_USER_STATS_DISPLAYED(10, "All users stats displayed"),
	
	ERR_UNRECOGNIZED_COMMAND(100, "Unrecognized command"),
	ERR_INVALID_PARAMETERS_ARGUMENTS(101, "Invalid parameters: missing arguments"),
	ERR_INVALID_PARAMETERS_FILE(102, "Invalid parameters: file does not exists"),
	ERR_INVALID_PARAMETERS_USER(103, "Invalid parameters: unrecognized user"),
	ERR_INVALID_PARAMETERS_NUMBER(104, "Invalid parameters: not a number"),
	ERR_TOO_MANY_ATTEMPTS(110, "Too many unsuccessful attempts. Bye!"),
	ERR_INVALID_PASSWORD(111, "Invalid password"),
	ERR_INTERNAL_SERVER_ERROR(666, "Internal server error!");
	 
	   private int code;
	   private String message;
	 
	   StatusCodes(int code, String message){
	      this.code = code;
	      this.message = message;
	   }

	   public int getCode(){
		   return code;
	   }
	 
	   public String getMessage(){
	      return message;
	   }
	   
}
