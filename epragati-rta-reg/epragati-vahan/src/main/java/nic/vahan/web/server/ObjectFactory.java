
package nic.vahan.web.server;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the nic.vahan.web.server package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetDetailsResponse_QNAME = new QName("http://server.web.vahan.nic/", "getDetailsResponse");
    private final static QName _GetEngineDetails_QNAME = new QName("http://server.web.vahan.nic/", "getEngineDetails");
    private final static QName _GetEngineDetailsResponse_QNAME = new QName("http://server.web.vahan.nic/", "getEngineDetailsResponse");
    private final static QName _ParseCurrentDateToString_QNAME = new QName("http://server.web.vahan.nic/", "parseCurrentDateToString");
    private final static QName _GetChasisDetailsResponse_QNAME = new QName("http://server.web.vahan.nic/", "getChasisDetailsResponse");
    private final static QName _GetDetails_QNAME = new QName("http://server.web.vahan.nic/", "getDetails");
    private final static QName _ParseCurrentDateToStringResponse_QNAME = new QName("http://server.web.vahan.nic/", "parseCurrentDateToStringResponse");
    private final static QName _GetChasisDetails_QNAME = new QName("http://server.web.vahan.nic/", "getChasisDetails");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: nic.vahan.web.server
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetEngineDetailsResponse }
     * 
     */
    public GetEngineDetailsResponse createGetEngineDetailsResponse() {
        return new GetEngineDetailsResponse();
    }

    /**
     * Create an instance of {@link ParseCurrentDateToString }
     * 
     */
    public ParseCurrentDateToString createParseCurrentDateToString() {
        return new ParseCurrentDateToString();
    }

    /**
     * Create an instance of {@link GetDetailsResponse }
     * 
     */
    public GetDetailsResponse createGetDetailsResponse() {
        return new GetDetailsResponse();
    }

    /**
     * Create an instance of {@link GetEngineDetails }
     * 
     */
    public GetEngineDetails createGetEngineDetails() {
        return new GetEngineDetails();
    }

    /**
     * Create an instance of {@link GetChasisDetailsResponse }
     * 
     */
    public GetChasisDetailsResponse createGetChasisDetailsResponse() {
        return new GetChasisDetailsResponse();
    }

    /**
     * Create an instance of {@link ParseCurrentDateToStringResponse }
     * 
     */
    public ParseCurrentDateToStringResponse createParseCurrentDateToStringResponse() {
        return new ParseCurrentDateToStringResponse();
    }

    /**
     * Create an instance of {@link GetDetails }
     * 
     */
    public GetDetails createGetDetails() {
        return new GetDetails();
    }

    /**
     * Create an instance of {@link GetChasisDetails }
     * 
     */
    public GetChasisDetails createGetChasisDetails() {
        return new GetChasisDetails();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.web.vahan.nic/", name = "getDetailsResponse")
    public JAXBElement<GetDetailsResponse> createGetDetailsResponse(GetDetailsResponse value) {
        return new JAXBElement<GetDetailsResponse>(_GetDetailsResponse_QNAME, GetDetailsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetEngineDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.web.vahan.nic/", name = "getEngineDetails")
    public JAXBElement<GetEngineDetails> createGetEngineDetails(GetEngineDetails value) {
        return new JAXBElement<GetEngineDetails>(_GetEngineDetails_QNAME, GetEngineDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetEngineDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.web.vahan.nic/", name = "getEngineDetailsResponse")
    public JAXBElement<GetEngineDetailsResponse> createGetEngineDetailsResponse(GetEngineDetailsResponse value) {
        return new JAXBElement<GetEngineDetailsResponse>(_GetEngineDetailsResponse_QNAME, GetEngineDetailsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParseCurrentDateToString }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.web.vahan.nic/", name = "parseCurrentDateToString")
    public JAXBElement<ParseCurrentDateToString> createParseCurrentDateToString(ParseCurrentDateToString value) {
        return new JAXBElement<ParseCurrentDateToString>(_ParseCurrentDateToString_QNAME, ParseCurrentDateToString.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetChasisDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.web.vahan.nic/", name = "getChasisDetailsResponse")
    public JAXBElement<GetChasisDetailsResponse> createGetChasisDetailsResponse(GetChasisDetailsResponse value) {
        return new JAXBElement<GetChasisDetailsResponse>(_GetChasisDetailsResponse_QNAME, GetChasisDetailsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.web.vahan.nic/", name = "getDetails")
    public JAXBElement<GetDetails> createGetDetails(GetDetails value) {
        return new JAXBElement<GetDetails>(_GetDetails_QNAME, GetDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParseCurrentDateToStringResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.web.vahan.nic/", name = "parseCurrentDateToStringResponse")
    public JAXBElement<ParseCurrentDateToStringResponse> createParseCurrentDateToStringResponse(ParseCurrentDateToStringResponse value) {
        return new JAXBElement<ParseCurrentDateToStringResponse>(_ParseCurrentDateToStringResponse_QNAME, ParseCurrentDateToStringResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetChasisDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.web.vahan.nic/", name = "getChasisDetails")
    public JAXBElement<GetChasisDetails> createGetChasisDetails(GetChasisDetails value) {
        return new JAXBElement<GetChasisDetails>(_GetChasisDetails_QNAME, GetChasisDetails.class, null, value);
    }

}
