/**
 * 
 */
package com.test.admin.hateoas;

import java.util.List;

import org.springframework.http.MediaType;

import com.google.common.base.Preconditions;
import com.ndportmann.mdc_webflux.service.model.Foo;
import com.thoughtworks.xstream.XStream;

/**
 * @author Gbenga
 *
 */
public final class XStreamMarshaller implements IMarshaller {

    private XStream xstream;

    public XStreamMarshaller() {
        super();

        xstream = new XStream();
        xstream.autodetectAnnotations(true);
        xstream.processAnnotations(Foo.class);
    }

    // API
    @Override
    public final <T> String encode(final T resource) {
        Preconditions.checkNotNull(resource);
        return xstream.toXML(resource);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <T> T decode(final String resourceAsString, final Class<T> clazz) {
        Preconditions.checkNotNull(resourceAsString);
        return (T) xstream.fromXML(resourceAsString);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> decodeList(final String resourcesAsString, final Class<T> clazz) {
        return this.decode(resourcesAsString, List.class);
    }

    @Override
    public final String getMime() {
        return MediaType.APPLICATION_XML.toString();
    }

}