package com.nixsolutions;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;

public class Sax {
    private boolean isEven = false;
    private Stack<String> parentTag;
    private Stack<Integer> counter;
    private String lastTag;
    private SAXParser parser;

    public void parse(String src, String dest)
            throws SAXException, TransformerException, FileNotFoundException,
            ParserConfigurationException {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        parser = factory.newSAXParser();
        XMLReader xr = new XMLFilterImpl(parser.getXMLReader()) {

            @Override
            public void startDocument() {
                parentTag = new Stack<>();
                counter = new Stack<>();
                counter.add(1);
            }

            @Override
            public void startElement(String uri, String localName,
                    String qName, Attributes atts) throws SAXException {
                if (isEven) {
                    return;
                }
                if (counter.peek() % 2 == 0) {
                    isEven = true;
                    lastTag = qName;
                    counter.add(counter.pop() + 1);
                } else {
                    parentTag.add(qName);
                    counter.add(1);
                    super.startElement(uri, localName, qName, atts);
                }
            }

            @Override
            public void endElement(String uri, String localName,
                    String qName) throws SAXException {
                if (qName.equals(lastTag)) {
                    isEven = false;
                } else if (qName.equals(parentTag.peek())) {
                    super.endElement(uri, localName, qName);
                    parentTag.pop();
                    counter.pop();
                    counter.add(counter.pop() + 1);
                }
            }

            @Override
            public void characters(char[] ch, int start, int length)
                    throws SAXException {
                if (!isEven) {
                    super.characters(ch, start, length);
                }
            }
        };

        Source src2 = new SAXSource(xr, new InputSource(src));
        OutputStream out = new FileOutputStream(dest);
        Result res = new StreamResult(out);
        TransformerFactory.newInstance().newTransformer().transform(src2, res);
    }

    public static void main(String[] args) throws ParserConfigurationException {
        Sax sax = new Sax();
        try {
            sax.parse("src/main/resources/source.xml",
                    "src/main/resources/result.xml");
        } catch (SAXException | TransformerException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}

