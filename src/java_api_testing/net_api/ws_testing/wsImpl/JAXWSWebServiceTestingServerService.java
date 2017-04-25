
package java_api_testing.net_api.ws_testing.wsImpl;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "JAXWS_WebServiceTesting_ServerService", targetNamespace = "http://ws_testing.net_api.java_api_testing/", wsdlLocation = "http://localhost:8080/myservice?WSDL")
public class JAXWSWebServiceTestingServerService
    extends Service
{

    private final static URL JAXWSWEBSERVICETESTINGSERVERSERVICE_WSDL_LOCATION;
    private final static WebServiceException JAXWSWEBSERVICETESTINGSERVERSERVICE_EXCEPTION;
    private final static QName JAXWSWEBSERVICETESTINGSERVERSERVICE_QNAME = new QName("http://ws_testing.net_api.java_api_testing/", "JAXWS_WebServiceTesting_ServerService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://localhost:8080/myservice?WSDL");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        JAXWSWEBSERVICETESTINGSERVERSERVICE_WSDL_LOCATION = url;
        JAXWSWEBSERVICETESTINGSERVERSERVICE_EXCEPTION = e;
    }

    public JAXWSWebServiceTestingServerService() {
        super(__getWsdlLocation(), JAXWSWEBSERVICETESTINGSERVERSERVICE_QNAME);
    }

    public JAXWSWebServiceTestingServerService(WebServiceFeature... features) {
        super(__getWsdlLocation(), JAXWSWEBSERVICETESTINGSERVERSERVICE_QNAME, features);
    }

    public JAXWSWebServiceTestingServerService(URL wsdlLocation) {
        super(wsdlLocation, JAXWSWEBSERVICETESTINGSERVERSERVICE_QNAME);
    }

    public JAXWSWebServiceTestingServerService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, JAXWSWEBSERVICETESTINGSERVERSERVICE_QNAME, features);
    }

    public JAXWSWebServiceTestingServerService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public JAXWSWebServiceTestingServerService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns JAXWSWebServiceTestingServer
     */
    @WebEndpoint(name = "JAXWS_WebServiceTesting_ServerPort")
    public JAXWSWebServiceTestingServer getJAXWSWebServiceTestingServerPort() {
        return super.getPort(new QName("http://ws_testing.net_api.java_api_testing/", "JAXWS_WebServiceTesting_ServerPort"), JAXWSWebServiceTestingServer.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns JAXWSWebServiceTestingServer
     */
    @WebEndpoint(name = "JAXWS_WebServiceTesting_ServerPort")
    public JAXWSWebServiceTestingServer getJAXWSWebServiceTestingServerPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://ws_testing.net_api.java_api_testing/", "JAXWS_WebServiceTesting_ServerPort"), JAXWSWebServiceTestingServer.class, features);
    }

    private static URL __getWsdlLocation() {
        if (JAXWSWEBSERVICETESTINGSERVERSERVICE_EXCEPTION!= null) {
            throw JAXWSWEBSERVICETESTINGSERVERSERVICE_EXCEPTION;
        }
        return JAXWSWEBSERVICETESTINGSERVERSERVICE_WSDL_LOCATION;
    }

}