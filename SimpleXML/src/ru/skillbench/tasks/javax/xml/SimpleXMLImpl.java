package ru.skillbench.tasks.javax.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;

import static javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION;

public class SimpleXMLImpl implements SimpleXML {
    /**
     * С помощью DOM API в Java-коде создать XML документ вида "&lt;tagName&gt;textNode&lt;/tagName&gt;".<br/>
     * В частности, для вызова createXML("root","&lt;R&amp;D&gt;") должно вернуться &lt;root&gt;&amp;lt;R&amp;amp;D&amp;gt;&lt;/root&gt;.<br/>
     * Требования:<br/>
     * - Результат должен быть корректным (well-formed) XML документом.<br/>
     * - Никаких переводов строк или других дополнительных символов не должно быть добавлено в textNode.<br/>
     * Правильный подход к решению:<br/>
     * - Использовать именно DOM, а не писать логику обработки спецсимволов вручную.<br/>
     * - С целью удаления в документе декларации "&lt;?xml...?&gt;" следует использовать метод
     * {@link Transformer#setOutputProperty(String, String)} для свойства OMIT_XML_DECLARATION.
     *
     * @param tagName  Имя тега элемента
     * @param textNode Текстовое содержимое тега.
     * @return Корректный XML документ без декларации "&lt;?xml...?&gt;"
     */
    @Override
    public String createXML(String tagName, String textNode) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document document = builder.newDocument();

        Element node = document.createElement(tagName);
        node.appendChild(document.createTextNode(textNode));
        document.appendChild(node);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        transformer.setOutputProperty(OMIT_XML_DECLARATION, "yes");

        DOMSource domSource = new DOMSource(document);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            transformer.transform(domSource, new StreamResult(baos));
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }

    /**
     * С помощью SAX API проверить, что во входящем потоке содержится корректный (well-formed) XML-документ.<br/>
     * В качестве результата вернуть имя корневого элемента документа,
     * а в случае ошибки (если документ не well-formed) бросить {@link SAXException}.<br/>
     * Требование: Потребляемая память не должна зависеть от размера входящего документа.<br/>
     * Примечание: Не следует требовать от документа корректности пространства имен
     * (в имени элемента может использоваться namespace, но без объявления).
     *
     * @param xmlStream Поток с XML документом
     * @return Имя корневого элемента.
     */
    @Override
    public String parseRootElement(InputStream xmlStream) throws SAXException {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = null;
        try {
            saxParser = saxParserFactory.newSAXParser();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        XMLReader xmlReader = saxParser.getXMLReader();
        GetStartDocumentName getStartDocumentName = new GetStartDocumentName();
        xmlReader.setContentHandler(getStartDocumentName);
        try {
            xmlReader.parse(new InputSource(xmlStream));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return getStartDocumentName.getName();
    }

    private static class GetStartDocumentName extends DefaultHandler {
        private String name;
        private boolean nameIsSet;

        String getName() {
            return name;
        }

        @Override
        public void startDocument() throws SAXException {
            name = "";
        }

        @Override
        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) throws SAXException {
            if (!nameIsSet) {
                name = qName;
                nameIsSet = true;
            }
        }
    }

    /*public static void main(String[] args) throws SAXException {
        System.out.println(new SimpleXMLImpl().createXML("root", "&lt;R&amp;D&gt;"));
        String str = """
                <baeldung>
                    <articles>
                        <article>
                            <title>Parsing an XML File Using SAX Parser</title>
                            <content>SAX Parser's Lorem ipsum...</content>
                        </article>
                        <article>
                            <title>Parsing an XML File Using DOM Parser</title>
                            <content>DOM Parser's Lorem ipsum...</content>
                        </article>
                        <article>
                            <title>Parsing an XML File Using StAX Parser</title>
                            <content>StAX's Lorem ipsum...</content>
                        </article>
                    </articles>
                </baeldung>""";
        System.out.println(new SimpleXMLImpl().parseRootElement(new ByteArrayInputStream(str.getBytes())));
    }*/
}
