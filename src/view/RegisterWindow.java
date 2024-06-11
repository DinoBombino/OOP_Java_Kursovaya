package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import DB.DBWorker;

public class RegisterWindow extends JFrame {
    private JButton btn_register;
    private JLabel jLabel_hello, jLabel_login, jLabel_password, jLabel_type;
    ///////////////////////////////////////////////////////////////////////////

    private JTextField tf_login;
    private JPasswordField pf_password;
    public int type = 0;
    private JTextField tf_type;

    public RegisterWindow() {
        super("Login");
        setSize(300, 200); // Размер окна
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

        jLabel_type = new JLabel("Type 1/2");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        add(jLabel_type, gbc);

        // Добавление текстового поля для ввода пароля
        tf_type = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        add(tf_type, gbc);

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
        btn_register.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String login = tf_login.getText();
                String password = pf_password.getText();
                int type = Integer.parseInt(tf_type.getText());
                DBWorker.addUser(login, password, type);
                JOptionPane.showMessageDialog(RegisterWindow.this, "Registered");
                new FirstWindow();
                dispose();
            }
        });
    }
}
