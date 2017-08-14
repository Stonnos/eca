/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.neural;

import eca.core.ClassifierIndexer;
import eca.core.converters.ImageSaver;
import eca.gui.ButtonUtils;
import eca.gui.PanelBorderUtils;
import eca.gui.choosers.SaveImageFileChooser;
import eca.gui.dialogs.JFontChooser;
import eca.gui.text.NumericFormat;
import eca.io.buffer.ImageCopier;
import eca.neural.functions.AbstractFunction;
import weka.core.Attribute;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * @author Рома
 */
public class NetworkVisualizer extends JPanel {

    private static final double MIN_SIZE = 20;
    private static final double MAX_SIZE = 80;
    private static final double STEP_BETWEEN_LEVELS = 200.0;
    private static final double STEP_BETWEEN_NODES = 40.0;
    private static final double STEP_SIZE = 10.0;

    private double neuronDiam = 25.0;

    private final NeuralNetwork net;
    private ArrayList<NeuronNode> nodes;
    private final JFrame frame;

    private final DecimalFormat fmt = NumericFormat.getInstance();

    private Color linkColor = Color.GRAY;
    private Color inLayerColor = Color.BLUE;
    private Color hidLayerColor = Color.BLACK;
    private Color outLayerColor = Color.RED;
    private Color textColor = Color.WHITE;
    private Color attrColor = Color.BLUE;
    private Color classColor = Color.RED;

    private final Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
    private Font nodeFont = new Font("Arial", Font.BOLD, 12);
    private Font attrFont = new Font("Arial", Font.BOLD, 11);

