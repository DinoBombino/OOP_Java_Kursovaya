package view;

import DB.DBWorker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Objects;

public class SellWindow extends JFrame {
    private final String vendorName;
    private final String soldName;
    private JButton btnBack, btnAdd;
    private JLabel jLabel_id, jLabel_total_coast, jLabel_quantity;
    private JTextField tf_id, tf_quantity;

    public SellWindow(String vendorName, String soldName) {
        super("Продать");
        setSize(280, 200); // Размер окна
        this.setLocationRelativeTo(null); // Окно по центру экрана
        this.vendorName = vendorName;
        this.soldName = soldName;
        init();
    }

    public void init() {
        // Установка менеджера компоновки
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);

        // Добавление надписи "Поступление"
        JLabel jLabel_title = new JLabel("Продать", SwingConstants.CENTER);
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
        gbc.gridy = 8;
        this.btnBack.setBackground(Color.GRAY);
        this.btnBack.setForeground(Color.WHITE);
        gbc.anchor = GridBagConstraints.CENTER;
        add(btnBack, gbc);

        btnAdd = new JButton("Продать");
        gbc.gridx = 1;
        gbc.gridy = 8;
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
                    sellProduct();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // Установка видимости окна
        setVisible(true);
    }

    private void sellProduct() throws SQLException, InterruptedException {
        int id = Integer.parseInt(tf_id.getText()); // получаем ID продукта из текстового поля
        int quantityToSend = Integer.parseInt(tf_quantity.getText()); // получаем количество для отправки из текстового поля
        DBWorker.sendProductFromDB1ToDB2(id, quantityToSend, vendorName, soldName);
        updateBoard();
        JOptionPane.showMessageDialog(null, "Товар продан");
    }

    private void updateBoard() {
        if (vendorName == "Vendor") VendorWindow.updateBoard();
        else Vendor2Window.updateBoard();
    }

}
