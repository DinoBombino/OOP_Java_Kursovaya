package view;

import DB.DBWorker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WrittenOffWindow extends JFrame  {
    private final String writtenOffName;
    private JTable table;
    private JLabel jLabel_sold;
    private JButton btnBack, btnSold;
    private JFrame additionalFrame; // Дополнительное окно для showSentProducts

    public WrittenOffWindow(String writtenOffName) throws SQLException {
        super("Списанное");
        setSize(800, 400); // Размер окна
        this.setLocationRelativeTo(null); // Окно по центру экрана
        this.writtenOffName = writtenOffName;
        init();
        updateBoard();
    }

    public void init() throws SQLException {
        // Установка менеджера компоновки
        setLayout(new BorderLayout());

        JLabel jLabel_title = new JLabel("Списанные товары", SwingConstants.CENTER);
        add(jLabel_title, BorderLayout.NORTH);

        // Создание таблицы
        String[] columnNames = {"id", "name", "quantity", "cost_per_one", "cost_per_all", "category"};
        Object[][] data = {
                {"", "", "", "", "", "", ""},
                {"", "", "", "", "", "", ""}
        };
        table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Панель с кнопками
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));

        btnBack = new JButton("Назад");
        this.btnBack.setBackground(Color.GRAY);
        this.btnBack.setForeground(Color.WHITE);
        buttonPanel.add(btnBack);

        add(buttonPanel, BorderLayout.SOUTH);

        addListeners();

        setVisible(true);
    }

    private void addListeners() {
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Закрываем окно при нажатии кнопки
            }
        });
    }

    private void updateBoard() {
        DBWorker dbWorker = new DBWorker();
        try {
            ResultSet resultSet = dbWorker.getAllFromDB(writtenOffName);
            // Создание модели таблицы и заполнение её данными из ResultSet
            DefaultTableModel model = new DefaultTableModel();
            String[] columnNames = {"id", "name", "quantity", "cost_per_one", "cost_per_all", "category"};
            model.addColumn("ID");
            model.addColumn("Название");
            model.addColumn("Количество");
            model.addColumn("Цена за единицу");
            model.addColumn("Цена за все");
            model.addColumn("Категория");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int quantity = resultSet.getInt("quantity");
                int cost_per_one = resultSet.getInt("cost_per_one");
                int cost_per_all = resultSet.getInt("cost_per_all");
                String category = resultSet.getString("category");
                model.addRow(new Object[]{id, name, quantity, cost_per_one, cost_per_all, category});
            }
            table.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

