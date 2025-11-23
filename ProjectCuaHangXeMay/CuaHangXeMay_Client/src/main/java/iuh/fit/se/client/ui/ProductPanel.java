package iuh.fit.se.client.ui;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import iuh.fit.se.client.service.ApiService;
import iuh.fit.se.common.Category;
import iuh.fit.se.common.Product;
import iuh.fit.se.common.Response;
import iuh.fit.se.common.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class ProductPanel extends JPanel {
    private User currentUser;
    private ApiService apiService;
    private Gson gson;

    private JTable productTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh, btnSearch;
    private JComboBox<String> cboCategory;
    private List<Category> categories;

    public ProductPanel(User user) {
        this.currentUser = user;
        this.apiService = new ApiService();
        this.gson = new Gson();
        initComponents();
        loadCategories();
        loadProducts();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("QUẢN LÝ SẢN PHẨM");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(new Color(52, 73, 94));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        // Toolbar
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        toolbarPanel.setBackground(Color.WHITE);

        txtSearch = new JTextField(20);
        txtSearch.setPreferredSize(new Dimension(250, 35));
        txtSearch.setFont(new Font("Arial", Font.PLAIN, 14));

        btnSearch = createButton("🔍 Tìm kiếm", new Color(52, 152, 219));
        btnSearch.addActionListener(e -> searchProducts());

        btnAdd = createButton("➕ Thêm mới", new Color(46, 204, 113));
        btnAdd.addActionListener(e -> showAddDialog());

        btnEdit = createButton("✏️ Sửa", new Color(241, 196, 15));
        btnEdit.addActionListener(e -> showEditDialog());

        btnDelete = createButton("🗑️ Xóa", new Color(231, 76, 60));
        btnDelete.addActionListener(e -> deleteProduct());

        btnRefresh = createButton("🔄 Làm mới", new Color(149, 165, 166));
        btnRefresh.addActionListener(e -> loadProducts());

        toolbarPanel.add(txtSearch);
        toolbarPanel.add(btnSearch);
        toolbarPanel.add(btnAdd);
        toolbarPanel.add(btnEdit);
        toolbarPanel.add(btnDelete);
        toolbarPanel.add(btnRefresh);

        headerPanel.add(toolbarPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Tên sản phẩm", "Hãng", "Model", "Màu sắc", "Giá", "Số lượng", "Danh mục", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productTable = new JTable(tableModel);
        productTable.setFont(new Font("Arial", Font.PLAIN, 13));
        productTable.setRowHeight(30);
        productTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        productTable.getTableHeader().setBackground(new Color(52, 73, 94));
        productTable.getTableHeader().setForeground(Color.WHITE);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        add(scrollPane, BorderLayout.CENTER);
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 35));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public void refresh() {
        loadProducts();
    }

    private void loadCategories() {
        Response response = apiService.getAllCategories();
        if (response.isSuccess()) {
            String json = gson.toJson(response.getData());
            categories = gson.fromJson(json, new TypeToken<List<Category>>(){}.getType());
        }
    }

    private void loadProducts() {
        tableModel.setRowCount(0);

        SwingWorker<Response, Void> worker = new SwingWorker<Response, Void>() {
            @Override
            protected Response doInBackground() throws Exception {
                return apiService.getAllProducts();
            }

            @Override
            protected void done() {
                try {
                    Response response = get();
                    if (response.isSuccess()) {
                        String json = gson.toJson(response.getData());
                        List<Product> products = gson.fromJson(json, new TypeToken<List<Product>>(){}.getType());

                        for (Product p : products) {
                            tableModel.addRow(new Object[]{
                                    p.getId(),
                                    p.getName(),
                                    p.getBrand(),
                                    p.getModel(),
                                    p.getColor(),
                                    String.format("%,.0f đ", p.getPrice()),
                                    p.getQuantity(),
                                    p.getCategoryName(),
                                    p.getStatus()
                            });
                        }
                    } else {
                        JOptionPane.showMessageDialog(ProductPanel.this,
                                response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ProductPanel.this,
                            "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void searchProducts() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadProducts();
            return;
        }

        tableModel.setRowCount(0);
        Response response = apiService.searchProducts(keyword);

        if (response.isSuccess()) {
            String json = gson.toJson(response.getData());
            List<Product> products = gson.fromJson(json, new TypeToken<List<Product>>(){}.getType());

            for (Product p : products) {
                tableModel.addRow(new Object[]{
                        p.getId(), p.getName(), p.getBrand(), p.getModel(),
                        p.getColor(), String.format("%,.0f đ", p.getPrice()),
                        p.getQuantity(), p.getCategoryName(), p.getStatus()
                });
            }
        }
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm sản phẩm", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Form fields
        JTextField txtName = new JTextField(20);
        JTextField txtModel = new JTextField(20);
        JTextField txtBrand = new JTextField(20);
        JTextField txtColor = new JTextField(20);
        JTextField txtPrice = new JTextField(20);
        JTextField txtQuantity = new JTextField(20);
        JTextArea txtDescription = new JTextArea(3, 20);

        JComboBox<String> cboCategory = new JComboBox<>();
        for (Category cat : categories) {
            cboCategory.addItem(cat.getName());
        }

        JComboBox<String> cboStatus = new JComboBox<>(new String[]{"AVAILABLE", "OUT_OF_STOCK", "DISCONTINUED"});

        // Add components
        int row = 0;
        addFormField(panel, gbc, row++, "Danh mục:", cboCategory);
        addFormField(panel, gbc, row++, "Tên sản phẩm:", txtName);
        addFormField(panel, gbc, row++, "Model:", txtModel);
        addFormField(panel, gbc, row++, "Hãng:", txtBrand);
        addFormField(panel, gbc, row++, "Màu sắc:", txtColor);
        addFormField(panel, gbc, row++, "Giá:", txtPrice);
        addFormField(panel, gbc, row++, "Số lượng:", txtQuantity);
        addFormField(panel, gbc, row++, "Trạng thái:", cboStatus);
        addFormField(panel, gbc, row++, "Mô tả:", new JScrollPane(txtDescription));

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");

        btnSave.addActionListener(e -> {
            try {
                Product product = new Product();
                product.setCategoryId(categories.get(cboCategory.getSelectedIndex()).getId());
                product.setName(txtName.getText());
                product.setModel(txtModel.getText());
                product.setBrand(txtBrand.getText());
                product.setColor(txtColor.getText());
                product.setPrice(new BigDecimal(txtPrice.getText()));
                product.setQuantity(Integer.parseInt(txtQuantity.getText()));
                product.setStatus((String) cboStatus.getSelectedItem());
                product.setDescription(txtDescription.getText());

                Response response = apiService.createProduct(product);
                if (response.isSuccess()) {
                    JOptionPane.showMessageDialog(dialog, "Thêm sản phẩm thành công!");
                    loadProducts();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);

        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);

        dialog.add(new JScrollPane(panel));
        dialog.setVisible(true);
    }

    private void showEditDialog() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần sửa!");
            return;
        }

        int productId = (int) tableModel.getValueAt(selectedRow, 0);
        // Similar to showAddDialog but with pre-filled data
        JOptionPane.showMessageDialog(this, "Chức năng đang phát triển!");
    }

    private void deleteProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa sản phẩm này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int productId = (int) tableModel.getValueAt(selectedRow, 0);
            Response response = apiService.deleteProduct(productId);

            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this, "Xóa sản phẩm thành công!");
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(this, response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(field, gbc);
    }
}
