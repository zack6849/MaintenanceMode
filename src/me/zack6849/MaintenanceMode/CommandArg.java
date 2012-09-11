package me.zack6849.MaintenanceMode;

public enum CommandArg
{
	DISABLE("DISABLE"),
	ENABLE("ENABLE"),
	RELOAD("RELOAD"),
	DEFAULT(""),
	HELP("HELP"),
	STATUS("STATUS"),
	INFO("INFO");
	@SuppressWarnings("unused")
	private final String argument;
	
	CommandArg(String argument)
	{
		this.argument = argument;
	}
}