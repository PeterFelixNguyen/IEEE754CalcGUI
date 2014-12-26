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
import javax.swing.JTextField;

/**
 * Custom JTextField that implement editable
 */
@SuppressWarnings("serial")
public class TextField extends JTextField implements Editable {

    @SuppressWarnings("unused")
    private TextField() {
    }

    /**
     * Custom one argument constructor
     * 
     * @param i number of cols
     */
    public TextField(int i) {
        setColumns(i);
    }

    /**
     * Interface method to request focus
     */
    @Override
    public void requestEditFocus() {
        requestFocus();
    }

    /**
     * Interface method to get text of component
     *
     * @return text value of the JTextArea component
     */
    @Override
    public String getEditText() {
        return getText();
    }

    /**
     * Interface method to clear the text of component
     */
    @Override
    public void clearEditText() {
        setText("");
    }
}
