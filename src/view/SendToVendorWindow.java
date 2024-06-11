package view;

import DB.DBWorker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class SendToVendorWindow extends JFrame {
    private JButton btnBack, btnSend;
    private JLabel jLabel_id, jLabel_quantity;
    private JTextField tf_id, tf_quantity;

    private String vendorName = null;
    String selectedVendor = "Магазин 1";

    public SendToVendorWindow() {
        super("Отправить");
        setSize(260, 250); // Размер окна
        this.setLocationRelativeTo(null); // Окно по центру экрана
        init();
    }

    public void init() {
        // Установка менеджера компоновки
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);

        // Добавление надписи "Поступление"
        JLabel jLabel_title = new JLabel("Отправить", SwingConstants.CENTER);
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
        add(jLabel_id, gbc);

        tf_id = new JTextField(10);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(tf_id, gbc);

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

        // Создание выпадающего списка
        String[] vendors = {"Магазин 1", "Магазин 2"};
        JComboBox<String> comboBox = new JComboBox<>(vendors);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(comboBox, gbc);
        comboBox.setSelectedItem("Магазин 1");

        // Buttons
        btnBack = new JButton("Назад");
        gbc.gridx = 0;
        gbc.gridy = 9;
        this.btnBack.setBackground(Color.GRAY);
        this.btnBack.setForeground(Color.WHITE);
        gbc.anchor = GridBagConstraints.CENTER;
        add(btnBack, gbc);

        btnSend = new JButton("Отправить");
        gbc.gridx = 0;
        gbc.gridy = 8;
        this.btnSend.setBackground(Color.GRAY);
        this.btnSend.setForeground(Color.WHITE);
        gbc.anchor = GridBagConstraints.CENTER;
        add(btnSend, gbc);

        // Добавление слушателей к кнопкам
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendProductToVendor();
            }
        });
        // Обработка события выбора из выпадающего списка
        comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectedVendor = (String) comboBox.getSelectedItem();
                if (selectedVendor.equals("Магазин 1")) {
                    // Если выбран Магазин1, устанавливаем текст в поле ToVendor
                    vendorName = "ToVendor";
                } else vendorName = "ToVendor2";

            }
        });

        // Установка видимости окна
        setVisible(true);
    }

    private void sendProductToVendor() {
        int id = Integer.parseInt(tf_id.getText()); // получаем ID продукта из текстового поля
        int quantityToSend = Integer.parseInt(tf_quantity.getText()); // получаем количество для отправки из текстового поля

        try {
            // вызываем метод sendProductFromStoreKeeperToVendor из класса DBWorker
            DBWorker.sendProductFromDB1ToDB2(id, quantityToSend, "StoreKeeper", vendorName);
            StoreKeeperWindow.updateBoard();
            JOptionPane.showMessageDialog(null, "Товар успешно отправлен в Vendor");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Ошибка при отправке продукта", "Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
