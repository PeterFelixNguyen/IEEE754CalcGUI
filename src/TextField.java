import javax.swing.JTextField;

@SuppressWarnings("serial")
public class TextField extends JTextField implements Editable {

    public TextField(int i) {
        setColumns(i);
    }

    @Override
    public void requestEditFocus() {
        requestFocus();
    }

    @Override
    public String getEditText() {
        return getText();
    }

    @Override
    public void clearEditText() {
        setText("");
    }
}
