import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.*;

public class SmartTruckLoadingApp extends JFrame {
    private JTextField weightField;
    private JTable resultTable;
    private JLabel totalValueLabel;
    private JButton calculateButton;
    private JLabel imageLabel;
    private java.util.List<String[]> selectedPackages = new ArrayList<>();

    static class PackageItem {
        String name;
        double weight;
        double value;

        public PackageItem(String name, double weight, double value) {
            this.name = name;
            this.weight = weight;
            this.value = value;
        }
    }

    static class Result {
        java.util.List<String[]> selectedItems;
        double totalValue;

        public Result(java.util.List<String[]> selectedItems, double totalValue) {
            this.selectedItems = selectedItems;
            this.totalValue = totalValue;
        }
    }

    public SmartTruckLoadingApp() {
        setTitle("🚚 Smart Truck Loading System");
        setSize(800, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        Color bgColor = new Color(10, 25, 50); // Dark blue background
        getContentPane().setBackground(bgColor);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(bgColor);
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon logo = new ImageIcon("logo.png");
        Image scaled = logo.getImage().getScaledInstance(500, 100, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(scaled));
        topPanel.add(imageLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(bgColor);

        String[] columnNames = {"Item Name", "Weight", "Value", "Fraction Taken"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        resultTable = new JTable(tableModel);
        resultTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        resultTable.setRowHeight(24);

        JTableHeader header = resultTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(65, 105, 225));
        header.setForeground(Color.WHITE);

        resultTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private final Color evenColor = new Color(30, 40, 70);
            private final Color oddColor = new Color(35, 45, 80);

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,boolean isSelected, boolean hasFocus,int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(row % 2 == 0 ? evenColor : oddColor);
                setForeground(Color.WHITE);
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(resultTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        totalValueLabel = new JLabel("Total Value: ");
        totalValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        totalValueLabel.setForeground(new Color(255, 215, 0));
        totalValueLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.add(totalValueLabel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        bottomPanel.setBackground(bgColor);

        JLabel weightLabel = new JLabel("Truck Capacity (kg):");
        weightLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        weightLabel.setForeground(Color.WHITE);
        bottomPanel.add(weightLabel);

        weightField = new JTextField(10);
        weightField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        weightField.setBackground(new Color(30, 30, 60));
        weightField.setForeground(Color.WHITE);
        bottomPanel.add(weightField);

        calculateButton = new JButton("Calculate") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(getForeground());
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
            }
        };
        calculateButton.setContentAreaFilled(false);
        calculateButton.setOpaque(true);
        calculateButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        calculateButton.setForeground(Color.WHITE);
        calculateButton.setFocusPainted(false);
        calculateButton.setBackground(new Color(58, 123, 213));
        calculateButton.setPreferredSize(new Dimension(120, 35));
        addHoverEffect(calculateButton, new Color(100, 149, 237), new Color(58, 123, 213));

        bottomPanel.add(calculateButton);
        calculateButton.addActionListener(e -> calculate());

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void calculate() {
        try {
            double capacity = Double.parseDouble(weightField.getText());

            java.util.List<PackageItem> items = new ArrayList<>();
            items.add(new PackageItem("Package A", 12, 80));
            items.add(new PackageItem("Package B", 18, 110));
            items.add(new PackageItem("Package C", 25, 150));
            items.add(new PackageItem("Package D", 22, 95));
            items.add(new PackageItem("Package E", 8, 60));


            Result result = solve(items, capacity);

            DefaultTableModel model = (DefaultTableModel) resultTable.getModel();
            model.setRowCount(0);
            totalValueLabel.setText("Total Value: ...");

            Timer timer = new Timer(200, null);
            final int[] index = {0};
            timer.addActionListener(e -> {
                if (index[0] < result.selectedItems.size()) {
                    model.addRow(result.selectedItems.get(index[0]));
                    index[0]++;
                } else {
                    timer.stop();
                    totalValueLabel.setText("Total Value: " + result.totalValue);
                }
            });
            timer.start();

            playSound("success.wav");

            JOptionPane.showMessageDialog(this,
                    "🎉 Load Calculation Successful!\nTotal Value: " + result.totalValue,
                    "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            playSound("error.wav");
            JOptionPane.showMessageDialog(this,
                    "❌ Invalid input or calculation failed!\nPlease enter a valid truck capacity.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static Result solve(java.util.List<PackageItem> items, double capacity) {
        items.sort((a, b) -> Double.compare(b.value / b.weight, a.value / a.weight));
        java.util.List<String[]> resultList = new ArrayList<>();
        double totalValue = 0;

        for (PackageItem item : items) {
            if (capacity == 0) break;

            if (item.weight <= capacity) {
                capacity -= item.weight;
                totalValue += item.value;
                resultList.add(new String[]{item.name, String.valueOf(item.weight), String.valueOf(item.value), "1.0"});
            } else {
                double fraction = capacity / item.weight;
                totalValue += item.value * fraction;
                resultList.add(new String[]{item.name, String.valueOf(capacity), String.format("%.2f", item.value * fraction), String.format("%.2f", fraction)});
                capacity = 0;
            }
        }
        return new Result(resultList, totalValue);
    }

    private void playSound(String fileName) {
        try {
            File soundFile = new File(fileName);
            javax.sound.sampled.AudioInputStream audioStream = javax.sound.sampled.AudioSystem.getAudioInputStream(soundFile);
            javax.sound.sampled.Clip clip = javax.sound.sampled.AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            System.err.println("Sound error: " + e.getMessage());
        }
    }

    private void addHoverEffect(JButton button, Color hoverColor, Color normalColor) {
        button.setBackground(normalColor);
        button.setBorderPainted(false);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(normalColor);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SmartTruckLoadingApp().setVisible(true));
    }
}
