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
 * <p><b>Name:</b> FunctionNode</p> 
 * <p><b>Description:</b> 
 * This node is used to evaluate every kind of function with a single parameter, like abs(...) or sin(...)
 * </p>
 * <p><b>Date:</b> 08/dic/06
 * <b>Time:</b> 16:31:14</p>
 * @author Bertoli Marco
 * @version 1.0
 */
public class FunctionNode implements ExpressionNode {
    /** List of supported functions */
    public static final String[] FUNCTIONS = new String[] {"-", "sin", "cos", "tan",
        "asin", "acos", "atan", "sinh", "cosh", "tanh", "asinh", "acosh", "atanh", 
        "ln", "log", "abs", "rand", "sqrt", "erf", "erfc", "gamma", "exp", "cot", "log2"};
    /** Child node */
    protected ExpressionNode child;
    /** Function of this node */
    protected int function;
    /** An array with children */
    protected ExpressionNode[] children;
    
    /**
     * Creates a function node.
     * @param child child node of this node
     * @param function function to be evaluated. This is the index in <code>FUNCTIONS</code> array
     * @see FunctionNode#FUNCTIONS
     */
    public FunctionNode(ExpressionNode child, int function) {
        this.child = child;
        this.function = function;
        children = new ExpressionNode[] {child};
    }
    
    /**
     * Creates a function node.
     * @param child child node of this node
     * @param function name of function to be evaluated.
     * @throws IllegalArgumentException if function is unsupported
     */
    public FunctionNode(ExpressionNode child, String function) throws IllegalArgumentException {
        this.child = child;
        this.function = -1;
        children = new ExpressionNode[] {child};
        for (int i=0; i<FUNCTIONS.length;i++) {
            if (FUNCTIONS[i].equals(function)) {
                this.function = i;
                break;
            }
        }
        if (this.function < 0)
            throw new IllegalArgumentException("Unrecognized function");
    }
    
    /* (non-Javadoc)
     * @see jmt.engine.math.parser.ExpressionNode#count()
     */
    public int count() {
        return 1 + child.count();
    }

    /* (non-Javadoc)
     * @see jmt.engine.math.parser.ExpressionNode#getDepth()
     */
    public int getDepth() {
        return 1 + child.getDepth();
    }

    /* (non-Javadoc)
     * @see jmt.engine.math.parser.ExpressionNode#getSubtype()
     */
    public String getSubtype() {
        return FUNCTIONS[function];
    }

    /* (non-Javadoc)
     * @see jmt.engine.math.parser.ExpressionNode#getType()
     */
    public int getType() {
        return ExpressionNode.FUNCTION_NODE;
    }

    /* (non-Javadoc)
     * @see jmt.engine.math.parser.ExpressionNode#getValue()
     */
    public double getValue() {
        switch (function) {
            case 0: return - child.getValue();
            case 1: return Math.sin(child.getValue());
            case 2: return Math.cos(child.getValue());
            case 3: return Math.tan(child.getValue());
            case 4: return Math.asin(child.getValue());
            case 5: return Math.acos(child.getValue());
            case 6: return Math.atan(child.getValue());
            case 7: return Sfun.sinh(child.getValue());
            case 8: return Sfun.cosh(child.getValue());
            case 9: return Sfun.tanh(child.getValue());
            case 10: return Sfun.asinh(child.getValue());
            case 11: return Sfun.acosh(child.getValue());
            case 12: return Sfun.atanh(child.getValue());
            case 13: return Math.log(child.getValue());
            case 14: return Math.log(child.getValue()) * 0.43429448190325182765;
            case 15: return Math.abs(child.getValue());
            case 16: return Math.random() * child.getValue();
            case 17: return Math.sqrt(child.getValue());
            case 18: return Sfun.erf(child.getValue());
            case 19: return Sfun.erfc(child.getValue());
            case 20: return Sfun.gamma(child.getValue());
            case 21: return Math.exp(child.getValue());
            case 22: return Sfun.cot(child.getValue());
            case 23: return Math.log(child.getValue()) * 1.442695040888963407360;
        }
        // This is never reached
        return 0;
    }

    /* (non-Javadoc)
     * @see jmt.engine.math.parser.ExpressionNode#setVariable(java.lang.String, double)
     */
    public void setVariable(String name, double value) {
        child.setVariable(name, value);
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
        ExpressionNode n_child = (ExpressionNode)child.clone();
        return new FunctionNode(n_child, function);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        // Special case for negation function
        if (function != 0)
            return this.getSubtype() + "(" + child.toString() + ")";
        else {
            if (child.getType() == CONSTANT_NODE || child.getType() == VARIABLE_NODE || 
                    (child.getType() == FUNCTION_NODE && !child.getSubtype().equals(FUNCTIONS[0])))
                return FUNCTIONS[0] + child.toString();
              else
                return FUNCTIONS[0] + "(" + child.toString() + ")";
        }
    }


}
