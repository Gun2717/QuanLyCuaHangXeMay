package iuh.fit.se.client.ui;

import iuh.fit.se.common.User;
import javax.swing.*;
import java.awt.*;

public class CustomerPanel extends JPanel {
    private User currentUser;

    public CustomerPanel(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("QUẢN LÝ KHÁCH HÀNG");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblTitle, BorderLayout.NORTH);

        // Thêm table và các chức năng tương tự ProductPanel
        JLabel lblContent = new JLabel("Nội dung quản lý khách hàng", SwingConstants.CENTER);
        add(lblContent, BorderLayout.CENTER);
    }

    public void refresh() {
        // Load customers
    }
}
