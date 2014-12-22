import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
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

public class Main {
    public static void main(String[] args) {
        Frame frame = new Frame();
        frame.setVisible(true);
    }
}

@SuppressWarnings("serial")
class Frame extends JFrame {

    public Frame() {
        add(new Panel());
        setJMenuBar(new MenuBar());
        setTitle("IEEE-754 Converter");
        setSize(480, 220);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

@SuppressWarnings("serial")
class Panel extends JPanel {
    private PrecisionSlider jsBitMode = new PrecisionSlider();
    private JTextField jtfInput = new JTextField(10);
    private JButton jbCalculate = new JButton("Calculate");
    private JLabel jlOutput = new JLabel(" ");

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
        jpRightInUpper.setBorder(new TitledBorder("Enter Value"));
        jpUpper.add(jpRightInUpper);

        JPanel container3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        container3.add(jtfInput);
        container3.add(jbCalculate);
        jpRightInUpper.add(container3);

        JPanel jpLower = new JPanel(new GridLayout(2,1));
        jpLower.setBorder(new TitledBorder("IEE-754 Bits:"));
        add(jpLower);

        JPanel container4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        container4.add(jlOutput);
        jpLower.add(container4);

        jbCalculate.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                BinaryFractionCalc calc = new BinaryFractionCalc(new Double(jtfInput.getText()));
                if (jsBitMode.getValue() == 1) {
                    jlOutput.setText(calc.getHalf());
                } else if (jsBitMode.getValue() == 2) {
                    jlOutput.setText(calc.getSingle());
                } else {
                    jlOutput.setText(calc.getDouble());
                }
            }});
    }
}

/**
 * The PrecisionSlider is a JSlider used to configure the precision-mode
 * for calculation of IEEE-754 fractions.
 * 
 * @author Peter Nguyen
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
 * 
 * @author Peter Nguyen
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