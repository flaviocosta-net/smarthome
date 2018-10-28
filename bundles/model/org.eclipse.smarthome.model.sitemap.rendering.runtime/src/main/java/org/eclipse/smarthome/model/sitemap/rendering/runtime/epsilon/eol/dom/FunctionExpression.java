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
package org.eclipse.smarthome.model.sitemap.rendering.runtime.epsilon.eol.dom;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.eol.dom.Expression;
import org.eclipse.epsilon.eol.dom.NameExpression;
import org.eclipse.epsilon.eol.dom.Operation;
import org.eclipse.epsilon.eol.dom.OperationCallExpression;
import org.eclipse.epsilon.eol.dom.Parameter;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.context.IEolContext;
import org.eclipse.epsilon.eol.types.EolCollectionType;

/**
 * EOL expression for functions. Functions have a name and generate a result based on provided parameters.
 *
 * @author Flavio Costa - Initial contribution
 */
public class FunctionExpression extends OperationCallExpression {

    /**
     * Function name prefix. New or custom functions can be created as an operation on an ETL script named with this
     * prefix, before the name the function will be referred to.
     */
    private static final String FUNCTION_PREFIX = "sitemap_";

    /**
     * Creates a new function expression.
     *
     * @param nameExpression Function name.
     * @param parameterExpressions Function parameters.
     */
    public FunctionExpression(NameExpression nameExpression, Expression... parameterExpressions) {
        super(null, new NameExpression(FUNCTION_PREFIX + nameExpression.getName()), parameterExpressions);
        this.nameExpression.setParent(this);
        this.getChildren().add(this.nameExpression);
        for (Expression pe : this.parameterExpressions) {
            pe.setParent(this);
            this.getChildren().add(pe);
        }
    }

    @Override
    public Object execute(IEolContext context) throws EolRuntimeException {
        // find the underlying operation by the name only, regardless of the parameters provided
        Operation helper = ((IEolModule) context.getModule()).getOperations().getOperation(nameExpression.getName());
        if (helper == null) {
            // apparently the operation does not exist, but let's delegate to the
            // implementation in the superclass and see what happens
            return super.execute(context);
        }

        // object that has the target method
        Object targetObject = context.getExecutorFactory().execute(targetExpression, context);

        // parameters for the method call
        ArrayList<Object> parameterValues = new ArrayList<Object>();

        for (Expression parameter : parameterExpressions) {
            parameterValues.add(context.getExecutorFactory().execute(parameter, context));
        }

        // let's check if Collection auto-boxing is expected
        List<Parameter> formalParameters = helper.getFormalParameters();
        if (formalParameters.size() == 1) {
            if (formalParameters.get(0).getCompilationType() instanceof EolCollectionType) {
                // function expects just a single Collection, so we do an "auto-boxing" here
                ArrayList<Object> sequence = new ArrayList<>();
                sequence.add(parameterValues);
                parameterValues = sequence;
            }
        }

        return ((IEolModule) context.getModule()).getOperations().execute(targetObject, helper, parameterValues,
                context);
    }
}
