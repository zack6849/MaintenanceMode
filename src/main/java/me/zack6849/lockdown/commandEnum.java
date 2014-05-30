package me.zack6849.lockdown;

public enum commandEnum{	
	ENABLE("ENABLE"),
	DISABLE("DISABLE"),
	RELOAD("RELOAD"),
	STATUS("STATUS"),
	HELP("HELP"),
	INFO("INFO"),
	DEFAULT(""),
	URGENT("URGENT");
	@SuppressWarnings("unused")
	private final String string;
	commandEnum(String string){
		this.string = string;
	}
}
