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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.text.BadLocationException;

/**
 * Starts the program
 */
public class Main {
    public static void main(String[] args) {
        Frame frame = new Frame();
        frame.setVisible(true);
    }
}

/**
 * The frame of the program
 */
@SuppressWarnings("serial")
class Frame extends JFrame {
    private Panel panel = new Panel();
    /**
     * Frame constructor
     */
    public Frame() {
        add(panel);
        setJMenuBar(new MenuBar(panel));
        setTitle("IEEE 754 Converter");
        setSize(540, 280);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

/**
 * The panel that contains the active calculator
 */
@SuppressWarnings("serial")
class Panel extends JPanel {
    // GUI components
    private PrecisionSlider jsBitMode = new PrecisionSlider();
    private TextArea jtaDecimal = new TextArea(3, 22);
    private TextField jtfBinary = new TextField(46);
    private TextMenu textMenu;
    private String activeTextField = "jtaDecimal";
    // Clipboard
    private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    // Calculator(s)
    private FloatingPoint floatingPoint;
    // String values
    private String binString = "";
    // Count bits
    private int countSignBits;
    private int countExpBits;
    private int countFracBits;
    private int countSpaces;
    private String defaultNumberInfo =
            "Sign (" + countSignBits + ")" +
                    "    Exponent (" + countExpBits + ")" +
                    "    Fraction (" + countFracBits + ")" +
                    "    Spaces (" + countSpaces+ ")";
    private JLabel jlNumberInfo = new JLabel(defaultNumberInfo);
    private int maxLength = 66;
    private final int maxSign = 1;
    private int maxExp = 11;
    private int maxMant = 52;
    private boolean isInvalid = true;
    // Flags
    private boolean isTrailing = true;
    private boolean isSpaced = true;

    /**
     * Panel constructor
     */
    public Panel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel jpUpper = new JPanel(new GridLayout(1,2));
        add(jpUpper);

        JPanel jpLeftInUpper = new JPanel(new GridLayout());
        jpLeftInUpper.setLayout(new BoxLayout(jpLeftInUpper, BoxLayout.Y_AXIS));
        jpLeftInUpper.setBorder(new TitledBorder("Select Precision"));
        jpUpper.add(jpLeftInUpper);

        JPanel container1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jsBitMode.setPreferredSize(new Dimension(220, 50));
        container1.add(jsBitMode);
        jpLeftInUpper.add(container1);

        JPanel jpRightInUpper = new JPanel();
        jpRightInUpper.setLayout(new BoxLayout(jpRightInUpper, BoxLayout.Y_AXIS));
        jpRightInUpper.setBorder(new TitledBorder("Decimal Value"));
        jpUpper.add(jpRightInUpper);

        JPanel container3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jtaDecimal.setLineWrap(true);
        jtaDecimal.setWrapStyleWord(true);
        // Start: Override tab behavior in jtaDecimal (keyboard focus instead of tab character)
        // Source: stackoverflow.com/questions/525855/moving-focus-from-jtextarea-using-tab-key
        Set<KeyStroke>
        strokes = new HashSet<KeyStroke>(Arrays.asList(KeyStroke.getKeyStroke("pressed TAB")));
        jtaDecimal.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, strokes);
        strokes = new HashSet<KeyStroke>(Arrays.asList(KeyStroke.getKeyStroke("shift pressed TAB")));
        jtaDecimal.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, strokes);
        // End
        JScrollPane jspDecimal = new JScrollPane(jtaDecimal);
        container3.add(jspDecimal);

        jpRightInUpper.add(container3);

        JPanel jpLower = new JPanel(new GridLayout(4,1));
        jpLower.setBorder(new TitledBorder("IEEE 754 Binary Representation"));
        add(jpLower);

        JPanel container4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        //jtfBinary.setEditable(false);
        container4.add(jtfBinary);
        JPanel container5 = new JPanel(new FlowLayout(FlowLayout.LEFT));

        container5.add(jlNumberInfo);
        jpLower.add(container4);
        jpLower.add(container5);

