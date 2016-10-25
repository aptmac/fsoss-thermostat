package ProducersAndConsumers;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.ScrollPane;
import java.awt.Toolkit;

public class DemoGUI extends JFrame {

    private static DemoGUI demo = new DemoGUI();
    private static final String OVER_CONSUME = "Produce << Consume";
    private static final String BALANCED = "Produce == Consume";
    private ProducerAndConsumer pnc;
    private JTextArea results;
    private JLabel textLabel;
    private long startTime;

    private DemoGUI() {}

    public static DemoGUI getInstance() { return demo; }

    public void appendText(String s) { results.append(s); }

    private void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    private void displayElapsedTime() {
        long elapsedTime = System.nanoTime() - startTime;
        results.append("Elapsed time: " + (elapsedTime / Math.pow(10, 9)) + " seconds.");
    }

    public void setupGUI() {
        this.setTitle("Thermostat & Byteman demo: Producers & Consumers");

        /* Set up the buttons for the GUI */
        JButton fastConsumeButton = new JButton(OVER_CONSUME);
        fastConsumeButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                textLabel.setText(OVER_CONSUME);
                results.setText("");
                SwingWorker worker = new SwingWorker() {
                    @Override
                    public Object doInBackground() {
                        setStartTime(System.nanoTime());
                        pnc = new ProducerAndConsumer(500, 150);
                        return null;
                    }

                    @Override
                    public void done() { displayElapsedTime(); }
                };
                worker.execute();
            }
        });
        fastConsumeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton balancedButton = new JButton(BALANCED);
        balancedButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                textLabel.setText(BALANCED);
                results.setText("");
                SwingWorker worker = new SwingWorker() {
                    @Override
                    public Object doInBackground() {
                        setStartTime(System.nanoTime());
                        pnc = new ProducerAndConsumer(500, 500);
                        return null;
                    }

                    @Override
                    public void done() { displayElapsedTime(); }
                };
                worker.execute();
            }
        });
        balancedButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        /* Set up the layout for the gui*/
        JPanel componentHolder = new JPanel();
        componentHolder.setLayout(new BoxLayout(componentHolder, BoxLayout.PAGE_AXIS));
        componentHolder.add(fastConsumeButton);
        componentHolder.add(balancedButton);
        this.add(componentHolder);

        /* Set up the label of the different modes */
        textLabel = new JLabel();
        textLabel.setVisible(true);
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textLabel.setFont(new Font(textLabel.getFont().getName(), Font.BOLD, 20));
        componentHolder.add(textLabel);

        /* Set up a text area to show the results */
        results = new JTextArea();
        results.setEditable(false);
        results.setFont(new Font(results.getFont().getName(), Font.PLAIN, 18));
        ScrollPane sp = new ScrollPane();
        sp.add(results);
        componentHolder.add(sp);

        /* Set up the frame dimensions and behavior */
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((int) screenSize.getWidth() / 2, (int) screenSize.getHeight() / 2);
        this.setSize(400, 700);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        DemoGUI demo = DemoGUI.getInstance();
        demo.setupGUI();
    }
}
