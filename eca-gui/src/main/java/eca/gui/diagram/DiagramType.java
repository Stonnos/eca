package eca.gui.diagram;

/**
 * Diagram type enum.
 *
 * @author Roman Batygin
 */
public enum DiagramType {

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

    /**
     * Finds diagram type by description
     *
     * @param description description string.
     * @return {@link DiagramType} object
     */
    public static DiagramType findByDescription(String description) {
        for (DiagramType distanceType : values()) {
            if (distanceType.getDescription().equals(description)) {
                return distanceType;
            }
        }
        return null;
    }

    public abstract <T> T handle(DiagramTypeVisitor<T> diagramTypeVisitor);
}
