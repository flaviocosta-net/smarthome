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
package org.eclipse.smarthome.model.sitemap.rendering.runtime.epsilon.eol.execute.context;

import org.eclipse.epsilon.eol.dom.NameExpression;
import org.eclipse.epsilon.eol.execute.context.FrameStack;
import org.eclipse.epsilon.eol.execute.context.Variable;
import org.eclipse.epsilon.eol.types.EolAnyType;
import org.eclipse.epsilon.etl.execute.context.IEtlContext;

/**
 * Provides convenient methods to manipulate variables in the call frame of an ETL transformation.
 *
 * @author Flavio Costa - Initial contribution
 */
public class EolFrame {

    /**
     * Common variables used in sitemap transformations.
     */
    public static enum VarName {
    REFERENCE("reference"),
    CONVERTER("converter"),
    VALUE_REF("valueRef"),
    SITEMAP("currentSitemap"),
    CONTAINER("currentContainer"),
    COMPONENT("currentComponent"),
    STATES("componentStates");

        /**
         * Name of the variable.
         */
        private final String variableName;

        /**
         * Creates a new Enum instance.
         *
         * @param variableName Name of the variable.
         */
        VarName(String variableName) {
            this.variableName = variableName;
        }

        @Override
        public String toString() {
            return this.variableName;
        }

        /**
         * Converts the variable name into an EOL name expression.
         *
         * @return NameExpression instance for this variable's name,
         */
        public NameExpression toNameExpression() {
            return new NameExpression(this.variableName);
        }
    }

    /**
     * Frame stack for the EOL execution.
     */
    private final FrameStack frame;

    /**
     * Creates an instance of this class based on a given frame stack.
     *
     * @param frameStack Frame stack for the EOL execution.
     */
    public EolFrame(FrameStack frameStack) {
        this.frame = frameStack;
    }

    /**
     * Creates an instance of this class based on the current frame stack of a given ETL context.
     *
     * @param context Execution context.
     * @return New object for this class.
     */
    public static EolFrame on(IEtlContext context) {
        return new EolFrame(context.getFrameStack());
    }

    /**
     * Returns an EOL variable from the current frame stack.
     *
     * @param varName Name of the variable.
     * @return Variable instance, or null if no such variable is defined in the current frame stack.
     */
    public Variable get(VarName varName) {
        return frame.get(varName.toString());
    }

    /**
     * Sets an EOL variable into the current frame stack.
     *
     * @param varName Name of the variable.
     * @param value Value to be set to the variable.
     */
    public void put(VarName varName, Object value) {
        frame.put(create(varName, value));
    }

    /**
     * Create a new EOL variable instance.
     *
     * @param varName Name of the variable.
     * @return New variable instance.
     */
    public static Variable create(VarName varName) {
        return create(varName, null, false);
    }

    /**
     * Create a new EOL read-only variable instance, and set its value.
     *
     * @param varName Name of the variable.
     * @param value Value of the variable.
     * @return New variable instance.
     */
    public static Variable create(VarName varName, Object value) {
        return create(varName, value, true);
    }

    /**
     * Create a new EOL variable instance, with the option to set its value and define whether it will be read-only or
     * not.
     *
     * @param varName Name of the variable.
     * @param value Value of the variable.
     * @param readOnly Whether the variable will be read-only or not.
     * @return New variable instance.
     */
    public static Variable create(VarName varName, Object value, boolean readOnly) {
        return new Variable(varName.toString(), value, EolAnyType.Instance, readOnly);
    }
}
