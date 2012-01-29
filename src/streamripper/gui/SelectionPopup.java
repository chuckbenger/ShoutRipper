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


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * SelectionPopup is used to let the user select and existing stream
 * or search for on
 * Extends: JPopupMenu
 * Implements ActionListener
 * @author cbenger
 */
public class SelectionPopup extends JPopupMenu implements ActionListener{

    private JMenuItem searchMenuItem; //Menu item for searching content
    private SearchFrame searchFrame; //Frame for searching for streams
    private StreamRipper streamRipper; //Reference to StreamRipper
    /**
     * Constructor for Selection pop-up
     */
    public SelectionPopup(StreamRipper streamRipper){
        initGui(); //initializes the the popup menu
        initEventListeners();
        searchFrame = new SearchFrame(this);
        this.streamRipper = streamRipper;
    }

    /**
     * Initializes the pop-up menu gui
     */
    private void initGui(){
        searchMenuItem = new JMenuItem("Search");
       
        add(searchMenuItem);
        addSeparator();
    }

    /**
     * Initializes the menu event listeners
     */
    private void initEventListeners(){
        searchMenuItem.addActionListener(this);
    }
  
    /**
     * Handles action performed events
     * @param e Action Event argument
     */
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == searchMenuItem){
            searchFrame.setVisible(true);
        }
    }

    public StreamRipper getRipper(){
        return streamRipper;
    }
}
