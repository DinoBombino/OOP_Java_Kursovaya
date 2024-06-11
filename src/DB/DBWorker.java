package DB;

import java.sql.*;

public class DBWorker {
    public static final String PATH_TO_DB_FILE = "mydb.db";
    public static final String URL = "jdbc:sqlite:" + PATH_TO_DB_FILE;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            initializeDatabase();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static void initializeDatabase() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "login TEXT," +
                "password TEXT" +
                "type INTEGER" + //кто сторкипер или вендор
                ");";

        String createStoreKeeperTableSQL = "CREATE TABLE IF NOT EXISTS StoreKeeper (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "quantity INTEGER," +
                "cost_per_one INTEGER," +
                "category TEXT" +
                ");";

        String createVendorTableSQL = "CREATE TABLE IF NOT EXISTS Vendor (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "quantity INTEGER," +
                "cost_per_one INTEGER," +
                "category TEXT" +
                ");";

        try (Connection connection = DriverManager.getConnection(URL);
             Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
            statement.execute(createStoreKeeperTableSQL);
            statement.execute(createVendorTableSQL);
        }
    }

    public static boolean checkCredentials(String login, String password) {
        String query = "SELECT * FROM users WHERE login = ? AND password = ?";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, login);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getUserType(String login, String password) {
        String query = "SELECT type FROM users WHERE login = ? AND password = ?";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, login);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("type");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // В случае, если пользователя с таким логином и паролем не найдено
    }

    public static boolean addProductStoreKeeper(String name, int quantity, int cost_per_one, String category) {
        String selectQuery = "SELECT id, quantity FROM StoreKeeper WHERE name = ? AND cost_per_one = ? AND category = ?";
        String updateQuery = "UPDATE StoreKeeper SET quantity = quantity + ? WHERE id = ?";
        String insertQuery = "INSERT INTO StoreKeeper (name, quantity, cost_per_one, category) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(URL)) {
            // Проверяем, существует ли продукт
            try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
                selectStmt.setString(1, name);
                selectStmt.setInt(2, cost_per_one);
                selectStmt.setString(3, category);

                ResultSet resultSet = selectStmt.executeQuery();
                if (resultSet.next()) {
                    // Продукт существует, обновляем количество
                    int id = resultSet.getInt("id");
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, quantity);
                        updateStmt.setInt(2, id);
                        updateStmt.executeUpdate();
                    }
                } else {
                    // Продукт не существует, добавляем новую запись
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                        insertStmt.setString(1, name);
                        insertStmt.setInt(2, quantity);
                        insertStmt.setInt(3, cost_per_one);
                        insertStmt.setString(4, category);
                        insertStmt.executeUpdate();
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void addUser(String login, String password, int type) {
        String query = "INSERT INTO users (login, password, type) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement insertStmt = connection.prepareStatement(query)) {
            insertStmt.setString(1, login);
            insertStmt.setString(2, password);
            insertStmt.setInt(3, type);
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void deleteFromDB(String DB, int id) throws InterruptedException {
        Thread.sleep(500);
        String deleteQuery = "DELETE FROM " + DB + " WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DriverManager.getConnection(URL);
            stmt = conn.prepareStatement(deleteQuery);
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Закрываем ресурсы в блоке finally
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }


    public static void sendProductFromDB1ToDB2(int id, int quantityToSend, String DB1, String DB2) throws SQLException, InterruptedException {
        Thread.sleep(500);
        String selectStoreKeeperQuery = "SELECT * FROM "+DB1+" WHERE id = ?";
        String updateStoreKeeperQuery = "UPDATE "+DB1+" SET quantity = ? WHERE id = ?";
        String selectVendorQuery = "SELECT * FROM "+DB2+" WHERE name = ? AND category = ?";
        String updateVendorQuery = "UPDATE "+DB2+" SET quantity = ? WHERE id = ?";
        String insertVendorQuery = "INSERT INTO "+DB2+" (name, quantity, cost_per_one, category) VALUES (?, ?, ?, ?)";
        String insertHistoryQuery = "INSERT INTO History (name, quantity, cost_per_one, category, vendor_name) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement selectStoreKeeperStmt = connection.prepareStatement(selectStoreKeeperQuery);
             PreparedStatement updateStoreKeeperStmt = connection.prepareStatement(updateStoreKeeperQuery);
             PreparedStatement selectVendorStmt = connection.prepareStatement(selectVendorQuery);
             PreparedStatement updateVendorStmt = connection.prepareStatement(updateVendorQuery);
             PreparedStatement insertVendorStmt = connection.prepareStatement(insertVendorQuery);
             PreparedStatement insertHistoryStmt = connection.prepareStatement(insertHistoryQuery)) {

            connection.setAutoCommit(false); // Включаем ручное управление транзакциями

            // Получаем информацию о продукте из таблицы StoreKeeper
            selectStoreKeeperStmt.setInt(1, id);
            ResultSet storeKeeperResultSet = selectStoreKeeperStmt.executeQuery();

            if (storeKeeperResultSet.next()) {
                String name = storeKeeperResultSet.getString("name");
                int availableQuantity = storeKeeperResultSet.getInt("quantity");
                int costPerOne = storeKeeperResultSet.getInt("cost_per_one");
                String category = storeKeeperResultSet.getString("category");

                if (quantityToSend <= availableQuantity && quantityToSend > 0) {
                    int newQuantity = availableQuantity - quantityToSend;

                    // Обновляем количество продукта в таблице StoreKeeper
                    updateStoreKeeperStmt.setInt(1, newQuantity);
                    updateStoreKeeperStmt.setInt(2, id);
                    updateStoreKeeperStmt.executeUpdate();

                    // Проверяем, есть ли такой продукт уже в Vendor
                    selectVendorStmt.setString(1, name);
                    selectVendorStmt.setString(2, category);
                    ResultSet vendorResultSet = selectVendorStmt.executeQuery();

                    if (vendorResultSet.next()) {
                        // Продукт уже есть в Vendor, увеличиваем его количество
                        int vendorId = vendorResultSet.getInt("id");
                        int vendorQuantity = vendorResultSet.getInt("quantity") + quantityToSend;

                        updateVendorStmt.setInt(1, vendorQuantity);
                        updateVendorStmt.setInt(2, vendorId);
                        updateVendorStmt.executeUpdate();
                    } else {
                        // Продукта еще нет в Vendor, добавляем новую запись
                        insertVendorStmt.setString(1, name);
                        insertVendorStmt.setInt(2, quantityToSend);
                        insertVendorStmt.setInt(3, costPerOne);
                        insertVendorStmt.setString(4, category);
                        insertVendorStmt.executeUpdate();
                    }

                    if (DB2.equals("ToVendor") || DB2.equals("ToVendor2")) {
                        insertHistoryStmt.setString(1, name);
                        insertHistoryStmt.setInt(2, quantityToSend);
                        insertHistoryStmt.setInt(3, costPerOne);
                        insertHistoryStmt.setString(4, category);
                        insertHistoryStmt.setString(5, DB2);
                        insertHistoryStmt.executeUpdate();
                    }

                    connection.commit(); // Фиксируем изменения в базе данных
                } else {
                    throw new IllegalArgumentException("Недостаточное количество товара для отправки или введено некорректное количество");
                }
            } else {
                throw new SQLException("Продукт с таким ID не найден в StoreKeeper");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getAllFromDB(String DB) throws SQLException {
        String query = "SELECT * FROM " + DB;
        System.out.println("Executing query: " + query); // Логируем SQL-запрос
        Connection connection = DriverManager.getConnection(URL);
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public static int getTotalCostPerAll(String DB) throws SQLException { // метод-поганец, который чуть мне жизнь не сломал
        String query = "SELECT SUM(cost_per_all) AS total_cost FROM " + DB;

        // Используем try-with-resources для автоматического закрытия ресурсов
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getInt("total_cost");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;  // Перебрасываем исключение, чтобы вызвать его обработку в вызывающем коде
        }
        return 0;
        
    }

    public void removeZeroQuantityRows(String ToVendor) throws SQLException {
        String deleteQuery = "DELETE FROM "+ToVendor+" WHERE quantity = 0";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {
            stmt.executeUpdate();
        }
    }

    public static void moveAllToVendor(String ToVendor, String Vendor) throws SQLException {
        String selectAllFromToVendorQuery = "SELECT * FROM " + ToVendor;
        String deleteFromToVendorQuery = "DELETE FROM "+ToVendor+" WHERE id = ?";
        String selectVendorQuery = "SELECT * FROM "+Vendor+" WHERE name = ? AND category = ?";
        String updateVendorQuery = "UPDATE "+Vendor+" SET quantity = ? WHERE id = ?";
        String insertVendorQuery = "INSERT INTO "+Vendor+" (name, quantity, cost_per_one, category) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement selectAllFromToVendorStmt = connection.prepareStatement(selectAllFromToVendorQuery);
             PreparedStatement deleteFromToVendorStmt = connection.prepareStatement(deleteFromToVendorQuery);
             PreparedStatement selectVendorStmt = connection.prepareStatement(selectVendorQuery);
             PreparedStatement updateVendorStmt = connection.prepareStatement(updateVendorQuery);
             PreparedStatement insertVendorStmt = connection.prepareStatement(insertVendorQuery)) {

            connection.setAutoCommit(false); // Включаем ручное управление транзакциями

            // Получаем всю информацию из таблицы ToVendor
            ResultSet toVendorResultSet = selectAllFromToVendorStmt.executeQuery();

            while (toVendorResultSet.next()) {
                int id = toVendorResultSet.getInt("id");
                String name = toVendorResultSet.getString("name");
                int quantity = toVendorResultSet.getInt("quantity");
                int costPerOne = toVendorResultSet.getInt("cost_per_one");
                String category = toVendorResultSet.getString("category");

                // Проверяем, есть ли такой продукт уже в Vendor
                selectVendorStmt.setString(1, name);
                selectVendorStmt.setString(2, category);
                ResultSet vendorResultSet = selectVendorStmt.executeQuery();

                if (vendorResultSet.next()) {
                    // Продукт уже есть в Vendor, увеличиваем его количество
                    int vendorId = vendorResultSet.getInt("id");
                    int vendorQuantity = vendorResultSet.getInt("quantity") + quantity;

                    updateVendorStmt.setInt(1, vendorQuantity);
                    updateVendorStmt.setInt(2, vendorId);
                    updateVendorStmt.executeUpdate();
                } else {
                    // Продукта еще нет в Vendor, добавляем новую запись
                    insertVendorStmt.setString(1, name);
                    insertVendorStmt.setInt(2, quantity);
                    insertVendorStmt.setInt(3, costPerOne);
                    insertVendorStmt.setString(4, category);
                    insertVendorStmt.executeUpdate();
                }

                // Удаляем продукт из ToVendor
                deleteFromToVendorStmt.setInt(1, id);
                deleteFromToVendorStmt.executeUpdate();
            }

            connection.commit(); // Фиксируем изменения в базе данных
        } catch (SQLException e) {
            throw new RuntimeException("Database operation failed", e);
        }
    }

    public static void moveAllToStoreKeeper(String ToVendor) throws SQLException, InterruptedException {
        Thread.sleep(500);
        String selectAllFromToVendorQuery = "SELECT * FROM " + ToVendor;
        String updateToVendorQuery = "UPDATE "+ToVendor+" SET quantity = ? WHERE id = ?";
        String selectStoreKeeperQuery = "SELECT * FROM StoreKeeper WHERE name = ? AND category = ?";
        String updateStoreKeeperQuery = "UPDATE StoreKeeper SET quantity = ? WHERE id = ?";
        String insertStoreKeeperQuery = "INSERT INTO StoreKeeper (name, quantity, cost_per_one, category) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement selectAllFromToVendorStmt = connection.prepareStatement(selectAllFromToVendorQuery);
             PreparedStatement updateToVendorStmt = connection.prepareStatement(updateToVendorQuery);
             PreparedStatement selectStoreKeeperStmt = connection.prepareStatement(selectStoreKeeperQuery);
             PreparedStatement updateStoreKeeperStmt = connection.prepareStatement(updateStoreKeeperQuery);
             PreparedStatement insertStoreKeeperStmt = connection.prepareStatement(insertStoreKeeperQuery)) {

            connection.setAutoCommit(false); // Включаем ручное управление транзакциями

            // Получаем всю информацию о продуктах из таблицы ToVendor
            ResultSet toVendorResultSet = selectAllFromToVendorStmt.executeQuery();

            while (toVendorResultSet.next()) {
                int id = toVendorResultSet.getInt("id");
                String name = toVendorResultSet.getString("name");
                int availableQuantity = toVendorResultSet.getInt("quantity");
                int costPerOne = toVendorResultSet.getInt("cost_per_one");
                String category = toVendorResultSet.getString("category");

                if (availableQuantity > 0) {
                    // Проверяем, есть ли такой продукт уже в StoreKeeper
                    selectStoreKeeperStmt.setString(1, name);
                    selectStoreKeeperStmt.setString(2, category);
                    ResultSet storeKeeperResultSet = selectStoreKeeperStmt.executeQuery();

                    if (storeKeeperResultSet.next()) {
                        // Продукт уже есть в StoreKeeper, увеличиваем его количество
                        int storeKeeperId = storeKeeperResultSet.getInt("id");
                        int storeKeeperQuantity = storeKeeperResultSet.getInt("quantity") + availableQuantity;

                        updateStoreKeeperStmt.setInt(1, storeKeeperQuantity);
                        updateStoreKeeperStmt.setInt(2, storeKeeperId);
                        updateStoreKeeperStmt.executeUpdate();
                    } else {
                        // Продукта еще нет в StoreKeeper, добавляем новую запись
                        insertStoreKeeperStmt.setString(1, name);
                        insertStoreKeeperStmt.setInt(2, availableQuantity);
                        insertStoreKeeperStmt.setInt(3, costPerOne);
                        insertStoreKeeperStmt.setString(4, category);
                        insertStoreKeeperStmt.executeUpdate();
                    }

                    // Обновляем количество продукта в таблице ToVendor
                    updateToVendorStmt.setInt(1, 0); // Все переместили, количество становится 0
                    updateToVendorStmt.setInt(2, id);
                    updateToVendorStmt.executeUpdate();
                }
            }

            connection.commit(); // Фиксируем изменения в базе данных
        } catch (SQLException e) {
            throw new RuntimeException("Database operation failed", e);
        }
    }

    public static void addById(int id, int quantityToSend) throws InterruptedException {
        Thread.sleep(500);
        String selectStoreKeeperQuery = "SELECT quantity FROM StoreKeeper WHERE id = ?";
        String updateStoreKeeperQuery = "UPDATE StoreKeeper SET quantity = ? WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement selectStoreKeeperStmt = connection.prepareStatement(selectStoreKeeperQuery);
             PreparedStatement updateStoreKeeperStmt = connection.prepareStatement(updateStoreKeeperQuery)) {

            connection.setAutoCommit(false); // Включаем ручное управление транзакциями

            // Получаем информацию о количестве из таблицы
            selectStoreKeeperStmt.setInt(1, id);
            ResultSet storeKeeperResultSet = selectStoreKeeperStmt.executeQuery();

            if (storeKeeperResultSet.next()) {
                int currentQuantity = storeKeeperResultSet.getInt("quantity");
                int newQuantity = currentQuantity + quantityToSend;

                // Обновляем количество в таблице
                updateStoreKeeperStmt.setInt(1, newQuantity);
                updateStoreKeeperStmt.setInt(2, id);
                updateStoreKeeperStmt.executeUpdate();

                connection.commit(); // Подтверждаем транзакцию
            } else {
                System.out.println("Нет строки с ID: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database operation failed", e);
        }
    }
}
