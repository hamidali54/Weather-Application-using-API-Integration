import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;

public class ModernWeatherGUI {

    private JFrame frame;
    private JTextField searchField;
    private JLabel cityLabel, tempLabel, descLabel, humidLabel, iconPlaceholder;
    private JButton searchButton;
    private Image bgImage;

    public static void main(String[] args) {
        // High-quality text rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> new ModernWeatherGUI().createAndShowGUI());
    }

    private void createAndShowGUI() {
        frame = new JFrame("Pro Weather Application 🌤️");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(950, 600);
        frame.setLocationRelativeTo(null);

        // --- LOAD BACKGROUND IMAGE ---
        URL bgURL = ModernWeatherGUI.class.getResource("bg2.png");
        if (bgURL != null) {
            bgImage = new ImageIcon(bgURL).getImage();
        }

        // --- 1. MAIN PANEL (Custom Gradient/Image) ---
        JPanel backgroundPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (bgImage != null) {
                    g2d.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                    // Dark overlay for better text readability
                    g2d.setColor(new Color(0, 0, 0, 80));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    GradientPaint gp = new GradientPaint(0, 0, new Color(15, 32, 39), 0, getHeight(), new Color(44, 83, 100));
                    g2d.setPaint(gp);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        backgroundPanel.setBorder(new EmptyBorder(40, 50, 40, 50));
        frame.setContentPane(backgroundPanel);

        // --- 2. HEADER AREA (Pill Search Bar) ---
        JPanel headerPanel = new JPanel(new BorderLayout(15, 0));
        headerPanel.setOpaque(false);

        // Modern Rounded Search Field
        searchField = new RoundedTextField(20);
        searchField.setText(" Search City...");
        
        searchButton = new JButton("Search");
        styleModernButton(searchButton);
        searchButton.addActionListener(e -> fetchAndDisplayWeather());

        headerPanel.add(searchField, BorderLayout.CENTER);
        headerPanel.add(searchButton, BorderLayout.EAST);
        backgroundPanel.add(headerPanel, BorderLayout.NORTH);

        // --- 3. MAIN CONTENT (Glass Card) ---
        JPanel glassCard = createGlassPanel(new BorderLayout(30, 0), 40);
        glassCard.setBorder(new EmptyBorder(50, 50, 50, 50));

        // LEFT SIDE: Information
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        cityLabel = createLabel("Explore Weather", new Font("Segoe UI", Font.BOLD, 42), Color.WHITE);
        tempLabel = createLabel("--°C", new Font("Segoe UI", Font.BOLD, 90), Color.WHITE);
        descLabel = createLabel("Waiting for input...", new Font("Segoe UI", Font.ITALIC, 22), new Color(200, 200, 200));
        
        // Stats Row (Humidity etc)
        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        statsRow.setOpaque(false);
        humidLabel = createLabel("💧 Humidity: -- %", new Font("Segoe UI", Font.PLAIN, 18), new Color(230, 230, 230));
        statsRow.add(humidLabel);

        infoPanel.add(cityLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(tempLabel);
        infoPanel.add(descLabel);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(statsRow);

        // RIGHT SIDE: Icon
        iconPlaceholder = new JLabel(getWeatherIcon(""));
        iconPlaceholder.setHorizontalAlignment(SwingConstants.CENTER);

        glassCard.add(infoPanel, BorderLayout.CENTER);
        glassCard.add(iconPlaceholder, BorderLayout.EAST);

        // Container to keep card from stretching too much
        JPanel cardContainer = new JPanel(new BorderLayout());
        cardContainer.setOpaque(false);
        cardContainer.setBorder(new EmptyBorder(30, 0, 10, 0));
        cardContainer.add(glassCard, BorderLayout.CENTER);

        backgroundPanel.add(cardContainer, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    // --- CUSTOM ROUNDED COMPONENTS ---
    class RoundedTextField extends JTextField {
        private int radius = 30;
        public RoundedTextField(int columns) {
            super(columns);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            setForeground(Color.WHITE);
            setCaretColor(Color.WHITE);
            setFont(new Font("Segoe UI", Font.PLAIN, 18));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(255, 255, 255, 40));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            super.paintComponent(g);
            g2.dispose();
        }
    }

    private void styleModernButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(255, 255, 255, 60));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(255, 255, 255, 100));
                btn.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(255, 255, 255, 60));
                btn.repaint();
            }
        });
    }

    private JPanel createGlassPanel(LayoutManager layout, int radius) {
        JPanel panel = new JPanel(layout) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Glass background
                g2d.setColor(new Color(255, 255, 255, 25)); 
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
                // Subtle border
                g2d.setColor(new Color(255, 255, 255, 50));
                g2d.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
                g2d.dispose();
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    private JLabel createLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    // --- REUSE YOUR EXISTING LOGIC ---
    private void fetchAndDisplayWeather() {
        String city = searchField.getText().trim();
        if (city.isEmpty() || city.equals("Search City...")) return;

        searchButton.setText("...");
        searchButton.setEnabled(false);

        // Note: Replace 'WeatherApp' with your actual logic class name
        SwingWorker<Object, Void> worker = new SwingWorker<>() {
            @Override
            protected Object doInBackground() throws Exception {
                // Simulating your WeatherApp.getWeatherData(city)
                return null; 
            }
            // Add your done() method logic here as per previous code
        };
        worker.execute();
    }

    private ImageIcon getWeatherIcon(String description) {
        // Keeping your logic but ensuring high quality scaling
        String imgPath = "sun.png"; 
        URL imgURL = ModernWeatherGUI.class.getResource(imgPath);
        if (imgURL != null) {
            Image img = new ImageIcon(imgURL).getImage().getScaledInstance(220, 220, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }
        return null;
    }
}
