/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.trees;

import eca.buffer.ImageCopier;
import eca.config.VelocityConfigService;
import eca.gui.ButtonUtils;
import eca.gui.PanelBorderUtils;
import eca.gui.choosers.SaveImageFileChooser;
import eca.gui.dialogs.JFontChooser;
import eca.gui.service.ClassifierIndexerService;
import eca.text.NumericFormatFactory;
import eca.trees.DecisionTreeClassifier.TreeNode;
import eca.trees.rules.AbstractRule;
import eca.trees.rules.NumericRule;
import eca.util.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.springframework.util.Assert;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Decision tree visualization panel.
 *
 * @author Roman Batygin
 */
public class TreeVisualizer extends JPanel {

    /**
     * Velocity configuration
     */
    private static final VelocityConfigService VELOCITY_CONFIGURATION =
            VelocityConfigService.getVelocityConfigService();

    private static final String VM_TEMPLATES_DECISION_TREE_NODE_VM = "vm-templates/optionsTable.vm";

    private static final double MIN_SIZE = 15;
    private static final double MAX_SIZE = 100;
    private static final int MIN_STROKE = 1;
    private static final int MAX_STROKE = 6;
    private static final double STEP_SIZE = 10.0;
    private static final String SAVE_IMAGE_MENU_TITLE = "Сохранить изображение";
    private static final String COPY_IMAGE_MENU_TEXT = "Копировать";
    private static final String OPTIONS_MENU_TEXT = "Настройки";
    private static final String INCREASE_IMAGE_MENU_TEXT = "Увеличить";
    private static final String DECREASE_IMAGE_MENU_TEXT = "Уменьшить";
    private static final int MIN_SCREEN_WIDTH = 100;
    private static final int STEP_BETWEEN_LEVEL_RESIZE_THRESHOLD = 50;
    private static final int MAX_STEP_BETWEEN_LEVELS = 180;
    private static final int MIN_STEP_BETWEEN_LEVELS = 100;
    private static final int SCREEN_WIDTH_MARGIN = 100;
    private static final String ARIAL = "Arial";

    private double nodeWidth = 25.0;
    private double nodeHeight = 25.0;

    private DecisionTreeClassifier tree;
    private ArrayList<NodeDescriptor> treeNodes;

    private Template template;
    private VelocityContext context;

    private Color linkColor = Color.GRAY;
    private Color classColor = Color.RED;
    private Color nodeColor = Color.WHITE;
    private Color leafColor = Color.GREEN;
    private Color borderColor = Color.BLACK;
    private Color textColor = Color.BLUE;
    private Color ruleColor = Color.BLACK;

