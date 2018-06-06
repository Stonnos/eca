/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.neural;

import eca.buffer.ImageCopier;
import eca.config.ConfigurationService;
import eca.config.IconType;
import eca.config.VelocityConfigService;
import eca.gui.ButtonUtils;
import eca.gui.PanelBorderUtils;
import eca.gui.choosers.SaveImageFileChooser;
import eca.gui.dialogs.JFontChooser;
import eca.gui.service.ClassifierIndexerService;
import eca.neural.functions.AbstractFunction;
import eca.neural.functions.ActivationFunctionsDictionary;
import eca.text.NumericFormatFactory;
import eca.util.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import weka.core.Attribute;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.io.File;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Neural network visualization panel.
 *
 * @author Roman Batygin
 */
public class NetworkVisualizer extends JPanel {

    private static final ConfigurationService CONFIG_SERVICE =
            ConfigurationService.getApplicationConfigService();

    /**
     * Velocity configuration
     */
    private static final VelocityConfigService VELOCITY_CONFIGURATION =
            VelocityConfigService.getVelocityConfigService();

    private static final String NEURON_OPTIONS_TABLE_VM = "vm-templates/optionsTable.vm";
    private static final String NETWORK_MODEL_VM = "vm-templates/neuralNetworkModel.vm";

    private static final double MIN_SIZE = 20;
    private static final double MAX_SIZE = 80;
    private static final double STEP_BETWEEN_LEVELS = 200.0;
    private static final double STEP_BETWEEN_NODES = 40.0;
    private static final double STEP_SIZE = 10.0;
    private static final String MODEL_TEXT_MENU = "Текстовое представление модели";
    private static final String SAVE_IMAGE_MENU_TEXT = "Сохранить изображение";
    private static final String COPY_IMAGE_MENU_TEXT = "Копировать";
    private static final String IMAGE_OPTIONS_MENU_TEXT = "Настройки";
    private static final String INCREASE_IMAGE_MENU_TEXT = "Увеличить";
    private static final String DECREASE_IMAGE_MENU_TEXT = "Уменьшить";

    private static final int SCREEN_WIDTH_MARGIN = 400;
    private static final int SCREEN_HEIGHT_MARGIN = 200;
    private static final String ARIAL = "Arial";

    private double neuronDiam = 25.0;

    private Template nodeTemplate;
    private VelocityContext nodeContext;

    private final NeuralNetwork net;
    private ArrayList<NeuronNode> nodes;
    private final JFrame frame;

    private final DecimalFormat decimalFormat = NumericFormatFactory.getInstance();

    private Color linkColor = Color.GRAY;
    private Color inLayerColor = Color.BLUE;
    private Color hidLayerColor = Color.BLACK;
    private Color outLayerColor = Color.RED;
    private Color textColor = Color.WHITE;
    private Color attrColor = Color.BLUE;
    private Color classColor = Color.RED;

    private final Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
    private Font nodeFont = new Font(ARIAL, Font.BOLD, 12);
    private Font attrFont = new Font(ARIAL, Font.BOLD, 11);