        /* Detect changes in Bit-Mode and perform new calculation */
        jsBitMode.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if (jsBitMode.getValue() == 1) {
                    maxLength = 18;
                    maxExp = 5;
                    maxMant = 10;
                } else if (jsBitMode.getValue() == 2) {
                    maxLength = 34;
                    maxExp = 8;
                    maxMant = 23;
                } else {
                    maxLength = 66;
                    maxExp = 11;
                    maxMant = 52;
                }
                activeTextField = "jtaDecimal";
                convertToBits();
            }
        });

        jtaDecimal.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                activeTextField = "jtaDecimal";
            }

            @Override
            public void focusLost(FocusEvent e) {
            }

        });

        /* Automatically perform the calculation based on current value of text field */
        jtaDecimal.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                convertToBits();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                convertToBits();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        jtfBinary.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                activeTextField = "jtfBinary";
            }

            @Override
            public void focusLost(FocusEvent e) {
            }
        });

        jtfBinary.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                countBits();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                countBits();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }

        });

        jtaDecimal.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                checkPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                checkPopup(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                checkPopup(e);
            }

            private void checkPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    textMenu = new TextMenu(jtaDecimal);
                    textMenu.notifyTextSelected(checkForSelectedText());

                    textMenu.show(jtaDecimal, e.getX(), e.getY());
                    textMenu.getClearItem().addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            //extended instructions
                            jtfBinary.setText("");
                        }
                    });

                    textMenu.getCutItem().addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            performCut(jtaDecimal);
                        }
                    });

                    textMenu.getCopyItem().addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            performCopy(jtaDecimal);
                        }
                    });

                    textMenu.getPasteItem().addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            performPaste(jtaDecimal);
                        }
                    });
                }
            }
        });

        jtfBinary.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                checkPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                checkPopup(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                checkPopup(e);
            }

            private void checkPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    textMenu = new TextMenu(jtfBinary);
                    textMenu.notifyTextSelected(checkForSelectedText());

                    textMenu.show(jtfBinary, e.getX(), e.getY());

                    textMenu.getClearItem().addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            //extended instructions
                            jtaDecimal.setText("");
                        }
                    });

                    textMenu.getCutItem().addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            performCut(jtfBinary);
                        }
                    });

                    textMenu.getCopyItem().addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            performCopy(jtfBinary);
                        }
                    });

                    textMenu.getPasteItem().addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            performPaste(jtfBinary);
                        }
                    });
                }
            }
        });
    }

    /**
     * Checks whether text is selected in the current
     * Editable object with the keyboard focus
     * 
     * @return true if text selected, false if not selected
     */
    public boolean checkForSelectedText() {
        return (getActiveField().getSelectedText() != null && getActiveField().getSelectedText().length() > 0);
    }

    /**
     * Sets the trailing mode for trialing zeros in
     * jtfBinary's bit String
     * 
     * @param state trailing state for jtfBinary
     */
    public void setTrailing(boolean state) {
        isTrailing = state;
    }

    /**
     * Sets the spacing mode for jtfBinary's bit String
     * 
     * @param state spacing state for jtfBinary
     */
    public void setSpaced(boolean state) {
        isSpaced = state;
    }

    /**
     * Performs a cut operation on the specified Editable object
     * 
     * @param editableField Editable object
     */
    public void performCut(Editable editableField) {
        StringSelection stringSelection = new StringSelection(editableField.getSelectedText());
        clipboard.setContents(stringSelection, editableField);
        editableField.replaceSelection("");
    }

    /**
     * Performs a copy operation on the specified Editable object
     * 
     * @param editableField Editable object
     */
    public void performCopy(Editable editableField) {
        StringSelection stringSelection = new StringSelection(editableField.getSelectedText());
        clipboard.setContents(stringSelection, editableField);
    }

    /**
     * Performs a paste operation on the specified Editable object
     * Source: http://www.javapractices.com/topic/TopicAction.do?Id=82
     * 
     * @param editableField Editable object
     */
    public void performPaste(Editable editableField) {
        String cbString = "";

        Transferable contents = clipboard.getContents(null);
        boolean hasTransferableText =
                (contents != null) &&
                contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        if (hasTransferableText) {
            try {
                cbString = (String)contents.getTransferData(DataFlavor.stringFlavor);
            }
            catch (UnsupportedFlavorException | IOException ex){
                System.out.println(ex);
                ex.printStackTrace();
            }
        }
        try {
            if (editableField.getSelectedText() == null) {
                editableField.getDocument().insertString(editableField.getCaretPosition(), cbString, null);
            } else {
                editableField.replaceSelection(cbString);
            }
        }
        catch (BadLocationException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * Returns the Editable JTextArea with the keyboard focus.
     * If none of the relevant components have the keyboard focus,
     * then returns null.
     * 
     * @return Editable JTextArea with the keyboard focus
     */
    public Editable getActiveField() {
        if (activeTextField == "jtaDecimal") {
            return jtaDecimal;
        } else if (activeTextField == "jtfBinary") {
            return jtfBinary;
        } else
            return null;
    }

    /**
     * @return Editable JTextArea for Decimal
     */
    public Editable getDecimalField() {
        return jtaDecimal;
    }

    /**
     * @return Editable JTextArea for Bnary
     */
    public Editable getBinaryField() {
        return jtfBinary;
    }

    /**
     * Clears the panel's input and output components
     */
    public void clear() {
        jtaDecimal.clearText();
        jtfBinary.clearText();
        jlNumberInfo.setText(defaultNumberInfo);
    }

    /**
     * Determine if binary number is invalid
     */
    private void countBits() {
        //int bitGroup = 1;
        int index = 0;
        int length = jtfBinary.getText().length();
        binString = jtfBinary.getText();
        countSignBits = 0;
        countExpBits = 0;
        countFracBits = 0;
        countSpaces = 0;
        int maxSpaces = 2;
        int tempMax = maxLength;

        if (!isSpaced) {
            tempMax = maxLength - 2;
            maxSpaces = 0;
        }

        if (length > tempMax) {
            isInvalid = true;
        } else {
            isInvalid = false;
        }

        if (isSpaced) {
            while (!isInvalid && index < length ) {
                if (binString.charAt(index) == '1' || binString.charAt(index) == '0') {
                    switch (countSpaces) {
                        case 0:
                            countSignBits++;
                            if (countSignBits > maxSign) {
                                isInvalid = true;
                            }
                            break;
                        case 1:
                            countExpBits++;
                            if (countExpBits > maxExp) {
                                isInvalid = true;
                            }
                            break;
                        case 2:
                            countFracBits++;
                            if (countFracBits > maxMant) {
                                isInvalid = true;
                            }
                            break;
                        default:
                            isInvalid = true;
                            break;
                    }
                }
                else if (binString.charAt(index) == ' ' && countSpaces < maxSpaces) {
                    if (index != 0 && binString.charAt(index-1) == ' ') {
                        isInvalid = true;
                    }
                    countSpaces++;
                } else {
                    isInvalid = true;
                }
                index++;
            }
        } else {
            int i = 0;
            while (i < length && !isInvalid) {
                if (binString.charAt(i) != '0' &&
                        binString.charAt(i) != '1') {
                    isInvalid = true;
                }
                if (countSignBits < maxSign) {
                    countSignBits++;
                } else if (countExpBits < maxExp) {
                    countExpBits++;
                } else if (countFracBits < maxMant) {
                    countFracBits++;
                }
                i++;
            }
        }

        if (isInvalid == true) {
            jlNumberInfo.setText("INVALID BINARY NUMBER");
            jtaDecimal.setText("");
        } else {
            int addSpace;

            if (isSpaced) {
                addSpace = 1;
            } else {
                addSpace = 0;
            }



            jlNumberInfo.setText(
                    "Sign (" + countSignBits + ")" +
                            "    Exponent (" + countExpBits + ")" +
                            "    Fraction (" + countFracBits + ")" +
                            "    Spaces (" + countSpaces+ ")");

            String full = jtfBinary.getText();

            if (activeTextField == "jtfBinary") {
                if (full.length() == maxLength) {
                    String arg1 = full.substring(0, 1);
                    String arg2 = full.substring(1+addSpace, 1+maxExp+addSpace);
                    String arg3 = full.substring(1+maxExp+addSpace+addSpace, 1+maxExp+addSpace+addSpace+maxMant);

                    floatingPoint = new FloatingPoint(arg1, arg2, arg3);
                    jtaDecimal.setText(floatingPoint.getDecimalVerbose());
                } else {
                    jtaDecimal.setText("");
                }
            }
        }
    }

    /**
     * Instructs the calculator to convert the bits based on jtaDecimal's value
     */
    public void convertToBits() {
        if (activeTextField == "jtaDecimal") {
            if (jtaDecimal.getText().length() > 0 ) {

                @SuppressWarnings("unused")
                BigDecimal checkNumber;
                boolean validNumber = true;

                try {
                    checkNumber = new BigDecimal(jtaDecimal.getText());
                } catch (NumberFormatException ex) {
                    validNumber = false;
                    jtfBinary.setText("");
                    jlNumberInfo.setText("INVALID DECIMAL NUMBER");
                }

                if (validNumber) {
                    if (jsBitMode.getValue() == 1) {
                        floatingPoint = new FloatingPoint(new BigDecimal(jtaDecimal.getText()), FloatingPoint.Precision.HALF);
                    } else if (jsBitMode.getValue() == 2) {
                        floatingPoint = new FloatingPoint(new BigDecimal(jtaDecimal.getText()), FloatingPoint.Precision.SINGLE);
                    } else {
                        floatingPoint = new FloatingPoint(new BigDecimal(jtaDecimal.getText()), FloatingPoint.Precision.DOUBLE);
                    }

                    String result = floatingPoint.spacedBinString();

                    if (!isTrailing) {
                        int i = maxLength - 1;
                        int numSpaces = 2;
                        while (i >= maxSign + maxExp + numSpaces) {
                            if (result.charAt(i) == '1') {
                                i++;
                                break;
                            }
                            i--;
                        }
                        result = result.substring(0, i);
                    }

                    if (!isSpaced) {
                        result = result.replaceAll("\\s","");
                    }

                    jtfBinary.setText(result);
                }
            } else {
                jtfBinary.setText("");
            }
        }
    }
}


/**
 * The PrecisionSlider is a JSlider used to configure the precision-mode
 * for calculation of IEEE 754 fractions.
 */
@SuppressWarnings("serial")
class PrecisionSlider extends JSlider {

    /**
     * PrecisionSlider constructor
     */
    public PrecisionSlider() {
        Dictionary<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
        labels.put(1, new JLabel("half"));
        labels.put(2, new JLabel("single"));
        labels.put(3, new JLabel("double"));

        setLabelTable(labels);
        setMinimum(1);
        setMaximum(3);
        setSnapToTicks(true);
        createStandardLabels(1);
        setMinorTickSpacing(1);
        setMajorTickSpacing(1);
        setPaintLabels(true);
        setPaintTicks(true);
        setValue(32);
    }
}

/**
 * This MenuBar contains all the calculator's other features. The mode menu on this
 * allows the user to switch calculators. The edit menu allows the user to modify or
 * access the output. The options menu contains output and input preferencees. The
 * help menu contains information on the calculator and the developers.
 */
@SuppressWarnings("serial")
class MenuBar extends JMenuBar {
    // Option items
    private JMenu jmOptions = new JMenu("Options");
    private JMenu jmGenericMenu = new JMenu("Generic Menu");
    private JRadioButtonMenuItem jmiItem1 = new JRadioButtonMenuItem("Item 1", null, true);
    private JRadioButtonMenuItem jmiItem2 = new JRadioButtonMenuItem("Item 2", null, false);
    private JCheckBoxMenuItem jcbmiTrailing = new JCheckBoxMenuItem("Keep trailing zeroes", null, true);
    private JCheckBoxMenuItem jcbmiSpaces = new JCheckBoxMenuItem("Use space delimiters", null, true);

    // Edit items
    private JMenu jmEdit = new JMenu("Edit");
    private JMenuItem jmiClear = new JMenuItem("Clear");
    private JMenuItem jmiCut = new JMenuItem("Cut");
    private JMenuItem jmiCopy = new JMenuItem("Copy");
    private JMenuItem jmiPaste = new JMenuItem("Paste");

    // Help items
    private JMenu jmHelp = new JMenu("Help");
    private JMenuItem jmiAboutCalc = new JMenuItem("About IEEE754Calc");
    private JMenuItem jmiDeveloperContact = new JMenuItem("Developer Contact");
    private JMenuItem jmiHowToUse = new JMenuItem("Calculator Manual");

    /**
     * MenuBar constructor with access to Panel
     * 
     * @param panel Panel to be accessed by MenuBar
     */
    public MenuBar(final Panel panel) {
        add(jmOptions);
        add(jmEdit);
        add(jmHelp);

        // Option items
        ButtonGroup showTrailFracGroup = new ButtonGroup();
        showTrailFracGroup.add(jmiItem1);
        showTrailFracGroup.add(jmiItem2);
        //jmOptions.add(jmGenericMenu);
        jmGenericMenu.add(jmiItem1);
        jmGenericMenu.add(jmiItem2);
        jmOptions.add(jcbmiTrailing);
        jmOptions.add(jcbmiSpaces);

        // Edit items
        jmEdit.add(jmiClear);
        jmEdit.add(jmiCut);
        jmEdit.add(jmiCopy);
        jmEdit.add(jmiPaste);
        jmEdit.addMenuListener(new MenuListener() {

            @Override
            public void menuSelected(MenuEvent e) {
                jmiCut.setEnabled(panel.checkForSelectedText());
                jmiCopy.setEnabled(panel.checkForSelectedText());
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }
        });

        // Help items
        jmHelp.add(jmiAboutCalc);
        jmHelp.add(jmiDeveloperContact);
        jmHelp.add(jmiHowToUse);

        // Option Actions
        jcbmiTrailing.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                panel.setTrailing(jcbmiTrailing.isSelected());
                panel.convertToBits();
            }
        });

        jcbmiSpaces.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                panel.setSpaced(jcbmiSpaces.isSelected());
                panel.convertToBits();
            }
        });

        // Edit Actions
        jmiClear.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                panel.clear();
            }
        });
        jmiCut.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                panel.performCut(panel.getActiveField());
            }
        });

        jmiCopy.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                panel.performCopy(panel.getActiveField());
            }
        });

        jmiPaste.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                panel.performPaste(panel.getActiveField());
            }
        });
    }

    /**
     * @return true if trailing zeros in the
     *         significant bits are shown, false otherwise
     */
    public boolean isTrailing() {
        return jcbmiTrailing.isSelected();
    }

    /**
     * @return true if bit parts are spaced, false otherwise
     */
    public boolean isSpaced() {
        return jcbmiSpaces.isSelected();
    }
}

