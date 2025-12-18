package gui;

import app.GlobalConstants;
import gui.components.CollectDonationDialog;
import models.Association;
import models.Donation;
import services.AssociationService;
import services.DonationCollectionService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;

public class AssociationPanel extends JPanel {
    private JTable donationTable;
    private DefaultTableModel tableModel;
    private AssociationService associationService;
    private DonationCollectionService donationCollectionService;
    private Association currentAssociation;

    public AssociationPanel(Association association, JPanel mainPanel, CardLayout cardLayout) {
        this.currentAssociation = association;
        this.associationService = new AssociationService();
        this.donationCollectionService = new DonationCollectionService();

        setLayout(new BorderLayout());

        // ===== Top Panel with Gradient =====
        JPanel topPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color startColor = GlobalConstants.SECONDARY_COLOR;
                Color endColor = GlobalConstants.LIGHT_BLUE_COLOR;
                g2d.setPaint(new GradientPaint(0, 0, startColor, 0, getHeight(), endColor));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        topPanel.setPreferredSize(new Dimension(GlobalConstants.FRAME_SIZE.width, 120));

        JLabel titleLabel = new JLabel("Available Donations", JLabel.CENTER);
        titleLabel.setFont(GlobalConstants.TITLE_FONT);
        titleLabel.setForeground(Color.BLACK);  // Text black
        topPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton collectDonationButton = new JButton("Collect Donation");
        collectDonationButton.setFont(GlobalConstants.LABEL_FONT);
        collectDonationButton.setBackground(GlobalConstants.BUTTON_BG_COLOR);
        collectDonationButton.setForeground(Color.WHITE);
        collectDonationButton.setFocusPainted(false);
        collectDonationButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        collectDonationButton.addActionListener(e -> handleCollectDonation(mainPanel));

        JButton associationDashboardButton = new JButton("Dashboard");
        associationDashboardButton.setFont(GlobalConstants.LABEL_FONT);
        associationDashboardButton.setBackground(GlobalConstants.BUTTON_BG_COLOR);
        associationDashboardButton.setForeground(Color.WHITE);
        associationDashboardButton.setFocusPainted(false);
        associationDashboardButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        associationDashboardButton.addActionListener(e -> {
            AssociationDashboardPanel dashboardPanel = new AssociationDashboardPanel(currentAssociation.getId(), mainPanel, cardLayout);
            mainPanel.add(dashboardPanel, "ASSOCIATION_DASHBOARD_PANEL");
            cardLayout.show(mainPanel, "ASSOCIATION_DASHBOARD_PANEL");
        });

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(GlobalConstants.LABEL_FONT);
        logoutButton.setBackground(GlobalConstants.BUTTON_BG_COLOR);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));

        buttonPanel.add(collectDonationButton);
        buttonPanel.add(associationDashboardButton);
        buttonPanel.add(logoutButton);

        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // ===== Table =====
        tableModel = new DefaultTableModel(new Object[]{"ID", "Type", "Description", "Quantity", "Available"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        donationTable = new JTable(tableModel);
        customizeTable();
        add(new JScrollPane(donationTable), BorderLayout.CENTER);

        loadDonations();
    }

    private void customizeTable() {
        donationTable.setRowHeight(30);
        donationTable.getTableHeader().setFont(GlobalConstants.TABLE_COLUMN_NAME_FONT);
        donationTable.setFont(GlobalConstants.LABEL_FONT);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < donationTable.getColumnCount(); i++)
            donationTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);

        int[] columnWidths = {50, 120, 330, 80, 70};
        for (int i = 0; i < columnWidths.length; i++)
            donationTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);

        donationTable.getTableHeader().setReorderingAllowed(false);
        donationTable.getTableHeader().setResizingAllowed(false);
        donationTable.setGridColor(GlobalConstants.TABLE_GRID_COLOR);
        donationTable.setBackground(GlobalConstants.TABLE_BG);
        donationTable.getTableHeader().setBackground(GlobalConstants.TABLE_HEADER_BG);
        donationTable.getTableHeader().setForeground(Color.WHITE);
        donationTable.setSelectionBackground(GlobalConstants.TABLE_SELECTION_BG);
        donationTable.setSelectionForeground(GlobalConstants.TABLE_SELECTION_FG);
    }

    private void loadDonations() {
        tableModel.setRowCount(0);
        ArrayList<Donation> donations = associationService.getAvailableDonations();
        for (Donation donation : donations) {
            tableModel.addRow(new Object[]{
                    donation.getId(),
                    donation.getType(),
                    donation.getDescription(),
                    donation.getQuantity(),
                    donation.isAvailable()
            });
        }
    }

    private void handleCollectDonation(JPanel mainPanel) {
        int selectedRow = donationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a donation to collect.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int donationId = (int) tableModel.getValueAt(selectedRow, 0);
        int availableQuantity = (int) tableModel.getValueAt(selectedRow, 3);

        if (availableQuantity == 0) {
            JOptionPane.showMessageDialog(this, "Donation unavailable currently.", "Not Available", JOptionPane.ERROR_MESSAGE);
            return;
        }

        CollectDonationDialog dialog = new CollectDonationDialog((JFrame) SwingUtilities.getWindowAncestor(this), availableQuantity);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            int qtyToCollect = dialog.getQuantityToCollect();
            boolean success = donationCollectionService.collectDonation(currentAssociation.getId(), donationId, qtyToCollect);

            if (success) {
                JOptionPane.showMessageDialog(this, "Donation collected successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadDonations();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to collect donation. Try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

