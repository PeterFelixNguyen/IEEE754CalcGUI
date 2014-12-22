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
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

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

    /**
     * Frame constructor
     */
    public Frame() {
        add(new Panel());
        setJMenuBar(new MenuBar());
        setTitle("IEEE 754 Converter");
        setSize(480, 220);
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
    private PrecisionSlider jsBitMode = new PrecisionSlider();
    private JTextField jtfInput = new JTextField(19);
    private JLabel jlOutput = new JLabel(" ");
    private BinaryFractionCalc calc;

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
        container1.add(jsBitMode);
        jpLeftInUpper.add(container1);

        JPanel jpRightInUpper = new JPanel();
        jpRightInUpper.setLayout(new BoxLayout(jpRightInUpper, BoxLayout.Y_AXIS));
        jpRightInUpper.setBorder(new TitledBorder("Enter Decimal Value"));
        jpUpper.add(jpRightInUpper);

        JPanel container3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        container3.add(jtfInput);
        jpRightInUpper.add(container3);

        JPanel jpLower = new JPanel(new GridLayout(2,1));
        jpLower.setBorder(new TitledBorder("IEEE 754 Bits"));
        add(jpLower);

        JPanel container4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        container4.add(jlOutput);
        jpLower.add(container4);

        /* Detect changes in Bit-Mode and perform new calculation */
        jsBitMode.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                convertToBits();
            }
        });

        /* Automatically perform the calculation based on current value of text field */
        jtfInput.getDocument().addDocumentListener(new DocumentListener() {

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


        /* Unfinished Implementation of DocumentFilter */

        /*
        AbstractDocument inputDoc = (AbstractDocument) jtfInput.getDocument();
        inputDoc.setDocumentFilter(new InputFilter(jsBitMode, jlOutput, jtfInput));
         */
    }

    /**
     * Instructs the calculator to convert the bits based on jtfInput's value
     */
    private void convertToBits() {
        if (jtfInput.getText().length() > 0) {

            @SuppressWarnings("unused")
            double checkFloat;
            boolean validNumber = true;

            try {
                checkFloat = new Double(jtfInput.getText());
            } catch (NumberFormatException ex) {
                validNumber = false;
                jlOutput.setText(" ");
            }

            if (validNumber) {
                calc = new BinaryFractionCalc(new Double(jtfInput.getText()));

                if (jsBitMode.getValue() == 1) {
                    jlOutput.setText(calc.getHalf());
                } else if (jsBitMode.getValue() == 2) {
                    jlOutput.setText(calc.getSingle());
                } else {
                    jlOutput.setText(calc.getDouble());
                }
            }
        }
    }
}

/**
 * Unfinished DocumentFilter intended to filter (block) specific input values
 */
class InputFilter extends DocumentFilter {
    private JSlider jsBitMode;
    private JLabel jlOutput;
    private JTextField jtfInput;
    private Pattern pattern; // implement later

    @SuppressWarnings("unused")
    private InputFilter() {
        // no default constructor
    }

    public InputFilter(JSlider jsBitMode, JLabel jlOutput, JTextField jtfInput) {
        this.jsBitMode = jsBitMode;
        this.jlOutput = jlOutput;
        this.jtfInput = jtfInput;
        pattern = Pattern.compile("([0-9]{1,2}|1[0-9]{2}|2[0-4][0-9]|25[0-5])");
    }

    public void processBits() {
        BinaryFractionCalc calc = new BinaryFractionCalc(new Double(jtfInput.getText()));
        if (jsBitMode.getValue() == 1) {
            jlOutput.setText(calc.getHalf());
        } else if (jsBitMode.getValue() == 2) {
            jlOutput.setText(calc.getSingle());
        } else {
            jlOutput.setText(calc.getDouble());
        }
    }

    // manual invocation
    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        String newStr = fb.getDocument().getText(0, fb.getDocument().getLength()) + string;
        Matcher m = pattern.matcher(newStr);

        if (m.matches()) {
            super.insertString(fb, offset, string, attr);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    // automatic invocation
    @Override
    public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException {
        super.remove(fb, offset, length);
    }

    // automatic invocation
    @Override
    public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attr) throws BadLocationException {
        if (length > 0) {
            fb.remove(offset, length);
        }

        insertString(fb, offset, string, attr);
    }
}

/**
 * The PrecisionSlider is a JSlider used to configure the precision-mode
 * for calculation of IEEE 754 fractions.
 */
@SuppressWarnings("serial")
class PrecisionSlider extends JSlider {

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
    // Mode items
    private JMenu jmMode = new JMenu("Mode");
    private JRadioButtonMenuItem jrbmiDecToBin = new JRadioButtonMenuItem("Decimal to Binary", null, true);
    private JRadioButtonMenuItem jrbmiBinToDec = new JRadioButtonMenuItem("Binary to Decimal", null, false);

    // Edit items
    private JMenu jmEdit = new JMenu("Edit");
    private JMenuItem jmiClear = new JMenuItem("Clear");
    private JMenuItem jmiCut = new JMenuItem("Cut");
    private JMenuItem jmiCopy = new JMenuItem("Copy");
    private JMenuItem jmiPaste = new JMenuItem("Paste");

    // Option items
    private JMenu jmOption = new JMenu("Options");
    private JMenu jmFraction = new JMenu("Trailing Zeroes");
    private JRadioButtonMenuItem jrbmiShowFracTrail = new JRadioButtonMenuItem("Show", null, true);
    private JRadioButtonMenuItem jrbmiHideFracTrail = new JRadioButtonMenuItem("Hide", null, false);

    // Help ITEMS
    private JMenu jmHelp = new JMenu("Help");
    private JMenuItem jmiAboutCalc = new JMenuItem("About IEEE754Calc");
    private JMenuItem jmiDeveloperContact = new JMenuItem("Developer Contact");
    private JMenuItem jmiHowToUse = new JMenuItem("Calculator Manual");

    /**
     * MenuBar constructor
     */
    public MenuBar() {
        add(jmMode);
        add(jmEdit);
        add(jmOption);
        add(jmHelp);

        // Mode items
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(jrbmiDecToBin);
        modeGroup.add(jrbmiBinToDec);
        jmMode.add(jrbmiDecToBin);
        jmMode.add(jrbmiBinToDec);

        // Edit items
        jmEdit.add(jmiClear);
        jmEdit.add(jmiCut);
        jmEdit.add(jmiCopy);
        jmEdit.add(jmiPaste);

        // Option items
        ButtonGroup showTrailFracGroup = new ButtonGroup();
        showTrailFracGroup.add(jrbmiShowFracTrail);
        showTrailFracGroup.add(jrbmiHideFracTrail);
        jmOption.add(jmFraction);
        jmFraction.add(jrbmiShowFracTrail);
        jmFraction.add(jrbmiHideFracTrail);

        // Help items
        jmHelp.add(jmiAboutCalc);
        jmHelp.add(jmiDeveloperContact);
        jmHelp.add(jmiHowToUse);
    }
}