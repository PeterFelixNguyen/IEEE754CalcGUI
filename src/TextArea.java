/**
 * Copyright 2014 Latrice Sebastian, Peter "Felix" Nguyen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Custom JTextArea that implement editable
 */
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;

import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class TextArea extends JTextArea implements Editable, ClipboardOwner {

    @SuppressWarnings("unused")
    private TextArea() {
    }

    /**
     * Custom two argument constructor
     * 
     * @param i number of rows
     * @param j number of cols
     */
    public TextArea(int i, int j) {
        setRows(i);
        setColumns(j);
    }

    /**
     * Interface method to clear the text of component
     */
    @Override
    public void clearText() {
        setText("");
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        // TODO Auto-generated method stub
    }

}
