/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.trees;

import eca.core.ClassifierIndexer;
import eca.core.converters.ImageSaver;
import eca.gui.ButtonUtils;
import eca.gui.PanelBorderUtils;
import eca.gui.choosers.SaveImageFileChooser;
import eca.gui.dialogs.JFontChooser;
import eca.gui.text.NumericFormat;
import eca.io.buffer.ImageCopier;
import eca.trees.DecisionTreeClassifier.TreeNode;
import eca.trees.rules.AbstractRule;
import eca.trees.rules.NumericRule;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * @author Рома
 */
public class TreeVisualizer extends JPanel {

    private static final double MIN_SIZE = 15;
    private static final double MAX_SIZE = 100;
    private static final int MIN_STROKE = 1;
    private static final int MAX_STROKE = 6;
    private static final double STEP_SIZE = 10.0;

    private double nodeWidth = 25.0;
    private double nodeHeight = 25.0;

    private DecisionTreeClassifier tree;
    private ArrayList<NodeDescriptor> treeNodes;

    private Color linkColor = Color.GRAY;
    private Color classColor = Color.RED;
    private Color color = Color.WHITE;
    private Color leafColor = Color.GREEN;
    private Color borderColor = Color.BLACK;
    private Color textColor = Color.BLUE;
    private Color ruleColor = Color.BLACK;

