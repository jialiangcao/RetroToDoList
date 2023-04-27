import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.DefaultMetalTheme;
import java.awt.Font;
import javax.swing.border.MatteBorder;
import java.util.Objects;


public class GUI {

    public static void main(String[] args) {


        SwingUtilities.invokeLater(GUI::createGUI);
    }

    private static void createGUI() {

        try {
            MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
            UIManager.setLookAndFeel(new MetalLookAndFeel());

        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("Reminders");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 500);

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());
        frame.setVisible(true);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(new MatteBorder(24, 0, 0, 0, Color.BLUE));
        ImageIcon originalIcon = new ImageIcon(Objects.requireNonNull(GUI.class.getResource("win95logo.png")));

        int scaledWidth = 50;
        int scaledHeight = 50;
        Image scaledImage = originalIcon.getImage().getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JLabel icon = new JLabel(scaledIcon);
        inputPanel.add(icon);

        JTextField reminderTitle = new JTextField(20);
        JTextField reminderDescription = new JTextField(20);
        JFormattedTextField dueDateInput = new JFormattedTextField(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        dueDateInput.setValue(LocalDate.now());
        dueDateInput.setColumns(10);
        JButton addButton = new JButton("Add Reminder");
        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setFont(new java.awt.Font("Monospaced", Font.BOLD, 16));
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new java.awt.Font("Monospaced", Font.BOLD, 16));
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(new java.awt.Font("Monospaced", Font.BOLD, 16));
        JLabel todayLabel = new JLabel("Today");
        JLabel pastLabel = new JLabel("Past");
        JLabel futureLabel = new JLabel("Future");
        JPanel panel1 = new JPanel();
        panel1.add(titleLabel);
        panel1.add(reminderTitle);
        panel1.add(descLabel);
        panel1.add(reminderDescription);
        panel1.add(dateLabel);
        panel1.add(dueDateInput);
        panel1.add(addButton);
        JPanel panel2 = new JPanel(new GridLayout(1,3));
        panel2.add(todayLabel);
        panel2.add(pastLabel);
        panel2.add(futureLabel);
        inputPanel.add(panel1);
        inputPanel.add(panel2);
        contentPane.add(inputPanel, BorderLayout.NORTH);
        todayLabel.setFont(new java.awt.Font("Monospaced", Font.BOLD, 16));
        pastLabel.setFont(new java.awt.Font("Monospaced", Font.BOLD, 16));
        futureLabel.setFont(new java.awt.Font("Monospaced", Font.BOLD, 16));

        JPanel remindersPanel = new JPanel(new GridLayout(1, 3));
        DefaultListModel<Reminder> todayListModel = new DefaultListModel<>();
        DefaultListModel<Reminder> pastListModel = new DefaultListModel<>();
        DefaultListModel<Reminder> futureListModel = new DefaultListModel<>();

        JList<Reminder> todayList = new JList<>(todayListModel);
        JList<Reminder> pastList = new JList<>(pastListModel);
        JList<Reminder> futureList = new JList<>(futureListModel);

        todayList.setCellRenderer(new ReminderListCellRenderer());
        pastList.setCellRenderer(new ReminderListCellRenderer());
        futureList.setCellRenderer(new ReminderListCellRenderer());

        remindersPanel.add(new JScrollPane(todayList));
        remindersPanel.add(new JScrollPane(pastList));
        remindersPanel.add(new JScrollPane(futureList));
        contentPane.add(remindersPanel, BorderLayout.CENTER);

        JPanel removePanel = new JPanel();
        removePanel.setLayout(new FlowLayout());
        JButton removeButton = new JButton("Mark as Complete");

        removePanel.add(removeButton);
        contentPane.add(removePanel, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = reminderTitle.getText().trim();
                String description = reminderDescription.getText().trim();
                LocalDate dueDate = LocalDate.parse(dueDateInput.getText(), DateTimeFormatter.ISO_DATE);

                if (!title.isEmpty() && !description.isEmpty()) {
                    Reminder reminder = new Reminder(title, description, dueDate);

                    if (dueDate.isEqual(LocalDate.now())) {
                        todayListModel.addElement(reminder);
                    } else if (dueDate.isBefore(LocalDate.now())) {
                        pastListModel.addElement(reminder);
                    } else {
                        futureListModel.addElement(reminder);
                    }

                    reminderTitle.setText("");
                    reminderDescription.setText("");
                    dueDateInput.setValue(LocalDate.now());
                }
            }
        });
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex;
                if ((selectedIndex = todayList.getSelectedIndex()) != -1) {
                    todayListModel.remove(selectedIndex);
                } else if ((selectedIndex = pastList.getSelectedIndex()) != -1) {
                    pastListModel.remove(selectedIndex);
                } else if ((selectedIndex = futureList.getSelectedIndex()) != -1) {
                    futureListModel.remove(selectedIndex);
                }
            }
        });

        frame.setVisible(true);
    }

    static class Reminder {
        private String title;
        private String description;
        private LocalDate dueDate;

        public Reminder(String title, String description, LocalDate dueDate) {
            this.title = title;
            this.description = description;
            this.dueDate = dueDate;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public LocalDate getDueDate() {
            return dueDate;
        }
    }

    static class ReminderListCellRenderer extends DefaultListCellRenderer {
        private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Reminder reminder = (Reminder) value;
            LocalDate dueDate = reminder.getDueDate();
            String dateString = dateFormatter.format(dueDate);

            String text = String.format("<html><b>%s</b><br>%s<br>Due: %s</html>", reminder.getTitle(), reminder.getDescription(), dateString);
            return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
        }
    }

}