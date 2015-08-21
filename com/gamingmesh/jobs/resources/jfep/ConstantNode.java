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
 * <p><b>Name:</b> ConstantNode</p> 
 * <p><b>Description:</b> 
 * A constant value node
 * </p>
 * <p><b>Date:</b> 08/dic/06
 * <b>Time:</b> 15:35:24</p>
 * @author Bertoli Marco
 * @version 1.0
 */
public class ConstantNode implements ExpressionNode {
    /** List of built-in constant names */
    public static final String[] CONSTANTS = new String[] {"pi", "e"};
    /** List of built-in constant values */
    public static final double[] VALUES = new double[] {Math.PI, Math.E};
    /** Value of the constant */
    protected double constant;
    /** Name of the constant. Only if it's built-in */
    protected String name;
    /** An empty array with children */
    protected ExpressionNode[] children = new ExpressionNode[0];
    
    /**
     * Builds a constant node
     * @param constant constant to be put in node
     */
    public ConstantNode(double constant) {
        this.constant = constant;
        name = null;
    }
    
    /**
     * Builds a constant node, with an unique constant
     * @param name name of the constant in the CONSTANTS array
     */
    public ConstantNode(String name) {
        this.name = name;
        for (int i=0; i<CONSTANTS.length;i++)
            if (CONSTANTS[i].equals(name)) {
                constant = VALUES[i];
                return;
            }
        throw new IllegalArgumentException("Unrecognized constant");
    }

    /**
     * Builds a constant node, with an unique constant
     * @param pos position of the constant in the CONSTANTS array
     * @see ConstantNode#CONSTANTS
     */
    public ConstantNode(int pos) {
        this.name = CONSTANTS[pos];
        this.constant = VALUES[pos];
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
        // Checks if this is integer or double
        if (Math.floor(constant) == constant)
            return Long.toString(Math.round(constant));
        else
            return Double.toString(constant);
    }

    /* (non-Javadoc)
     * @see jmt.engine.math.parser.ExpressionNode#getType()
     */
    public int getType() {
        return ExpressionNode.CONSTANT_NODE;
    }

    /* (non-Javadoc)
     * @see jmt.engine.math.parser.ExpressionNode#getValue()
     */
    public double getValue() {
        return constant;
    }

    /* (non-Javadoc)
     * @see jmt.engine.math.parser.ExpressionNode#setVariable(java.lang.String, double)
     */
    public void setVariable(String name, double value) {
        // Nothing to be done here...
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
        return new ConstantNode(constant);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        if (name == null)
            return getSubtype();
        else
            return name;
    }
}
