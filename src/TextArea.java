import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class TextArea extends JTextArea implements Editable {

    public TextArea(int i, int j) {
        setRows(i);
        setColumns(j);
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
