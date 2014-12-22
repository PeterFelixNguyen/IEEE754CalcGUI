import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
