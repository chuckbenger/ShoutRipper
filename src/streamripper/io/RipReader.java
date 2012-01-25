/*
 *  Copyright 2012 cbenger.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */



package streamripper.io;

//~--- JDK imports ------------------------------------------------------------

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.net.Socket;
import java.net.UnknownHostException;

/**
 * RipReader is used to read meta data and music data
 * from a shoutcast stream
 * @author cbenger
 */
public class RipReader implements Runnable {
    private final short          MAX_BUFFER      = 1024;              // Max buffer size
    private final String         SONG_NAME_FIELD = "StreamTitle=";    // The field containing the current songs title
    private byte[]               buffer;                              // The buffer for holding data
    private int                  counter;                             // Number of bytes read
    private String               currentSong;                         // Current playing song
    private BufferedInputStream  in;                                  // Input stream
    private int                  metaint;                             // Rate in which meta data is sent from server
    private BufferedOutputStream out;                                 // output stream
    private Socket               socket;                              // Socket to connect to a server
    private String               streamName;                          // The name of the stream
    private String               type;                                // The type of the audio stream
    private RipWriter            writer;
   

    /**
     * Constructor for RipReader
     * @param url the url of the shoutcast server
     * @throws UnknownHostException specified host wasn't found
     * @throws IOException General IO error connecting to server
     */
    public RipReader(String url) throws UnknownHostException, IOException {
        String[] split = url.split(":");    // Splits the ip and port

        socket = new Socket(split[0], Integer.parseInt(split[1]));      // Connects to server
        in     = new BufferedInputStream(socket.getInputStream());      // Sets up the input stream
        out    = new BufferedOutputStream(socket.getOutputStream());    // Sets up the output stream
        System.out.println("Connected to " + url);
        sendMetaDataRequest(url);
        buffer = new byte[MAX_BUFFER];
        new Thread(this).start();
    }

    /**
     * Sends a meta data request to the server
     */
    private void sendMetaDataRequest(String host) throws IOException {
        String httpReq = "GET / HTTP/1.1\r\n" + "Host: " + host + "\r\n" + "Connection: close\r\n"
                         + "icy-metadata: 1\r\n" + "transferMode.dlna.org: Streaming\n\r\n" + "HEAD / HTTP/1.1\r\n"
                         + "Host: " + host + "\r\n" + "User-Agent: StreamRipper\n\r\n";

        out.write(httpReq.getBytes());
        out.flush();
        System.out.println("Send meta data request");
    }

    public void run() {
        boolean firstRead = true;
        int     offset    = 0;

        while (true) {
            try {

                // Reads data into the buffer until full then process whats in the buffer
                int numRead = in.read(buffer, offset, buffer.length - offset);

                offset  += numRead;    // Increases the offset
                counter += numRead;    // The total number of bytes read
                
                if (offset == buffer.length) {

                    // If this is the first read then read the stream information
                    // else hanlde the data
                    if (firstRead) {
                        getStreamInfo(new String(buffer));
                        firstRead = false;
                    } else {
                        handle(offset);
                    }

                    offset = 0;
                }
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
    }

    /**
     * Handles the specified number of bytes
     * @param read
     */
    private void handle(int read) throws IOException {
        
        if (counter >= metaint) {
            int position = read - (counter - metaint);
            int length   = buffer[position + 1] * 16;

            if (length > 0) {
                byte[] metadata = new byte[length];

                System.arraycopy(buffer, position + 1, metadata, 0, length);

                String data = new String(metadata);

                if (data.contains(SONG_NAME_FIELD)) {
                    int    start    = data.indexOf(SONG_NAME_FIELD) + SONG_NAME_FIELD.length() + 1;
                    String tempName = data.substring(start, data.indexOf(';')-1);

                    if (!tempName.equals(currentSong)) {
                        currentSong = tempName;
                        System.out.println("Downloading => " + currentSong);
                        if(writer != null)
                            writer.close();
                        writer = new RipWriter(currentSong);
                    }
                }
            }
            counter = read - (position + length + 1);
        } else{
            if(writer != null)
             writer.write(buffer,0,read);
        }
    }

    /**
     * Gets the stream information
     * @param data the string data to parse data from
     */
    private void getStreamInfo(String data) throws UnsupportedEncodingException {

        // System.out.println(data);
        final String NAME     = "icy-name:";
        final String META_INT = "icy-metaint:";
        final String TYPE     = "content-type:";
        final String BIT_RATE = "icy-br:192";
        int          start;
        String[]     splitData = data.split("\n");

        for (String s : splitData) {
            if (s.contains(NAME)) {
                start      = s.indexOf(NAME) + NAME.length();
                streamName = s.substring(start, s.length());
            } else if (s.contains(TYPE)) {
                start = s.indexOf(TYPE) + TYPE.length();
                type  = s.substring(start, s.length());
            } else if (s.contains(META_INT)) {
                start   = s.indexOf(META_INT) + META_INT.length();
                metaint = Integer.parseInt(s.substring(start, s.length()).trim());
            } else if (s.contains(BIT_RATE)) {
                counter -= data.substring(0, data.indexOf(BIT_RATE)).getBytes("UTF8").length;
                counter -= s.getBytes("UTF8").length + 2;
            }
        }

        System.out.println("Station: " + streamName + "\nType: " + type);
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
