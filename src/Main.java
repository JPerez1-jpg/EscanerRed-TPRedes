import views.InterfazScanner;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InterfazScanner ventana = new InterfazScanner();
            ventana.setVisible(true);
        });
    }
}