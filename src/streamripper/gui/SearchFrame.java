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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import streamripper.io.StationInfo;

import streamripper.utils.RipUtils;

//~--- JDK imports ------------------------------------------------------------

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.IOException;

import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 * SearchFrame provides a class for searching for streams
 * from Shoutcast dir list
 * @author cbenger
 */
public final class SearchFrame extends JFrame implements ActionListener {
    private final String           BITRATE_NODE  = "dirbitrate";    // Root node in html
    private final String[]         COLUMN_NAMES  = { "Name", "Genre", "Listeners", "Bitrate", "Type" };    // Column names for jtable
    private final short            FRAME_HEIGHT  = 200;              // Height of the frame
    private final short            FRAME_WIDTH   = 400;              // Width of the frame
    private final String           GENRE_NODE    = "dirgenre";       // Root node in html
    private final String           LISTENER_NODE = "dirlistners";    // Root node in html
    private final String           ROOT_NODE     = "dirlist";        // Root node in html
    private final String           SEARCH_URL    = "http://www.shoutcast.com/Internet-Radio/";    // Url to search for content
    private final String           TYPE_NODE     = "dirtype";        // Root node in html
    private JMenuItem              bothJMenuItem;                    // Play and download the streams
    private JMenuItem              downloadJMenuItem;                // Just download the stream
    private JMenuItem              playJMenuItem;                    // Just play the stream
    private JPopupMenu             popupMenu;                        // Popup menu prompting what action to take
    private ArrayList<StationInfo> queryResults;                     // Results from a search
    private JTable                 resultsJTable;                    // Table for displays search results
    private Dimension              screenSize;                       // Size of the screen
    private JTextField             searchJTextField;                 // Text field for searching for streams
    private SelectionPopup         selectionPopup;                   // Reference to the selection popup
    private DefaultTableModel      tableModel;                       // Table model for manipulating a jtable

    /**
     * Constructor for SearchFrame
     * @param selectionPopup the pop-up menu to reference
     */
    public SearchFrame(SelectionPopup selectionPopup) {
        super("Search");
        this.selectionPopup = selectionPopup;
        this.setLayout(new BorderLayout(0, 0));
        this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        this.setUndecorated(true);
        this.initGui();                // Initialize the gui
        this.setupEventListeners();    // Sets up the event listeners
        screenSize   = Toolkit.getDefaultToolkit().getScreenSize();
        queryResults = new ArrayList<StationInfo>();
        this.setLocation(screenSize.width - FRAME_WIDTH - 100, screenSize.height - FRAME_HEIGHT - 50);

        // Adds a new event to hide the window when deactivated
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDeactivated(WindowEvent e) {
                setVisible(false);
            }
        });
    }

    /**
     * Initializes the gui components
     */
    private void initGui() {

        // Creates gui components
        searchJTextField  = new JTextField();
        popupMenu         = new JPopupMenu();
        downloadJMenuItem = new JMenuItem("Download");
        playJMenuItem     = new JMenuItem("Play");
        bothJMenuItem     = new JMenuItem("Play & Download");

        // Create a new table model and disable user edits
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultsJTable = new JTable(tableModel);

        // Adjusts the width of the first column i.e. station name field
        resultsJTable.getColumnModel().getColumn(0).setPreferredWidth(150);

        // Adds components to the popup menu
        popupMenu.add(playJMenuItem);
        popupMenu.add(downloadJMenuItem);
        popupMenu.addSeparator();
        popupMenu.add(bothJMenuItem);

        // Creates a JScroll pane to contain the results table
        JScrollPane pane = new JScrollPane(resultsJTable);

        // Adds components to the Frame
        add(searchJTextField, BorderLayout.NORTH);
        add(pane, BorderLayout.CENTER);
    }

    /**
     * Sets up the search frames event handlers
     */
    private void setupEventListeners() {
        downloadJMenuItem.addActionListener(this);
        playJMenuItem.addActionListener(this);
        bothJMenuItem.addActionListener(this);
        searchJTextField.addActionListener(this);
        resultsJTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    popupMenu.show(resultsJTable, e.getX(), e.getY());
                }
            }
        });
    }

    /**
     * Handles action performed events
     * @param e Action event arguments
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchJTextField) {
            performQuery();
        } else if (e.getSource() == playJMenuItem) {}
        else if (e.getSource() == downloadJMenuItem) {

            // Grabs an StationObject form of the selected row
            StationInfo selectedStation = constructStationObject();

            // Loops through each query result finding the correlating station
            for (StationInfo info : queryResults) {

                // Compares two station objects 0 == same
                if (info.compareTo(selectedStation) == 0) {
                    try {

                        // Parses the pls data for the station
                        RipUtils.parsePls(info);

                        // Starts ripping the stream
                        selectionPopup.getRipper().addStreamDownload(info);
                    } catch (MalformedURLException ex) {
                        JOptionPane.showMessageDialog(null, "Pls url was malformed", "Error connecting to stream",
                                                      JOptionPane.ERROR_MESSAGE);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Failed to connect to stream",
                                                      "Error connecting to stream", JOptionPane.ERROR_MESSAGE);
                    }

                    break;
                }
            }
        }
    }

    /**
     * Performs a query to shoutcast based on the users search
     */
    private void performQuery() {
        try {

            // Clears the array list
            queryResults.clear();

            // Removes all rows from the table
            while (tableModel.getRowCount() > 0) {
                tableModel.removeRow(tableModel.getRowCount() - 1);
            }

            // Grabs all html from specifed link and constructs an iterator to loop
            // through all stations found
            Document          doc      = Jsoup.connect(SEARCH_URL + searchJTextField.getText()).get();
            Iterator<Element> stations = doc.getElementsByClass(ROOT_NODE).iterator();

            // Loops through each station extracting station info
            while (stations.hasNext()) {
                String   plsLink     = null;               // Pls link of the station
                String   name        = null;               // Name of the station
                String   genre       = null;               // Genere of the station
                String   listeners   = null;               // Number of listeners
                String   bitrate     = null;               // Bit rate of the station
                String   type        = null;               // Audio type of the stream
                Element  station     = stations.next();    // Gets the next station
                Elements stationInfo = station.getElementsByAttribute("title");

                if (!stationInfo.isEmpty()) {
                    plsLink = stationInfo.first().attr("href");
                    name    = stationInfo.first().attr("title");
                }

                genre     = station.getElementsByClass(GENRE_NODE).text();
                listeners = station.getElementsByClass(LISTENER_NODE).text();
                bitrate   = station.getElementsByClass(BITRATE_NODE).text();
                type      = station.getElementsByClass(TYPE_NODE).text();

                // Adds the new station to the array list
                queryResults.add(new StationInfo(plsLink, name, genre, listeners, bitrate, type));
                tableModel.addRow(queryResults.get(queryResults.size() - 1).toArray());
                stations.remove();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Constructs a StationInfo object out of the currently selected row
     * @return returns a new StationInfo object
     */
    private StationInfo constructStationObject() {
        String name      = tableModel.getValueAt(resultsJTable.getSelectedRow(), 0).toString();
        String genre     = tableModel.getValueAt(resultsJTable.getSelectedRow(), 1).toString();
        String listeners = tableModel.getValueAt(resultsJTable.getSelectedRow(), 2).toString();
        String bitrate   = tableModel.getValueAt(resultsJTable.getSelectedRow(), 3).toString();
        String type      = tableModel.getValueAt(resultsJTable.getSelectedRow(), 4).toString();

        return new StationInfo(null, name, genre, listeners, bitrate, type);
    }
}

