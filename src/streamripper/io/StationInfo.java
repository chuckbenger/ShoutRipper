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

import java.util.ArrayList;

/**
 * StationInfo is used to hold information about a particular station
 * @author cbenger
 */
public final class StationInfo implements Comparable<StationInfo> {
    private String            Bitrate;        // Station bitrate
    private ArrayList<String> StationUrls;    // Url's of servers
    private String            genre;          // Genre of the station
    private String            listeners;      // Number of listeners
    private String            name;           // Name of the station
    private String            plsLink;        // Link to the pls file
    private String            type;           // Station audio format

    /**
     * Constructor for StationInfo
     * @param plsLink Link to the pls file
     * @param name Name of the station
     * @param genre Genre of the station
     * @param listeners Number of listeners
     * @param Bitrate Station bitrate
     * @param type Station audio format
     */
    public StationInfo(String plsLink, String name, String genre, String listeners, String Bitrate, String type) {
        this.plsLink   = plsLink;
        this.name      = name;
        this.genre     = genre;
        this.listeners = listeners;
        this.Bitrate   = Bitrate;
        this.type      = type;
        StationUrls    = new ArrayList<String>();
    }

    public String getBitrate() {
        return Bitrate;
    }

    public String getGenre() {
        return genre;
    }

    public String getListeners() {
        return listeners;
    }

    public String getName() {
        return name;
    }

    public String getPlsLink() {
        return plsLink;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "StationInfo{" + "plsLink=" + plsLink + "name=" + name + "genre=" + genre + "listeners=" + listeners
               + "Bitrate=" + Bitrate + "type=" + type + '}';
    }

    public ArrayList<String> getStationUrls() {
        return StationUrls;
    }

    public void setStationUrls(ArrayList<String> StationUrls) {
        this.StationUrls = StationUrls;
    }

    /**
     * Gets a array representing the Station information
     * @return returns a string array
     */
    public String[] toArray() {
        return new String[] { name, genre, listeners, Bitrate, type };
    }

    /**
     * Compares two station object to determine if they are the same
     * @param o the object to compare to
     * @return returns 0 if they are the same
     */
    public int compareTo(StationInfo o) {
        return (this.name.equals(o.name) && this.listeners.equals(o.listeners) && this.genre.equals(o.genre)
                && this.type.equals(o.type) && this.Bitrate.equals(o.Bitrate))
               ? 0
               : 1;
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
