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

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

//~--- JDK imports ------------------------------------------------------------

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


/**
 *
 * @author cbenger
 */
public class RipPlayer {
    private Player player;    // Used to play mp3 music

    public RipPlayer(String file) {
        File mp3File = new File(file);

        if (mp3File.exists()) {
            try {
                try {
                    player = new Player(new FileInputStream(mp3File));
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            } catch (JavaLayerException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void play() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    player.play();
                    System.out.println("ending");
                } catch (JavaLayerException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    public void close() {
        player.close();
    }

    public boolean isComplete(){
        return player.isComplete();
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