    private int stroke = 2;
    private Stroke nodeStroke = new BasicStroke(stroke, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL);
    private Stroke linkStroke = new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL);
    private Font nodeFont = new Font("Arial", Font.BOLD, 12);
    private Font ruleFont = new Font("Arial", Font.BOLD, 11);
    private final DecimalFormat fmt = NumericFormat.getInstance();

    private double screen_width = 100.0;
    private double step_between_levels = 100.0;

    private Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

    public TreeVisualizer(DecisionTreeClassifier tree, int digits) {
        this.setTree(tree);
        fmt.setMaximumFractionDigits(digits);
        this.createPopupMenu();
        this.setLayout(null);
    }

    public final DecisionTreeClassifier getTree() {
        return tree;
    }

    public final void setTree(DecisionTreeClassifier tree) {
        if (tree == null) {
            throw new NullPointerException();
        }
        this.tree = tree;
        this.createNodes();
        this.computeCoordinates(tree.root);
        this.setDimension();
    }

    public void setStroke(int stroke) {
        if (stroke < MIN_STROKE || stroke > MAX_STROKE) {
            throw new IllegalArgumentException("Illegal value of stroke!");
        }
        nodeStroke = new BasicStroke(stroke, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL);
        this.stroke = stroke;
    }

    public int getStroke() {
        return stroke;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setLeafColor(Color leafColor) {
        this.leafColor = leafColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
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

    public void setRuleColor(Color ruleColor) {
        this.ruleColor = ruleColor;
    }

    public Color color() {
        return color;
    }

    public Color linkColor() {
        return linkColor;
    }

    public Color ruleColor() {
        return ruleColor;
    }

    public Color leafColor() {
        return leafColor;
    }

    public Color borderColor() {
        return borderColor;
    }

    public Color classColor() {
        return classColor;
    }

    public Color textColor() {
        return textColor;
    }

    public void setNodeWidth(double width) {
        if (width < MIN_SIZE || width > MAX_SIZE) {
            throw new IllegalArgumentException("Wrong value of width!");
        }
        this.nodeWidth = width;
    }

    public void setNodeHeight(double height) {
        if (height < MIN_SIZE || height > MAX_SIZE) {
            throw new IllegalArgumentException("Wrong value of height!");
        }
        this.nodeHeight = height;
    }

    public double getNodeWidth() {
        return nodeWidth;
    }

    public double getNodeHeight() {
        return nodeHeight;
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
        JMenuItem saveImage = new JMenuItem("Сохранить изображение");
        JMenuItem copyImage = new JMenuItem("Копировать");
        JMenuItem options = new JMenuItem("Настройки");
        JMenuItem increase = new JMenuItem("Увеличить");
        JMenuItem decrease = new JMenuItem("Уменьшить");
        //-----------------------------------
        options.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                TreeOptions frame = new TreeOptions(null);
                frame.setVisible(true);
                if (frame.dialogResult()) {
                    nodeWidth = frame.getNodeWidth();
                    nodeHeight = frame.getNodeHeight();
                    setStroke(frame.getStroke());
                    nodeFont = frame.getNodeFont();
                    ruleFont = frame.getRuleFont();
                    ruleColor = frame.getRuleColor();
                    textColor = frame.getTextColor();
                    linkColor = frame.getLinkColor();
                    color = frame.getNodeColor();
                    leafColor = frame.getLeafColor();
                    classColor = frame.getClassColor();
                    borderColor = frame.getBorderColor();
                    TreeVisualizer.this.setBackground(frame.getBackgroundColor());
                    resizeTree();
                }
                frame.dispose();
            }
        });
        //-----------------------------------
        increase.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
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
            }
        });
        //-----------------------------------
        decrease.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
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
                    JOptionPane.showMessageDialog(TreeVisualizer.this.getParent(), e.getMessage(),
                            null, JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        //---------------------------------------------------
        saveImage.addActionListener(new ActionListener() {

            SaveImageFileChooser fileChooser;
            ClassifierIndexer indexer = new ClassifierIndexer();

            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (fileChooser == null) {
                        fileChooser = new SaveImageFileChooser();
                    }
                    fileChooser.setSelectedFile(new File(indexer.getIndex(tree)));
                    File file = fileChooser.saveFile(TreeVisualizer.this.getParent());
                    if (file != null) {
                        ImageSaver.saveImage(file, getImage());
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
        screen_width = 100;
        step_between_levels = nodeHeight >= 50 ? 180 : 100;
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
        screen_width = Double.max(screen_width, p.x1());
        if (!x.isLeaf()) {
            for (TreeNode child : x.children()) {
                shiftTree(child, i);
            }
        }
    }

    private void computeCoordinates(TreeNode x) {
        if (x.isLeaf()) {
            NodeDescriptor p = treeNodes.get(x.index());
            p.setRect(screen_width, x.getDepth() * step_between_levels,
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
            root.setRect((l.x1() + r.x1()) / 2, x.getDepth() * step_between_levels,
                    nodeWidth, nodeHeight);
        }
    }

    /**
     *
     */
    private class NodeDescriptor {

        TreeNode node;
        Rectangle2D.Double rect;
        JLabel info = new JLabel();

        NodeDescriptor(TreeNode node) {
            this.node = node;
            TreeVisualizer.this.add(info);
            info.setCursor(handCursor);
            info.setToolTipText(toString());
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

        @Override
        public String toString() {
            StringBuilder str = new StringBuilder("<html><head><style>"
                    + ".attr {font-weight: bold;}</style></head><body>");
            str.append("<table>");
            str.append("<tr>");
            str.append("<td class = 'attr'>Номер узла:</td>").append("<td>").
                    append(node.index()).append("</td>");
            str.append("</tr><tr>");
            String type = isLeaf() ? "<td>Лист</td>" : "<td>Внутренний</td>";
            str.append("<td class = 'attr'>Тип узла:</td>").append(type);
            str.append("</tr><tr>");
            str.append("<td class = 'attr'>Число объектов:</td>").append("<td>").
                    append(String.valueOf(objectsNum())).append("</td>");
            str.append("</tr><tr>");
            str.append("<td class = 'attr'>Глубина узла:</td>").append("<td>").
                    append(String.valueOf(node.getDepth())).append("</td>");
            str.append("</tr>");
            if (!isLeaf()) {
                str.append("<tr>");
                str.append("<td class = 'attr'>Атрибут для расщепления:</td>").append("<td>").
                        append(getRule().attribute().name()).append("</td>");
                str.append("</tr>");
            }
            int c = (int) node.classValue();
            str.append("<tr>");
            str.append("<td class = 'attr'>Ошибка узла:</td>").append("<td>").
                    append(fmt.format(tree.nodeError(node))).append("</td>");
            str.append("</tr><tr>");
            str.append("<td class = 'attr'>Значение класса:</td>").append("<td>").append(c)
                    .append(" (").append(tree.data.classAttribute().value(c)).append(")</td>");
            str.append("</tr></table></body></html>");
            return str.toString();
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
            g.setColor(isLeaf() ? leafColor : color);
            g.fill(rect);
            paintBorder(g);
            paintString(g);
            paintClass(g);
        }

    } //End of class NodeDescriptor

    private void setDimension() {
        Dimension dim = new Dimension((int) (screen_width + 100),
                (int) (tree.depth() * (nodeHeight + step_between_levels)));
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
                for (TreeNode child : parent.child) {
                    innerNodes.add(child);
                }
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
        g.drawString(rule instanceof NumericRule ? ((NumericRule) rule).rule(index, fmt) : rule.rule(index),
                (float) xt, (float) yt);
    }

    /**
     *
     */
    private class TreeOptions extends JDialog {

        boolean dialogResult;
        Font nFont = nodeFont;
        Font rFont = ruleFont;
        JSpinner widthSpinner = new JSpinner();
        JSpinner heightSpinner = new JSpinner();
        JSpinner strokeSpinner = new JSpinner();
        Color ruleC = ruleColor;
        Color textC = textColor;
        Color linkC = linkColor;
        Color leafC = leafColor;
        Color nodeC = color;
        Color classC = classColor;
        Color borderC = borderColor;
        Color backgroundColor = TreeVisualizer.this.getBackground();

        public TreeOptions(JFrame parent) {
            super(parent, "Настройки");
            this.setLayout(new GridBagLayout());
            this.setModal(true);
            this.setResizable(false);
            JPanel panel = new JPanel(new GridLayout(13, 2, 10, 10));
            panel.setBorder(PanelBorderUtils.createTitledBorder("Параметры дерева"));
            //-------------------------------------------
            widthSpinner.setModel(new SpinnerNumberModel(nodeWidth, MIN_SIZE, MAX_SIZE, 1));
            heightSpinner.setModel(new SpinnerNumberModel(nodeHeight, MIN_SIZE, MAX_SIZE, 1));
            strokeSpinner.setModel(new SpinnerNumberModel(stroke, MIN_STROKE, MAX_STROKE, 1));
            panel.add(new JLabel("Ширина узла:"));
            panel.add(widthSpinner);
            panel.add(new JLabel("Высота узла:"));
            panel.add(heightSpinner);
            panel.add(new JLabel("Толщина границы узла:"));
            panel.add(strokeSpinner);
            //------------------------------------
            JButton nodeButton = new JButton("Выбрать...");
            nodeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    JFontChooser nodeFontchooser = new JFontChooser(TreeOptions.this, nFont);
                    nodeFontchooser.setVisible(true);
                    if (nodeFontchooser.dialogResult()) {
                        nFont = nodeFontchooser.getSelectedFont();
                    }
                }
            });
            JButton ruleButton = new JButton("Выбрать...");
            //----------------------------------------------
            ruleButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    JFontChooser ruleFontchooser = new JFontChooser(TreeOptions.this, rFont);
                    ruleFontchooser.setVisible(true);
                    if (ruleFontchooser.dialogResult()) {
                        rFont = ruleFontchooser.getSelectedFont();
                    }
                }
            });
            //--------------------------------------------------
            JButton ruleColorButton = new JButton("Выбрать...");
            ruleColorButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    Color obj = JColorChooser.showDialog(TreeOptions.this, "Выбор цвета правила",
                            ruleC);
                    if (obj != null) {
                        ruleC = obj;
                    }
                }
            });
            //--------------------------------------------------
            JButton textColorButton = new JButton("Выбрать...");
            textColorButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    Color obj = JColorChooser.showDialog(TreeOptions.this, "Выбор цвета текста",
                            textC);
                    if (obj != null) {
                        textC = obj;
                    }
                }
            });
            //--------------------------------------------------
            JButton linkColorButton = new JButton("Выбрать...");
            linkColorButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    Color obj = JColorChooser.showDialog(TreeOptions.this, "Выбор цвета связи",
                            linkC);
                    if (obj != null) {
                        linkC = obj;
                    }
                }
            });
            //--------------------------------------------------
            JButton nodeColorButton = new JButton("Выбрать...");
            nodeColorButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    Color obj = JColorChooser.showDialog(TreeOptions.this, "Выбор цвета узла",
                            nodeC);
                    if (obj != null) {
                        nodeC = obj;
                    }
                }
            });
            //--------------------------------------------------
            JButton leafColorButton = new JButton("Выбрать...");
            leafColorButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    Color obj = JColorChooser.showDialog(TreeOptions.this, "Выбор цвета листа",
                            leafC);
                    if (obj != null) {
                        leafC = obj;
                    }
                }
            });
            //--------------------------------------------------
            JButton classColorButton = new JButton("Выбрать...");
            classColorButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    Color obj = JColorChooser.showDialog(TreeOptions.this, "Выбор цвета класса",
                            classC);
                    if (obj != null) {
                        classC = obj;
                    }
                }
            });
            //--------------------------------------------------
            JButton borderColorButton = new JButton("Выбрать...");
            borderColorButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    Color obj = JColorChooser.showDialog(TreeOptions.this, "Выбор цвета границы узла",
                            borderC);
                    if (obj != null) {
                        borderC = obj;
                    }
                }
            });
            //--------------------------------------------------
            JButton backgroundColorButton = new JButton("Выбрать...");
            backgroundColorButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    Color obj = JColorChooser.showDialog(TreeOptions.this, "Выбор цвета фона",
                            backgroundColor);
                    if (obj != null) {
                        backgroundColor = obj;
                    }
                }
            });
            //------------------------------------
            panel.add(new JLabel("Шрифт узла:"));
            panel.add(nodeButton);
            panel.add(new JLabel("Шрифт правила:"));
            panel.add(ruleButton);
            panel.add(new JLabel("Цвет правила:"));
            panel.add(ruleColorButton);
            panel.add(new JLabel("Цвет текста:"));
            panel.add(textColorButton);
            panel.add(new JLabel("Цвет связи:"));
            panel.add(linkColorButton);
            panel.add(new JLabel("Цвет узла:"));
            panel.add(nodeColorButton);
            panel.add(new JLabel("Цвет листа:"));
            panel.add(leafColorButton);
            panel.add(new JLabel("Цвет класса:"));
            panel.add(classColorButton);
            panel.add(new JLabel("Цвет границы узла:"));
            panel.add(borderColorButton);
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

        public int getStroke() {
            return ((SpinnerNumberModel) strokeSpinner.getModel()).getNumber().intValue();
        }

        public double getNodeWidth() {
            return ((SpinnerNumberModel) widthSpinner.getModel()).getNumber().doubleValue();
        }

        public double getNodeHeight() {
            return ((SpinnerNumberModel) heightSpinner.getModel()).getNumber().doubleValue();
        }

        public Font getNodeFont() {
            return nFont;
        }

        public Font getRuleFont() {
            return rFont;
        }

        public Color getRuleColor() {
            return ruleC;
        }

        public Color getTextColor() {
            return textC;
        }

        public Color getLinkColor() {
            return linkC;
        }

        public Color getNodeColor() {
            return nodeC;
        }

        public Color getLeafColor() {
            return leafC;
        }

        public Color getClassColor() {
            return classC;
        }

        public Color getBorderColor() {
            return borderC;
        }

        public Color getBackgroundColor() {
            return backgroundColor;
        }

        public boolean dialogResult() {
            return dialogResult;
        }
    }

} //End of class TreeVisualizer