    public NetworkVisualizer(NeuralNetwork net, JFrame frame, int digits) {
        this.net = net;
        this.frame = frame;
        this.decimalFormat.setMaximumFractionDigits(digits);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                for (NeuronNode neuron : nodes) {
                    neuron.dispose();
                }
            }
        });
        this.nodes = new ArrayList<>();
        this.setDimension();
        this.createNodes();
        this.createPopupMenu();
        this.setLayout(null);
    }

    public Image getImage() {
        Image img = this.createImage(getMinimumSize().width, getMinimumSize().height);
        drawNet((Graphics2D) img.getGraphics());
        return img;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawNet((Graphics2D) g);
    }

    private void drawNet(Graphics2D g2d) {
        Enumeration<Attribute> attr = net.getData().enumerateAttributes();
        for (Neuron n : net.getMultilayerPerceptron().inLayerNeurons) {
            drawInArrow(g2d, nodes.get(n.index()), attr.nextElement().name());
        }

        Enumeration<Object> values = net.getData().classAttribute().enumerateValues();
        for (Neuron n : net.getMultilayerPerceptron().outLayerNeurons) {
            drawOutArrow(g2d, nodes.get(n.index()), values.nextElement().toString());
        }

        for (NeuronNode n : nodes) {
            for (Iterator<NeuralLink> i = n.neuron().outLinks(); i.hasNext(); ) {
                drawLink(g2d, i.next());
            }
            n.paint(g2d);
        }
    }

    private void createPopupMenu() {
        JPopupMenu popMenu = new JPopupMenu();
        JMenuItem textView = new JMenuItem(MODEL_TEXT_MENU);
        textView.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.INFO_ICON)));
        JMenuItem saveImage = new JMenuItem(SAVE_IMAGE_MENU_TEXT);
        saveImage.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.SAVE_ICON)));
        JMenuItem copyImage = new JMenuItem(COPY_IMAGE_MENU_TEXT);
        copyImage.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.COPY_ICON)));
        JMenuItem options = new JMenuItem(IMAGE_OPTIONS_MENU_TEXT);
        options.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.SETTINGS_ICON)));
        JMenuItem increase = new JMenuItem(INCREASE_IMAGE_MENU_TEXT);
        increase.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.PLUS_ICON)));
        JMenuItem decrease = new JMenuItem(DECREASE_IMAGE_MENU_TEXT);
        decrease.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.MINUS_ICON)));

        increase.addActionListener(evt -> {
            neuronDiam += STEP_SIZE;
            if (neuronDiam > MAX_SIZE) {
                neuronDiam = MAX_SIZE;
            }
            nodeFont = new Font(nodeFont.getName(), Font.BOLD, (int) (neuronDiam / 2));
            resizeNetwork();
        });
        //-----------------------------------
        decrease.addActionListener(evt -> {
            neuronDiam -= STEP_SIZE;
            if (neuronDiam < MIN_SIZE) {
                neuronDiam = MIN_SIZE;
            }
            nodeFont = new Font(nodeFont.getName(), Font.BOLD, (int) (neuronDiam / 2));
            resizeNetwork();
        });
        options.addActionListener(evt -> {
            NeuronOptions dialog = new NeuronOptions(frame);
            dialog.setVisible(true);
            if (dialog.dialogResult()) {
                neuronDiam = dialog.getNodeDiameter();
                nodeFont = dialog.getSelectedNodeFont();
                attrFont = dialog.getSelectedAttributeFont();
                linkColor = dialog.getSelectedLinkColor();
                classColor = dialog.getSelectedClassColor();
                attrColor = dialog.getSelectedAttributeColor();
                inLayerColor = dialog.getSelectedInNeuronColor();
                outLayerColor = dialog.getSelectedOutNeuronColor();
                hidLayerColor = dialog.getSelectedHidNeuronColor();
                textColor = dialog.getSelectedTextColor();
                NetworkVisualizer.this.setBackground(dialog.getSelectedBackgroundColor());
                resizeNetwork();
            }
            dialog.dispose();
        });
        //-----------------------------------
        copyImage.addActionListener(new ActionListener() {
            ImageCopier copier = new ImageCopier();

            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    copier.setImage(getImage());
                    copier.copy();
                } catch (Throwable e) {
                    JOptionPane.showMessageDialog(frame, e.getMessage(),
                            null, JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        //-----------------------------------
        textView.addActionListener(new ActionListener() {

            NetworkInfo networkModelinfo;

            @Override
            public void actionPerformed(ActionEvent evt) {
                if (networkModelinfo == null) {
                    networkModelinfo = new NetworkInfo();
                    frame.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent evt) {
                            networkModelinfo.dispose();
                        }
                    });
                }
                networkModelinfo.setVisible(true);
            }
        });
        //-----------------------------------
        saveImage.addActionListener(new ActionListener() {

            SaveImageFileChooser fileChooser;

            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (fileChooser == null) {
                        fileChooser = new SaveImageFileChooser();
                    }
                    fileChooser.setSelectedFile(new File(ClassifierIndexerService.getIndex(net)));
                    File file = fileChooser.getSelectedFile(frame);
                    if (file != null) {
                        FileUtils.write(file, getImage());

                    }
                } catch (Throwable e) {
                    JOptionPane.showMessageDialog(frame, e.getMessage(),
                            null, JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        //-----------------------------------
        popMenu.add(options);
        popMenu.addSeparator();
        popMenu.add(increase);
        popMenu.add(decrease);
        popMenu.addSeparator();
        popMenu.add(textView);
        popMenu.addSeparator();
        popMenu.add(saveImage);
        popMenu.add(copyImage);
        this.setComponentPopupMenu(popMenu);
    }

    /**
     * Neural network model text representation frame.
     */
    private class NetworkInfo extends JFrame {

        static final String INFO_TITLE = "Модель нейронной сети";
        static final String NEURON_NODES = "neuronNodes";
        static final String CONTENT_TYPE = "text/html";
        static final int PREFERRED_WIDTH = 500;
        static final int PREFERRED_HEIGHT = 350;

        NetworkInfo() {
            this.setLayout(new GridBagLayout());
            this.setTitle(INFO_TITLE);
            this.setIconImage(frame.getIconImage());
            JTextPane textInfo = new JTextPane();
            textInfo.setEditable(false);
            textInfo.setContentType(CONTENT_TYPE);
            textInfo.setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
            //----------------------------------------           
            textInfo.setText(getNeuralNetworkStructureAsHtml());
            textInfo.setCaretPosition(0);
            //----------------------------------------
            JScrollPane scrollPanel = new JScrollPane(textInfo);
            JButton okButton = ButtonUtils.createOkButton();
            //-----------------------------------
            okButton.addActionListener(evt -> setVisible(false));
            //----------------------------------------
            this.add(scrollPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            this.add(okButton, new GridBagConstraints(0, 1, 1, 1, 1, 0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE,
                    new Insets(4, 0, 4, 0), 0, 0));
            //----------------------------------------
            this.getRootPane().setDefaultButton(okButton);
            this.pack();
            this.setLocationRelativeTo(frame);
        }

        String getNeuralNetworkStructureAsHtml() {
            Template modelTemplate = VELOCITY_CONFIGURATION.getTemplate(NETWORK_MODEL_VM);
            VelocityContext modelContext = new VelocityContext();
            Map<String, Map<String, String>> inputOptionsMap = new LinkedHashMap<>();
            for (NeuronNode neuronNode : nodes) {
                inputOptionsMap.put(String.valueOf(neuronNode.neuron().index()), neuronNode.fillNeuronOptionsMap());
            }
            modelContext.put(NEURON_NODES, inputOptionsMap);
            StringWriter stringWriter = new StringWriter();
            modelTemplate.merge(modelContext, stringWriter);
            return stringWriter.toString();
        }
    }

    /**
     * Neuron info frame.
     */
    private class NeuronInfo extends JFrame {

        static final String NODE_INDEX_FORMAT = "Узел %d";
        static final String CONTENT_TYPE = "text/html";
        static final int PREFERRED_WIDTH = 350;
        static final int PREFERRED_HEIGHT = 150;

        NeuronNode neuron;

        NeuronInfo(NeuronNode neuron) {
            this.neuron = neuron;
            this.setTitle(String.format(NODE_INDEX_FORMAT, neuron.neuron().index()));
            this.setLayout(new GridBagLayout());
            this.setIconImage(frame.getIconImage());
            JTextPane textInfo = new JTextPane();
            textInfo.setContentType(CONTENT_TYPE);
            textInfo.setEditable(false);
            textInfo.setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
            //----------------------------------------           
            textInfo.setText(neuron.getNeuronInfoAsHtml());
            textInfo.setCaretPosition(0);
            //----------------------------------------
            JScrollPane scrollPanel = new JScrollPane(textInfo);
            JButton okButton = ButtonUtils.createOkButton();
            //-----------------------------------
            okButton.addActionListener(evt -> setVisible(false));
            //----------------------------------------
            this.add(scrollPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            this.add(okButton, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE,
                    new Insets(4, 0, 4, 0), 0, 0));
            //----------------------------------------
            this.getRootPane().setDefaultButton(okButton);
            this.setResizable(false);
            this.pack();
            this.setLocationRelativeTo(frame);
        }
    }

    /**
     * Neuron node model.
     */
    private class NeuronNode {

        static final String TOOL_TIP_TEXT =
                "<html><body>Щелкните левой кнопкой мыши<br>для просмотра информации</body></html>";
        static final String ACTIVATION_FUNCTION_TEXT = "Активационная функция:";
        static final String ACTIVATION_FUNCTION_FORMULA_TEXT = "Формула активационной функции:";
        static final String LAYER_TEXT = "Слой:";
        static final String NODE_INDEX_TEXT = "Номер узла:";
        static final String IN_LAYER_TEXT = "Входной";
        static final String OUT_LAYER_TEXT = "Выходной";
        static final String HIDDEN_LAYER_TEXT = "Скрытый";
        static final String NEURAL_LINK_FORMAT = "Вес связи (%d,%d):";
        static final String NODE_PARAMS = "params";

        final Neuron neuron;
        Ellipse2D.Double ellipse;
        NeuronInfo info;
        JLabel infoLabel = new JLabel();

        NeuronNode(Neuron neuron, Ellipse2D.Double ellipse) {
            this.neuron = neuron;
            this.ellipse = ellipse;
            NetworkVisualizer.this.add(infoLabel);
            infoLabel.setCursor(handCursor);
            infoLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent evt) {
                    createInfo();
                }
            });
            infoLabel.setToolTipText(TOOL_TIP_TEXT);
        }

        String getNeuronInfoAsHtml() {
            if (nodeTemplate == null) {
                nodeTemplate = VELOCITY_CONFIGURATION.getTemplate(NEURON_OPTIONS_TABLE_VM);
            }
            if (nodeContext == null) {
                nodeContext = new VelocityContext();
            }
            nodeContext.put(NODE_PARAMS, fillNeuronOptionsMap());
            StringWriter stringWriter = new StringWriter();
            nodeTemplate.merge(nodeContext, stringWriter);
            return stringWriter.toString();
        }

        Map<String, String> fillNeuronOptionsMap() {
            Map<String, String> paramsMap = new LinkedHashMap<>();
            paramsMap.put(NODE_INDEX_TEXT, String.valueOf(neuron.index()));
            switch (neuron.getType()) {
                case Neuron.IN_LAYER:
                    paramsMap.put(LAYER_TEXT, IN_LAYER_TEXT);
                    break;
                case Neuron.OUT_LAYER:
                    paramsMap.put(LAYER_TEXT, OUT_LAYER_TEXT);
                    break;
                case Neuron.HIDDEN_LAYER:
                    paramsMap.put(LAYER_TEXT, HIDDEN_LAYER_TEXT);
                    break;
            }
            if (neuron.getType() != Neuron.IN_LAYER) {
                paramsMap.put(ACTIVATION_FUNCTION_TEXT,
                        neuron.getActivationFunction().getActivationFunctionType().getDescription());
                if (neuron.getActivationFunction() instanceof AbstractFunction) {
                    AbstractFunction abstractFunction = (AbstractFunction) neuron.getActivationFunction();
                    if (abstractFunction.getCoefficient() != ActivationFunctionsDictionary.DEFAULT_COEFFICIENT) {
                        paramsMap.put(ACTIVATION_FUNCTION_FORMULA_TEXT,
                                String.format(abstractFunction.getActivationFunctionType().getFormulaFormat(),
                                        decimalFormat.format(abstractFunction.getCoefficient())));
                    } else {
                        paramsMap.put(ACTIVATION_FUNCTION_FORMULA_TEXT,
                                abstractFunction.getActivationFunctionType().getFormula());
                    }
                }
            }
            for (Iterator<NeuralLink> link = neuron.outLinks(); link.hasNext(); ) {
                NeuralLink edge = link.next();
                paramsMap.put(String.format(NEURAL_LINK_FORMAT, edge.source().index(), edge.target().index()),
                        decimalFormat.format(edge.getWeight()));
            }
            return paramsMap;
        }

        void dispose() {
            if (info != null) {
                info.dispose();
            }
        }

        void createInfo() {
            if (info == null) {
                info = new NeuronInfo(this);
            }
            info.setVisible(true);
        }

        Neuron neuron() {
            return neuron;
        }

        Ellipse2D.Double getEllipse() {
            return ellipse;
        }

        void setEllipse(Ellipse2D.Double ellipse) {
            this.ellipse = ellipse;
        }

        void setRect(double x, double y, double w, double h) {
            ellipse.setFrame(x, y, w, h);
        }

        double width() {
            return ellipse.width;
        }

        double height() {
            return ellipse.height;
        }

        double x1() {
            return ellipse.x;
        }

        double x2() {
            return ellipse.x + width();
        }

        double y1() {
            return ellipse.y;
        }

        double y2() {
            return ellipse.y + height();
        }

        double centerX() {
            return (x1() + x2()) / 2.0;
        }

        double centerY() {
            return (y1() + y2()) / 2.0;
        }

        boolean contains(double x, double y) {
            return ellipse.contains(x, y);
        }

        void paintString(Graphics2D g) {
            FontMetrics fm = g.getFontMetrics(nodeFont);
            g.setPaint(textColor);
            g.setFont(nodeFont);
            String text = String.valueOf(neuron.index());
            infoLabel.setSize((int) neuronDiam, (int) neuronDiam);
            infoLabel.setLocation((int) x1(), (int) y1());
            g.drawString(text, (float) x1()
                            + ((float) width() - fm.stringWidth(text)) / 2.0f,
                    (float) y1() + fm.getAscent()
                            + ((float) width() - (fm.getAscent() + fm.getDescent())) / 2.0f);
        }

        void paint(Graphics2D g) {
            switch (neuron.getType()) {
                case Neuron.IN_LAYER:
                    g.setColor(inLayerColor);
                    break;
                case Neuron.OUT_LAYER:
                    g.setColor(outLayerColor);
                    break;
                case Neuron.HIDDEN_LAYER:
                    g.setColor(hidLayerColor);
                    break;
                default:
                    g.setColor(Color.BLACK);
            }
            g.fill(ellipse);
            paintString(g);
        }

    } //End of class NeuronNode

    private void resizeNetwork() {
        this.setDimension();
        this.computeCoordinates();
        this.getRootPane().repaint();
    }

    private double screenWidth() {
        return net.getMultilayerPerceptron().layersNum() * (STEP_BETWEEN_LEVELS + 1 + neuronDiam) + SCREEN_WIDTH_MARGIN;
    }

    private double screenHeight() {
        int max = Integer.max(Integer.max(net.getMultilayerPerceptron().inLayerNeurons.length,
                net.getMultilayerPerceptron().outLayerNeurons.length),
                maxHiddenLayerSize());
        return max * (STEP_BETWEEN_NODES + 1 + neuronDiam) + SCREEN_HEIGHT_MARGIN;
    }

    private int maxHiddenLayerSize() {
        int max = 0;
        for (Neuron[] layer : net.getMultilayerPerceptron().hiddenLayerNeurons) {
            max = Integer.max(max, layer.length);
        }
        return max;
    }

    private double startY(int size) {
        double length = size * neuronDiam + (size - 1) * STEP_BETWEEN_NODES;
        return (this.getMaximumSize().height - length) / 2.0;
    }

    private double startX() {
        double length = net.getMultilayerPerceptron().layersNum() * neuronDiam
                + (net.getMultilayerPerceptron().layersNum() - 1) * STEP_BETWEEN_LEVELS;
        return (this.getMaximumSize().width - length) / 2.0;
    }

    private void computeCoordinates() {
        double w = startX(), h = startY(net.getMultilayerPerceptron().inLayerNeurons.length);
        for (Neuron n : net.getMultilayerPerceptron().inLayerNeurons) {
            nodes.get(n.index()).setRect(w, h,
                    neuronDiam, neuronDiam);
            h += neuronDiam + STEP_BETWEEN_NODES;
        }

        for (Neuron[] layer : net.getMultilayerPerceptron().hiddenLayerNeurons) {
            h = startY(layer.length);
            w += neuronDiam + STEP_BETWEEN_LEVELS;
            for (Neuron n : layer) {
                nodes.get(n.index()).setRect(w, h,
                        neuronDiam, neuronDiam);
                h += neuronDiam + STEP_BETWEEN_NODES;
            }
        }

        w += neuronDiam + STEP_BETWEEN_LEVELS;
        h = startY(net.getMultilayerPerceptron().outLayerNeurons.length);
        for (Neuron n : net.getMultilayerPerceptron().outLayerNeurons) {
            nodes.get(n.index()).setRect(w, h,
                    neuronDiam, neuronDiam);
            h += neuronDiam + STEP_BETWEEN_NODES;
        }
    }

    private void createNodes() {
        double w = startX(), h = startY(net.getMultilayerPerceptron().inLayerNeurons.length);
        for (Neuron n : net.getMultilayerPerceptron().inLayerNeurons) {
            nodes.add(new NeuronNode(n, new Ellipse2D.Double(w, h,
                    neuronDiam, neuronDiam)));
            h += neuronDiam + STEP_BETWEEN_NODES;
        }

        for (Neuron[] layer : net.getMultilayerPerceptron().hiddenLayerNeurons) {
            h = startY(layer.length);
            w += neuronDiam + STEP_BETWEEN_LEVELS;
            for (Neuron n : layer) {
                nodes.add(new NeuronNode(n, new Ellipse2D.Double(w, h,
                        neuronDiam, neuronDiam)));
                h += neuronDiam + STEP_BETWEEN_NODES;
            }
        }

        w += neuronDiam + STEP_BETWEEN_LEVELS;
        h = startY(net.getMultilayerPerceptron().outLayerNeurons.length);
        for (Neuron n : net.getMultilayerPerceptron().outLayerNeurons) {
            nodes.add(new NeuronNode(n, new Ellipse2D.Double(w, h,
                    neuronDiam, neuronDiam)));
            h += neuronDiam + STEP_BETWEEN_NODES;
        }
    }

    private void setDimension() {
        Dimension dim = new Dimension((int) screenWidth(), (int) screenHeight());
        this.setPreferredSize(dim);
        this.setMinimumSize(dim);
        this.setMaximumSize(dim);
    }

    private void drawLink(Graphics2D g, NeuralLink link) {
        NeuronNode u = nodes.get(link.source().index());
        NeuronNode v = nodes.get(link.target().index());
        g.setColor(linkColor);
        g.draw(new Line2D.Double(u.x2(), u.centerY(), v.x1(), v.centerY()));
    }

    private void drawInArrow(Graphics2D g, NeuronNode u, String name) {
        g.setColor(Color.BLACK);
        Line2D.Double arrow = new Line2D.Double(u.x1() - STEP_BETWEEN_LEVELS / 2,
                u.centerY(), u.x1(), u.centerY());
        g.draw(arrow);
        Path2D.Double path = new Path2D.Double();
        path.moveTo(u.x1(), u.centerY());
        path.lineTo(u.x1() - STEP_BETWEEN_LEVELS / 10,
                u.centerY() - neuronDiam / 5);
        path.lineTo(u.x1() - STEP_BETWEEN_LEVELS / 10,
                u.centerY() + neuronDiam / 5);
        path.closePath();
        g.fill(path);
        g.setColor(attrColor);
        g.setFont(attrFont);
        g.drawString(name, (float) ((arrow.getX1() + arrow.getX2()) / 3.0), (float) u.y1());
    }

    private void drawOutArrow(Graphics2D g, NeuronNode u, String value) {
        g.setColor(Color.BLACK);
        Line2D.Double arrow = new Line2D.Double(u.x2(),
                u.centerY(), u.x2() + STEP_BETWEEN_LEVELS / 2, u.centerY());
        g.draw(arrow);
        Path2D.Double path = new Path2D.Double();
        path.moveTo(arrow.getX2(), arrow.getY2());
        path.lineTo(arrow.getX2() - STEP_BETWEEN_LEVELS / 10,
                arrow.getY2() - neuronDiam / 5);
        path.lineTo(arrow.getX2() - STEP_BETWEEN_LEVELS / 10,
                arrow.getY2() + neuronDiam / 5);
        path.closePath();
        g.fill(path);
        g.setColor(classColor);
        g.setFont(attrFont);
        g.drawString(value, (float) ((arrow.getX1() + arrow.getX2()) / 2.0), (float) (arrow.getY1() - 5.0));
    }

    /**
     * Neural network image options dialog.
     */
    private class NeuronOptions extends JDialog {

        static final String TITLE_TEXT = "Настройки";
        static final String OPTIONS_TITLE = "Параметры сети";
        static final String NEURON_DIAM_TEXT = "Диаметр нейрона:";
        static final String SELECT_BUTTON_TEXT = "Выбрать...";
        static final String SELECT_IN_LAYER_COLOR_TEXT = "Выбор цвета нейрона вх. слоя";
        static final String SELECT_OUT_LAYER_COLOR_TEXT = "Выбор цвета нейрона вых. слоя";
        static final String SELECT_LINK_COLOR_TEXT = "Выбор цвета связи";
        static final String SELECT_HIDDEN_LAYER_COLOR_TEXT = "Выбор цвета нейронов скрытого слоя";
        static final String SELECT_ATTR_COLOR_TEXT = "Выбор цвета атрибута";
        static final String SELECT_CLASS_COLOR_TEXT = "Выбор цвета класса";
        static final String SELECT_TEXT_COLOR = "Выбор цвета текста";
        static final String SELECT_BACKGROUND_TEXT = "Выбор цвета фона";
        static final String NODE_FONT_TEXT = "Шрифт узла:";
        static final String ATTR_FONT_TEXT = "Шрифт атрибута:";
        static final String ATTR_COLOR_TEXT = "Цвет атрибута:";
        static final String TEXT_COLOR_TEXT = "Цвет текста:";
        static final String LINK_COLOR_TEXT = "Цвет связи:";
        static final String IN_LAYER_COLOR_TEXT = "Цвет нейрона вх. слоя:";
        static final String OUT_LAYER_COLOR_TEXT = "Цвет нейрона вых. слоя:";
        static final String HIDDEN_LAYER_COLOR_TEXT = "Цвет нейрона скрытого слоя:";
        static final String CLASS_COLOR_TEXT = "Цвет класса:";
        static final String BACKGROUND_TEXT = "Цвет фона:";


        boolean dialogResult;
        Font selectedNodeFont = nodeFont;
        Font selectedAttrFont = attrFont;
        JSpinner diamSpinner = new JSpinner();
        Color selectedInLayerColor = inLayerColor;
        Color selectedOutLayerColor = outLayerColor;
        Color selectedHiddenLayerColor = hidLayerColor;
        Color selectedLinkColor = linkColor;
        Color selectedAttrColor = attrColor;
        Color selectedClassColor = classColor;
        Color selectedTextColor = textColor;
        Color selectedBackgroundColor = NetworkVisualizer.this.getBackground();

        NeuronOptions(Window parent) {
            super(parent, TITLE_TEXT);
            this.setLayout(new GridBagLayout());
            this.setModal(true);
            this.setResizable(false);
            this.init();
            this.pack();
            this.setLocationRelativeTo(parent);
        }

        void init() {
            JPanel panel = new JPanel(new GridLayout(11, 2, 10, 10));
            panel.setBorder(PanelBorderUtils.createTitledBorder(OPTIONS_TITLE));
            //-------------------------------------------
            diamSpinner.setModel(new SpinnerNumberModel(neuronDiam, MIN_SIZE, MAX_SIZE, 1));
            panel.add(new JLabel(NEURON_DIAM_TEXT));
            panel.add(diamSpinner);
            //------------------------------------
            JButton nodeButton = new JButton(SELECT_BUTTON_TEXT);
            nodeButton.addActionListener(evt -> {
                JFontChooser nodeFontChooser = new JFontChooser(NeuronOptions.this, selectedNodeFont);
                nodeFontChooser.setVisible(true);
                if (nodeFontChooser.dialogResult()) {
                    selectedNodeFont = nodeFontChooser.getSelectedFont();
                }
            });
            JButton attrButton = new JButton(SELECT_BUTTON_TEXT);
            //----------------------------------------------
            attrButton.addActionListener(evt -> {
                JFontChooser ruleFontChooser = new JFontChooser(NeuronOptions.this, selectedAttrFont);
                ruleFontChooser.setVisible(true);
                if (ruleFontChooser.dialogResult()) {
                    selectedAttrFont = ruleFontChooser.getSelectedFont();
                }
            });
            //--------------------------------------------------
            JButton inColorButton = new JButton(SELECT_BUTTON_TEXT);
            inColorButton.addActionListener(evt -> {
                Color newInLayerColor = JColorChooser.showDialog(NeuronOptions.this,
                        SELECT_IN_LAYER_COLOR_TEXT, selectedInLayerColor);
                if (newInLayerColor != null) {
                    selectedInLayerColor = newInLayerColor;
                }
            });
            //--------------------------------------------------
            JButton outColorButton = new JButton(SELECT_BUTTON_TEXT);
            outColorButton.addActionListener(evt -> {
                Color newOutLayerColor = JColorChooser.showDialog(NeuronOptions.this,
                        SELECT_OUT_LAYER_COLOR_TEXT, selectedOutLayerColor);
                if (newOutLayerColor != null) {
                    selectedOutLayerColor = newOutLayerColor;
                }
            });
            //--------------------------------------------------
            JButton linkColorButton = new JButton(SELECT_BUTTON_TEXT);
            linkColorButton.addActionListener(evt -> {
                Color newLinkColor = JColorChooser.showDialog(NeuronOptions.this,
                        SELECT_LINK_COLOR_TEXT, selectedLinkColor);
                if (newLinkColor != null) {
                    selectedLinkColor = newLinkColor;
                }
            });
            //--------------------------------------------------
            JButton hidColorButton = new JButton(SELECT_BUTTON_TEXT);
            hidColorButton.addActionListener(evt -> {
                Color newHiddenLayerColor = JColorChooser.showDialog(NeuronOptions.this,
                        SELECT_HIDDEN_LAYER_COLOR_TEXT, selectedHiddenLayerColor);
                if (newHiddenLayerColor != null) {
                    selectedHiddenLayerColor = newHiddenLayerColor;
                }
            });
            //--------------------------------------------------
            JButton attrColorButton = new JButton(SELECT_BUTTON_TEXT);
            attrColorButton.addActionListener(evt -> {
                Color newAttrColor = JColorChooser.showDialog(NeuronOptions.this, SELECT_ATTR_COLOR_TEXT,
                        selectedAttrColor);
                if (newAttrColor != null) {
                    selectedAttrColor = newAttrColor;
                }
            });
            //--------------------------------------------------
            JButton classColorButton = new JButton(SELECT_BUTTON_TEXT);
            classColorButton.addActionListener(evt -> {
                Color newClassColor = JColorChooser.showDialog(NeuronOptions.this, SELECT_CLASS_COLOR_TEXT,
                        selectedClassColor);
                if (newClassColor != null) {
                    selectedClassColor = newClassColor;
                }
            });
            //--------------------------------------------------
            JButton textColorButton = new JButton(SELECT_BUTTON_TEXT);
            textColorButton.addActionListener(evt -> {
                Color newTextColor = JColorChooser.showDialog(NeuronOptions.this, SELECT_TEXT_COLOR,
                        selectedTextColor);
                if (newTextColor != null) {
                    selectedTextColor = newTextColor;
                }
            });
            //--------------------------------------------------
            JButton backgroundColorButton = new JButton(SELECT_BUTTON_TEXT);
            backgroundColorButton.addActionListener(evt -> {
                Color newBackgroundColor = JColorChooser.showDialog(NeuronOptions.this, SELECT_BACKGROUND_TEXT,
                        selectedBackgroundColor);
                if (newBackgroundColor != null) {
                    selectedBackgroundColor = newBackgroundColor;
                }
            });
            //------------------------------------
            panel.add(new JLabel(NODE_FONT_TEXT));
            panel.add(nodeButton);
            panel.add(new JLabel(ATTR_FONT_TEXT));
            panel.add(attrButton);
            panel.add(new JLabel(ATTR_COLOR_TEXT));
            panel.add(attrColorButton);
            panel.add(new JLabel(TEXT_COLOR_TEXT));
            panel.add(textColorButton);
            panel.add(new JLabel(LINK_COLOR_TEXT));
            panel.add(linkColorButton);
            panel.add(new JLabel(IN_LAYER_COLOR_TEXT));
            panel.add(inColorButton);
            panel.add(new JLabel(OUT_LAYER_COLOR_TEXT));
            panel.add(outColorButton);
            panel.add(new JLabel(HIDDEN_LAYER_COLOR_TEXT));
            panel.add(hidColorButton);
            panel.add(new JLabel(CLASS_COLOR_TEXT));
            panel.add(classColorButton);
            panel.add(new JLabel(BACKGROUND_TEXT));
            panel.add(backgroundColorButton);
            //-------------------------------------------
            JButton okButton = ButtonUtils.createOkButton();
            JButton cancelButton = ButtonUtils.createCancelButton();

            okButton.addActionListener(evt -> {
                dialogResult = true;
                setVisible(false);
            });
            cancelButton.addActionListener(evt -> {
                dialogResult = false;
                setVisible(false);
            });
            //---------------------------------------------------------
            this.add(panel, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 7, 15, 7), 0, 0));
            this.add(okButton, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                    GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(0, 0, 8, 3), 0, 0));
            this.add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                    GridBagConstraints.WEST, GridBagConstraints.WEST, new Insets(0, 3, 8, 0), 0, 0));
            //-----------------------------------------------------------
            this.getRootPane().setDefaultButton(okButton);
        }

        double getNodeDiameter() {
            return ((SpinnerNumberModel) diamSpinner.getModel()).getNumber().doubleValue();
        }

        Font getSelectedNodeFont() {
            return selectedNodeFont;
        }

        Font getSelectedAttributeFont() {
            return selectedAttrFont;
        }

        Color getSelectedInNeuronColor() {
            return selectedInLayerColor;
        }

        Color getSelectedTextColor() {
            return selectedTextColor;
        }

        Color getSelectedLinkColor() {
            return selectedLinkColor;
        }

        Color getSelectedOutNeuronColor() {
            return selectedOutLayerColor;
        }

        Color getSelectedHidNeuronColor() {
            return selectedHiddenLayerColor;
        }

        Color getSelectedClassColor() {
            return selectedClassColor;
        }

        Color getSelectedAttributeColor() {
            return selectedAttrColor;
        }

        Color getSelectedBackgroundColor() {
            return selectedBackgroundColor;
        }

        boolean dialogResult() {
            return dialogResult;
        }
    }

}
