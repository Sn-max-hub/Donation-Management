package gui;

import app.GlobalConstants;
import models.DonationCollection;
import services.DonationCollectionService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;

public class AssociationDashboardPanel extends JPanel {
    private JTable collectedTable;
    private DefaultTableModel tableModel;
    private DonationCollectionService donationCollectionService;

    public AssociationDashboardPanel(int associationId, JPanel mainPanel, CardLayout cardLayout) {
        donationCollectionService = new DonationCollectionService();
        setLayout(new BorderLayout());

        // Top panel with Back and Logout
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setPreferredSize(new Dimension(GlobalConstants.FRAME_SIZE.width, 60));
        topPanel.setBackground(GlobalConstants.LIGHT_BLUE_COLOR);

        JButton backButton = new JButton("Back");
        backButton.setFont(GlobalConstants.LABEL_FONT);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "ASSOCIATION_PANEL"));

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(GlobalConstants.LABEL_FONT);
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));

        topPanel.add(backButton);
        topPanel.add(logoutButton);
        add(topPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new Object[]{"Type", "Description", "Quantity", "Donor", "Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        collectedTable = new JTable(tableModel);
        customizeTable();
        add(new JScrollPane(collectedTable), BorderLayout.CENTER);

        loadCollectedDonations(associationId);
    }

    private void customizeTable() {
        collectedTable.setRowHeight(30);
        collectedTable.getTableHeader().setFont(GlobalConstants.TABLE_COLUMN_NAME_FONT);
        collectedTable.setFont(GlobalConstants.LABEL_FONT);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < collectedTable.getColumnCount(); i++)
            collectedTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);

        int[] columnWidths = {70, 300, 80, 100, 100};
        for (int i = 0; i < columnWidths.length; i++)
            collectedTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);

        collectedTable.setGridColor(GlobalConstants.TABLE_GRID_COLOR);
        collectedTable.setSelectionBackground(GlobalConstants.TABLE_SELECTION_BG);
        collectedTable.setSelectionForeground(GlobalConstants.TABLE_SELECTION_FG);
        collectedTable.setBackground(GlobalConstants.TABLE_BG);
        collectedTable.getTableHeader().setBackground(GlobalConstants.TABLE_HEADER_BG);
        collectedTable.getTableHeader().setForeground(Color.WHITE);
    }

    private void loadCollectedDonations(int associationId) {
        tableModel.setRowCount(0);
        ArrayList<DonationCollection> collections = donationCollectionService.getDonationsCollectedByAssociation(associationId);
        for (DonationCollection d : collections) {
            tableModel.addRow(new Object[]{
                    d.getType(), d.getDescription(), d.getQuantity(), d.getAssociationName(), d.getDateDonationCollected()
            });
        }
    }
}

