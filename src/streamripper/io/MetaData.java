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

/**
 * Meta provides a class for encapsulating meta-data
 * received from a stream
 * @author cbenger
 */
public final class MetaData {
    public final static int SONG_CHANGE = 1;    // Song changed type
    private int             type;               // Type of meta data
    private String          value;              // Value of the meta data

    /**
     * Constructor for MetaData
     * @param type the type of meta data
     * @param value the value of the meta data
     */
    public MetaData(int type, String value) {
        this.type  = type;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
