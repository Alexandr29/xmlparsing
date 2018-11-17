package com.nixsolutions;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileWriter;
import java.io.IOException;

public class SaxParser extends DefaultHandler {

    private int count = 0;

    private boolean elementIsEven = true;

    private String evenElement;

    private StringBuilder builder;

    private FileWriter fileWriter;

    private static final String FILE_WITH_RESULT = "src/main/resources/result2.xml";

    public void parse(String filename) throws ParserConfigurationException, SAXException, IOException {
        fileWriter = new FileWriter(FILE_WITH_RESULT);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();

        parser.parse(filename, this);
    }

    @Override
    public void endDocument() {
        try {
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void startElement(String s1, String s2, String elementName, Attributes attributes) {
        if (++count % 2 == 0 && elementIsEven) {
            evenElement = elementName;
            elementIsEven = false;
        }

        if (elementIsEven) {
            builder = new StringBuilder();
            builder.append("<").append(elementName);
            if (attributes != null) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    builder.append(" ").append(attributes.getQName(i))
                            .append("=\"").append(attributes.getValue(i))
                            .append('"');
                }
            }
            builder.append(">");
            try {
                fileWriter.write("\n" + builder.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (elementIsEven) {
            builder = new StringBuilder();
            builder.append("</").append(qName).append('>');
            try {
                fileWriter.write(builder.toString() + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (qName.equals(evenElement) && !elementIsEven) {
            elementIsEven = true;
            evenElement = null;
        }
    }

    @Override
    public void characters(char[] c, int start, int length) {
        if (elementIsEven) {
            builder = new StringBuilder();
            String characterData = (new String(c, start, length)).trim();
            builder.append(characterData);

            try {
                fileWriter.write(builder.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args)
            throws IOException, SAXException, ParserConfigurationException {
        SaxParser parser = new SaxParser();
        parser.parse("src/main/resources/source.xml");
    }
}
