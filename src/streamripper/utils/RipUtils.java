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



package streamripper.utils;

//~--- non-JDK imports --------------------------------------------------------

import streamripper.io.StationInfo;

//~--- JDK imports ------------------------------------------------------------

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 *
 * @author cbenger
 */
public class RipUtils {

    /**
     * Parses the stream URL from a pls file
     * @param station the station open to obtain the pls link and to set the streams
     * @throws MalformedURLException pls link was malformed
     * @throws IOException general IO exception
     */
    public static void parsePls(StationInfo station) throws MalformedURLException, IOException {
        
        URL            url     = new URL(station.getPlsLink());
        BufferedReader in      = new BufferedReader(new InputStreamReader(url.openStream()));
        String         line    = null;
        String         plsData = "";
        ArrayList<String> streams = new ArrayList<String>();

        //Read in all pls data
        while ((line = in.readLine()) != null) {
            plsData += line;
        }

        //Splits the data by the file delimiter
        String[] links = plsData.split("File");

        //Grab the ip and port from each split string
        for (String s : links) {
            if (s.contains("http://")) {
                s = s.substring(s.indexOf("http://") + "http://".length() , s.indexOf("Title"));
                streams.add(s);
            }
        }

        //If stream was empty throw an exception
        if(streams.isEmpty())
            throw new IOException();

        //Sets the stations urls
        station.setStationUrls(streams);
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
