package eca.gui.diagram;

import eca.core.DescriptiveEnum;

/**
 * Diagram type enum.
 *
 * @author Roman Batygin
 */
public enum DiagramType implements DescriptiveEnum {

    /**
     * Frequency diagram
     */
    FREQUENCY_DIAGRAM("Гистограмма частот") {
        @Override
        public <T> T handle(DiagramTypeVisitor<T> diagramTypeVisitor) {
            return diagramTypeVisitor.caseFrequencyDiagram();
        }
    },

    /**
     * Pie diagram
     */
    PIE_DIAGRAM("Круговая диаграмма") {
        @Override
        public <T> T handle(DiagramTypeVisitor<T> diagramTypeVisitor) {
            return diagramTypeVisitor.casePieDiagram();
        }
    },

    /**
     * Pie 3D diagram
     */
    PIE_3D_DIAGRAM("Круговая диаграмма 3D") {
        @Override
        public <T> T handle(DiagramTypeVisitor<T> diagramTypeVisitor) {
            return diagramTypeVisitor.casePie3dDiagram();
        }
    };

    private String description;

    DiagramType(String description) {
        this.description = description;
    }

    /**
     * Returns diagram description.
     *
     * @return diagram description
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Returns diagram description.
     *
     * @return diagram description
     */
    public static String[] getDescriptions() {
        DiagramType[] values = values();
        String[] descriptions = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            descriptions[i] = values[i].getDescription();
        }
        return descriptions;
    }

    public abstract <T> T handle(DiagramTypeVisitor<T> diagramTypeVisitor);
}
