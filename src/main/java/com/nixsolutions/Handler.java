package com.nixsolutions;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Handler extends DefaultHandler {
    private StringBuilder xml = new StringBuilder();

    public StringBuilder getStringBuilder() {
        return xml;
    }
    private int even = 0;
    private boolean printCharacters;
    private String causeIgnore = "";
    private String openTag = "open";
    private String closeTag = "close";
    private boolean tagIsClosed;
    private Map<String, Integer> rootNodeCounter;
    private Map<String, String> rootNode;

    private Stack<String> tags = new Stack<>();
    private Stack<Integer> tagCount = new Stack<>();


    @Override
    public void startDocument() {
        rootNodeCounter = new HashMap<>();
        rootNode = new HashMap<>();
        xml.append("<?xml version = \"1.0\" encoding = \"" +
                "UTF-8" + "\"?>" + "\n");
    }
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (!openTag.equals(qName) || !tagIsClosed) {
            if (!tagIsClosed) {
                rootNodeCounter.put(openTag, 1);
                rootNode.put(qName, openTag);
            } else {
                rootNode.put(qName, rootNode.get(closeTag));
                int value = rootNodeCounter.get(rootNode.get(closeTag));
                rootNodeCounter.put(rootNode.get(closeTag), ++value);
            }
            openTag = qName;
        } else {
            rootNode.put(qName, rootNode.get(openTag));
            openTag = qName;
            int value = rootNodeCounter.get(rootNode.get(qName));
            rootNodeCounter.put(rootNode.get(qName), ++value);
        }
        tagIsClosed = false;
        xml.append("\n");
        even = rootNodeCounter.get(rootNode.get(qName));
        if (closeTag.equals(causeIgnore)) {
            printCharacters = false;
        }

        if ((even % 2) != 0 && !printCharacters) {
            xml.append('<').append(qName);
            if (attributes != null) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    xml.append(" ").append(attributes.getQName(i))
                            .append("=\"").append(attributes.getValue(i))
                            .append('"');
                }
            }
            xml.append('>');
        } else if (!printCharacters) {
            printCharacters = true;
            causeIgnore = qName;
        }
    }
    @Override
    public void endElement(String uri, String localName, String qName) {
        closeTag = qName;
        if (!openTag.equals(closeTag)) {
            rootNodeCounter.remove(qName);
        }
        tagIsClosed = true;
        even = rootNodeCounter.get(rootNode.get(qName));
        if (qName.equals(causeIgnore)) {
            printCharacters = false;
        }
        if ((even % 2) != 0 && !printCharacters) {
            xml.append("</").append(qName).append('>'+"\n");
        }
    }
    @Override
    public void characters(char[] characters, int start, int length) {
        if ((even % 2) != 0 && !printCharacters) {
            String characterData = (new String(characters, start, length)).trim();
            if (!characterData.contains("\n") && characterData.length() > 0) {
                xml.append(characterData);
            }
        }
    }
}
