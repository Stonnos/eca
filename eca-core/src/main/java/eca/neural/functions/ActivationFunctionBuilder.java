package eca.neural.functions;

/**
 * Activation function builder.
 *
 * @author Roman Batygin
 */
public class ActivationFunctionBuilder implements ActivationFunctionTypeVisitor<AbstractFunction> {

    @Override
    public AbstractFunction caseLogistic() {
        return new LogisticFunction();
    }

    @Override
    public AbstractFunction caseHyperbolicTangent() {
        return new HyperbolicTangentFunction();
    }

    @Override
    public AbstractFunction caseSinusoid() {
        return new SinusoidFunction();
    }

    @Override
    public AbstractFunction caseExponential() {
        return new ExponentialFunction();
    }

    @Override
    public AbstractFunction caseSoftSign() {
        return new SoftSignFunction();
    }

    @Override
    public AbstractFunction caseInverseSquareRootUnit() {
        return new IsruFunction();
    }

}
