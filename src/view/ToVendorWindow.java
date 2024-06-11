package view;
import DB.DBWorker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ToVendorWindow extends JFrame {

    private final String vendorName;
    private final String secondDBName;
    private final String writtenOffName;
    private JTable table;
    private JButton btnBack, btnSell;

    private JFrame additionalFrame; // Дополнительное окно для showSentProducts

    public ToVendorWindow(String vendorName, String secondDBName, String writtenOffName) {
        super("Vendor");
        setSize(800, 400); // Размер окна
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Закрытие окна
        this.setLocationRelativeTo(null); // Окно по центру экрана
        this.vendorName = vendorName;
        this.secondDBName = secondDBName;
        this.writtenOffName = writtenOffName;
        showSentProducts(); // Изменено с showSentProducts()
    }

    private void showSentProducts() {
        DBWorker dbWorker = new DBWorker();
        try {
            ResultSet resultSet = dbWorker.getAllFromDB(vendorName);
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

            // Показать данные в дополнительном окне
            showAdditionalFrame(model);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAdditionalFrame(DefaultTableModel model) {
        if (additionalFrame != null) {
            additionalFrame.dispose(); // Закрываем предыдущее дополнительное окно, если оно существует
        }

        additionalFrame = new JFrame("Sent Products");
        additionalFrame.setSize(800, 400);

        JTable additionalTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(additionalTable);
        additionalFrame.add(scrollPane);

        // Создание панели для кнопок "Принять" и "Отклонить"
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton acceptButton = new JButton("Принять");
        acceptButton.setBackground(Color.GRAY);
        acceptButton.setForeground(Color.WHITE);
        JButton rejectButton = new JButton("Отклонить");
        rejectButton.setBackground(Color.GRAY);
        rejectButton.setForeground(Color.WHITE);
        JButton acceptAllButton = new JButton("Принять всё");
        acceptAllButton.setBackground(Color.GRAY);
        acceptAllButton.setForeground(Color.WHITE);
        JButton rejectAllButton = new JButton("Отклонить всё");
        rejectAllButton.setBackground(Color.GRAY);
        rejectAllButton.setForeground(Color.WHITE);

        // Создаем поле JTextField для ввода ID
        JTextField idField = new JTextField(5); // 10 символов в ширину
        // Создаем поле JTextField для ввода количества
        JTextField quantityField = new JTextField(5); // 5 символов в ширину

        acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String idText = idField.getText(); // Получаем текст из поля ввода ID
                String quantityText = quantityField.getText(); // Получаем текст из поля ввода количества
                if (!idText.isEmpty() && !quantityText.isEmpty()) { // Проверяем, что поля ввода не пустые
                    try {
                        int id = Integer.parseInt(idText); // Преобразуем текст в число
                        int quantity = Integer.parseInt(quantityText); // Преобразуем текст в число
                        DBWorker dbWorker = new DBWorker();
                        dbWorker.sendProductFromDB1ToDB2(id, quantity, vendorName, secondDBName); // Перемещение выбранного товара в Vendor с указанным количеством
                        dbWorker.removeZeroQuantityRows(vendorName);
                        updateBoard();
                        showSentProducts();

                        JOptionPane.showMessageDialog(ToVendorWindow.this, "Отправлено: ID:"+id+" в количестве "+quantity+"шт в магазин");
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(ToVendorWindow.this, "Введите корректный ID и количество"); // Обработка некорректного ввода ID или количества
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        // Обработка ошибки
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    JOptionPane.showMessageDialog(ToVendorWindow.this, "Введите ID и количество для перемещения"); // Просим пользователя ввести ID и количество, если поля пустые
                }
            }
        });

        acceptAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    DBWorker dbWorker = new DBWorker();
                    dbWorker.moveAllToVendor(vendorName, secondDBName); // Вызов метода для перемещения всех товаров
                    dbWorker.removeZeroQuantityRows(vendorName);
                    updateBoard();
                    showSentProducts();
                    JOptionPane.showMessageDialog(ToVendorWindow.this, "Все товары успешно перемещены в магазин");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ToVendorWindow.this, "Произошла ошибка при перемещении товаров");
                }
            }
        });

        rejectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String idText = idField.getText(); // Получаем текст из поля ввода ID
                String quantityText = quantityField.getText(); // Получаем текст из поля ввода количества
                if (!idText.isEmpty() && !quantityText.isEmpty()) { // Проверяем, что поля ввода не пустые
                    try {
                        int id = Integer.parseInt(idText); // Преобразуем текст в число
                        int quantity = Integer.parseInt(quantityText); // Преобразуем текст в число
                        DBWorker dbWorker = new DBWorker();
                        dbWorker.sendProductFromDB1ToDB2(id, quantity, vendorName, "StoreKeeper"); // Перемещение выбранного товара в Vendor с указанным количеством
                        updateBoard();
                        dbWorker.removeZeroQuantityRows(vendorName);
                        showSentProducts();
                        JOptionPane.showMessageDialog(ToVendorWindow.this, "Отправлено: ID:"+id+" в количестве "+quantity+"шт обратно на склад");
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(ToVendorWindow.this, "Введите корректный ID и количество"); // Обработка некорректного ввода ID или количества
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        // Обработка ошибки
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    JOptionPane.showMessageDialog(ToVendorWindow.this, "Введите ID и количество для перемещения"); // Просим пользователя ввести ID и количество, если поля пустые
                }
            }
        });
        rejectAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    DBWorker dbWorker = new DBWorker();
                    dbWorker.moveAllToStoreKeeper(vendorName); // Вызов метода для перемещения всех товаров обратно в StoreKeeper
                    dbWorker.removeZeroQuantityRows(vendorName);
                    updateBoard();
                    showSentProducts();
                    JOptionPane.showMessageDialog(ToVendorWindow.this, "Все товары успешно перемещены обратно на склад");
                } catch (SQLException | InterruptedException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ToVendorWindow.this, "Произошла ошибка при перемещении товаров");
                }
            }
        });


        // Добавляем поля JTextField на панель кнопок
        buttonPanel.add(idField);
        buttonPanel.add(new JLabel("Количество:")); // Добавляем метку для поля ввода количества
        buttonPanel.add(quantityField);
        buttonPanel.add(acceptButton);
        buttonPanel.add(acceptAllButton);
        buttonPanel.add(rejectAllButton);
        buttonPanel.add(rejectButton);
        additionalFrame.add(buttonPanel, BorderLayout.SOUTH);

        additionalFrame.setLocationRelativeTo(this); // Расположение относительно главного окна
        additionalFrame.setVisible(true);
    }
    private void updateBoard() {
        if (vendorName == "ToVendor") VendorWindow.updateBoard();
        else Vendor2Window.updateBoard();
    }
}

