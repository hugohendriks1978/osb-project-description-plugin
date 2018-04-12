package nl.redrock.maven.plugins.misc;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * XML Utility class 
 * @author Hugo Hendriks
 */
public class XMLUtils {

    private static final Logger logger = Logger.getLogger(XMLUtils.class.getName());

    public static void updateDescription(String aFile, String aDescription) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(aFile);

            // Get the root element
            Node xmlFragment = doc.getFirstChild();

            // Get the staff element by tag name directly
            Node description = doc.getElementsByTagName("proj:description").item(0);

            if (description != null) {// update the description element
                description.setTextContent(aDescription);
            } else {
                //get the isImmutable node
                Node isImmutable = doc.getElementsByTagName("proj:isImmutable").item(0);
                Element projDescription = doc.createElement("proj:description");
                projDescription.appendChild(doc.createTextNode(aDescription));
                //append the description before the isImmutable node
                xmlFragment.insertBefore(projDescription, isImmutable);
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(aFile));
            transformer.transform(source, result);

            logger.log(Level.INFO, "Description updated");

        } catch (ParserConfigurationException pce) {
            logger.log(Level.SEVERE, pce.getLocalizedMessage());
        } catch (TransformerException tfe) {
            logger.log(Level.SEVERE, tfe.getLocalizedMessage());
        } catch (IOException ioe) {
            logger.log(Level.SEVERE, ioe.getLocalizedMessage());
        } catch (SAXException sae) {
            logger.log(Level.SEVERE, sae.getLocalizedMessage());
        }
    }
}
