package iuh.fit.se.client.ui;

import iuh.fit.se.common.User;
import javax.swing.*;
import java.awt.*;

public class UserPanel extends JPanel {
    private User currentUser;

    public UserPanel(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("QUẢN LÝ NGƯỜI DÙNG");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblTitle, BorderLayout.NORTH);

        JLabel lblContent = new JLabel("Nội dung quản lý người dùng", SwingConstants.CENTER);
        add(lblContent, BorderLayout.CENTER);
    }

    public void refresh() {
        // Load users
    }
}
