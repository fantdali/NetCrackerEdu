package ru.skillbench.tasks.javax.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.*;

public class XPathCallerImpl implements XPathCaller {
    /**
     * Для заданного отдела выбрать всех сотрудников.
     *
     * @param src     XML документ для поиска
     * @param deptno  Номер отдела deptno
     * @param docType "emp" - для файла типа emp.xml; "emp-hier" - для файла типа emp-hier.xml
     */
    @Override
    public Element[] getEmployees(Document src, String deptno, String docType) {
        String expression = "emp".equals(docType)
                ? "/content/emp/employee"
                : "//employee";
        expression += "[@deptno=" + deptno + "]";
        return nodeListToElementArray(getNodeList(src, expression));
    }

    /**
     * Выбрать имя самого высокооплачиваемого сотрудника.
     *
     * @param src     XML документ для поиска
     * @param docType "emp" - для файла типа emp.xml; "emp-hier" - для файла типа emp-hier.xml
     */
    @Override
    public String getHighestPayed(Document src, String docType) {
        String expression = "emp".equals(docType)
                ? "/employee[not(/sal</employee/sal)]"
                : "/employee[not(/sal</employee/sal)]";
        Element[] element = nodeListToElementArray(getNodeList(src, expression));
        element = nodeListToElementArray(element[0].getElementsByTagName("ename"));
        return element[0].getTextContent();
    }

    /**
     * Выбрать имя самого высокооплачиваемого сотрудника (любого, если таких несколько).
     *
     * @param src     XML документ для поиска
     * @param deptno  Номер отдела deptno
     * @param docType "emp" - для файла типа emp.xml; "emp-hier" - для файла типа emp-hier.xml
     */
    @Override
    public String getHighestPayed(Document src, String deptno, String docType) {
        String expression = "emp".equals(docType)
                ? "//employee[@deptno="+ deptno +"][not(sal<//employee[@deptno="+ deptno +"]/sal)]"
                : "//employee[@deptno="+ deptno +"][not(sal<//employee[@deptno="+ deptno +"]/sal)]";
        Element[] element = nodeListToElementArray(getNodeList(src, expression));
        element = nodeListToElementArray(element[0].getElementsByTagName("ename"));
        return element[0].getTextContent();
    }

    /**
     * Выбрать всех топовых менеджеров (менеджер топовый, если над ним нет менеджера)
     *
     * @param src     XML документ для поиска
     * @param docType "emp" - для файла типа emp.xml; "emp-hier" - для файла типа emp-hier.xml
     */
    @Override
    public Element[] getTopManagement(Document src, String docType) {
        String expression = "emp".equals(docType)
                ? "/content/emp/employee[not(@mgr)]"
                : "/employee";
        return nodeListToElementArray(getNodeList(src, expression));
    }

    /**
     * Выбрать всех сотрудников, не являющихся менеджерами.
     * Считать, что сотрудник не является менеджером, если у него нет подчиненных.
     *
     * @param src     XML документ для поиска
     * @param docType "emp" - для файла типа emp.xml; "emp-hier" - для файла типа emp-hier.xml
     */
    @Override
    public Element[] getOrdinaryEmployees(Document src, String docType) {
        String expression = "emp".equals(docType)
                ? "/content/emp/employee[not(@empno = (/content/emp/employee/@mgr))]"
                : "//employee[not(./employee)]";
        return nodeListToElementArray(getNodeList(src, expression));
    }

    /**
     * Для заданного сотрудника(empno) найти всех коллег, которые в подчинении у того же менеджера.
     *
     * @param src     XML документ для поиска
     * @param empno   Номер сотрудника empno
     * @param docType "emp" - для файла типа emp.xml; "emp-hier" - для файла типа emp-hier.xml
     */
    @Override
    public Element[] getCoworkers(Document src, String empno, String docType) {
        String expression = "emp".equals(docType)
                ? new StringBuilder().append("/content/emp/employee").append("[not(@empno='")
                .append(empno).append("') and ").append("@mgr = (/content/emp/employee[@empno='")
                .append(empno).append("']/@mgr)]").toString()
                : new StringBuilder().append("//employee[@empno='").append(empno)
                .append("']/../").append("employee[not(@empno='").append(empno).append("')]").toString();
        return nodeListToElementArray(getNodeList(src, expression));
    }

    private NodeList getNodeList(Document src, String expression) {
        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList result = null;
        try {
            XPathExpression xPathExpression = xpath.compile(expression);
            result = (NodeList) xPathExpression.evaluate(src, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return result;
    }

    private Element[] nodeListToElementArray(NodeList result) {
        Element[] elements = new Element[result.getLength()];
        for (int i = 0; i < result.getLength(); ++i) {
            if (result.item(i).getNodeType() == Node.ELEMENT_NODE) {
                elements[i] = (Element) result.item(i);
            }
        }
        return elements;
    }

    /*public static void main(String[] args) throws ParserConfigurationException, IOException,
            SAXException, TransformerException {

        XPathCaller xPathCaller = new XPathCallerImpl();
        File xmlFile = new File("src/emp-hier.xml");
        DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);
        //Element[] elements = xPathCaller.getEmployees(doc, "20", "emp");
        //Element[] elements = xPathCaller.getCoworkers(doc, "7654","emp");
        *//*for (Element element : elements) {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(new DOMSource(element), new StreamResult(System.out));
        }*//*
        System.out.println(xPathCaller.getHighestPayed(doc, "20","emp-hier"));
    }*/
}
