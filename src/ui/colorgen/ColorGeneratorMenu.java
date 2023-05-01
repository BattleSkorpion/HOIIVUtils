package ui.colorgen;

import mapgen.colorgen.ColorGenerator;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ColorGeneratorMenu extends JFrame {
    private JPanel colorGeneratorJPanel;
    private JSlider redMinSlider;
    private JSlider redMaxSlider;
    private JSlider greenMinSlider;
    private JSlider greenMaxSlider;
    private JSlider blueMinSlider;
    private JSlider blueMaxSlider;
    private JButton generateButton;
    private JTextField numColorsTextField;
    private JLabel minGreenAmtLabel;
    private JLabel maxGreenAmtLabel;
    private JLabel minRedAmtLabel;
    private JLabel maxRedAmtLabel;
    private JLabel minBlueAmtLabel;
    private JLabel maxBlueAmtLabel;

    public ColorGeneratorMenu() {
        super("Color Generator");

        /* vars */
        JSlider[] sliders = {redMinSlider, redMaxSlider, greenMinSlider, greenMaxSlider, blueMinSlider, blueMaxSlider};
        JLabel[] sliderAmtLabels = {minRedAmtLabel, maxRedAmtLabel, minGreenAmtLabel, maxGreenAmtLabel, minBlueAmtLabel, maxBlueAmtLabel};

        /* color generator */
        ColorGenerator colorGenerator = new ColorGenerator();

        setContentPane(colorGeneratorJPanel);
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();

        /* action listeners */
        generateButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                colorGenerator.generateColors(getNumColorsGenerate());
            }
        });

        for (int i = 0; i < sliders.length; i++) {
            int finalI = i;
            sliders[i].addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    int value = sliders[finalI].getValue();
                    updateValuesFromSlider(value, sliderAmtLabels[finalI], finalI);
                }
            });
        }
    }

    private void updateValuesFromSlider(int value, JLabel label, int index) {
        label.setText(Integer.toString(value));

        switch (index) {
            case 0:
                ColorGenerator.setRedMin(value);
                break;
            case 1:
                ColorGenerator.setRedMax(value);
                break;
            case 2:
                ColorGenerator.setGreenMin(value);
                break;
            case 3:
                ColorGenerator.setGreenMax(value);
                break;
            case 4:
                ColorGenerator.setBlueMin(value);
                break;
            case 5:
                ColorGenerator.setBlueMax(value);
                break;
            default:
                break;
        }
    }

    private int getNumColorsGenerate() {
        int numColors;
        try {
            numColors = Integer.parseInt(numColorsTextField.getText());
        }
        catch (Exception exc) {
            numColors = 0;
            System.err.println(exc.getMessage());
            System.err.println("\t" + "in " + this);
        }

        if (numColors > (1 << 24) - 1) {
            numColors = (1 << 24) - 1;
            String err = "Error: Attempting to generate more unique colors than is possible. Will generate max possible "
                    + "[" + numColors + "]" + " instead.";
            JOptionPane.showMessageDialog(this, err, this.getTitle(), JOptionPane.WARNING_MESSAGE);
            System.err.println(err);
        }
        return numColors;
    }


}
