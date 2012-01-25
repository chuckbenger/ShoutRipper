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

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cbenger
 */
public class RipWriter {


    private BufferedOutputStream out;

    public RipWriter(String name){
        try {
            name = name.replaceAll("/" ,"");
            out = new BufferedOutputStream(new FileOutputStream("c:/" + name + ".mp3"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RipWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void write(byte [] data,int offset,int length){
        try {
            out.write(data,offset,length);
        } catch (IOException ex) {
            Logger.getLogger(RipWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void close(){
        try {
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(RipWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
