package com.mathquiz.view;

import com.mathquiz.config.AppConfig;
import com.mathquiz.config.AppTheme;
import com.mathquiz.service.SoundService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MascotShopPanel extends JPanel {

    private final QuizNavigator navigator;
    private final AppConfig config;
    private final SoundService sound;

    private JLabel coinsLabel;
    private JPanel gridPanel;

    private static class ShopItem {
        String id;
        String name;
        String type; // "accessory" or "theme"
        String emoji;
        int cost;

        ShopItem(String id, String name, String type, String emoji, int cost) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.emoji = emoji;
            this.cost = cost;
        }
    }

    private final List<ShopItem> items = new ArrayList<>();

    public MascotShopPanel(QuizNavigator navigator, SoundService sound) {
        this.navigator = navigator;
        this.sound = sound;
        this.config = AppConfig.getInstance();

        // Register shop items
        items.add(new ShopItem("glasses", "Cool Glasses", "accessory", "🕶️", 100));
        items.add(new ShopItem("hat", "Wizard Hat", "accessory", "🧙‍♂️", 150));
        items.add(new ShopItem("bowtie", "Fancy Bow Tie", "accessory", "🎀", 80));
        items.add(new ShopItem("crown", "Gold Crown", "accessory", "👑", 300));
        items.add(new ShopItem("amethyst", "Amethyst Theme", "theme", "🟣", 200));
        items.add(new ShopItem("emerald", "Emerald Theme", "theme", "🟢", 200));
        items.add(new ShopItem("ruby", "Ruby Theme", "theme", "🔴", 250));

        build();
    }

    public void refresh() {
        coinsLabel.setText("🪙 " + config.getCoins() + " Coins");
        gridPanel.removeAll();
        for (ShopItem item : items) {
            gridPanel.add(makeItemCard(item));
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private void build() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(22, 32, 22, 32));

        // Header Panel
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel sub = new JLabel("MASCOT CUSTOMIZATION");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 10));
        sub.setForeground(AppTheme.getAccentGold());
        left.add(sub);

        JLabel title = new JLabel("Archie's Custom Shop");
        title.setFont(new Font("Serif", Font.PLAIN, 24));
        title.setForeground(AppTheme.getTextDark());
        left.add(title);
        header.add(left, BorderLayout.WEST);

        // Coin display and Back Button
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        right.setOpaque(false);

        coinsLabel = new JLabel("🪙 0 Coins");
        coinsLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        coinsLabel.setForeground(AppTheme.getAccentGold());
        right.add(coinsLabel);

        JButton back = new JButton("← Back");
        back.setFont(new Font("SansSerif", Font.PLAIN, 12));
        back.setBackground(AppTheme.getBgCard());
        back.setForeground(AppTheme.getTextMuted());
        back.setFocusPainted(false);
        back.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                new EmptyBorder(8, 18, 8, 18)));
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));
        back.addActionListener(e -> navigator.goToWelcome());
        right.add(back);

        header.add(right, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Items Grid (Scrollable)
        gridPanel = new JPanel(new GridLayout(0, 3, 16, 16));
        gridPanel.setOpaque(false);

        JScrollPane scroll = new JScrollPane(gridPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setViewportBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        add(scroll, BorderLayout.CENTER);
    }

    private JPanel makeItemCard(ShopItem item) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(AppTheme.getBgCard());
        card.setOpaque(true);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                new EmptyBorder(12, 14, 12, 14)));

        // Emoji display
        JLabel emojiLbl = new JLabel(item.emoji, SwingConstants.CENTER);
        emojiLbl.setFont(new Font("SansSerif", Font.PLAIN, 36));
        card.add(emojiLbl, BorderLayout.NORTH);

        // Name and details
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JLabel nameLbl = new JLabel(item.name, SwingConstants.CENTER);
        nameLbl.setFont(new Font("Serif", Font.BOLD, 14));
        nameLbl.setForeground(AppTheme.getTextDark());
        nameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(nameLbl);

        JLabel descLbl = new JLabel(item.type.toUpperCase() + "  ·  🪙 " + item.cost, SwingConstants.CENTER);
        descLbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
        descLbl.setForeground(AppTheme.getTextMuted());
        descLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(descLbl);

        card.add(center, BorderLayout.CENTER);

        // Button state
        JButton actionBtn = new JButton();
        actionBtn.setFocusPainted(false);
        actionBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        actionBtn.setFont(new Font("SansSerif", Font.BOLD, 11));

        boolean unlocked = config.isItemUnlocked(item.id);
        boolean equipped = false;

        if ("accessory".equals(item.type)) {
            equipped = config.getEquippedAccessory().equalsIgnoreCase(item.name);
        } else {
            equipped = config.getEquippedTheme().equalsIgnoreCase(item.id);
        }

        if (equipped) {
            actionBtn.setText("✓ Equipped");
            actionBtn.setBackground(new Color(230, 245, 230)); // Soft green
            actionBtn.setForeground(new Color(34, 139, 34));
            actionBtn.setEnabled(false);
            actionBtn.setBorder(BorderFactory.createLineBorder(new Color(180, 220, 180), 1));
        } else if (unlocked) {
            actionBtn.setText("Equip");
            actionBtn.setBackground(AppTheme.getBgPrimary());
            actionBtn.setForeground(AppTheme.getTextDark());
            actionBtn.setBorder(BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1));
            actionBtn.addActionListener(e -> equipItem(item));
        } else {
            actionBtn.setText("Buy 🪙 " + item.cost);
            actionBtn.setBackground(AppTheme.getAccentGold());
            actionBtn.setForeground(AppTheme.getBgPrimary());
            actionBtn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            actionBtn.addActionListener(e -> buyItem(item));
        }

        card.add(actionBtn, BorderLayout.SOUTH);
        return card;
    }

    private void buyItem(ShopItem item) {
        int coins = config.getCoins();
        if (coins < item.cost) {
            sound.playIncorrect();
            JOptionPane.showMessageDialog(this,
                    "Oops! You don't have enough coins yet. Complete more math quizzes to earn coins! 🦉",
                    "Not Enough Coins",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        config.setCoins(coins - item.cost);
        config.unlockItem(item.id);
        equipItem(item);
    }

    private void equipItem(ShopItem item) {
        sound.playClick();
        if ("accessory".equals(item.type)) {
            config.setEquippedAccessory(item.name);
        } else {
            config.setEquippedTheme(item.id);
        }

        refresh();

        if (navigator instanceof QuizFrame) {
            ((QuizFrame) navigator).updateAllThemes();
        }
    }

    public void applyTheme() {
        setBackground(AppTheme.getBgPrimary());
        coinsLabel.setForeground(AppTheme.getAccentGold());
        refresh();
    }
}
