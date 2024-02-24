import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import java.awt.*;
/**
 * 功能：请求分页管理系统
 *
 */

public class Main extends JFrame {
    JPanel jp1, jp2;
    JLabel jlb1;
    JButton jb1;

    public static void main(String[] args) {
        Main win = new Main();
    }

    //创建新面板
    public Main() {
        //创建面板
        super("请求分页管理系统");
        jp1 = new JPanel();
        jp2 = new JPanel();
        jp2.setBackground(Color.GRAY);
        //创建标签
        jlb1 = new JLabel("欢迎使用请求分页管理系统");
        jlb1.setFont(new java.awt.Font("Dialog", 1, 30));
        //创建按钮
        jb1 = new JButton("进入系统");
        jb1.setFont(new java.awt.Font("Dialog", 1, 40));
        jb1.setPreferredSize(new Dimension(250, 100));

        //在构造函数中实例化监听器，然后向按钮添加这个监听器
        myaction act = new myaction();
        jb1.addActionListener(act);

        //设置布局管理
        this.setLayout(new GridLayout(2, 1));//网格式布局

        //加入各个组件
        jp1.add(jlb1);
        jp2.add(jb1);
        jb1.setLocation(500, 200);

        //加入到JFrame
        this.add(jp1);
        this.add(jp2);
        //设置窗体
        this.setSize(800, 550);//窗体大小
        this.setLocationRelativeTo(null);//在屏幕中间显示(居中显示)
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//退出关闭JFrame
        this.setVisible(true);//显示窗体
    }

    //建完监听器内部类之后，要实例化它才能用，在构造函数中实例化，等主类实例化调用构造函数时执行
    class myaction implements ActionListener {
        //继承监听器接口，然后改写下面这个方法，来实现点击事件行为
        public void actionPerformed(ActionEvent e) {
            if ((JButton) e.getSource() == jb1) {
                setVisible(false); //不用加this
                dispose();//本窗口销毁,释放内存资源
                //打开新的窗体
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new PageReplacementSimulator2().setVisible(true);
                    }
                });
            }
        }
    }
}
