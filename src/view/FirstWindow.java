package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import DB.DBWorker;

public class FirstWindow extends JFrame {
    private JButton btn_login, btn_register;
    private JLabel jLabel_hello, jLabel_login, jLabel_password;
///////////////////////////////////////////////////////////////////////////
    private JButton btnStore, btnVendor;///*Временное решение - Удалить////
///////////////////////////////////////////////////////////////////////////
    private JTextField tf_login;
    private JPasswordField pf_password;
    public int type = 0;

    public FirstWindow() {
        super("Login");
        setSize(300, 200); // Размер окна
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Закрытие окна
        this.setLocationRelativeTo(null); // Окно по центру экрана
        init();
    }

    public void init() {
        // Установка менеджера компоновки
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);

        // Добавление надписи "Hello"
        jLabel_hello = new JLabel("Hello", SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(jLabel_hello, gbc);

        // Добавление надписи "Login"
        jLabel_login = new JLabel("Login");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(jLabel_login, gbc);

        // Добавление текстового поля для ввода логина
        tf_login = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(tf_login, gbc);

        // Добавление надписи "Password"
        jLabel_password = new JLabel("Password");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        add(jLabel_password, gbc);

        // Добавление текстового поля для ввода пароля
        pf_password = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(pf_password, gbc);

        btn_login = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        this.btn_login.setBackground(Color.GRAY);
        this.btn_login.setForeground(Color.WHITE);
        gbc.anchor = GridBagConstraints.CENTER;
        add(btn_login, gbc);

        btn_register = new JButton("Register");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        this.btn_register.setBackground(Color.GRAY);
        this.btn_register.setForeground(Color.WHITE);
        gbc.anchor = GridBagConstraints.CENTER;
        add(btn_register, gbc);

        addListeners();

        setVisible(true);
    }

    private void addListeners() {
        btn_login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String login = tf_login.getText();
                char [] password = pf_password.getPassword();
                login(login, new String(password));
                dispose();
            }
        });
        btn_register.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RegisterWindow();
            }
        });
    }

    private void login(String login, String password) {

        if (DBWorker.checkCredentials(login, password)) {
            JOptionPane.showMessageDialog(this, "Login successful");
            type = DBWorker.getUserType(login, password);
            if (type == 1) new StoreKeeperWindow();
            else if (type == 2) new VendorWindow();
            else new Vendor2Window();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid login or password");
            new FirstWindow();
        }

    }
}
