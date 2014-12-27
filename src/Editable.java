import java.awt.datatransfer.ClipboardOwner;

import javax.swing.text.Document;

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
 * Interface intended for GUI components that can be edited.
 */
public interface Editable extends ClipboardOwner {
    public void requestFocus();
    public String getText();
    public String getSelectedText();
    public void clearText();
    public void replaceSelection(String string);
    public Document getDocument();
    public int getCaretPosition();
}
