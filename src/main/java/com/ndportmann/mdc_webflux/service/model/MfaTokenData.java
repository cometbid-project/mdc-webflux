/**
 * 
 */
package com.ndportmann.mdc_webflux.service.model;

import java.io.Serializable;

/**
 * @author Gbenga
 *
 */
public class MfaTokenData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8929480063881926996L;

	private String qrCode;
	private String mfaCode;

	public MfaTokenData() {
	}

	public String getQrCode() {
		return qrCode;
	}

	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}

	public String getMfaCode() {
		return mfaCode;
	}

	public void setMfaCode(String mfaCode) {
		this.mfaCode = mfaCode;
	}

	public MfaTokenData(String qrCode, String mfaCode) {
		this.qrCode = qrCode;
		this.mfaCode = mfaCode;
	}
}