/**
 * This TextMenu contains commands to modify the current active
 * text field and provides clipboard functionality.
 */
@SuppressWarnings("serial")
class TextMenu extends JPopupMenu {
    private JMenuItem jmiClear = new JMenuItem("Clear");
    private JMenuItem jmiCopy = new JMenuItem("Copy");
    private JMenuItem jmiCut = new JMenuItem("Cut");
    private JMenuItem jmiPaste = new JMenuItem("Paste");

    /**
     * TextMenu constructor for a specified
     * Editable object.
     * 
     * @param editable object that implement Editable
     */
    public TextMenu (final Editable editable) {
        add(jmiClear);
        add(jmiCut);
        add(jmiCopy);
        add(jmiPaste);

        jmiClear.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                editable.requestFocus();
                editable.clearText();
            }
        });

        jmiCut.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // leave empty, implement outside
            }
        });

        jmiCopy.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // leave empty, implement outside
            }
        });

        jmiPaste.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // leave empty, implement outside
            }
        });
    }

    /**
     * @return jmiClear
     */
    public JMenuItem getClearItem() {
        return jmiClear;
    }

    /**
     * @return jmiCut
     */
    public JMenuItem getCutItem() {
        return jmiCut;
    }

    /**
     * @return jmiCopy
     */
    public JMenuItem getCopyItem() {
        return jmiCopy;
    }

    /**
     * @return jmiPaste
     */
    public JMenuItem getPasteItem() {
        return jmiPaste;
    }

    /**
     * Set the enabled state of jmiCut and jmiCopy
     * 
     * @param state enabled state of jmiCut and jmiCopy
     */
    public void notifyTextSelected(boolean state) {
        jmiCut.setEnabled(state);
        jmiCopy.setEnabled(state);
    }
}