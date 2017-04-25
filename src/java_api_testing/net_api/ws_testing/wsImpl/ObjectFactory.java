
package java_api_testing.net_api.ws_testing.wsImpl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the java_api_testing.net_api.ws_testing.wsImpl package. 
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

    private final static QName _GetMyTransferObject_QNAME = new QName("http://ws_testing.net_api.java_api_testing/", "getMyTransferObject");
    private final static QName _SetMyTransferObjectResponse_QNAME = new QName("http://ws_testing.net_api.java_api_testing/", "setMyTransferObjectResponse");
    private final static QName _GetMyTransferObjectResponse_QNAME = new QName("http://ws_testing.net_api.java_api_testing/", "getMyTransferObjectResponse");
    private final static QName _SetMyTransferObject_QNAME = new QName("http://ws_testing.net_api.java_api_testing/", "setMyTransferObject");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: java_api_testing.net_api.ws_testing.wsImpl
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SetMyTransferObject }
     * 
     */
    public SetMyTransferObject createSetMyTransferObject() {
        return new SetMyTransferObject();
    }

    /**
     * Create an instance of {@link SetMyTransferObjectResponse }
     * 
     */
    public SetMyTransferObjectResponse createSetMyTransferObjectResponse() {
        return new SetMyTransferObjectResponse();
    }

    /**
     * Create an instance of {@link GetMyTransferObjectResponse }
     * 
     */
    public GetMyTransferObjectResponse createGetMyTransferObjectResponse() {
        return new GetMyTransferObjectResponse();
    }

    /**
     * Create an instance of {@link GetMyTransferObject }
     * 
     */
    public GetMyTransferObject createGetMyTransferObject() {
        return new GetMyTransferObject();
    }

    /**
     * Create an instance of {@link MyTransferObject }
     * 
     */
    public MyTransferObject createMyTransferObject() {
        return new MyTransferObject();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMyTransferObject }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws_testing.net_api.java_api_testing/", name = "getMyTransferObject")
    public JAXBElement<GetMyTransferObject> createGetMyTransferObject(GetMyTransferObject value) {
        return new JAXBElement<GetMyTransferObject>(_GetMyTransferObject_QNAME, GetMyTransferObject.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetMyTransferObjectResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws_testing.net_api.java_api_testing/", name = "setMyTransferObjectResponse")
    public JAXBElement<SetMyTransferObjectResponse> createSetMyTransferObjectResponse(SetMyTransferObjectResponse value) {
        return new JAXBElement<SetMyTransferObjectResponse>(_SetMyTransferObjectResponse_QNAME, SetMyTransferObjectResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMyTransferObjectResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws_testing.net_api.java_api_testing/", name = "getMyTransferObjectResponse")
    public JAXBElement<GetMyTransferObjectResponse> createGetMyTransferObjectResponse(GetMyTransferObjectResponse value) {
        return new JAXBElement<GetMyTransferObjectResponse>(_GetMyTransferObjectResponse_QNAME, GetMyTransferObjectResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetMyTransferObject }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws_testing.net_api.java_api_testing/", name = "setMyTransferObject")
    public JAXBElement<SetMyTransferObject> createSetMyTransferObject(SetMyTransferObject value) {
        return new JAXBElement<SetMyTransferObject>(_SetMyTransferObject_QNAME, SetMyTransferObject.class, null, value);
    }

}
