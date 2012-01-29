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



package streamripper.gui;

//~--- JDK imports ------------------------------------------------------------

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.lang.reflect.Method;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JWindow;
import javax.swing.Timer;

/**
 *
 * @author cbenger
 */
public class SongInfo extends JWindow implements ActionListener {
    private final String END          = "</body></html>";
    private final short  FRAME_HEIGHT = 100;    // Height of the frame
    private final short  FRAME_WIDTH  = 200;    // Width of the frame
    private final String HEAD         = "<html>" + "<head>" + "<style type=\"text/css\">"
                                        + "body{background:black;padding:10px;}" + "h1{color:white;}"
                                        + "p{color:white;margin-top:-20px;}" + "</style>" + "</head>" + "<body>";
    private Timer       fadeTimer;         // Timer for fading the jframe
    private float       fadeValue;         // Current fade value of the jframe
    private JEditorPane songEditorPane;    // Displays the current songs information

    /**
     * Constructor for SongInfo
     */
    public SongInfo() {
        this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        this.initGui();
        this.initEventListeners();
        this.getContentPane().setBackground(Color.black);
        this.fadeTimer = new Timer(50, this);

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

        // Places the frame in the top right corner
        this.setLocation(dimension.width - FRAME_WIDTH - 100, FRAME_HEIGHT - 50);
    }

    /**
     * Initializes the gui
     */
    private void initGui() {
        songEditorPane = new JEditorPane("text/html", "");
        songEditorPane.setEditable(false);

        // Adds components to the window
        add(songEditorPane);
    }

    /**
     * Sets up the event handlers for the window
     */
    private void initEventListeners() {
        songEditorPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (fadeTimer.isRunning()) {
                    fadeValue = 1f;
                }
            }
        });
    }

    /**
     * Sets the information for the current playing song
     * and starts a timer to fade out the frame
     * @param title the title of the song
     * @param artist the artist of the song
     */
    public void setSongInfo(String title, String artist) {

        // Constructs to song information
        String html = HEAD + "<h1>" + title + "</h1><p>By:" + artist + "</p>" + END;

        songEditorPane.setText(html);
        setVisible(true);

        // Sets the default fade value and starts the timer
        fadeValue = 1f;
        fadeTimer.start();
    }

    /**
     * Handles ActionPerformed events
     * @param ee ActionEvent arguments
     */
    public void actionPerformed(ActionEvent ee) {

        // If the fade value > 0 keep fading else stop timer and hide frame
        if (fadeValue > 0) {
            try {

                // Checks if user has support for fading
                Class<?> awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
                Method   mSetWindowOpacity = awtUtilitiesClass.getMethod("setWindowOpacity", Window.class, float.class);

                mSetWindowOpacity.invoke(null, this, Float.valueOf(fadeValue));
                fadeValue -= .01f;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "You do not have jre update 10. Please update your Java.");
            }
        } else {
            fadeTimer.stop();
            setVisible(false);
        }
    }
}