    private int stroke = 2;
    private Stroke nodeStroke = new BasicStroke(stroke, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL);
    private Stroke linkStroke = new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL);
    private Font nodeFont = new Font(ARIAL, Font.BOLD, 12);
    private Font ruleFont = new Font(ARIAL, Font.BOLD, 11);
    private final DecimalFormat decimalFormat = NumericFormatFactory.getInstance();

    private double screenWidth = 100.0;
    private double stepBetweenLevels = 100.0;

    private Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

    public TreeVisualizer(DecisionTreeClassifier tree, int digits) {
        this.decimalFormat.setMaximumFractionDigits(digits);
        this.setTree(tree);
        this.createPopupMenu();
        this.setLayout(null);
    }

    public void setTree(DecisionTreeClassifier tree) {
        Assert.notNull(tree, "Tree is not specified!");
        this.tree = tree;
        this.createNodes();
        this.computeCoordinates(tree.root);
        this.setDimension();
    }

    public void setStroke(int stroke) {
        if (stroke < MIN_STROKE || stroke > MAX_STROKE) {
            throw new IllegalArgumentException(String.format("Unexpected stoke value: %d", stroke));
        }
        nodeStroke = new BasicStroke(stroke, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL);
        this.stroke = stroke;
    }

    public int getStroke() {
        return stroke;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawTree((Graphics2D) g);

    }

    public Image getImage() {
        Image img = this.createImage(getMinimumSize().width, getMinimumSize().height);
        drawTree((Graphics2D) img.getGraphics());
        return img;
    }

    private void createPopupMenu() {
        JPopupMenu popMenu = new JPopupMenu();
        JMenuItem saveImage = new JMenuItem(SAVE_IMAGE_MENU_TITLE);
        JMenuItem copyImage = new JMenuItem(COPY_IMAGE_MENU_TEXT);
        JMenuItem options = new JMenuItem(OPTIONS_MENU_TEXT);
        JMenuItem increase = new JMenuItem(INCREASE_IMAGE_MENU_TEXT);
        JMenuItem decrease = new JMenuItem(DECREASE_IMAGE_MENU_TEXT);
        //-----------------------------------
        options.addActionListener(evt -> {
            TreeOptions frame = new TreeOptions(null);
            frame.setVisible(true);
            if (frame.dialogResult()) {
                nodeWidth = frame.getNodeWidth();
                nodeHeight = frame.getNodeHeight();
                setStroke(frame.getStroke());
                nodeFont = frame.getSelectedNodeFont();
                ruleFont = frame.getSelectedRuleFont();
                ruleColor = frame.getSelectedRuleColor();
                textColor = frame.getSelectedTextColor();
                linkColor = frame.getSelectedLinkColor();
                nodeColor = frame.getSelectedNodeColor();
                leafColor = frame.getSelectedLeafColor();
                classColor = frame.getSelectedClassColor();
                borderColor = frame.getSelectedBorderColor();
                TreeVisualizer.this.setBackground(frame.getBackgroundColor());
                resizeTree();
            }
            frame.dispose();
        });
        //-----------------------------------
        increase.addActionListener(evt -> {
            nodeWidth += STEP_SIZE;
            nodeHeight += STEP_SIZE;
            if (nodeWidth > MAX_SIZE) {
                nodeWidth = MAX_SIZE;
            }
            if (nodeHeight > MAX_SIZE) {
                nodeHeight = MAX_SIZE;
            }
            nodeFont = new Font(nodeFont.getName(), Font.BOLD, (int) (nodeHeight / 2));
            resizeTree();
        });
        //-----------------------------------
        decrease.addActionListener(evt -> {
            nodeWidth -= STEP_SIZE;
            nodeHeight -= STEP_SIZE;
            if (nodeWidth < MIN_SIZE) {
                nodeWidth = MIN_SIZE;
            }
            if (nodeHeight < MIN_SIZE) {
                nodeHeight = MIN_SIZE;
            }
            nodeFont = new Font(nodeFont.getName(), Font.BOLD, (int) (nodeHeight / 2));
            resizeTree();
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
                    JOptionPane.showMessageDialog(TreeVisualizer.this.getParent(), e.getMessage(),
                            null, JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        //---------------------------------------------------
        saveImage.addActionListener(new ActionListener() {

            SaveImageFileChooser fileChooser;

            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (fileChooser == null) {
                        fileChooser = new SaveImageFileChooser();
                    }
                    fileChooser.setSelectedFile(new File(ClassifierIndexerService.getIndex(tree)));
                    File file = fileChooser.getSelectedFile(TreeVisualizer.this.getParent());
                    if (file != null) {
                        FileUtils.write(file, getImage());
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(TreeVisualizer.this.getParent(), e.getMessage(),
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
        popMenu.add(copyImage);
        popMenu.addSeparator();
        popMenu.add(saveImage);
        this.setComponentPopupMenu(popMenu);
    }

    private void resizeTree() {
        screenWidth = MIN_SCREEN_WIDTH;
        stepBetweenLevels =
                nodeHeight >= STEP_BETWEEN_LEVEL_RESIZE_THRESHOLD ? MAX_STEP_BETWEEN_LEVELS : MIN_STEP_BETWEEN_LEVELS;
        computeCoordinates(tree.root);
        setDimension();
        getRootPane().repaint();
    }

    private void drawTree(Graphics2D g2d) {
        for (NodeDescriptor p : treeNodes) {
            p.paint(g2d);
            if (!p.isLeaf()) {
                for (int i = 0; i < p.childrenNum(); i++) {
                    TreeNode c = p.node.getChild(i);
                    drawLink(g2d, p, treeNodes.get(c.index()), i);
                }
            }
        }
    }

    private void shiftTree(TreeNode x, int i) {
        NodeDescriptor p = treeNodes.get(x.index());
        p.setRect(p.x2() + i * MAX_SIZE + (i - 1) * nodeHeight,
                p.y1(), nodeWidth, nodeHeight);
        screenWidth = Double.max(screenWidth, p.x1());
        if (!x.isLeaf()) {
            for (TreeNode child : x.children()) {
                shiftTree(child, i);
            }
        }
    }

    private void computeCoordinates(TreeNode x) {
        if (x.isLeaf()) {
            NodeDescriptor p = treeNodes.get(x.index());
            p.setRect(screenWidth, x.getDepth() * stepBetweenLevels,
                    nodeWidth, nodeHeight);
        } else {
            TreeNode left = x.getChild(0);
            TreeNode right = x.lastChild();
            for (TreeNode child : x.children()) {
                computeCoordinates(child);
            }
            for (int i = 1; i < x.childrenNum(); i++) {
                shiftTree(x.getChild(i), i);
            }
            NodeDescriptor root = treeNodes.get(x.index());
            NodeDescriptor l = treeNodes.get(left.index());
            NodeDescriptor r = treeNodes.get(right.index());
            root.setRect((l.x1() + r.x1()) / 2, x.getDepth() * stepBetweenLevels,
                    nodeWidth, nodeHeight);
        }
    }

    /**
     * Tree node descriptor.
     */
    private class NodeDescriptor {

        static final String NODE_INDEX = "Номер узла:";
        static final String NODE_TYPE_TEXT = "Тип узла:";
        static final String OBJECTS_NUM_TEXT = "Число объектов:";
        static final String NODE_DEPTH_TEXT = "Глубина узла:";
        static final String SPLIT_ATTR_TEXT = "Атрибут для расщепления:";
        static final String NODE_ERROR_TEXT = "Ошибка узла:";
        static final String CLASS_VALUE_TEXT = "Значение класса:";
        static final String LEAF_TEXT = "Лист";
        static final String INNER_NODE_TEXT = "Внутренний";
        static final String CLASS_FORMAT = "%d (%s)";
        static final String NODE_PARAMS = "params";

        TreeNode node;
        Rectangle2D.Double rect;
        JLabel info = new JLabel();

        NodeDescriptor(TreeNode node) {
            this.node = node;
            TreeVisualizer.this.add(info);
            info.setCursor(handCursor);
            info.setToolTipText(getNodeInfoAsHtml());
        }

        NodeDescriptor(TreeNode node, Rectangle2D.Double rect) {
            this(node);
            this.rect = rect;
        }

        Rectangle2D.Double getRectangle() {
            return rect;
        }

        void setRectangle(Rectangle2D.Double rect) {
            this.rect = rect;
        }

        boolean isLeaf() {
            return node.isLeaf();
        }

        int objectsNum() {
            return node.objectsNum();
        }

        int childrenNum() {
            return node.childrenNum();
        }

        int getDepth() {
            return node.getDepth();
        }

        AbstractRule getRule() {
            return node.getRule();
        }

        void setRect(double x, double y, double w, double h) {
            rect.setRect(x, y, w, h);
        }

        double width() {
            return rect.width;
        }

        double height() {
            return rect.height;
        }

        double x1() {
            return rect.x;
        }

        double x2() {
            return rect.x + width();
        }

        double y1() {
            return rect.y;
        }

        double y2() {
            return rect.y + height();
        }

        double centerX() {
            return (x1() + x2()) / 2.0;
        }

        boolean contains(double x, double y) {
            return rect.contains(x, y);
        }

        String getNodeInfoAsHtml() {
            if (template == null) {
                template = VELOCITY_CONFIGURATION.getTemplate(VM_TEMPLATES_DECISION_TREE_NODE_VM);
            }
            if (context == null) {
                context = new VelocityContext();
            }
            context.put(NODE_PARAMS, fillNodeParams());
            StringWriter stringWriter = new StringWriter();
            template.merge(context, stringWriter);
            return stringWriter.toString();
        }

        Map<String, String> fillNodeParams() {
            Map<String, String> map = new LinkedHashMap<>();
            map.put(NODE_INDEX, String.valueOf(node.index()));
            map.put(NODE_TYPE_TEXT, isLeaf() ? LEAF_TEXT : INNER_NODE_TEXT);
            map.put(OBJECTS_NUM_TEXT, String.valueOf(objectsNum()));
            map.put(NODE_DEPTH_TEXT, String.valueOf(node.getDepth()));
            if (!isLeaf()) {
                map.put(SPLIT_ATTR_TEXT, getRule().attribute().name());
            }
            int classValue = (int) node.classValue();
            map.put(NODE_ERROR_TEXT, decimalFormat.format(tree.calculateNodeError(node)));
            map.put(CLASS_VALUE_TEXT,
                    String.format(CLASS_FORMAT, classValue, tree.getData().classAttribute().value(classValue)));
            return map;
        }

        void paintString(Graphics2D g) {
            FontMetrics fm = g.getFontMetrics(nodeFont);
            g.setPaint(textColor);
            g.setFont(nodeFont);
            String size = String.valueOf(objectsNum());
            info.setSize((int) nodeWidth, (int) nodeHeight);
            info.setLocation((int) x1(), (int) y1());
            g.drawString(size, (float) x1()
                            + ((float) width() - fm.stringWidth(size)) / 2.0f,
                    (float) y1() + fm.getAscent()
                            + ((float) height() - (fm.getAscent() + fm.getDescent())) / 2.0f);
        }

        void paintClass(Graphics2D g) {
            if (isLeaf()) {
                g.setColor(classColor);
                g.drawString(String.valueOf((int) node.classValue()),
                        (float) x2(), (float) y1());
            }
        }

        void paintBorder(Graphics2D g) {
            g.setStroke(nodeStroke);
            g.setColor(borderColor);
            g.draw(rect);
        }

        void paint(Graphics2D g) {
            g.setColor(isLeaf() ? leafColor : nodeColor);
            g.fill(rect);
            paintBorder(g);
            paintString(g);
            paintClass(g);
        }

    } //End of class NodeDescriptor

    private void setDimension() {
        Dimension dim = new Dimension((int) (screenWidth + SCREEN_WIDTH_MARGIN),
                (int) (tree.depth() * (nodeHeight + stepBetweenLevels)));
        this.setPreferredSize(dim);
        this.setMinimumSize(dim);
        this.setMaximumSize(dim);
    }

    private void createNodes() {
        treeNodes = new ArrayList<>(tree.numNodes());
        ArrayDeque<TreeNode> innerNodes = new ArrayDeque<>(tree.numNodes());
        int index = -1;
        innerNodes.add(tree.root);
        while (!innerNodes.isEmpty()) {
            TreeNode parent = innerNodes.pop();
            parent.setIndex(++index);
            if (!parent.isLeaf()) {
                innerNodes.addAll(Arrays.asList(parent.child));
            }
            treeNodes.add(new NodeDescriptor(parent, new Rectangle2D.Double()));
        }
    }

    private void drawLink(Graphics2D g, NodeDescriptor p, NodeDescriptor child, int index) {
        g.setColor(linkColor);
        g.setStroke(linkStroke);
        double x = p.x1() + index * (p.width() / (p.childrenNum() - 1));
        g.draw(new Line2D.Double(x, p.y2(), child.centerX(), child.y1()));
        double xt = (x + child.centerX()) / 2.0;
        double yt = (p.y2() + child.y1()) / 2.0;
        g.setColor(ruleColor);
        g.setFont(ruleFont);
        AbstractRule rule = p.getRule();
        g.drawString(rule instanceof NumericRule ? ((NumericRule) rule).rule(index, decimalFormat) : rule.rule(index),
                (float) xt, (float) yt);
    }

    /**
     * Decision tree options dialog.
     */
    private class TreeOptions extends JDialog {

        static final String TITLE_TEXT = "Настройки";
        static final String TREE_OPTIONS_TITLE = "Параметры дерева";
        static final String NODE_WIDTH_TEXT = "Ширина узла:";
        static final String NODE_HEIGHT_TITLE = "Высота узла:";
        static final String NODE_STROKE_TITLE = "Толщина границы узла:";
        static final String CHOOSE_BUTTON_TEXT = "Выбрать...";
        static final String CHOOSE_RULE_COLOR_TEXT = "Выбор цвета правила";
        static final String CHOOSE_TEXT_COLOR = "Выбор цвета текста";
        static final String CHOOSE_LINK_COLOR_TEXT = "Выбор цвета связи";
        static final String CHOOSE_NODE_COLOR_TEXT = "Выбор цвета узла";
        static final String CHOOSE_LEAF_COLOR_TEXT = "Выбор цвета листа";
        static final String CHOOSE_CLASS_COLOR_TEXT = "Выбор цвета класса";
        static final String CHOOSE_NODE_BORDER_COLOR_TEXT = "Выбор цвета границы узла";
        static final String CHOOSE_BACKGROUND_COLOR_TEXT = "Выбор цвета фона";
        static final String NODE_FONT_TEXT = "Шрифт узла:";
        static final String RULE_FONT_TEXT = "Шрифт правила:";
        static final String RULE_COLOR_TEXT = "Цвет правила:";
        static final String TEXT_COLOR_TEXT = "Цвет текста:";
        static final String LINK_COLOR_TEXT = "Цвет связи:";
        static final String NODE_COLOR_TEXT = "Цвет узла:";
        static final String LEAF_COLOR_TEXT = "Цвет листа:";
        static final String CLASS_COLOR_TEXT = "Цвет класса:";
        static final String NODE_BORDER_COLOR_TEXT = "Цвет границы узла:";
        static final String BACK_COLOR_TEXT = "Цвет фона:";

        boolean dialogResult;
        Font selectedNodeFont = nodeFont;
        Font selectedRuleFont = ruleFont;
        JSpinner widthSpinner = new JSpinner();
        JSpinner heightSpinner = new JSpinner();
        JSpinner strokeSpinner = new JSpinner();
        Color selectedRuleColor = ruleColor;
        Color selectedTextColor = textColor;
        Color selectedLinkColor = linkColor;
        Color selectedLeafColor = leafColor;
        Color selectedNodeColor = nodeColor;
        Color selectedClassColor = classColor;
        Color selectedBorderColor = borderColor;
        Color backgroundColor = TreeVisualizer.this.getBackground();

        TreeOptions(JFrame parent) {
            super(parent, TITLE_TEXT);
            this.setLayout(new GridBagLayout());
            this.setModal(true);
            this.setResizable(false);
            this.init();
            this.pack();
            this.setLocationRelativeTo(parent);
        }

        void init() {
            JPanel panel = new JPanel(new GridLayout(13, 2, 10, 10));
            panel.setBorder(PanelBorderUtils.createTitledBorder(TREE_OPTIONS_TITLE));
            //-------------------------------------------
            widthSpinner.setModel(new SpinnerNumberModel(nodeWidth, MIN_SIZE, MAX_SIZE, 1));
            heightSpinner.setModel(new SpinnerNumberModel(nodeHeight, MIN_SIZE, MAX_SIZE, 1));
            strokeSpinner.setModel(new SpinnerNumberModel(stroke, MIN_STROKE, MAX_STROKE, 1));
            panel.add(new JLabel(NODE_WIDTH_TEXT));
            panel.add(widthSpinner);
            panel.add(new JLabel(NODE_HEIGHT_TITLE));
            panel.add(heightSpinner);
            panel.add(new JLabel(NODE_STROKE_TITLE));
            panel.add(strokeSpinner);
            //------------------------------------
            JButton nodeButton = new JButton(CHOOSE_BUTTON_TEXT);
            nodeButton.addActionListener(evt -> {
                JFontChooser nodeFontChooser = new JFontChooser(TreeOptions.this, selectedNodeFont);
                nodeFontChooser.setVisible(true);
                if (nodeFontChooser.dialogResult()) {
                    selectedNodeFont = nodeFontChooser.getSelectedFont();
                }
            });
            JButton ruleButton = new JButton(CHOOSE_BUTTON_TEXT);
            //----------------------------------------------
            ruleButton.addActionListener(evt -> {
                JFontChooser ruleFontChooser = new JFontChooser(TreeOptions.this, selectedRuleFont);
                ruleFontChooser.setVisible(true);
                if (ruleFontChooser.dialogResult()) {
                    selectedRuleFont = ruleFontChooser.getSelectedFont();
                }
            });
            //--------------------------------------------------
            JButton ruleColorButton = new JButton(CHOOSE_BUTTON_TEXT);
            ruleColorButton.addActionListener(evt -> {
                Color newRuleColor = JColorChooser.showDialog(TreeOptions.this, CHOOSE_RULE_COLOR_TEXT,
                        selectedRuleColor);
                if (newRuleColor != null) {
                    selectedRuleColor = newRuleColor;
                }
            });
            //--------------------------------------------------
            JButton textColorButton = new JButton(CHOOSE_BUTTON_TEXT);
            textColorButton.addActionListener(evt -> {
                Color newTextColor = JColorChooser.showDialog(TreeOptions.this, CHOOSE_TEXT_COLOR,
                        selectedTextColor);
                if (newTextColor != null) {
                    selectedTextColor = newTextColor;
                }
            });
            //--------------------------------------------------
            JButton linkColorButton = new JButton(CHOOSE_BUTTON_TEXT);
            linkColorButton.addActionListener(evt -> {
                Color newLinkColor = JColorChooser.showDialog(TreeOptions.this, CHOOSE_LINK_COLOR_TEXT,
                        selectedLinkColor);
                if (newLinkColor != null) {
                    selectedLinkColor = newLinkColor;
                }
            });
            //--------------------------------------------------
            JButton nodeColorButton = new JButton(CHOOSE_BUTTON_TEXT);
            nodeColorButton.addActionListener(evt -> {
                Color newLeafColor = JColorChooser.showDialog(TreeOptions.this, CHOOSE_NODE_COLOR_TEXT,
                        selectedNodeColor);
                if (newLeafColor != null) {
                    selectedNodeColor = newLeafColor;
                }
            });
            //--------------------------------------------------
            JButton leafColorButton = new JButton(CHOOSE_BUTTON_TEXT);
            leafColorButton.addActionListener(evt -> {
                Color obj = JColorChooser.showDialog(TreeOptions.this, CHOOSE_LEAF_COLOR_TEXT,
                        selectedLeafColor);
                if (obj != null) {
                    selectedLeafColor = obj;
                }
            });
            //--------------------------------------------------
            JButton classColorButton = new JButton(CHOOSE_BUTTON_TEXT);
            classColorButton.addActionListener(evt -> {
                Color newClassColor = JColorChooser.showDialog(TreeOptions.this, CHOOSE_CLASS_COLOR_TEXT,
                        selectedClassColor);
                if (newClassColor != null) {
                    selectedClassColor = newClassColor;
                }
            });
            //--------------------------------------------------
            JButton borderColorButton = new JButton(CHOOSE_BUTTON_TEXT);
            borderColorButton.addActionListener(evt -> {
                Color newBorderColor = JColorChooser.showDialog(TreeOptions.this, CHOOSE_NODE_BORDER_COLOR_TEXT,
                        selectedBorderColor);
                if (newBorderColor != null) {
                    selectedBorderColor = newBorderColor;
                }
            });
            //--------------------------------------------------
            JButton backgroundColorButton = new JButton(CHOOSE_BUTTON_TEXT);
            backgroundColorButton.addActionListener(evt -> {
                Color newBackgroundColor = JColorChooser.showDialog(TreeOptions.this, CHOOSE_BACKGROUND_COLOR_TEXT,
                        backgroundColor);
                if (newBackgroundColor != null) {
                    backgroundColor = newBackgroundColor;
                }
            });
            //------------------------------------
            panel.add(new JLabel(NODE_FONT_TEXT));
            panel.add(nodeButton);
            panel.add(new JLabel(RULE_FONT_TEXT));
            panel.add(ruleButton);
            panel.add(new JLabel(RULE_COLOR_TEXT));
            panel.add(ruleColorButton);
            panel.add(new JLabel(TEXT_COLOR_TEXT));
            panel.add(textColorButton);
            panel.add(new JLabel(LINK_COLOR_TEXT));
            panel.add(linkColorButton);
            panel.add(new JLabel(NODE_COLOR_TEXT));
            panel.add(nodeColorButton);
            panel.add(new JLabel(LEAF_COLOR_TEXT));
            panel.add(leafColorButton);
            panel.add(new JLabel(CLASS_COLOR_TEXT));
            panel.add(classColorButton);
            panel.add(new JLabel(NODE_BORDER_COLOR_TEXT));
            panel.add(borderColorButton);
            panel.add(new JLabel(BACK_COLOR_TEXT));
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

        int getStroke() {
            return ((SpinnerNumberModel) strokeSpinner.getModel()).getNumber().intValue();
        }

        double getNodeWidth() {
            return ((SpinnerNumberModel) widthSpinner.getModel()).getNumber().doubleValue();
        }

        double getNodeHeight() {
            return ((SpinnerNumberModel) heightSpinner.getModel()).getNumber().doubleValue();
        }

        Font getSelectedNodeFont() {
            return selectedNodeFont;
        }

        Font getSelectedRuleFont() {
            return selectedRuleFont;
        }

        Color getSelectedRuleColor() {
            return selectedRuleColor;
        }

        Color getSelectedTextColor() {
            return selectedTextColor;
        }

        Color getSelectedLinkColor() {
            return selectedLinkColor;
        }

        Color getSelectedNodeColor() {
            return selectedNodeColor;
        }

        Color getSelectedLeafColor() {
            return selectedLeafColor;
        }

        Color getSelectedClassColor() {
            return selectedClassColor;
        }

        Color getSelectedBorderColor() {
            return selectedBorderColor;
        }

        Color getBackgroundColor() {
            return backgroundColor;
        }

        boolean dialogResult() {
            return dialogResult;
        }
    }

} //End of class TreeVisualizer
