package eca.gui.diagram;

/**
 * Diagram type visitor interface.
 *
 * @param <T> - generic type
 * @author Roman Batygin
 */
public interface DiagramTypeVisitor<T> {

    T caseFrequencyDiagram();

    T casePieDiagram();

    T casePie3dDiagram();
}
