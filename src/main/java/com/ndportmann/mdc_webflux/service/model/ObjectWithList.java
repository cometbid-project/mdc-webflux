/**
 * 
 */
package com.ndportmann.mdc_webflux.service.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlAccessType;
import lombok.Data;

/**
 * @author Gbenga
 *
 */
@Data
@XmlRootElement(name = "persons")
@XmlSeeAlso({Person.class})
@XmlAccessorType(XmlAccessType.FIELD)
public class ObjectWithList<T> {

	//@XmlElementWrapper(name = "MyGenericList")
	@XmlElement(name = "person")
	private List<T> list = null;

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

}
