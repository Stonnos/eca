/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.frames;

import eca.ApplicationProperties;
import eca.core.converters.DataSaver;
import eca.dataminer.AutomatedStacking;
import eca.dataminer.AutomatedHeterogeneousEnsemble;
import eca.dataminer.ClassifiersSetBuilder;
import eca.dataminer.AutomatedNeuralNetwork;
import eca.gui.ExecutorService;
import eca.gui.actions.CallbackAction;
import eca.gui.actions.DataGeneratorLoader;
import eca.gui.dialogs.BaseOptionsDialog;
import eca.gui.dialogs.ClassifierBuilderDialog;
import eca.gui.dialogs.DatabaseConnectionDialog;
import eca.gui.dialogs.DataGeneratorDialog;
import eca.gui.dialogs.DecisionTreeOptionsDialog;
import eca.gui.dialogs.EnsembleOptionsDialog;
import eca.gui.dialogs.ExecutorDialog;
import eca.gui.dialogs.KNNOptionDialog;
import eca.gui.dialogs.LoadDialog;
import eca.gui.dialogs.LogisticOptionsDialogBase;
import eca.gui.dialogs.NetworkOptionsDialog;
import eca.gui.dialogs.NumberFormatDialog;
import eca.gui.dialogs.RandomForestsOptionDialog;
import eca.gui.dialogs.StackingOptionsDialog;
import eca.gui.dialogs.TestingSetOptionsDialog;
import eca.gui.text.DateFormat;
import eca.neural.functions.SineFunction;
import eca.neural.functions.TanhFunction;
import eca.neural.functions.LogisticFunction;
import eca.neural.functions.ExponentialFunction;
import eca.neural.functions.ActivationFunction;
import eca.jdbc.DataBaseConnection;
import eca.gui.actions.DataBaseConnectionAction;
import eca.gui.actions.URLLoader;
import eca.gui.actions.InstancesLoader;
import eca.gui.PanelBorderUtils;
import eca.gui.actions.ModelLoader;
import eca.ensemble.AdaBoostClassifier;
import eca.ensemble.ModifiedHeterogeneousClassifier;
import eca.ensemble.RandomForests;
import eca.ensemble.IterativeBuilder;
import eca.ensemble.Iterable;
import eca.ensemble.CVIterativeBuilder;
import eca.ensemble.HeterogeneousClassifier;
import eca.ensemble.StackingClassifier;
import eca.trees.DecisionTreeClassifier;
import eca.trees.ID3;
import eca.trees.CART;
import eca.trees.CHAID;
import eca.trees.C45;
import eca.gui.choosers.OpenDataFileChooser;
import eca.gui.choosers.SaveDataFileChooser;
import eca.gui.tables.InstancesTable;
import eca.gui.tables.AttributesTable;
import eca.gui.tables.StatisticsTableBuilder;
import eca.gui.choosers.OpenModelChooser;
import eca.gui.enums.ClassifiersNames;
import eca.neural.NeuralNetwork;
import eca.gui.enums.EnsemblesNames;
import eca.metrics.KNearestNeighbours;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import weka.core.Instances;
import java.io.*;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import javax.imageio.ImageIO;
import eca.core.evaluation.Evaluation;
import weka.classifiers.Classifier;
import weka.classifiers.AbstractClassifier;
import eca.regression.Logistic;
import eca.beans.ModelDescriptor;
import eca.ensemble.AbstractHeterogeneousClassifier;
import eca.net.DataLoaderImpl;
import java.net.*;
import eca.core.TestMethod;
import eca.Reference;

/**
 *
 * @author Рома
 */
public class JMainFrame extends JFrame {

    private static final ApplicationProperties APPLICATION_PROPERTIES = ApplicationProperties.getInstance();

    private static final Color frameColor = new Color(198, 226, 255);
    
    private static final String ensembleBuildingProgressTitle = "Пожалуйста подождите, идет построение ансамбля...";

    private static final String networkBuildingProgressTitle = "Пожалуйста подождите, идет обучение нейронной сети...";
    
    private final JDesktopPane panels = new JDesktopPane();
    
    private JMenu algorithmsMenu;

    private JMenu dataMinerMenu;

    private JMenuItem saveFileMenu;

    private JMenuItem attrStatisticsMenu;
    
    private JMenu windowsMenu;

    private static final double widthCoefficient = 0.8;

    private static final double heightCoefficient = 0.9;

    private ResultsHistory resultsHistory = new ResultsHistory();
    
    private int maximumFractionDigits;
    
    private boolean isStarted;
    
    private final TestingSetOptionsDialog testingSetFrame
            = new TestingSetOptionsDialog(this);

    private ResultHistoryFrame resultHistoryFrame;
    
    public JMainFrame() {
        Locale.setDefault(Locale.ENGLISH);
        this.init();
        this.makeGUI();
        resultHistoryFrame = new ResultHistoryFrame(this, resultsHistory);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.algorithmsMenu.setEnabled(false);
        this.saveFileMenu.setEnabled(false);
        this.attrStatisticsMenu.setEnabled(false);
        this.dataMinerMenu.setEnabled(false);
        this.createWindowListener();
        this.setLocationRelativeTo(null);
    }

