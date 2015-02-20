/**
 * Copyright 2006 Bertoli Marco
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gamingmesh.jobs.resources.jfep;

/**
 * <p><b>Name:</b> VariableNode</p> 
 * <p><b>Description:</b> 
 * A node holding a double variable.
 * </p>
 * <p><b>Date:</b> 08/dic/06
 * <b>Time:</b> 15:56:59</p>
 * @author Bertoli Marco
 * @version 1.0
 */
public class VariableNode implements ExpressionNode {
    /** Value of the variable */
    protected double value;
    /** True if variable was not initialized */
    protected boolean error;
    /** Name of the variable */
    protected String name;
    /** An empty array with children */
    protected ExpressionNode[] children = new ExpressionNode[0];
    
    /**
     * Creates a new variable node with given name.
     * @param name name of the variable
     * @param error throws an exception if value is get but variable
     * is not initialized. Otherwise 0.0 is returned.
     */
    public VariableNode(String name, boolean error) {
        this.name = name;
        value = 0.0;
        this.error = error;
    }
    
    /* (non-Javadoc)
     * @see jmt.engine.math.parser.ExpressionNode#count()
     */
    public int count() {
        return 1;
    }

    /* (non-Javadoc)
     * @see jmt.engine.math.parser.ExpressionNode#getDepth()
     */
    public int getDepth() {
        return 1; // This is a leaf node
    }

    /* (non-Javadoc)
     * @see jmt.engine.math.parser.ExpressionNode#getSubtype()
     */
    public String getSubtype() {
        return name;
    }

    /* (non-Javadoc)
     * @see jmt.engine.math.parser.ExpressionNode#getType()
     */
    public int getType() {
        return ExpressionNode.VARIABLE_NODE;
    }

    /* (non-Javadoc)
     * @see jmt.engine.math.parser.ExpressionNode#getValue()
     */
    public double getValue() {
        if (!error)
            return value;
        else
            throw new EvaluationException("Variable '" + name + "' was not initialized.");
    }

    /* (non-Javadoc)
     * @see jmt.engine.math.parser.ExpressionNode#setVariable(java.lang.String, double)
     */
    public void setVariable(String name, double value) {
        if (this.name.equals(name)) {
            this.value = value;
            error = false;
        }
    }
    
    /* (non-Javadoc)
     * @see org.mbertoli.jfep.ExpressionNode#getChildrenNodes()
     */
    public ExpressionNode[] getChildrenNodes() {
        return children;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        VariableNode node = new VariableNode(name, error);
        node.value = value;
        return node;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getSubtype();
    }
}
