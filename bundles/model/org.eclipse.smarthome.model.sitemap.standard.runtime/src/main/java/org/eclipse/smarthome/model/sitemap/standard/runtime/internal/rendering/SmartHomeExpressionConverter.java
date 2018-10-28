/**
 * Copyright (c) 2014,2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.smarthome.model.sitemap.standard.runtime.internal.rendering;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.epsilon.eol.dom.EagerOperatorExpression;
import org.eclipse.epsilon.eol.dom.EqualsOperatorExpression;
import org.eclipse.epsilon.eol.dom.Expression;
import org.eclipse.epsilon.eol.dom.GreaterEqualOperatorExpression;
import org.eclipse.epsilon.eol.dom.GreaterThanOperatorExpression;
import org.eclipse.epsilon.eol.dom.LessEqualOperatorExpression;
import org.eclipse.epsilon.eol.dom.LessThanOperatorExpression;
import org.eclipse.epsilon.eol.dom.NameExpression;
import org.eclipse.epsilon.eol.dom.NotEqualsOperatorExpression;
import org.eclipse.epsilon.eol.dom.RealLiteral;
import org.eclipse.epsilon.eol.dom.StringLiteral;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.items.ItemNotFoundException;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.epsilon.eol.dom.ExpressionConverter;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.epsilon.eol.dom.FunctionExpression;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.epsilon.eol.dom.MonadExpression;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.epsilon.eol.dom.TaggedUnionExpression;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.epsilon.eol.dom.ValueReferenceExpression;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.reference.MappingReference;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.reference.ValueReference;
import org.eclipse.smarthome.model.sitemap.rendering.runtime.reference.types.ItemValueReference;
import org.eclipse.smarthome.model.sitemap.standard.definition.ConstantSitemapExpression;
import org.eclipse.smarthome.model.sitemap.standard.definition.ExpressionType;
import org.eclipse.smarthome.model.sitemap.standard.definition.FunctionSitemapExpression;
import org.eclipse.smarthome.model.sitemap.standard.definition.MonadSitemapExpression;
import org.eclipse.smarthome.model.sitemap.standard.definition.PredicateSitemapExpression;
import org.eclipse.smarthome.model.sitemap.standard.definition.SitemapExpression;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

/**
 * Component implementing the {@link ExpressionConverter} for standard sitemaps.
 *
 * @author Flavio Costa - Initial contribution.
 */
@Component(service = ExpressionConverter.class, property = "sitemap.type=smarthome")
public class SmartHomeExpressionConverter implements ExpressionConverter {

    /**
     * Separator for function-subfunctions.
     */
    private static final String FUNCTION_NAME_SEPARATOR = "-";

