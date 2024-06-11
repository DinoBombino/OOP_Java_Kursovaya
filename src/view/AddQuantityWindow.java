package view;

import DB.DBWorker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddQuantityWindow extends JFrame {
    private JButton btnBack, btnAdd;
    private JLabel jLabel_id, jLabel_quantity;
    private JTextField tf_id, tf_quantity;

    public AddQuantityWindow() {
        super("Добавление Количества");
        setSize(280, 200); // Размер окна
        this.setLocationRelativeTo(null); // Окно по центру экрана
        init();
    }

    public void init() {
        // Установка менеджера компоновки
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);

        // Добавление надписи "Добавление Количества"
        JLabel jLabel_title = new JLabel("Добавление Количества", SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(jLabel_title, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;

        // Labels and Text Fields

        jLabel_id = new JLabel("ID");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(jLabel_id, gbc);

        tf_id = new JTextField(10);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(tf_id, gbc);

        jLabel_quantity = new JLabel("Количество");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        add(jLabel_quantity, gbc);

        tf_quantity = new JTextField(10);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(tf_quantity, gbc);

        // Buttons
        btnBack = new JButton("Назад");
        gbc.gridx = 0;
        gbc.gridy = 3;
        this.btnBack.setBackground(Color.GRAY);
        this.btnBack.setForeground(Color.WHITE);
        gbc.anchor = GridBagConstraints.CENTER;
        add(btnBack, gbc);

        btnAdd = new JButton("Добавить");
        gbc.gridx = 1;
        gbc.gridy = 3;
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
                    int id = Integer.parseInt(tf_id.getText());
                    int quantity = Integer.parseInt(tf_quantity.getText());

                    // Используем метод для добавления количества по id
                    DBWorker.addById(id, quantity);

                    // Обновляем таблицу
                    StoreKeeperWindow.updateBoard();
                    JOptionPane.showMessageDialog(AddQuantityWindow.this, "Добавлено");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(AddQuantityWindow.this, "Пожалуйста, введите корректные числовые значения для ID и количества.");
                } catch (InterruptedException ex) {
                    JOptionPane.showMessageDialog(AddQuantityWindow.this, "Произошла ошибка: " + ex.getMessage());
                }
            }
        });

        // Установка видимости окна
        setVisible(true);
    }
}

