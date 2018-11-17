package com.nixsolutions;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.Map;

public class Handler extends DefaultHandler {
    private StringBuilder stringBuilder = new StringBuilder();

    public StringBuilder getStringBuilder() {
        return stringBuilder;
    }

    int even = 0;
    boolean printCharacters = false;
    String causeIgnore = "";
    private String lastStartTag = " ";
    private String lastEndTag = "";
    private String thisTag = " ";
    private boolean tagIsClosed;
    private Map<String, Integer> countOfNodeElements;
    private Map<String, String> nodeParent;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (!thisTag.equals(qName) || !tagIsClosed) {

            if (!tagIsClosed) {
                countOfNodeElements.put(lastStartTag, 1);
                nodeParent.put(qName, lastStartTag);
            } else if (tagIsClosed) {
                nodeParent.put(qName, nodeParent.get(lastEndTag));
                int value = countOfNodeElements.get(nodeParent.get(lastEndTag));
                countOfNodeElements.put(nodeParent.get(lastEndTag), ++value);
            }
            lastStartTag = qName;

        } else {
            nodeParent.put(qName, nodeParent.get(lastStartTag));
            lastStartTag = qName;
            int value = countOfNodeElements.get(nodeParent.get(qName));
            countOfNodeElements.put(nodeParent.get(qName), ++value);
        }
        tagIsClosed = false;
        stringBuilder.append("\n");
        even = countOfNodeElements.get(nodeParent.get(qName));

        if (lastEndTag.equals(causeIgnore)) {
            printCharacters = false;
        }
        if ((even % 2) != 0 && !printCharacters) {
            stringBuilder.append('<').append(qName);

            if (attributes != null) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    stringBuilder.append(" ").append(attributes.getQName(i))
                            .append("=\"").append(attributes.getValue(i))
                            .append('"');
                }
            }
            stringBuilder.append('>');

        } else if (!printCharacters) {
            printCharacters = true;
            causeIgnore = qName;
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        lastEndTag = qName;
        if (!lastStartTag.equals(lastEndTag)) {
            countOfNodeElements.remove(qName);
        }
        tagIsClosed = true;
        even = countOfNodeElements.get(nodeParent.get(qName));

        if (qName.equals(causeIgnore)) {
            printCharacters = false;
        }
        if ((even % 2) != 0 && !printCharacters) {
            stringBuilder.append("</").append(qName).append('>').append(System.lineSeparator());
        }
    }

    public void startDocument() {
        countOfNodeElements = new HashMap<>();
        nodeParent = new HashMap<>();
        stringBuilder.append("<?xml version = \"1.0\" encoding = \"" +
                "UTF-8" + "\"?>" + "\n");
    }

    public void characters(char characters[], int start, int length) {
        if ((even % 2) != 0 && !printCharacters) {
            String characterData = (new String(characters, start, length)).trim();

            if (characterData.indexOf("\n") < 0 && characterData.length() > 0) {
                stringBuilder.append(characterData);
            }
        }
    }
}
