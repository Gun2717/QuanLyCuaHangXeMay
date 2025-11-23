package iuh.fit.se.client.ui;

import iuh.fit.se.common.User;
import javax.swing.*;
import java.awt.*;

public class OrderPanel extends JPanel {
    private User currentUser;

    public OrderPanel(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("QUẢN LÝ ĐƠN HÀNG");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblTitle, BorderLayout.NORTH);

        JLabel lblContent = new JLabel("Nội dung quản lý đơn hàng", SwingConstants.CENTER);
        add(lblContent, BorderLayout.CENTER);
    }

    public void refresh() {
        // Load orders
    }
}
