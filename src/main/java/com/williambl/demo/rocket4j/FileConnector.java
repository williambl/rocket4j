package com.williambl.demo.rocket4j;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class FileConnector implements Connector {

    public FileConnector(Path path) throws RocketConnectionException {
        this.readAndLoadTracks(path);
    }

    private void readAndLoadTracks(Path path) throws RocketConnectionException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        Document doc;

        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
            doc = docBuilder.parse(path.toFile());
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RocketConnectionException("Could not read the project file", e);
        }

        NodeList trackList = doc.getElementsByTagName("track");
        int totalTracks = trackList.getLength();
        //logger.finer("Total number of tracks: " + totalTracks);

        // Read all tracks
        for(int s = 0; s < totalTracks; s++){
            Element trackElement = (Element)trackList.item(s);
            NodeList keyList = trackElement.getElementsByTagName("key");

            if (keyList.getLength() > 0) {
                // Create new track
                Track track = tracks.getOrCreate(trackElement.getAttribute("name"));
                int totalKeys = keyList.getLength();

                // Add all found keys
                for (int i = 0; i < keyList.getLength(); i++) {
                    Element key = (Element)keyList.item(i);

                    int row = Integer.parseInt(key.getAttribute("row"));
                    float value = Float.parseFloat(key.getAttribute("value"));
                    int keyType = Integer.parseInt(key.getAttribute("interpolation"));

                    track.addOrUpdateKey(new TrackKey(row, value, keyType));
                }

            }
        }
    }

    @Override
    public void update() {
    }

    @Override
    public void requestTrack(String name) {
    }

    @Override
    public void onControllerRowChanged(int row) {
    }

    @Override
    public void setRocket(Rocket4J rocket4J) {}

    @Override
    public void close() throws Exception {

    }
}
