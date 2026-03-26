import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;

public class ModernWeatherGUI {

    private JFrame frame;
    private JTextField searchField;
    private JLabel cityLabel, tempLabel, descLabel, humidLabel, iconPlaceholder;
    private JButton searchButton;
    private Image bgImage; // Background image variable

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> new ModernWeatherGUI().createAndShowGUI());
    }

    private void createAndShowGUI() {
        frame = new JFrame("Pro Weather Application 🌤️");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 550); // Using standard size
        frame.setLocationRelativeTo(null);

        // --- LOAD BACKGROUND IMAGE ---
        // Ensure you have "background.jpg" in your project folder/resources
        URL bgURL = ModernWeatherGUI.class.getResource("bg2.png");
        if (bgURL != null) {
            bgImage = new ImageIcon(bgURL).getImage();
        } else {
            System.err.println("Background image not found! Fallback to gradient.");
        }

        // --- 1. THEME & BACKGROUND ---
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                if (bgImage != null) {
                    // Agar image mil jaye, toh usey pure panel par fill karein (scale kar ke)
                    g2d.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    // Fallback: Agar image na mile toh purana gradient draw kar de
                    GradientPaint gp = new GradientPaint(0, 0, new Color(20, 30, 48), 0, getHeight(), new Color(36, 59, 85));
                    g2d.setPaint(gp);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.setBorder(new EmptyBorder(30, 20, 30, 20));
        frame.setContentPane(backgroundPanel);

        // --- 2. INPUT AREA ---
        JPanel searchPanel = createGlassPanel(new BorderLayout(10, 0), 20);
        searchPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setForeground(Color.WHITE);
        searchField.setCaretColor(Color.WHITE);
        searchField.setOpaque(false);
        searchField.setBorder(null);

        searchButton = new JButton("Search");
        styleButton(searchButton);
        searchButton.addActionListener(e -> fetchAndDisplayWeather());

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        backgroundPanel.add(searchPanel, BorderLayout.NORTH);

        // --- 3. RESULTS AREA (Glassmorphism Card) ---
        JPanel mainCard = createGlassPanel(new BorderLayout(20, 0), 30);
        mainCard.setBorder(new EmptyBorder(40, 40, 40, 40));

        // --- LEFT PANEL (Text Labels) ---
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        cityLabel = createLabel("Enter City...", new Font("Segoe UI", Font.BOLD, 36), SwingConstants.LEFT);
        leftPanel.add(cityLabel);
        leftPanel.add(Box.createVerticalStrut(20));

        tempLabel = createLabel("-- °C", new Font("Segoe UI", Font.BOLD, 72), SwingConstants.LEFT);
        leftPanel.add(tempLabel);
        leftPanel.add(Box.createVerticalStrut(10));

        descLabel = createLabel("Waiting for input", new Font("Segoe UI", Font.ITALIC, 24), SwingConstants.LEFT);
        descLabel.setForeground(new Color(220, 220, 220));
        leftPanel.add(descLabel);
        leftPanel.add(Box.createVerticalStrut(30));

        humidLabel = createLabel("Humidity: -- %", new Font("Segoe UI", Font.PLAIN, 20), SwingConstants.LEFT);
        leftPanel.add(humidLabel);

        // --- RIGHT PANEL (Weather Icon) ---
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);

        iconPlaceholder = new JLabel(getWeatherIcon(""));
        iconPlaceholder.setHorizontalAlignment(SwingConstants.CENTER);
        iconPlaceholder.setVerticalAlignment(SwingConstants.CENTER);
        rightPanel.add(iconPlaceholder, BorderLayout.CENTER);

        mainCard.add(leftPanel, BorderLayout.CENTER);
        mainCard.add(rightPanel, BorderLayout.EAST);

        // Wrapper to position the card
        JPanel cardWrapper = new JPanel(new BorderLayout());
        cardWrapper.setOpaque(false);
        cardWrapper.setBorder(new EmptyBorder(40, 0, 0, 0));

        cardWrapper.add(mainCard, BorderLayout.CENTER);

        backgroundPanel.add(cardWrapper, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    // --- AAPKA ORIGINAL FETCH METHOD ---
    private void fetchAndDisplayWeather() {
        String city = searchField.getText().trim();
        if (city.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter a city name!", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        searchButton.setText("Wait...");
        searchButton.setEnabled(false);

        SwingWorker<WeatherApp.WeatherAppResponse, Void> worker = new SwingWorker<>() {
            @Override
            protected WeatherApp.WeatherAppResponse doInBackground() throws Exception {
                return WeatherApp.getWeatherData(city);
            }

            @Override
            protected void done() {
                try {
                    WeatherApp.WeatherAppResponse data = get();

                    String descRaw = data.weather.get(0).description;
                    String formattedDesc = descRaw.substring(0, 1).toUpperCase() + descRaw.substring(1);

                    cityLabel.setText(data.name);
                    tempLabel.setText((int)Math.round(data.main.temp) + "°C");
                    humidLabel.setText("💧 Humidity: " + data.main.humidity + "%");
                    descLabel.setText(formattedDesc);

                    iconPlaceholder.setIcon(getWeatherIcon(descRaw));

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "City not found!", "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    searchButton.setText("Search");
                    searchButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    // --- AAPKA PICTURE LOAD KARNE WALA METHOD ---
    private ImageIcon getWeatherIcon(String description) {
        String imgPath = "sunn.png";
        String desc = description.toLowerCase();

        if (desc.contains("clear") || desc.contains("dust") || desc.contains("haze")) {
            imgPath = "sun.png";
        } else if (desc.contains("cloud")  || desc.contains("mist")) {
            imgPath = "clouds.png";
        } else if (desc.contains("rain") || desc.contains("drizzle")) {
            imgPath = "rain.png";
        } else if (desc.contains("snow")) {
            imgPath = "/icons/snow.png";
        }

        URL imgURL = ModernWeatherGUI.class.getResource(imgPath);
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            Image img = icon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } else {
            System.err.println("Could not find icon file: " + imgPath);
            return new ImageIcon();
        }
    }

    // --- UI HELPERS ---
    private JPanel createGlassPanel(LayoutManager layout, int radius) {
        JPanel panel = new JPanel(layout) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 30)); // 30 is the transparency (alpha)
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
                g2d.setColor(new Color(255, 255, 255, 50));
                g2d.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
                g2d.dispose();
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    private JLabel createLabel(String text, Font font, int alignment) {
        JLabel label = new JLabel(text, alignment);
        label.setFont(font);
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(0, 122, 255, 180));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
    }
}