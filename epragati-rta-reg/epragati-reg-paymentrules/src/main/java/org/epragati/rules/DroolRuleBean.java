
package org.epragati.rules;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author saikiran.kola
 *
 */

public class DroolRuleBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<String> fileLocation;

	private List<String> file;

	/**
	 * @return the fileLocation
	 */
	public List<String> getFileLocation() {
		return fileLocation;
	}

	/**
	 * @param fileLocation
	 *            the fileLocation to set
	 */
	public void setFileLocation(List<String> fileLocation) {
		this.fileLocation = fileLocation;
	}

	/**
	 * @return the file
	 */
	public List<String> getFile() {
		return file;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public void setFile(List<String> file) {
		this.file = file;
	}

}