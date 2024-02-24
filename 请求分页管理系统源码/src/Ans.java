import javax.swing.*;
import java.awt.*;
import java.io.*;

public class Ans extends JFrame {
    JTextArea textArea;
    JScrollPane jsc;
    JPanel jp1, jp2;
    JLabel title;

    public static void main(String[] args) {
        Ans ans = new Ans();
    }

    public void init() {
        textArea = new JTextArea();
        textArea.setLineWrap(true);  //设置自动换行
        jp1 = new JPanel();
        jp2 = new JPanel();
        title = new JLabel("程序结果清单");
        title.setFont(new Font("宋体", Font.BOLD, 24));

        jsc = new JScrollPane(textArea);


        setLayout(new BorderLayout());

        jp1.add(title);
        jp1.setPreferredSize(new Dimension(0, 80));
        add(jp1, BorderLayout.NORTH);
        add(jsc, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1500, 1000);
        setVisible(true);
        this.setLocationRelativeTo(null);
        setResizable(false);
    }

    public Ans() {
        init();
        String line;
        StringBuffer content = new StringBuffer("");
        textArea.setFont(new Font("SansSerif", Font.PLAIN, 16));
        try {
            BufferedReader reader = new BufferedReader(new FileReader("ans.txt"));
            while ((line = reader.readLine()) != null) {
                content.append(line + "\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        textArea.setText(content.toString());
    }

    public void saveAns() {
        String content = textArea.getText();
        BufferedWriter writer;

        try {
            writer = new BufferedWriter(new FileWriter("ans.txt"));
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