    /**
     * ESH item registry.
     */
    private ItemRegistry itemRegistry;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    public void setItemRegistry(ItemRegistry itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    public void unsetItemRegistry() {
        this.itemRegistry = null;
    }

    /**
     * Identifies the type of an expression.
     *
     * @param expr Expression.
     * @return Expression type.
     */
    private ExpressionType getType(SitemapExpression expr) {
        if (expr == null) {
            return null;
        }
        ExpressionType type = ExpressionType.get(expr.eClass().getName());
        if (type == null) {
            throw new UnsupportedOperationException("Type not supported: " + expr.eClass().getName());
        }
        return type;
    }

    @Override
    public Expression convert(ValueReference<?, ?> valueRef, Object object) {
        if (object instanceof String) {
            return new StringLiteral((String) object);
        }
        if (object instanceof ValueReference) {
            return new ValueReferenceExpression((ValueReference<?, ?>) object);
        }
        if (object instanceof SitemapExpression) {
            return convert(valueRef, (SitemapExpression) object);
        }
        if (object instanceof MappingReference) {
            MappingReference<Item, State> mapRef = (MappingReference<Item, State>) object;
            Map<Expression, Expression> mappings = new HashMap<>();
            for (Entry<String, ?> e : mapRef.getMappings().entrySet()) {
                mappings.put(convert(valueRef, e.getKey()), convert(valueRef, e.getValue()));
            }
            return new TaggedUnionExpression(new ValueReferenceExpression(mapRef.getValue()), mappings);
        }
        throw new UnsupportedOperationException("Cannot convert object of type " + object.getClass());
    }

    /**
     * Converts a standard sitemap expression into an EOL expression.
     *
     * @param valueRef Implicit value reference for the expression.
     * @param expr Sitemap expression.
     * @return Corresponding EOL expression.
     */
    public Expression convert(ValueReference<?, ?> valueRef, SitemapExpression expr) {
        try {
            ExpressionType exprType = getType(expr);
            if (exprType != null) {
                switch (exprType) {
                    case CONSTANT:
                        return asEolLiteral((ConstantSitemapExpression) expr);
                    case FUNCTION:
                        return asEolFunction(valueRef, (FunctionSitemapExpression) expr);
                    case MONAD:
                        return asEolMonad(valueRef, (MonadSitemapExpression) expr);
                    case PREDICATE:
                        return asEolPredicate(valueRef, (PredicateSitemapExpression) expr);
                    default:
                        throw new IllegalArgumentException("Unsupported expression type: " + exprType);
                }
            }
            return null;
        } catch (EolRuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts a standard sitemap constant expression into an EOL expression.
     *
     * @param expr Sitemap expression.
     * @return Corresponding EOL expression.
     */
    private Expression asEolLiteral(ConstantSitemapExpression expr) {
        String value = expr.getValue().replaceAll("^\"|\"$", "");
        return new StringLiteral(value);
    }

    /**
     * Converts a standard sitemap function expression into an EOL expression.
     *
     * @param valueRef Implicit value reference for the expression.
     * @param expr Sitemap expression.
     * @return Corresponding EOL expression.
     */
    private FunctionExpression asEolFunction(ValueReference<?, ?> valueRef, FunctionSitemapExpression expr) {
        List<Expression> args = expr.getArguments().stream().map(a -> convert(valueRef, a))
                .collect(Collectors.toList());
        String operationName = expr.getName();
        String[] nameParts = operationName.split(FUNCTION_NAME_SEPARATOR, 2);
        if (nameParts.length > 1) {
            // function name is "function-subfunction", so we actually call
            // "function" passing the "subfunction" as the first argument
            operationName = nameParts[0];
            args.add(0, new StringLiteral(nameParts[1]));
        }
        return new FunctionExpression(new NameExpression(operationName), args.toArray(new Expression[args.size()]));
    }

    /**
     * Converts a standard sitemap monad expression into an EOL expression.
     *
     * @param valueRef Implicit value reference for the expression.
     * @param expr Sitemap expression.
     * @return Corresponding EOL expression.
     */
    private MonadExpression asEolMonad(ValueReference<?, ?> valueRef, MonadSitemapExpression expr)
            throws EolRuntimeException {
        SitemapExpression state = expr.getState();
        Expression stateExpression;
        if (state instanceof PredicateSitemapExpression) {
            stateExpression = asEolPredicate(valueRef, (PredicateSitemapExpression) state);
        } else if (state instanceof ConstantSitemapExpression) {
            stateExpression = asEolPredicate(valueRef, (ConstantSitemapExpression) state);
        } else {
            throw new UnsupportedOperationException("Invalid monad state: " + state);
        }
        StringLiteral resultExpression = new StringLiteral(expr.getResult());
        return new MonadExpression(stateExpression, resultExpression);
    }

    /**
     * Converts a standard sitemap constant expression into an EOL expression as a predicate.
     *
     * @param valueRef Implicit value reference for the expression.
     * @param expr Sitemap expression.
     * @return Corresponding EOL expression.
     */
    private EagerOperatorExpression asEolPredicate(ValueReference<?, ?> valueRef, ConstantSitemapExpression expr) {
        Expression firstOperand = convert(valueRef, valueRef); // first operand is the implicit reference
        Expression secondOperand = convert(valueRef, expr); // second operand is the constant value
        return new EqualsOperatorExpression(firstOperand, secondOperand);
    }

    /**
     * Converts a standard sitemap predicate expression into an EOL expression.
     *
     * @param valueRef Implicit value reference for the expression.
     * @param expr Sitemap expression.
     * @return Corresponding EOL expression.
     */
    private EagerOperatorExpression asEolPredicate(ValueReference<?, ?> valueRef, PredicateSitemapExpression expr)
            throws EolRuntimeException {
        // Sitemap sitemap =
        // Thing thing = thingRegistry.get(new ThingUID(pred.getHref()));
        Expression firstOperand;
        ValueReference<?, ?> effectiveValueRef;
        try {
            String explicitReference = expr.getHref();
            if (explicitReference == null) {
                // convert the implicit reference
                effectiveValueRef = valueRef;
            } else {
                // there is an explicit reference, use it (and, for now, assume it is an Item)
                effectiveValueRef = new ItemValueReference(itemRegistry.getItem(explicitReference));
            }
        } catch (ItemNotFoundException e) {
            throw new EolRuntimeException(e.getMessage());
        }
        firstOperand = convert(valueRef, effectiveValueRef);

        // Handle the sign
        String value;
        if (expr.getSign() != null) {
            value = expr.getSign() + expr.getValue();
        } else {
            value = expr.getValue();
        }
        // Remove quotes - this occurs in some instances where multiple types
        // are defined in the Xtext definitions
        String parsedValue = value.replace("^\"|\"$", "");
        Object referenceType = effectiveValueRef.getValue();
        Expression secondOperand;
        if (referenceType instanceof Number) {
            secondOperand = new RealLiteral(Double.parseDouble(parsedValue));
        } else {
            // by default, treat the value as a String
            secondOperand = new StringLiteral(parsedValue);
        }

        EagerOperatorExpression predicate;
        switch (expr.getOperator()) {
            case EQUAL:
                predicate = new EqualsOperatorExpression(firstOperand, secondOperand);
                break;
            case LTE:
                predicate = new LessEqualOperatorExpression(firstOperand, secondOperand);
                break;
            case LESS:
                predicate = new LessThanOperatorExpression(firstOperand, secondOperand);
                break;
            case GTE:
                predicate = new GreaterEqualOperatorExpression(firstOperand, secondOperand);
                break;
            case GREATER:
                predicate = new GreaterThanOperatorExpression(firstOperand, secondOperand);
                break;
            case NOT:
            case NOTEQUAL:
                predicate = new NotEqualsOperatorExpression(firstOperand, secondOperand);
                break;
            default:
                throw new UnsupportedOperationException("Invalid operator: " + expr.getOperator());
        }

        firstOperand.setParent(predicate);
        secondOperand.setParent(secondOperand);
        predicate.getChildren().add(firstOperand);
        predicate.getChildren().add(secondOperand);
        return predicate;
    }
}
