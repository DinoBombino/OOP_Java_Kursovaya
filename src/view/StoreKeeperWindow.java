package view;

import DB.DBWorker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StoreKeeperWindow extends JFrame {
    private static JTable table;
    private JButton btnBack;
    private JButton btnAdd;
    private JButton btnSend;
    private JButton btnDelete;
    private JButton btnAddID;
    private JButton btnHis;

    public StoreKeeperWindow() {
        super("StoreKeeper");
        setSize(800, 400); // Размер окна
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Закрытие окна
        this.setLocationRelativeTo(null); // Окно по центру экрана
        init();
        updateBoard();
    }

    public void init() {
        // Установка менеджера компоновки
        setLayout(new BorderLayout());

        // Добавление надписи "StoreKeeper"
        JLabel jLabel_title = new JLabel("StoreKeeper", SwingConstants.CENTER);
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

        btnAdd = new JButton("Поступление");
        this.btnAdd.setBackground(Color.GRAY);
        this.btnAdd.setForeground(Color.WHITE);
        buttonPanel.add(btnAdd);

        btnAddID = new JButton("Добавление по ID");
        btnAddID.setBackground(Color.GRAY);
        btnAddID.setForeground(Color.WHITE);
        buttonPanel.add(btnAddID);

        btnSend = new JButton("Отправить");
        this.btnSend.setBackground(Color.GRAY);
        this.btnSend.setForeground(Color.WHITE);
        buttonPanel.add(btnSend);

        btnDelete = new JButton("Удалить");
        this.btnDelete.setBackground(Color.GRAY);
        this.btnDelete.setForeground(Color.WHITE);
        buttonPanel.add(btnDelete);

        btnHis = new JButton("История");
        this.btnHis.setBackground(Color.GRAY);
        this.btnHis.setForeground(Color.WHITE);
        buttonPanel.add(btnHis);


        add(buttonPanel, BorderLayout.SOUTH);

        addListeners();

        // Установка видимости окна
        setVisible(true);
    }

    private void addListeners() {
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FirstWindow();
                dispose(); // Закрываем окно при нажатии кнопки
            }
        });
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new EntranceWindow();
            }
        });
        btnAddID.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddQuantityWindow();
            }
        });
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SendToVendorWindow();
            }
        });
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    // Получаем ID выбранной строки (предполагаем, что ID находится в первом столбце)
                    int id = (int) table.getValueAt(selectedRow, 0);
                    // Удаление выбранного предмета из базы данных
                    try {
                        DBWorker.deleteFromDB("StoreKeeper", id);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }

                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    model.removeRow(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(StoreKeeperWindow.this, "Выберите строку для удаления");
                }
            }
        });
        btnHis.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new HistoryWindow();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    protected static void updateBoard() {
        DBWorker dbWorker = new DBWorker();
        try {
            ResultSet resultSet = dbWorker.getAllFromDB("StoreKeeper");
            // Создание модели таблицы и заполнение её данными из ResultSet
            DefaultTableModel model = new DefaultTableModel();
            String[] columnNames = {"id", "name", "quantity", "cost_per_one", "cost_per_all", "category"};
            model.addColumn("ID");
            model.addColumn("Название");
            model.addColumn("Количество");
            model.addColumn("Цена за единицу");
            model.addColumn("Цена за все");
            model.addColumn("Категория");

            // Проверка наличия данных в ResultSet
            if (!resultSet.isBeforeFirst()) {
                System.out.println("No data returned from the query.");
            }

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