    public NetworkVisualizer(NeuralNetwork net, JFrame frame, int digits) {
        this.net = net;
        this.frame = frame;
        fmt.setMaximumFractionDigits(digits);
        //----------------------------------------------
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                for (NeuronNode neuron : nodes) {
                    neuron.dispose();
                }
            }
        });
        //----------------------------------------------
        nodes = new ArrayList<>();
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

    public void setNeuronDiam(double diam) {
        if (diam < MIN_SIZE || diam > MAX_SIZE) {
            throw new IllegalArgumentException("Wrong value of diametr!");
        }
        this.neuronDiam = diam;
    }

    public double getNeuronDiam() {
        return neuronDiam;
    }

    public void setInLayerColor(Color color) {
        this.inLayerColor = color;
    }

    public void setOutLayerColor(Color color) {
        this.outLayerColor = color;
    }

    public void setHidLayerColor(Color color) {
        this.hidLayerColor = color;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public void setClassColor(Color classColor) {
        this.classColor = classColor;
    }

    public void setLinkColor(Color linkColor) {
        this.linkColor = linkColor;
    }

    public void setAttributeColor(Color color) {
        this.attrColor = color;
    }

    public Color getInLayercolor() {
        return inLayerColor;
    }

    public Color linkColor() {
        return linkColor;
    }

    public Color getAttributeColor() {
        return attrColor;
    }

    public Color getOutLayerColor() {
        return outLayerColor;
    }

    public Color hidLayerColor() {
        return hidLayerColor;
    }

    public Color classColor() {
        return classColor;
    }

    public Color textColor() {
        return textColor;
    }

    private void drawNet(Graphics2D g2d) {
        Enumeration<Attribute> attr = net.getData().enumerateAttributes();
        for (Neuron n : net.network().inLayerNeurons) {
            drawInArrow(g2d, nodes.get(n.index()), attr.nextElement().name());
        }
        //-------------------------------------
        Enumeration<Object> values = net.getData().classAttribute().enumerateValues();
        for (Neuron n : net.network().outLayerNeurons) {
            drawOutArrow(g2d, nodes.get(n.index()), values.nextElement().toString());
        }
        //-------------------------------------
        for (NeuronNode n : nodes) {
            for (Iterator<NeuralLink> i = n.neuron().outLinks(); i.hasNext(); ) {
                drawLink(g2d, i.next());
            }
            n.paint(g2d);
        }
    }

    public StringBuilder getInfo() {
        StringBuilder textInfo = new StringBuilder();
        for (NeuronNode n : nodes) {
            textInfo.append(n.getInfo()).append("\n");
        }
        return textInfo;
    }

    private void createPopupMenu() {
        JPopupMenu popMenu = new JPopupMenu();
        JMenuItem textView = new JMenuItem("Текстовое представление модели");
        JMenuItem saveImage = new JMenuItem("Сохранить изображение");
        JMenuItem copyImage = new JMenuItem("Копировать");
        JMenuItem options = new JMenuItem("Настройки");
        JMenuItem increase = new JMenuItem("Увеличить");
        JMenuItem decrease = new JMenuItem("Уменьшить");
        //-----------------------------------
        //-----------------------------------
        increase.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                neuronDiam += STEP_SIZE;
                if (neuronDiam > MAX_SIZE) {
                    neuronDiam = MAX_SIZE;
                }
                nodeFont = new Font(nodeFont.getName(), Font.BOLD, (int) (neuronDiam / 2));
                resizeNetwork();
            }
        });
        //-----------------------------------
        decrease.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                neuronDiam -= STEP_SIZE;
                if (neuronDiam < MIN_SIZE) {
                    neuronDiam = MIN_SIZE;
                }
                nodeFont = new Font(nodeFont.getName(), Font.BOLD, (int) (neuronDiam / 2));
                resizeNetwork();
            }
        });
        options.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                NeuronOptions dialog = new NeuronOptions(frame);
                dialog.setVisible(true);
                if (dialog.dialogResult()) {
                    neuronDiam = dialog.getNodeDiametr();
                    nodeFont = dialog.getNodeFont();
                    attrFont = dialog.getAttributeFont();
                    linkColor = dialog.getLinkColor();
                    classColor = dialog.getClassColor();
                    attrColor = dialog.getAttributeColor();
                    inLayerColor = dialog.getInNeuronColor();
                    outLayerColor = dialog.getOutNeuronColor();
                    hidLayerColor = dialog.getHidNeuronColor();
                    textColor = dialog.getTextColor();
                    NetworkVisualizer.this.setBackground(dialog.getBackgroundColor());
                    resizeNetwork();
                }
                dialog.dispose();
            }
        });
        //-----------------------------------
        copyImage.addActionListener(new ActionListener() {
            ImageCopier copier = new ImageCopier();

            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    copier.setImage(getImage());
                    copier.copy();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame, e.getMessage(),
                            null, JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        //-----------------------------------
        textView.addActionListener(new ActionListener() {

            NetworkInfo info;

            @Override
            public void actionPerformed(ActionEvent evt) {
                if (info == null) {
                    info = new NetworkInfo();
                    frame.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent evt) {
                            info.dispose();
                        }
                    });
                }
                info.setVisible(true);
            }
        });
        //-----------------------------------
        saveImage.addActionListener(new ActionListener() {

            SaveImageFileChooser fileChooser;
            ClassifierIndexer indexer = new ClassifierIndexer();

            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (fileChooser == null) {
                        fileChooser = new SaveImageFileChooser();
                    }
                    fileChooser.setSelectedFile(new File(indexer.getIndex(net)));
                    File file = fileChooser.saveFile(frame);
                    if (file != null) {
                        ImageSaver.saveImage(file, getImage());

                    }
                } catch (IOException e) {
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
     *
     */
    private class NetworkInfo extends JFrame {

        NetworkInfo() {
            this.setLayout(new GridBagLayout());
            this.setTitle("Модель нейронной сети");
            JTextArea textInfo = new JTextArea(20, 50);
            textInfo.setWrapStyleWord(true);
            textInfo.setLineWrap(true);
            textInfo.setEditable(false);
            textInfo.setFont(new Font("Arial", Font.BOLD, 12));
            //----------------------------------------           
            textInfo.setText(getInfo().toString());
            textInfo.setCaretPosition(0);
            //----------------------------------------
            JScrollPane scrollPanel = new JScrollPane(textInfo);
            JButton okButton = ButtonUtils.createOkButton();
            //-----------------------------------
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    setVisible(false);
                }
            });
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
    }

    /**
     *
     */
    private class NeuronInfo extends JFrame {

        NeuronNode neuron;

        NeuronInfo(NeuronNode neuron) {
            this.neuron = neuron;
            this.setTitle("Узел " + neuron.neuron().index());
            this.setLayout(new GridBagLayout());
            JTextArea textInfo = new JTextArea(8, 26);
            textInfo.setWrapStyleWord(true);
            textInfo.setLineWrap(true);
            textInfo.setEditable(false);
            textInfo.setFont(new Font("Arial", Font.BOLD, 10));
            //----------------------------------------           
            textInfo.setText(neuron.getInfo().toString());
            textInfo.setCaretPosition(0);
            //----------------------------------------
            JScrollPane scrollPanel = new JScrollPane(textInfo);
            JButton okButton = ButtonUtils.createOkButton();
            //-----------------------------------
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    setVisible(false);
                }
            });
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
     *
     */
    private class NeuronNode {

        private final Neuron neuron;
        private Ellipse2D.Double ellipse;
        private NeuronInfo info;
        JLabel infoLabel = new JLabel();

        public NeuronNode(Neuron neuron, Ellipse2D.Double ellipse) {
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
            infoLabel.setToolTipText("<html><body>Щелкните левой кнопкой мыши<br>"
                    + "для просмотра информации</body></html>");
        }

        public StringBuilder getInfo() {
            StringBuilder text = new StringBuilder("Номер узла: " + neuron.index() + "\n");
            if (neuron.getType() != Neuron.IN_LAYER) {
                text.append("Активационная функция: ");
                text.append(neuron.getActivationFunction().getClass().getSimpleName()).append("\n");
                if (neuron.getActivationFunction() instanceof AbstractFunction) {
                    text.append(((AbstractFunction) neuron.getActivationFunction()).coefficientToString())
                            .append("\n");
                }
            }
            text.append("Слой: ");
            switch (neuron.getType()) {
                case Neuron.IN_LAYER:
                    text.append("Входной");
                    break;
                case Neuron.OUT_LAYER:
                    text.append("Выходной");
                    break;
                case Neuron.HIDDEN_LAYER:
                    text.append("Скрытый");
                    break;
            }
            text.append("\n");
            for (Iterator<NeuralLink> link = neuron.outLinks(); link.hasNext(); ) {
                NeuralLink edge = link.next();
                text.append("Вес связи (").append(edge.source().index()).
                        append(",").append(edge.target().index()).
                        append(") = ").
                        append(fmt.format(edge.getWeight())).append("\n");
            }
            return text;
        }

        public void dispose() {
            if (info != null) {
                info.dispose();
            }
        }

        public void createInfo() {
            if (info == null) {
                info = new NeuronInfo(this);
            }
            info.setVisible(true);
        }

        public Neuron neuron() {
            return neuron;
        }

        public Ellipse2D.Double getEllipse() {
            return ellipse;
        }

        public void setEllipse(Ellipse2D.Double ellipse) {
            this.ellipse = ellipse;
        }

        public void setRect(double x, double y, double w, double h) {
            ellipse.setFrame(x, y, w, h);
        }

        public double width() {
            return ellipse.width;
        }

        public double height() {
            return ellipse.height;
        }

        public double x1() {
            return ellipse.x;
        }

        public double x2() {
            return ellipse.x + width();
        }

        public double y1() {
            return ellipse.y;
        }

        public double y2() {
            return ellipse.y + height();
        }

        public double centerX() {
            return (x1() + x2()) / 2.0;
        }

        public double centerY() {
            return (y1() + y2()) / 2.0;
        }

        public boolean contains(double x, double y) {
            return ellipse.contains(x, y);
        }

        public void paintString(Graphics2D g) {
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

        public void paint(Graphics2D g) {
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
        return net.network().layersNum() * (STEP_BETWEEN_LEVELS + 1 + neuronDiam) + 400;
    }

    private double screenHeight() {
        int max = Integer.max(Integer.max(net.network().inLayerNeurons.length,
                net.network().outLayerNeurons.length),
                maxHiddenLayerSize());
        return max * (STEP_BETWEEN_NODES + 1 + neuronDiam) + 200;
    }

    private int maxHiddenLayerSize() {
        int max = 0;
        for (Neuron[] layer : net.network().hiddenLayerNeurons) {
            max = Integer.max(max, layer.length);
        }
        return max;
    }

    private double startY(int size) {
        double length = size * neuronDiam + (size - 1) * STEP_BETWEEN_NODES;
        return (this.getMaximumSize().height - length) / 2.0;
    }

    private double startX() {
        double length = net.network().layersNum() * neuronDiam
                + (net.network().layersNum() - 1) * STEP_BETWEEN_LEVELS;
        return (this.getMaximumSize().width - length) / 2.0;
    }

    private void computeCoordinates() {
        double w = startX(), h = startY(net.network().inLayerNeurons.length);
        for (Neuron n : net.network().inLayerNeurons) {
            nodes.get(n.index()).setRect(w, h,
                    neuronDiam, neuronDiam);
            h += neuronDiam + STEP_BETWEEN_NODES;
        }
        //-----------------------------------------------------
        for (Neuron[] layer : net.network().hiddenLayerNeurons) {
            h = startY(layer.length);
            w += neuronDiam + STEP_BETWEEN_LEVELS;
            for (Neuron n : layer) {
                nodes.get(n.index()).setRect(w, h,
                        neuronDiam, neuronDiam);
                h += neuronDiam + STEP_BETWEEN_NODES;
            }
        }
        //-----------------------------------------------------
        w += neuronDiam + STEP_BETWEEN_LEVELS;
        h = startY(net.network().outLayerNeurons.length);
        for (Neuron n : net.network().outLayerNeurons) {
            nodes.get(n.index()).setRect(w, h,
                    neuronDiam, neuronDiam);
            h += neuronDiam + STEP_BETWEEN_NODES;
        }
    }

    private void createNodes() {
        double w = startX(), h = startY(net.network().inLayerNeurons.length);
        for (Neuron n : net.network().inLayerNeurons) {
            nodes.add(new NeuronNode(n, new Ellipse2D.Double(w, h,
                    neuronDiam, neuronDiam)));
            h += neuronDiam + STEP_BETWEEN_NODES;
        }
        //-----------------------------------------------------
        for (Neuron[] layer : net.network().hiddenLayerNeurons) {
            h = startY(layer.length);
            w += neuronDiam + STEP_BETWEEN_LEVELS;
            for (Neuron n : layer) {
                nodes.add(new NeuronNode(n, new Ellipse2D.Double(w, h,
                        neuronDiam, neuronDiam)));
                h += neuronDiam + STEP_BETWEEN_NODES;
            }
        }
        //-----------------------------------------------------
        w += neuronDiam + STEP_BETWEEN_LEVELS;
        h = startY(net.network().outLayerNeurons.length);
        for (Neuron n : net.network().outLayerNeurons) {
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
        g.draw(new Line2D.Double(u.x2(),
                u.centerY(),
                v.x1(),
                v.centerY()));
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
        //----------------------------------------
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
        //-----------------------------
        g.setColor(classColor);
        g.setFont(attrFont);
        g.drawString(value, (float) ((arrow.getX1() + arrow.getX2()) / 2.0), (float) (arrow.getY1() - 5.0));
    }

    /**
     *
     */
    private class NeuronOptions extends JDialog {

        boolean dialogResult;
        Font nFont = nodeFont;
        Font aFont = attrFont;
        JSpinner diamSpinner = new JSpinner();
        Color inC = inLayerColor;
        Color outC = outLayerColor;
        Color hidC = hidLayerColor;
        Color linkC = linkColor;
        Color attrC = attrColor;
        Color classC = classColor;
        Color textC = textColor;
        Color backgroundColor = NetworkVisualizer.this.getBackground();

        public NeuronOptions(Window parent) {
            super(parent, "Настройки");
            this.setLayout(new GridBagLayout());
            this.setModal(true);
            this.setResizable(false);
            JPanel panel = new JPanel(new GridLayout(11, 2, 10, 10));
            panel.setBorder(PanelBorderUtils.createTitledBorder("Параметры сети"));
            //-------------------------------------------
            diamSpinner.setModel(new SpinnerNumberModel(neuronDiam, MIN_SIZE, MAX_SIZE, 1));
            panel.add(new JLabel("Диаметр нейрона:"));
            panel.add(diamSpinner);
            //------------------------------------
            JButton nodeButton = new JButton("Выбрать...");
            nodeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    JFontChooser nodeFontchooser = new JFontChooser(NeuronOptions.this, nFont);
                    nodeFontchooser.setVisible(true);
                    if (nodeFontchooser.dialogResult()) {
                        nFont = nodeFontchooser.getSelectedFont();
                    }
                }
            });
            JButton attrButton = new JButton("Выбрать...");
            //----------------------------------------------
            attrButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    JFontChooser ruleFontchooser = new JFontChooser(NeuronOptions.this, aFont);
                    ruleFontchooser.setVisible(true);
                    if (ruleFontchooser.dialogResult()) {
                        aFont = ruleFontchooser.getSelectedFont();
                    }
                }
            });
            //--------------------------------------------------
            JButton inColorButton = new JButton("Выбрать...");
            inColorButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    Color obj = JColorChooser.showDialog(NeuronOptions.this, "Выбор цвета нейрона вх. слоя",
                            inC);
                    if (obj != null) {
                        inC = obj;
                    }
                }
            });
            //--------------------------------------------------
            JButton outColorButton = new JButton("Выбрать...");
            outColorButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    Color obj = JColorChooser.showDialog(NeuronOptions.this, "Выбор цвета нейрона вых. слоя",
                            outC);
                    if (obj != null) {
                        outC = obj;
                    }
                }
            });
            //--------------------------------------------------
            JButton linkColorButton = new JButton("Выбрать...");
            linkColorButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    Color obj = JColorChooser.showDialog(NeuronOptions.this, "Выбор цвета связи",
                            linkC);
                    if (obj != null) {
                        linkC = obj;
                    }
                }
            });
            //--------------------------------------------------
            JButton hidColorButton = new JButton("Выбрать...");
            hidColorButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    Color obj = JColorChooser.showDialog(NeuronOptions.this, "Выбор нейрона скрытого слоя",
                            hidC);
                    if (obj != null) {
                        hidC = obj;
                    }
                }
            });
            //--------------------------------------------------
            JButton attrColorButton = new JButton("Выбрать...");
            attrColorButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    Color obj = JColorChooser.showDialog(NeuronOptions.this, "Выбор цвета атрибута",
                            attrC);
                    if (obj != null) {
                        attrC = obj;
                    }
                }
            });
            //--------------------------------------------------
            JButton classColorButton = new JButton("Выбрать...");
            classColorButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    Color obj = JColorChooser.showDialog(NeuronOptions.this, "Выбор цвета класса",
                            classC);
                    if (obj != null) {
                        classC = obj;
                    }
                }
            });
            //--------------------------------------------------
            JButton textColorButton = new JButton("Выбрать...");
            textColorButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    Color obj = JColorChooser.showDialog(NeuronOptions.this, "Выбор цвета текста",
                            textC);
                    if (obj != null) {
                        textC = obj;
                    }
                }
            });
            //--------------------------------------------------
            JButton backgroundColorButton = new JButton("Выбрать...");
            backgroundColorButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    Color obj = JColorChooser.showDialog(NeuronOptions.this, "Выбор цвета фона",
                            backgroundColor);
                    if (obj != null) {
                        backgroundColor = obj;
                    }
                }
            });
            //------------------------------------
            panel.add(new JLabel("Шрифт узла:"));
            panel.add(nodeButton);
            panel.add(new JLabel("Шрифт атрибута:"));
            panel.add(attrButton);
            panel.add(new JLabel("Цвет атрибута:"));
            panel.add(attrColorButton);
            panel.add(new JLabel("Цвет текста:"));
            panel.add(textColorButton);
            panel.add(new JLabel("Цвет связи:"));
            panel.add(linkColorButton);
            panel.add(new JLabel("Цвет нейрона вх. слоя:"));
            panel.add(inColorButton);
            panel.add(new JLabel("Цвет нейрона вых. слоя:"));
            panel.add(outColorButton);
            panel.add(new JLabel("Цвет нейрона скрытого слоя:"));
            panel.add(hidColorButton);
            panel.add(new JLabel("Цвет класса:"));
            panel.add(classColorButton);
            panel.add(new JLabel("Цвет фона:"));
            panel.add(backgroundColorButton);
            //-------------------------------------------
            JButton okButton = ButtonUtils.createOkButton();
            JButton cancelButton = ButtonUtils.createCancelButton();

            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    dialogResult = true;
                    setVisible(false);
                }
            });
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    dialogResult = false;
                    setVisible(false);
                }
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
            this.pack();
            this.setLocationRelativeTo(parent);
        }

        public double getNodeDiametr() {
            return ((SpinnerNumberModel) diamSpinner.getModel()).getNumber().doubleValue();
        }

        public Font getNodeFont() {
            return nFont;
        }

        public Font getAttributeFont() {
            return aFont;
        }

        public Color getInNeuronColor() {
            return inC;
        }

        public Color getTextColor() {
            return textC;
        }

        public Color getLinkColor() {
            return linkC;
        }

        public Color getOutNeuronColor() {
            return outC;
        }

        public Color getHidNeuronColor() {
            return hidC;
        }

        public Color getClassColor() {
            return classC;
        }

        public Color getAttributeColor() {
            return attrC;
        }

        public Color getBackgroundColor() {
            return backgroundColor;
        }

        public boolean dialogResult() {
            return dialogResult;
        }
    }

}
