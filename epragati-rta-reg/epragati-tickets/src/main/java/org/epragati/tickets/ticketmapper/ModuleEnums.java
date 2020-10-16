package org.epragati.tickets.ticketmapper;

public enum ModuleEnums {

	REGISTRATION("REGISTRATION"), DL("DL"), FC("FC"), VCR("VCR"), TAX("TAX"), PERT("PERT"),SPECIALNUMBER("SPECIALNUMBER");

	private String modules;

	private ModuleEnums(String modules) {
		this.modules = modules;
	}

	public String getModules() {
		return modules;
	}

	public void setModules(String modules) {
		this.modules = modules;
	}

}
