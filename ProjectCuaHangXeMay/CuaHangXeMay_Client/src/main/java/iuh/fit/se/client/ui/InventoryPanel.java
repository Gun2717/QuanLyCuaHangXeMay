package iuh.fit.se.client.ui;

import iuh.fit.se.common.User;
import javax.swing.*;
import java.awt.*;

public class InventoryPanel extends JPanel {
    private User currentUser;

    public InventoryPanel(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("QUẢN LÝ TỒN KHO");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblTitle, BorderLayout.NORTH);

        JLabel lblContent = new JLabel("Nội dung quản lý tồn kho", SwingConstants.CENTER);
        add(lblContent, BorderLayout.CENTER);
    }

    public void refresh() {
        // Load inventory
    }
}
