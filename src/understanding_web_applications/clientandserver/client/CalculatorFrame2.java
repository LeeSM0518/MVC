package understanding_web_applications.clientandserver.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@SuppressWarnings("serial")
public class CalculatorFrame2 extends JFrame implements ActionListener {
  CalculatorAgent calcAgent;
  JTextField operand1 = new JTextField(4);
  JTextField operator = new JTextField(2);
  JTextField operand2 = new JTextField(4);
  JButton equal = new JButton("=");
  JTextField result = new JTextField(6);
  JButton clear = new JButton("Clear");

  public CalculatorFrame2() {
    try {
      calcAgent = new CalculatorAgent("localhost", 8888);
    } catch (Exception err) {
      JOptionPane.showMessageDialog(
          null, err.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      System.exit(0);
    }

    this.setTitle("Lesson01-Exam02");

    Container contentPane = this.getContentPane();
    contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

    contentPane.add(Box.createVerticalGlue());
    contentPane.add(this.createInputForm());
    contentPane.add(this.createToolBar());
    contentPane.add(Box.createVerticalGlue());

    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        calcAgent.close();
        System.exit(0);
      }
    });

    this.pack();
    this.setLocationRelativeTo(null);
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    if (event.getSource() == equal) {
      compute();
    } else {
      clearForm();
    }
  }

  private void compute() {
    double a = Double.parseDouble(operand1.getText());
    double b = Double.parseDouble(operand2.getText());
    double r = 0;

    try {
      r = calcAgent.compute(operator.getText(), a, b);
      result.setText(Double.toString(r));

    } catch (Exception err) {
      JOptionPane.showMessageDialog(
          null, err.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void clearForm() {
    this.operand1.setText("");
    this.operand2.setText("");
    this.result.setText("");
  }


  private Box createInputForm() {
    Box box = Box.createHorizontalBox();
    box.setMaximumSize(new Dimension(300, 30));
    box.setAlignmentY(Box.CENTER_ALIGNMENT);
    box.add(operand1);
    box.add(operator);
    box.add(operand2);
    box.add(equal);
    box.add(result);
    equal.addActionListener(this);
    return box;
  }

  private Box createToolBar() {
    Box box = Box.createHorizontalBox();
    box.add(clear);
    clear.addActionListener(this);
    return box;
  }

  public static void main(String[] args) {
    CalculatorFrame2 app = new CalculatorFrame2();
    app.setVisible(true);
  }
}
