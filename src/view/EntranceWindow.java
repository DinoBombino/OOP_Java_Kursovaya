package view;

import DB.DBWorker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EntranceWindow extends JFrame {
    private JButton btnBack, btnAdd;
    private JLabel jLabel_id, jLabel_name, jLabel_quantity, jLabel_costPerOne, jLabel_costPerAll, jLabel_category;
    private JTextField tf_id, tf_name, tf_quantity, tf_costPerOne, tf_costPerAll, tf_category;

    public EntranceWindow() {
        super("Поступление");
        setSize(280, 330); // Размер окна
        this.setLocationRelativeTo(null); // Окно по центру экрана
        init();
    }

    public void init() {
        // Установка менеджера компоновки
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);

        // Добавление надписи "Поступление"
        JLabel jLabel_title = new JLabel("Поступление", SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(jLabel_title, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;

        // Labels and Text Fields

        jLabel_name = new JLabel("Название");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        add(jLabel_name, gbc);

        tf_name = new JTextField(10);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(tf_name, gbc);

        jLabel_quantity = new JLabel("Количество");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        add(jLabel_quantity, gbc);

        tf_quantity = new JTextField(10);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        add(tf_quantity, gbc);

        jLabel_costPerOne = new JLabel("Цена за единицу");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        add(jLabel_costPerOne, gbc);

        tf_costPerOne = new JTextField(10);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        add(tf_costPerOne, gbc);


        jLabel_category = new JLabel("Категория");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.EAST;
        add(jLabel_category, gbc);

        tf_category = new JTextField(10);
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        add(tf_category, gbc);

        // Buttons
        btnBack = new JButton("Назад");
        gbc.gridx = 0;
        gbc.gridy = 7;
        this.btnBack.setBackground(Color.GRAY);
        this.btnBack.setForeground(Color.WHITE);
        gbc.anchor = GridBagConstraints.CENTER;
        add(btnBack, gbc);

        btnAdd = new JButton("Добавить");
        gbc.gridx = 1;
        gbc.gridy = 7;
        this.btnAdd.setBackground(Color.GRAY);
        this.btnAdd.setForeground(Color.WHITE);
        gbc.anchor = GridBagConstraints.CENTER;
        add(btnAdd, gbc);

        // Добавление слушателей к кнопкам
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = tf_name.getText();
                    int quantity = Integer.parseInt(tf_quantity.getText());
                    int cost_per_one = Integer.parseInt(tf_costPerOne.getText());
                    String category = tf_category.getText();

                    // Используем новый метод для добавления или обновления продукта
                    DBWorker.addProductStoreKeeper(name, quantity, cost_per_one, category);

                    // Обновляем таблицу
                    StoreKeeperWindow.updateBoard();
                    JOptionPane.showMessageDialog(EntranceWindow.this, "Добавлено");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(EntranceWindow.this, "Пожалуйста, введите корректные числовые значения для количества и цены.");
                }
            }
        });

        // Установка видимости окна
        setVisible(true);
    }
}