    private void init() {
        try {
            this.setTitle(APPLICATION_PROPERTIES.getTitle());
            this.maximumFractionDigits = APPLICATION_PROPERTIES.getMaximumFractionDigits();
            this.setIconImage(ImageIO.read(getClass().getClassLoader().getResource(APPLICATION_PROPERTIES.getIconUrl())));
            ToolTipManager.sharedInstance().setDismissDelay(APPLICATION_PROPERTIES.getTooltipDismissTime());
        } catch (Exception e) {
        }
    }
    
    private void closeWindow() {
        if (isStarted) {
            int result = JOptionPane.showConfirmDialog(JMainFrame.this,
                    "Вы уверены, что хотите выйти?", null,
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                JMainFrame.this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                System.exit(0);
            }
        } else {
            JMainFrame.this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            System.exit(0);
        }
    }
    
    private void createWindowListener() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                JMainFrame.this.closeWindow();
            }
        });
    }

    /**
     *
     */
    private class DataInternalFrame extends JInternalFrame {
        
        private static final String UPPER_TITLE = "Информация о данных";
        private static final String DATA_TITLE = "Таблица с данными";
        private static final String ATTR_TITLE = "Выбранные атрибуты";
        private static final String CLASS_TITLE = "Выбранный класс";
        
        private final Instances data;
        
        private JPanel upperPanel;
        private JPanel lowerPanel;
        private JTextField relationName;
        private JTextField numInstances;
        private JTextField numAttributes;
        
        private JScrollPane dataScrollPane;
        private JScrollPane attrScrollPane;
        private JPanel attrPanel;
        
        private JComboBox<String> classBox;
        private InstancesTable instanceTable;
        private AttributesTable attributesTable;
        private JButton selectButton;
        private JButton resetButton;
        
        private JMenuItem menu;
        
        public DataInternalFrame(Instances data, JMenuItem menu) throws Exception {
            this.setLayout(new GridBagLayout());
            this.makeUpperPanel();
            this.makeLowerPanel();
            this.setFrameColor(frameColor);
            this.data = data;
            this.setMenu(menu);
            this.createPopMenu();
            this.setRelationInfo();
            this.convertDataToTables();
            this.setClosable(true);
            this.setResizable(true);
            this.setMaximizable(true);
            this.pack();
        }
        
        public final void setMenu(JMenuItem menu) {
            this.menu = menu;
        }
        
        public final JMenuItem getMenu() {
            return menu;
        }
        
        public Instances getData() throws Exception {
            return attributesTable.createData();
        }
        
        public void check() throws Exception {
            attributesTable.check();
        }
        
        public final void setFrameColor(Color color) {
            this.setBackground(color);
            upperPanel.setBackground(color);
            lowerPanel.setBackground(color);
            attrPanel.setBackground(color);
            dataScrollPane.setBackground(color);
            attrScrollPane.setBackground(color);
        }
        
        private void createPopMenu() {
            JPopupMenu popMenu = new JPopupMenu();
            JMenuItem nameMenu = new JMenuItem("Изменение названия данных");
            JMenuItem colorMenu = new JMenuItem("Выбор цвета фона");
            JMenuItem saveMenu = new JMenuItem("Сохранить...");
            //-----------------------------------
            nameMenu.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    String name = (String) JOptionPane.showInputDialog(DataInternalFrame.this,
                            "Название:",
                            "Новое название данных", JOptionPane.INFORMATION_MESSAGE, null,
                            null, data.relationName());
                    if (name != null) {
                        String trimName = name.trim();
                        if (!trimName.isEmpty()) {
                            instanceTable.data().setRelationName(trimName);
                            relationName.setText(trimName);
                            menu.setText(trimName);
                        }
                    }
                }
            });
            //-----------------------------------
            colorMenu.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    Color color = JColorChooser.showDialog(DataInternalFrame.this, colorMenu.getText(),
                            getBackground());
                    if (color != null) {
                        setFrameColor(color);
                    }
                }
            });
            //-----------------------------------
            saveMenu.addActionListener(new ActionListener() {
                
                SaveDataFileChooser fileChooser;
                
                @Override
                public void actionPerformed(ActionEvent evt) {
                    try {
                        if (dataValidated()) {
                            if (fileChooser == null) {
                                fileChooser = new SaveDataFileChooser();
                            }
                            fileChooser.setSelectedFile(new File(instanceTable.data().relationName()));
                            File file = fileChooser.saveFile(DataInternalFrame.this);
                            if (file != null) {
                                DataSaver.saveData(file, DataInternalFrame.this.getData());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(DataInternalFrame.this, e.getMessage(),
                                null, JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            //-----------------------------------
            popMenu.add(nameMenu);
            popMenu.add(colorMenu);
            popMenu.addSeparator();
            popMenu.add(saveMenu);
            this.setComponentPopupMenu(popMenu);
        }
        
        private void setRelationInfo() {
            relationName.setText(data.relationName());
            numInstances.setText(String.valueOf(data.numInstances()));
            numAttributes.setText(String.valueOf(data.numAttributes()));
        }
        
        private void makeUpperPanel() {
            upperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            upperPanel.setBorder(PanelBorderUtils.createTitledBorder(UPPER_TITLE));
            //----------------------------------------------
            relationName = new JTextField(30);
            relationName.setEditable(false);
            relationName.setBackground(Color.WHITE);
            numInstances = new JTextField(6);
            numInstances.setEditable(false);
            numInstances.setBackground(Color.WHITE);
            numAttributes = new JTextField(4);
            numAttributes.setEditable(false);
            numAttributes.setBackground(Color.WHITE);
            upperPanel.add(new JLabel("Название: "));
            upperPanel.add(relationName);
            upperPanel.add(new JLabel("Число объектов: "));
            upperPanel.add(numInstances);
            upperPanel.add(new JLabel("Число атрибутов: "));
            upperPanel.add(numAttributes);
            //----------------------------------------------
            this.add(upperPanel, new GridBagConstraints(0, 0, 1, 1, 1, 0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(5, 0, 5, 0), 0, 0));
            //----------------------------------------------
        }
        
        private void makeLowerPanel() {
            lowerPanel = new JPanel(new GridBagLayout());
            //--------------------------------------------
            dataScrollPane = new JScrollPane();
            dataScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            dataScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            dataScrollPane.setBorder(PanelBorderUtils.createTitledBorder(DATA_TITLE));
            //-------------------------------------------
            this.makeAttrPanel();
            //------------------------------------------
            lowerPanel.add(dataScrollPane, new GridBagConstraints(0, 0, 1, 2, 1, 1,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 2, 5), 0, 0));
            lowerPanel.add(attrPanel, new GridBagConstraints(1, 0, 1, 1, 0, 1,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 2, 0), 0, 0));
            //---------------------------------------------
            this.add(lowerPanel, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        }
        
        private void makeAttrPanel() {
            attrPanel = new JPanel(new GridBagLayout());
            attrPanel.setBorder(PanelBorderUtils.createTitledBorder(ATTR_TITLE));
            selectButton = new JButton("Выбрать все");
            resetButton = new JButton("Сброс");
            //-------------------------------------------
            selectButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    attributesTable.selectAllAttributes();
                }
            });
            //-------------------------------------------
            resetButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    attributesTable.resetValues();
                }
            });
            //-------------------------------------------
            attrScrollPane = new JScrollPane();
            attrScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            attrScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            double width = 0.35 * widthCoefficient * Toolkit.getDefaultToolkit().getScreenSize().width;
            attrScrollPane.setPreferredSize(new Dimension((int) width,
                    400));
            //-------------------------------------------
            classBox = new JComboBox<>();
            classBox.setBorder(PanelBorderUtils.createTitledBorder(CLASS_TITLE));
            Dimension dim = new Dimension((int) width, 50);
            classBox.setPreferredSize(dim);
            classBox.setMinimumSize(dim);
            classBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    data.setClassIndex(classBox.getSelectedIndex());
                }
            });
            //------------------------------------------
            attrPanel.add(selectButton, new GridBagConstraints(0, 0, 1, 1, 1, 0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 5, 2), 0, 0));
            attrPanel.add(resetButton, new GridBagConstraints(1, 0, 1, 1, 1, 0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 2, 5, 0), 0, 0));
            attrPanel.add(attrScrollPane, new GridBagConstraints(0, 1, 2, 1, 0, 1,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));
            attrPanel.add(classBox, new GridBagConstraints(0, 2, 2, 1, 0, 0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE,
                    new Insets(0, 0, 2, 0), 0, 0));
        }
        
        private void convertDataToTables() {
            for (int i = 0; i < data.numAttributes(); i++) {
                classBox.addItem(data.attribute(i).name());
            }
            data.setClassIndex(data.numAttributes() - 1);
            classBox.setSelectedIndex(data.classIndex());
            instanceTable = new InstancesTable(data, numInstances);
            dataScrollPane.setViewportView(instanceTable);
            attributesTable = new AttributesTable(instanceTable, classBox);
            attrScrollPane.setViewportView(attributesTable);
            dataScrollPane.setComponentPopupMenu(instanceTable.getComponentPopupMenu());
        }
        
    } //End of class DataInternalFrame

    /**
     *
     */
    private class ModelBuilder implements CallbackAction {
        
        Classifier model;
        Instances data;
        Evaluation evaluation;
        
        ModelBuilder(Classifier model, Instances data) {
            this.model = model;
            this.data = data;
        }
        
        @Override
        public void apply() throws Exception {
            evaluation = createEvaluation(model, data);
        }
        
        public Evaluation evaluation() {
            return evaluation;
        }

    } //End of class ModelBuilder

    /**
     *
     */
    public class ResultsHistory extends DefaultListModel<String> {

        private ArrayList<ResultsFrameBase> resultsFrameBases = new ArrayList<>();

        public void createResultFrame(String title,
                               Classifier classifier,
                               Instances data,
                               Evaluation e,
                               int digits) throws Exception {
            ResultsFrameBase res = new ResultsFrameBase(JMainFrame.this,
                    title, classifier, data, e,
                    digits);
            StatisticsTableBuilder stat = new StatisticsTableBuilder(digits);
            res.setStatisticaTable(stat.createStatistica(classifier, e));
            ResultsFrameBase.createResults(res, digits);
            add(res);
            res.setVisible(true);
        }

        public void add(ResultsFrameBase resultsFrameBase) {
            resultsFrameBases.add(resultsFrameBase);
            addElement(DateFormat.SIMPLE_DATE_FORMAT.format(resultsFrameBase.getIndexer().getCurrentDate()) +
                    "  " + resultsFrameBase.classifier().getClass().getSimpleName());
        }

        public ArrayList<ResultsFrameBase> getResultsFrameBases() {
            return resultsFrameBases;
        }

        public ResultsFrameBase getFrame(int i) {
            return resultsFrameBases.get(i);
        }

    }

    public void process(ExecutorDialog executorDialog, CallbackAction successAction) throws Exception {
        ExecutorService.process(executorDialog, successAction, new CallbackAction() {
            @Override
            public void apply() {
                JOptionPane.showMessageDialog(JMainFrame.this,
                       executorDialog.getErrorMessageText(), "", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    private void createAndShowLoaderFrame(BaseOptionsDialog frame) throws Exception {
        frame.showDialog();
        if (frame.dialogResult()) {
            ModelBuilder builder = new ModelBuilder(frame.classifier(), frame.data());
            LoadDialog progress = new LoadDialog(JMainFrame.this,
                    builder, "Пожалуйста подождите, идет построение модели...");

            process(progress, new CallbackAction() {
                @Override
                public void apply() throws Exception {
                    resultsHistory.createResultFrame(frame.getTitle(), frame.classifier(), frame.data(),
                            builder.evaluation(), maximumFractionDigits);
                }
            });

        }
        frame.dispose();
    }


    private DataInternalFrame selectedPanel() {
        return (DataInternalFrame) panels.getSelectedFrame();
    }
    
    private Instances data() throws Exception {
        return selectedPanel().getData();
    }
    
    private void makeGUI() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (widthCoefficient * dim.width), (int) (heightCoefficient * dim.height));
        this.makeMenu();
        panels.setBackground(Color.GRAY);
        this.add(panels);
    }
    
    public void createDataFrame(Instances data) throws Exception {
        final DataInternalFrame frame = new DataInternalFrame(data, new JCheckBoxMenuItem(data.relationName()));

        frame.addInternalFrameListener(new InternalFrameAdapter() {
            
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                windowsMenu.remove(frame.getMenu());
                if (panels.getComponentCount() == 0) {
                    algorithmsMenu.setEnabled(false);
                    saveFileMenu.setEnabled(false);
                    dataMinerMenu.setEnabled(false);
                    attrStatisticsMenu.setEnabled(false);
                }
            }
            
            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
                frame.getMenu().setSelected(true);
            }
            
            @Override
            public void internalFrameDeactivated(InternalFrameEvent e) {
                frame.getMenu().setSelected(false);
            }
        });
        //-----------------------------
        frame.getMenu().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    frame.setSelected(true);
                    frame.getMenu().setSelected(true);
                } catch (Exception e) {
                }
            }
        });
        //--------------------------------------------
        panels.add(frame);
        frame.setVisible(true);
        algorithmsMenu.setEnabled(true);
        saveFileMenu.setEnabled(true);
        dataMinerMenu.setEnabled(true);
        attrStatisticsMenu.setEnabled(true);
        windowsMenu.add(frame.getMenu());
        isStarted = true;
    }
    
    private void makeMenu() {
        JMenuBar menu = new JMenuBar();
        JMenu fileMenu = new JMenu("Файл");
        algorithmsMenu = new JMenu("Классификаторы");
        dataMinerMenu = new JMenu("Data Miner");
        JMenu optionsMenu = new JMenu("Настройки");
        JMenu serviceMenu = new JMenu("Сервис");
        windowsMenu = new JMenu("Окна");
        JMenu referenceMenu = new JMenu("Справка");
        menu.add(fileMenu);
        menu.add(algorithmsMenu);
        menu.add(dataMinerMenu);
        menu.add(optionsMenu);
        menu.add(serviceMenu);
        menu.add(windowsMenu);
        menu.add(referenceMenu);
        //-------------------------------
        JMenuItem openFileMenu = new JMenuItem("Открыть...");
        openFileMenu.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
        //-------------------------------
        openFileMenu.addActionListener(new ActionListener() {
            
            OpenDataFileChooser fileChooser;
            
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (fileChooser == null) {
                        fileChooser = new OpenDataFileChooser();
                    }
                    File file = fileChooser.openFile(JMainFrame.this);
                    if (file != null) {
                        InstancesLoader loader = new InstancesLoader(file);
                        LoadDialog progress = new LoadDialog(JMainFrame.this,
                                loader,
                                "Пожалуйста подождите, идет загрузка данных...");

                        process(progress, new CallbackAction() {
                            @Override
                            public void apply() throws Exception {
                                createDataFrame(loader.data());
                            }
                        });
                        //---------------------------------------
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(JMainFrame.this, e.getMessage(),
                            null, JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        //------------------------------------------------
        JMenuItem sampleMenu = new JMenuItem("Настройка метода оценки точности");
        sampleMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                testingSetFrame.showDialog();
            }
        });
        optionsMenu.add(sampleMenu);
        //------------------------------------------------
        JMenuItem digitsMenu = new JMenuItem("Настройка формата чисел");
        digitsMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                NumberFormatDialog dialog = new NumberFormatDialog(JMainFrame.this,
                        "Формат чисел", "Количество десятичных знаков:", maximumFractionDigits);
                dialog.setVisible(true);
                if (dialog.dialogResult()) {
                    maximumFractionDigits = dialog.getValue();
                }
                dialog.dispose();
            }
        });
        optionsMenu.add(digitsMenu);
        //-------------------------------
        fileMenu.add(openFileMenu);
        saveFileMenu = new JMenuItem("Сохранить...");
        saveFileMenu.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
        fileMenu.add(saveFileMenu);
        //-------------------------------------------------
        saveFileMenu.addActionListener(new ActionListener() {
            
            SaveDataFileChooser fileChooser;
            
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (dataValidated()) {
                        if (fileChooser == null) {
                            fileChooser = new SaveDataFileChooser();
                        }
                        fileChooser.setSelectedFile(new File(data().relationName()));
                        File file = fileChooser.saveFile(JMainFrame.this);
                        if (file != null) {
                            DataSaver.saveData(file, data());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(JMainFrame.this, e.getMessage(),
                            null, JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        //---------------------------------------------
        JMenuItem dbMenu = new JMenuItem("Подключиться к базе данных");
        dbMenu.setAccelerator(KeyStroke.getKeyStroke("ctrl shift D"));
        fileMenu.addSeparator();
        fileMenu.add(dbMenu);
        dbMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                DatabaseConnectionDialog conn = new DatabaseConnectionDialog(JMainFrame.this);
                conn.setVisible(true);
                if (conn.dialogResult()) {
                    try {
                        DataBaseConnection connection = new DataBaseConnection();
                        connection.setConnectionDescriptor(conn.getConnectionDescriptor());
                        LoadDialog progress = new LoadDialog(JMainFrame.this,
                                new DataBaseConnectionAction(connection),
                                "Пожалуйста подождите, идет подключение к базе данных...");

                        process(progress, new CallbackAction() {
                            @Override
                            public void apply() throws Exception {
                                QueryFrame dbframe = new QueryFrame(JMainFrame.this, connection);
                                dbframe.setVisible(true);
                            }
                        });

                    } catch (Throwable e) {
                        JOptionPane.showMessageDialog(JMainFrame.this,
                                e.getMessage(),
                                "", JOptionPane.WARNING_MESSAGE);
                    }
                }
                conn.dispose();
            }
        });
        //---------------------------------------------
        JMenuItem urlMenu = new JMenuItem("Загрузить данные из сети");
        urlMenu.setAccelerator(KeyStroke.getKeyStroke("ctrl shift N"));
        fileMenu.addSeparator();
        fileMenu.add(urlMenu);
        urlMenu.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent evt) {
                String path = (String) JOptionPane.showInputDialog(JMainFrame.this,
                        "URL файла:",
                        "Загрузка данных из сети", JOptionPane.INFORMATION_MESSAGE, null,
                        null, "http://kt.ijs.si/Branax/Repository/WEKA/Iris.xls");
                if (path != null) {
                    try {
                        //http://kt.ijs.si/Branax/Repository/WEKA/Iris.xls
                        URLLoader loader = new URLLoader(new DataLoaderImpl(new URL(path.trim())));
                        LoadDialog progress = new LoadDialog(JMainFrame.this,
                                loader,
                                "Пожалуйста подождите, идет загрузка данных...");

                        process(progress, new CallbackAction() {
                            @Override
                            public void apply() throws Exception {
                                createDataFrame(loader.data());
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(JMainFrame.this, e.getMessage(),
                                null, JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        //------------------------------------------------------
        JMenuItem loadModelMenu = new JMenuItem("Загрузить модель");
        loadModelMenu.setAccelerator(KeyStroke.getKeyStroke("ctrl M"));
        fileMenu.addSeparator();
        fileMenu.add(loadModelMenu);
        loadModelMenu.addActionListener(new ActionListener() {
            
            OpenModelChooser fileChooser;
            
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (fileChooser == null) {
                        fileChooser = new OpenModelChooser();
                    }
                    File file = fileChooser.openFile(JMainFrame.this);
                    if (file != null) {
                        ModelLoader loader = new ModelLoader(file);
                        LoadDialog progress = new LoadDialog(JMainFrame.this,
                                loader,
                                "Пожалуйста подождите, идет загрузка модели...");

                        process(progress, new CallbackAction() {
                            @Override
                            public void apply() throws Exception {
                                ModelDescriptor model = loader.model();
                                resultsHistory.createResultFrame(model.description, model.classifier,
                                        model.data, model.evaluation, model.digits);
                            }
                        });

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(JMainFrame.this, e.getMessage(),
                            null, JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JMenuItem generatorMenu = new JMenuItem("Генерация выборки");
        generatorMenu.setAccelerator(KeyStroke.getKeyStroke("ctrl shift G"));
        fileMenu.addSeparator();
        fileMenu.add(generatorMenu);
        generatorMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                DataGeneratorDialog dialog = new DataGeneratorDialog(JMainFrame.this);
                dialog.setVisible(true);
                if (dialog.dialogResult()) {
                    try {
                        DataGeneratorLoader loader = new DataGeneratorLoader(dialog.getDataGenerator());
                        LoadDialog progress = new LoadDialog(JMainFrame.this,
                                loader,
                                "Пожалуйста подождите, идет генерация данных...");

                        process(progress, new CallbackAction() {
                            @Override
                            public void apply() throws Exception {
                                createDataFrame(loader.getResult());
                            }
                        });

                    }
                    catch (Throwable ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(JMainFrame.this, ex.getMessage(),
                                null, JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        //---------------------------------------------
        JMenuItem exitMenu = new JMenuItem("Выход");
        fileMenu.addSeparator();
        fileMenu.add(exitMenu);
        //-------------------------------------------------
        exitMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                JMainFrame.this.closeWindow();
            }
        });
        //-------------------------------
        JMenuItem aboutProgrammMenu = new JMenuItem("О программе");
        JMenuItem reference = new JMenuItem("Показать справку");
        reference.setAccelerator(KeyStroke.getKeyStroke("F1"));
        //-------------------------------------------------
        aboutProgrammMenu.addActionListener(new ActionListener() {
            
            AboutProgramFrame frame;
            
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (frame == null) {
                    frame = new AboutProgramFrame(JMainFrame.this);
                }
                frame.setVisible(true);
            }
        });
        //-------------------------------------------------
        reference.addActionListener(new ActionListener() {
            
            Reference ref;
            
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (ref == null) {
                        ref = new Reference();
                    }
                    ref.openReference();
                } catch (Throwable e) {
                    JOptionPane.showMessageDialog(JMainFrame.this, e.getMessage(),
                            null, JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        //-----------------------------------------------
        referenceMenu.add(aboutProgrammMenu);
        referenceMenu.add(reference);
        //---------------------------------------------------
        JMenuItem aNeuralMenu = new JMenuItem("Автоматическое построение: нейронные сети");
        JMenuItem aHeteroEnsMenu = new JMenuItem("Автоматическое построение: неоднородный ансамблевый алгоритм");
        JMenuItem aRndSubspMenu = new JMenuItem("Автоматическое построение: модифицированный неоднородный ансамблевый алгоритм");
        JMenuItem aAdaBoostMenu = new JMenuItem("Автоматическое построение: алгоритм AdaBoost");
        JMenuItem aStackingMenu = new JMenuItem("Автоматическое построение: алгоритм Stacking");
        //--------------------------------------------------
        aNeuralMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    try {
                        ActivationFunction[] func = {new LogisticFunction(),
                            new TanhFunction(), new SineFunction(), new ExponentialFunction(),
                            new LogisticFunction(2)};
                        AutomatedNeuralNetwork net
                                = new AutomatedNeuralNetwork(100, func, data(), new NeuralNetwork(data()));
                        ExperimentFrame exp = new AutomatedNeuralNetworkFrame(net, JMainFrame.this, maximumFractionDigits);
                        exp.setVisible(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(JMainFrame.this,
                                e.getMessage(),
                                "", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        //--------------------------------------------------
        aRndSubspMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    try {
                        createEnsembleExperiment(new ModifiedHeterogeneousClassifier(null), aRndSubspMenu.getText(), data());
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(JMainFrame.this,
                                e.getMessage(),
                                "", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        //--------------------------------------------------
        aHeteroEnsMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    try {
                        createEnsembleExperiment(new HeterogeneousClassifier(null), aHeteroEnsMenu.getText(), data());
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(JMainFrame.this,
                                e.getMessage(),
                                "", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        //--------------------------------------------------
        aAdaBoostMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    try {
                        createEnsembleExperiment(new AdaBoostClassifier(null), aAdaBoostMenu.getText(), data());
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(JMainFrame.this,
                                e.getMessage(),
                                "", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        //--------------------------------------------------
        aStackingMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    try {
                        createStackingExperiment(new StackingClassifier(), aStackingMenu.getText(), data());
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(JMainFrame.this,
                                e.getMessage(),
                                "", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        //----------------------------------------
        dataMinerMenu.add(aNeuralMenu);
        dataMinerMenu.add(aHeteroEnsMenu);
        dataMinerMenu.add(aRndSubspMenu);
        dataMinerMenu.add(aAdaBoostMenu);
        dataMinerMenu.add(aStackingMenu);
        //-------------------------------
        JMenu classifiersMenu = new JMenu("Индувидуальные алгоритмы");
        JMenu ensembleMenu = new JMenu("Ансамблевые алгоритмы");
        algorithmsMenu.add(classifiersMenu);
        algorithmsMenu.add(ensembleMenu);
        //------------------------------
        JMenu treesMenu = new JMenu("Деревья решений");
        classifiersMenu.add(treesMenu);
        JMenuItem id3Item = new JMenuItem(ClassifiersNames.ID3);
        JMenuItem c45Item = new JMenuItem(ClassifiersNames.C45);
        JMenuItem cartItem = new JMenuItem(ClassifiersNames.CART);
        JMenuItem chaidItem = new JMenuItem(ClassifiersNames.CHAID);
        id3Item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    createTreeOptionDialog(ClassifiersNames.ID3, new ID3());
                }
            }
        });
        c45Item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    createTreeOptionDialog(ClassifiersNames.C45, new C45());
                }
            }
        });
        cartItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    createTreeOptionDialog(ClassifiersNames.CART, new CART());
                }
            }
        });
        chaidItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    createTreeOptionDialog(ClassifiersNames.CHAID, new CHAID());
                }
            }
        });
        treesMenu.add(id3Item);
        treesMenu.add(c45Item);
        treesMenu.add(cartItem);
        treesMenu.add(chaidItem);
        //------------------------------------------------------------------
        JMenuItem logisticItem = new JMenuItem(ClassifiersNames.LOGISTIC);
        classifiersMenu.add(logisticItem);
        logisticItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    try {
                        Instances set = data();
                        LogisticOptionsDialogBase frame = new LogisticOptionsDialogBase(JMainFrame.this,
                                ClassifiersNames.LOGISTIC,
                                new Logistic(), set);
                        createAndShowLoaderFrame(frame);
                        //------------------------------------
                    } catch (Throwable e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(JMainFrame.this,
                                e.getMessage(),
                                "", JOptionPane.WARNING_MESSAGE);
                    }

                }
            }
        });
        //------------------------------------------------------------------
        JMenuItem mlpItem = new JMenuItem(ClassifiersNames.NEURAL_NETWORK);
        classifiersMenu.add(mlpItem);
        mlpItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    try {
                        Instances set = data();
                        NetworkOptionsDialog frame = new NetworkOptionsDialog(JMainFrame.this,
                                ClassifiersNames.NEURAL_NETWORK,
                                new NeuralNetwork(set), set);
                        frame.showDialog();
                        computeResults(frame, networkBuildingProgressTitle);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(JMainFrame.this,
                                e.getMessage(),
                                "", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        //------------------------------------------------------------------
        JMenuItem knnItem = new JMenuItem(ClassifiersNames.KNN);
        classifiersMenu.add(knnItem);
        knnItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    try {
                        KNNOptionDialog frame = new KNNOptionDialog(JMainFrame.this, ClassifiersNames.KNN,
                                new KNearestNeighbours(), data());
                        frame.showDialog();
                        if (frame.dialogResult()) {
                            Evaluation e = createEvaluation(frame.classifier(), frame.data());
                            resultsHistory.createResultFrame(frame.getTitle(), frame.classifier(),
                                    frame.data(), e, maximumFractionDigits);
                        }
                        frame.dispose();
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(JMainFrame.this,
                                e.getMessage(),
                                "", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        //----------------------------------
        JMenuItem heterogeneousItem = new JMenuItem(EnsemblesNames.HETEROGENEOUS_ENSEMBLE);
        heterogeneousItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    createEnsembleOptionDialog(EnsemblesNames.HETEROGENEOUS_ENSEMBLE,
                            new HeterogeneousClassifier(null), true);
                }
            }
        });
        ensembleMenu.add(heterogeneousItem);
        //----------------------------------
        JMenuItem boostingItem = new JMenuItem(EnsemblesNames.BOOSTING);
        boostingItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    createEnsembleOptionDialog(EnsemblesNames.BOOSTING,
                            new AdaBoostClassifier(null), false);
                }
            }
        });
        //----------------------------------
        JMenuItem rndSubSpaceItem = new JMenuItem(EnsemblesNames.MODIFIED_HETEROGENEOUS_ENSEMBLE);
        rndSubSpaceItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    createEnsembleOptionDialog(EnsemblesNames.MODIFIED_HETEROGENEOUS_ENSEMBLE,
                            new ModifiedHeterogeneousClassifier(null), true);
                }
            }
        });
        ensembleMenu.add(rndSubSpaceItem);
        ensembleMenu.add(boostingItem);
        //----------------------------------
        JMenuItem rndForestsItem = new JMenuItem(EnsemblesNames.RANDOM_FORESTS);
        rndForestsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    try {
                        Instances data = data();
                        RandomForestsOptionDialog frame
                                = new RandomForestsOptionDialog(JMainFrame.this, EnsemblesNames.RANDOM_FORESTS,
                                        new RandomForests(data), data);
                        frame.showDialog();
                        computeResults(frame, ensembleBuildingProgressTitle);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(JMainFrame.this,
                                e.getMessage(),
                                "", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        ensembleMenu.add(rndForestsItem);
        //-------------------------------------------------
        JMenuItem stackingItem = new JMenuItem(EnsemblesNames.STACKING);
        stackingItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    try {
                        StackingOptionsDialog frame
                                = new StackingOptionsDialog(JMainFrame.this, EnsemblesNames.STACKING,
                                        new StackingClassifier(), data());
                        createAndShowLoaderFrame(frame);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(JMainFrame.this,
                                e.getMessage(),
                                "", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        ensembleMenu.add(stackingItem);

        JMenuItem historyMenu = new JMenuItem("История классификаторов");
        historyMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                resultHistoryFrame.setVisible(true);
            }
        });
        serviceMenu.add(historyMenu);

        attrStatisticsMenu = new JMenuItem("Статистика по атрибутам");
        attrStatisticsMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    try {
                        AttributesStatisticsFrame frame =
                                new AttributesStatisticsFrame(data(), JMainFrame.this, maximumFractionDigits);
                        frame.setVisible(true);
                    }
                    catch (Throwable ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(JMainFrame.this,
                                ex.getMessage(),
                                "", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        serviceMenu.add(attrStatisticsMenu);
        //----------------------------------
        this.setJMenuBar(menu);
    }
    
    private void computeResults(BaseOptionsDialog frame, String msg) throws Exception {
        if (frame.dialogResult()) {
            try {
                IterativeBuilder itCls = itClassifier((Iterable) frame.classifier(),
                        frame.data());

                ClassifierBuilderDialog progress
                        = new ClassifierBuilderDialog(JMainFrame.this, itCls, msg);

                process(progress, new CallbackAction() {
                    @Override
                    public void apply() throws Exception {
                        resultsHistory.createResultFrame(frame.getTitle(), frame.classifier(),
                                frame.data(), itCls.evaluation(), maximumFractionDigits);
                    }
                });

            } catch (Throwable e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(JMainFrame.this,
                        e.getMessage(),
                        "", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    private boolean dataValidated() {
        try {
            selectedPanel().check();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(JMainFrame.this,
                    e.getMessage(),
                    "", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
    
    private void createTreeOptionDialog(String title, DecisionTreeClassifier tree) {
        try {
            DecisionTreeOptionsDialog frame
                    = new DecisionTreeOptionsDialog(JMainFrame.this, title,
                            tree, data());
            createAndShowLoaderFrame(frame);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(JMainFrame.this,
                    e.getMessage(),
                    "", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void createEnsembleOptionDialog(String title, AbstractHeterogeneousClassifier ens,
            boolean sample) {
        try {
            EnsembleOptionsDialog frame
                    = new EnsembleOptionsDialog(JMainFrame.this, title, ens,
                            data());
            frame.setSampleEnabled(sample);
            frame.showDialog();
            computeResults(frame, ensembleBuildingProgressTitle);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(JMainFrame.this,
                    e.getMessage(),
                    "", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private IterativeBuilder itClassifier(Iterable model, Instances data)
            throws Exception {
        IterativeBuilder itCls = null;
        switch (testingSetFrame.getTestingSetType()) {
            case TestMethod.TRAINING_SET:
                itCls = model.getIterativeBuilder(data);
                break;
            
            case TestMethod.CROSS_VALIDATION:
                itCls = new CVIterativeBuilder(model, data, testingSetFrame.numFolds(), testingSetFrame.numValids());
                break;
        }
        return itCls;
    }
    
    private Evaluation createEvaluation(Classifier model, Instances data) throws Exception {
        Evaluation e = new Evaluation(data);
        //---------------------------------------
        switch (testingSetFrame.getTestingSetType()) {
            case TestMethod.TRAINING_SET:
                model.buildClassifier(data);
                e.evaluateModel(model, data);
                break;
            
            case TestMethod.CROSS_VALIDATION:
                e.kCrossValidateModel(AbstractClassifier.makeCopy(model), data,
                        testingSetFrame.numFolds(), testingSetFrame.numValids(), new Random());
                model.buildClassifier(data);
                break;
        }
        //---------------------------------------
        return e;
    }
    
    private void createEnsembleExperiment(AbstractHeterogeneousClassifier classifier,
            String title, Instances data) throws Exception {
        classifier.setClassifiersSet(ClassifiersSetBuilder.createClassifiersSet(data));
        AutomatedHeterogeneousEnsemble exp = new AutomatedHeterogeneousEnsemble(classifier, data);
        AutomatedHeterogeneousEnsembleFrame frame
                = new AutomatedHeterogeneousEnsembleFrame(title, exp, this, maximumFractionDigits);
        frame.setVisible(true);
    }
    
    private void createStackingExperiment(StackingClassifier classifier,
            String title, Instances data) throws Exception {
        classifier.setClassifiers(ClassifiersSetBuilder.createClassifiersSet(data));
        AutomatedStacking exp = new AutomatedStacking(classifier, data);
        AutomatedStackingFrame frame
                = new AutomatedStackingFrame(title, exp, this, maximumFractionDigits);
        frame.setVisible(true);
    }
    
} //End of class JMainFrame
