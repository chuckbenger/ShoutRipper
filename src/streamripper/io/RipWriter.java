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
import streamripper.utils.RipPlayer;

/**
 *
 * @author cbenger
 */
public class RipWriter {


    private BufferedOutputStream out;
    private String directory;
    private int totalBytes;
    private RipPlayer ripPlayer;
  
    public RipWriter(String directory){
        try {
            this.directory = directory;
            out = new BufferedOutputStream(new FileOutputStream(this.directory));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RipWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

  
    public void write(byte [] data,int offset,int length){
        try {
            out.write(data,offset,length);
            totalBytes += length;

            if(ripPlayer == null & totalBytes >= 10860){
               ripPlayer = new RipPlayer(directory);
               ripPlayer.play();
            }else if(ripPlayer != null){

                if(ripPlayer.isComplete()){
                    ripPlayer.close();
                    ripPlayer = null;
                    totalBytes = 0;
                }
            }
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
