import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class PageReplacementSimulator2 extends JFrame {

    private DefaultTableModel tableModel;
    private JTable table;
    private JButton simulateButton;

    private JTextField pageNumberField, addressNumberField;
    private JButton generateButton;     //点击生成页面访问流
    private JButton ansButton;     //点击显示保存的结果清单
    private JButton saveButton;     //点击保存结果清单

    private JTextArea addressSequenceArea;  //显示页面访问流的文本域

    private static final int INSTRUCTION_COUNT = 100;
    private static final int PAGE_NUM = 50;
    private int pageNum;        //页面数
    private int addressNum;     //访问地址数
    private List<Integer> addressSequence;  //地址序列

    //设置标签和按钮的字体
    public void setFont() {
        generateButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
        ansButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
        saveButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
        simulateButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
        generateButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
    }

    //开始执行前需满足的条件
    boolean canSimulate() {
        // System.out.println(addressSequenceArea.getText().length());
        return (addressSequenceArea.getText().length() != 0);
    }

    public PageReplacementSimulator2() {
        setTitle("请求分页管理模拟器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        // Input and generation panel
        JPanel inputPanel = new JPanel(new FlowLayout());
        pageNumberField = new JTextField(10);   //页面数输入框

        JLabel pageNum = new JLabel("页面数:");
        pageNum.setFont(new Font("微软雅黑", Font.BOLD, 17));
        inputPanel.add(pageNum);
        inputPanel.add(pageNumberField);

        // 显示生成的地址序列的文本区域
        addressNumberField = new JTextField(10);    //地址数输入框
        JLabel addressNum = new JLabel("地址数:");
        addressNum.setFont(new Font("微软雅黑", Font.BOLD, 17));
        inputPanel.add(addressNum);
        inputPanel.add(addressNumberField);

        //生成地址序列的按钮
        generateButton = new JButton("生成地址序列");
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateAddressSequence();  //进行生成地址序列
            }
        });
        inputPanel.add(generateButton);
        //显示地址序列（页号序列）的文本域
        addressSequenceArea = new JTextArea(5, 40);
        addressSequenceArea.setEditable(false);
        addressSequenceArea.setLineWrap(true);
        JScrollPane scrollPaneTextArea = new JScrollPane(addressSequenceArea);
        inputPanel.add(scrollPaneTextArea);

        // Simulation result panel
        tableModel = new DefaultTableModel();
        tableModel.addColumn("内存容量");
        tableModel.addColumn("LRU 缺页次数");
        tableModel.addColumn("LRU 中断次数");
        tableModel.addColumn("LRU 页面置换次数");
        tableModel.addColumn("LRU 命中率");
        tableModel.addColumn("OPT 缺页次数");
        tableModel.addColumn("OPT 中断次数");
        tableModel.addColumn("OPT 页面置换次数");
        tableModel.addColumn("OPT 命中率");

        table = new JTable(tableModel);
        // 创建一个字体对象，设置字体、样式和大小
        Font font = new Font("微软雅黑", Font.PLAIN, 16); // 这里将字体设置为Arial，大小为14
        // 将表格中的字体设置为定义的字体对象
        table.setFont(font);

        JScrollPane scrollPaneTable = new JScrollPane(table);

        //开始执行的按钮
        simulateButton = new JButton("开始模拟");
        simulateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (canSimulate())
                    simulate();
                else {
                    JOptionPane.showMessageDialog(PageReplacementSimulator2.this, "请先输入数据！", "提示", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        JPanel controlPanel = new JPanel();
        controlPanel.add(simulateButton);

        //保存结果的按钮
        saveButton = new JButton("保存结果");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveTableDataToFile("ans.txt");
            }
        });
        inputPanel.add(saveButton);

        //显示结果的按钮
        ansButton = new JButton("显示结果清单");
        ansButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Ans().setVisible(true);
            }
        });
        inputPanel.add(ansButton);

        setFont();
        // 布局设置
        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPaneTable, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }

    //将地址序列显示在文本域中
    private void generateAddressSequence() {
        try {
            addressNum = Integer.parseInt(addressNumberField.getText());
            pageNum = Integer.parseInt(pageNumberField.getText());
            //接受生成的页号序列
            addressSequence = generateRandomInstructions(addressNum, pageNum);

            // 显示序列
            StringBuilder sequenceText = new StringBuilder("生成的地址序列：\n");
            for (int address : addressSequence) {
                sequenceText.append(address).append(" ");
            }
            //检查页面数是否大于内存数
            if (pageNum <= 20) {
                JOptionPane.showMessageDialog(this, "页面数不建议小于内存数，请重新输入！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            addressSequenceArea.setText(sequenceText.toString());

        } catch (NumberFormatException ex) {    //若输入的不是数字，进行提示
            JOptionPane.showMessageDialog(this, "输入无效，请输入数字！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    //开始执行
    private void simulate() {
        tableModel.setRowCount(0);  //先清空表格

        for (int memorySize = 5; memorySize <= 20; memorySize++) {
            //获取页号序列
            List<Integer> instructionSequence = addressSequence;
            List<Integer> pageAddresses = transformToPageAddresses(instructionSequence);

            //获取两个算法的执行结果
            SimulationResult lruResult = simulateLRU(pageAddresses, memorySize);
            SimulationResult optResult = simulateOPT(pageAddresses, memorySize);
            //将结果显示在表格中
            tableModel.addRow(new Object[]{
                    memorySize,
                    lruResult.pageFaults,
                    lruResult.interrupts,
                    lruResult.pageReplacements,
                    //lruResult.hitRatio,
                    String.format("%.3f", lruResult.hitRatio), // 格式化命中率
                    optResult.pageFaults,
                    optResult.interrupts,
                    optResult.pageReplacements,
                    //optResult.hitRatio
                    String.format("%.3f", optResult.hitRatio) // 格式化命中率
            });
        }
    }

    //根据页数和地址数生成随机地址序列
    private List<Integer> generateRandomInstructions(int numInstructions, int numPages) {
        Random random = new Random();
        List<Integer> instructions = new ArrayList<>();
        for (int i = 0; i < numInstructions; i++) {
            instructions.add(random.nextInt(numPages + 5)); //令可产生大于页面数的页号访问
        }
        return instructions;
    }

    //将指令地址序列转换成页号序列
    private List<Integer> transformToPageAddresses(List<Integer> instructionSequence) {
        return new ArrayList<>(instructionSequence);
    }

    //执行LRU算法
    private SimulationResult simulateLRU(List<Integer> pageAddresses, int memorySize) {
        int pageFaults = 0; //缺页次数
        int interrupts = 0; //中断次数
        int pageReplacements = 0;   //页面置换次数
        List<Integer> memory = new LinkedList<>();

        for (int i = 0; i < pageAddresses.size(); i++) {
            int page = pageAddresses.get(i);

            if (page >= pageNum) {
                System.out.println("invoked");
                interrupts++; // 访问的页号大于页表长度，产生越界中断
                continue;
            }

            if (!memory.contains(page)) {   //不在内存中，缺页
                pageFaults++;
                interrupts++;
                if (memory.size() < memorySize) {   //内存未满，直接放入内存
                    memory.add(page);
                } else {                //若内存已满，需置换，表头的页为最久未被使用
                    memory.remove(0);
                    memory.add(page);
                    pageReplacements++;
                }
            } else {        //在内存中，更新访问到的项放到表尾，表示最新被访问到
                memory.remove(Integer.valueOf(page));
                memory.add(page);
            }
        }

        double hitRatio = 1.0 - ((double) interrupts / pageAddresses.size());
        return new SimulationResult(pageFaults, interrupts, pageReplacements, hitRatio);
    }

    //执行OPT算法
    private SimulationResult simulateOPT(List<Integer> pageAddresses, int memorySize) {
        int pageFaults = 0; //缺页次数
        int interrupts = 0; //中断次数
        int pageReplacements = 0;       //页面置换次数
        List<Integer> memory = new ArrayList<>();   // 用于模拟内存的列表

        for (int i = 0; i < pageAddresses.size(); i++) {
            int page = pageAddresses.get(i);    // 获取当前访问的页号

            if (page >= pageNum) {
                interrupts++; // 访问的页号大于页表长度，产生越界中断
                continue;
            }

            if (!memory.contains(page)) {   // 如果内存中不存在该页，缺页
                pageFaults++;
                interrupts++;
                if (memory.size() < memorySize) {   // 如果内存未满
                    memory.add(page);    // 将该页加入内存
                } else {
                    // 找到最远未来出现的页面
                    int farthestIndex = findFarthestPage(memory, pageAddresses, i);
                    memory.set(farthestIndex, page);
                    pageReplacements++;
                }
            } else {
                // 内存中存在该页，无需操作
            }
        }

        double hitRatio = 1.0 - ((double) interrupts / pageAddresses.size());
        return new SimulationResult(pageFaults, interrupts, pageReplacements, hitRatio);
    }

    // 找到最远未来出现的页面
    private int findFarthestPage(List<Integer> memory, List<Integer> pageAddresses, int currentIndex) {
        int farthestIndex = -1; // 最远页面的索引
        int farthestDistance = -1; // 最远页面的距离

        for (int i = 0; i < memory.size(); i++) {
            int nextPage = memory.get(i); // 获取内存中的下一页
            // 查找该页面在未来的访问序列中的下次出现位置
            int nextOccurrence = pageAddresses.subList(currentIndex, pageAddresses.size()).indexOf(nextPage);

            if (nextOccurrence == -1) { // 如果未来没有再次出现该页面
                return i; // 返回该页面的索引作为最远页面
            }

            if (nextOccurrence > farthestDistance) { // 如果当前页面出现位置更远
                farthestIndex = i; // 更新最远页面的索引
                farthestDistance = nextOccurrence; // 更新最远页面的距离
            }
        }

        return farthestIndex; // 返回最远页面的索引
    }

    // 模拟结果类
    private static class SimulationResult {
        private final int pageFaults;
        private final int interrupts;
        private final int pageReplacements;
        private final double hitRatio;

        public SimulationResult(int pageFaults, int interrupts, int pageReplacements, double hitRatio) {
            this.pageFaults = pageFaults;
            this.interrupts = interrupts;
            this.pageReplacements = pageReplacements;
            this.hitRatio = hitRatio;
        }
    }

    //把结果写入到文件中
    private void saveTableDataToFile(String filePath) {
        try {
            FileWriter writer = new FileWriter(filePath, true); // 设置为追加模式

            // 获取当前时间并格式化
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = dateFormat.format(new Date());
            // 写入当前时间
            writer.write("===========================================执行时间：" + currentTime + "===========================================\n");
            // 写入列名
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                writer.write(tableModel.getColumnName(i) + "\t");
            }
            writer.write("\n");

            // 获取表格数据
            Vector<Vector<Object>> data = tableModel.getDataVector();
            for (Vector<Object> row : data) {
                for (int i = 0; i < row.size(); i++) {
                    Object cell = row.get(i);
                    if (i != 3 && i != 7)
                        writer.write(cell.toString() + "\t");
                    else
                        writer.write(cell.toString() + "\t\t");
                }
                writer.write("\n");
            }
            writer.write("\n");
            writer.close();
            System.out.println("数据已以追加形式写入文件：" + filePath);
            JOptionPane.showMessageDialog(this, "结果保存成功！！", "success", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PageReplacementSimulator2().setVisible(true);
            }
        });
    }
}

