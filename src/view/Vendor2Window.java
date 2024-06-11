package view;
import DB.DBWorker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Vendor2Window extends JFrame {
    private static JTable table;
    private JButton btnBack, btnSell, btnTran, btnDel, btnSol, btnWri;


    public Vendor2Window() {
        super("Vendor 2");
        setSize(900, 400); // Размер окна
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Закрытие окна
        this.setLocationRelativeTo(null); // Окно по центру экрана
        init();
        updateBoard();
    }

    public void init() {
        // Установка менеджера компоновки
        setLayout(new BorderLayout());

        // Добавление надписи "StoreKeeper"
        JLabel jLabel_title = new JLabel("Vendor 2", SwingConstants.CENTER);
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

        btnSell = new JButton("Продать");
        this.btnSell.setBackground(Color.GRAY);
        this.btnSell.setForeground(Color.WHITE);
        buttonPanel.add(btnSell);

        btnTran = new JButton("Проверить");
        this.btnTran.setBackground(Color.GRAY);
        this.btnTran.setForeground(Color.WHITE);
        buttonPanel.add(btnTran);

        btnDel = new JButton("Списать");
        this.btnDel.setBackground(Color.GRAY);
        this.btnDel.setForeground(Color.WHITE);
        buttonPanel.add(btnDel);

        btnSol = new JButton("Проданные товары");
        this.btnSol.setBackground(Color.GRAY);
        this.btnSol.setForeground(Color.WHITE);
        buttonPanel.add(btnSol);

        btnWri = new JButton("Списанные товары");
        this.btnWri.setBackground(Color.GRAY);
        this.btnWri.setForeground(Color.WHITE);
        buttonPanel.add(btnWri);

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
        btnSell.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SellWindow("Vendor2", "Sold2");
            }
        });
        btnTran.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ToVendorWindow("ToVendor2", "Vendor2", "WrittenOff2");
            }
        });

        btnDel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new WriteOffWindow("Vendor2", "WrittenOff2");
            }
        });
        btnSol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new SoldProductWindows("Sold2");
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        btnWri.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new WrittenOffWindow("WrittenOff2");
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    protected static void updateBoard() {
        DBWorker dbWorker = new DBWorker();
        try {
            ResultSet resultSet = dbWorker.getAllFromDB("Vendor2");
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

