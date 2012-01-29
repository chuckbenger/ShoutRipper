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

//~--- non-JDK imports --------------------------------------------------------

import streamripper.io.MetaData;
import streamripper.io.MetaDataListener;
import streamripper.io.RipReader;
import streamripper.io.StationInfo;

//~--- JDK imports ------------------------------------------------------------

import java.awt.AWTException;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import java.io.IOException;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.MouseInputAdapter;

/**
 * This class provides a graphical interface
 * for downloading mp3 stream
 * @author cbenger
 */
public class StreamRipper implements ActionListener, MetaDataListener {
    private final String         LOGO = "images/logo.gif";    // System tray logo
    private Menu                 downloadsMenu;               // Downloads menu
    private MenuItem             exitItem;                    // Exit menu item
    private MenuItem             optionsMenuItem;             // Options menu item
    private PopupMenu            popupMenu;                   // Popup menu for interaction
    private ArrayList<RipReader> readers;                     // Arraylist containing currently running readers
    private SelectionPopup       selectionPopup;              // Stream selection popup menu
    private SongInfo             songInfo;                    // Used to display song information
    private MenuItem             startAllMenuItem;            // Start all downloads menu item
    private MenuItem             stopAllMenuItem;             // Stop all downloads menu item
    private SystemTray           tray;                        // System try interaction
    private TrayIcon             trayIcon;                    // Icon to display in system tray

    /**
     * Constructor for StreamRipper
     */
    public StreamRipper() {

        // Checks if the system has system tray support
        if (SystemTray.isSupported()) {
            try {
                tray = SystemTray.getSystemTray();    // Gets the system tray
                initMenu();
                trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(LOGO), "Stream Ripper", popupMenu);
                trayIcon.setImageAutoSize(true);
                tray.add(trayIcon);
                selectionPopup = new SelectionPopup(this);
                readers        = new ArrayList<RipReader>();
                songInfo       = new SongInfo();
                initListeners();
            } catch (AWTException ex) {}
        } else {
            JOptionPane.showMessageDialog(null, "System tray not supported\nPlease upgrade to the newest Java version",
                                          "System try not supported", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Initializes the gui components
     */
    private void initMenu() {
        popupMenu        = new PopupMenu();    // Creates a new popup menu
        exitItem         = new MenuItem("Exit");
        downloadsMenu    = new Menu("Downloads");
        stopAllMenuItem  = new MenuItem("Stop All");
        startAllMenuItem = new MenuItem("Start All");
        optionsMenuItem  = new MenuItem("Options");

        // Adds items to download manager menu
        downloadsMenu.add(stopAllMenuItem);
        downloadsMenu.add(startAllMenuItem);

        // Adds items to popup menu
        popupMenu.add(downloadsMenu);
        popupMenu.addSeparator();
        popupMenu.add(optionsMenuItem);
        popupMenu.addSeparator();
        popupMenu.add(exitItem);
    }

    /**
     * Sets up event listeners
     */
    private void initListeners() {
        exitItem.addActionListener(this);
        stopAllMenuItem.addActionListener(this);
        startAllMenuItem.addActionListener(this);
        optionsMenuItem.addActionListener(this);
        trayIcon.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectionPopup.show(null, e.getX(), e.getY());
            }
        });
    }

    /**
     * Main method
     * @param args command line arguments
     */
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(StreamRipper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(StreamRipper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(StreamRipper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(StreamRipper.class.getName()).log(Level.SEVERE, null, ex);
        }

        StreamRipper ripper = new StreamRipper();
    }

    /**
     * Handles components that have action event listeners
     * @param e ActionEvent arguments
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == exitItem) {
            System.exit(0);
        } else if (e.getSource() == stopAllMenuItem) {}
        else if (e.getSource() == startAllMenuItem) {}
        else if (e.getSource() == optionsMenuItem) {}
    }

    /**
     * Starts downloading a specified stream
     * @param stationInfo holds information on the stream
     * @throws IOException
     */
    public void addStreamDownload(StationInfo stationInfo) throws IOException {
        RipReader ripReader = new RipReader(stationInfo, "c:\\", true);

        ripReader.addMetaDataListener(this);
        readers.add(ripReader);
    }

    /**
     * Method called when meta data is received
     * @param data the data received
     */
    public void MetaDataRecieved(MetaData data) {
        if (data.getType() == MetaData.SONG_CHANGE) {
            String[] info = data.getValue().split("-");

            if (info.length >= 2) {
                songInfo.setSongInfo(info[0], info[1]);
            } else {
                songInfo.setSongInfo(info[0], "");
            }
        }
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
