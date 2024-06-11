package view;

import DB.DBWorker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SoldProductWindows extends JFrame  {
    private final String soldName;
    private JTable table;
    private JLabel jLabel_sold;
    private JButton btnBack, btnSold;
    private JFrame additionalFrame; // Дополнительное окно для showSentProducts

    public SoldProductWindows(String soldName) throws SQLException {
        super("Vendor");
        setSize(800, 400); // Размер окна
        this.setLocationRelativeTo(null); // Окно по центру экрана
        this.soldName = soldName;
        init();
        updateBoard();
    }

    public void init() throws SQLException {
        // Установка менеджера компоновки
        setLayout(new BorderLayout());

        JLabel jLabel_title = new JLabel("Проданные товары", SwingConstants.CENTER);
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

        int sold = DBWorker.getTotalCostPerAll(soldName);

        btnSold = new JButton("Продано за все время: " + sold);
        this.btnSold.setBackground(Color.BLACK);
        this.btnSold.setForeground(Color.WHITE);
        buttonPanel.add(btnSold);

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

    private void updateBoard() throws SQLException {
        DBWorker dbWorker = new DBWorker();

        ResultSet resultSet = dbWorker.getAllFromDB(soldName);
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
    }
}
