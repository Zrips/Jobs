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
 * <p><b>Name:</b> OperatorNode</p> 
 * <p><b>Description:</b> 
 * A Node that holds an arithmetic operation (+,-,*,/,^)
 * </p>
 * <p><b>Date:</b> 08/dic/06
 * <b>Time:</b> 15:59:56</p>
 * @author Bertoli Marco
 * @version 1.0
 */
public class OperatorNode implements ExpressionNode {
    /** List of supported operators */
    public static final char[] OPERATIONS = new char[] {'+', '-', '*', '/', '%', '^'};
    /** Children nodes */
    protected ExpressionNode left, right; // Children nodes
    /** Operation of this node */
    protected char operation;
    /** An array with children */
    protected ExpressionNode[] children;
    
    /**
     * Creates a new operation node
     * @param left left child
     * @param right right child
     * @param operation operation to be performed
     * @see OperatorNode#OPERATIONS
     */
    public OperatorNode(ExpressionNode left, ExpressionNode right, char operation) {
        this.operation = operation;
        this.left = left;
        this.right = right;
        children = new ExpressionNode[] {left, right};
    }
    
    /* (non-Javadoc)
     * @see jmt.engine.math.parser.ExpressionNode#count()
     */
    public int count() {
        return 1 + left.count() + right.count();
    }

    /* (non-Javadoc)
     * @see jmt.engine.math.parser.ExpressionNode#getDepth()
     */
    public int getDepth() {
        return 1 + Math.max(left.getDepth(), right.getDepth());
    }

    /* (non-Javadoc)
     * @see jmt.engine.math.parser.ExpressionNode#getSubtype()
     */
    public String getSubtype() {
        return Character.toString(operation);
    }

    /* (non-Javadoc)
     * @see jmt.engine.math.parser.ExpressionNode#getType()
     */
    public int getType() {
        return ExpressionNode.OPERATOR_NODE;
    }

    /* (non-Javadoc)
     * @see jmt.engine.math.parser.ExpressionNode#getValue()
     */
    public double getValue() {
        switch (operation) {
            case '+': return left.getValue() + right.getValue();
            case '-': return left.getValue() - right.getValue();
            case '*': return left.getValue() * right.getValue();
            case '/': return left.getValue() / right.getValue();
            case '%': return left.getValue() % right.getValue();
            case '^': return Math.pow(left.getValue(), right.getValue());
        }
        // Never reached
        return 0;
    }

    /* (non-Javadoc)
     * @see jmt.engine.math.parser.ExpressionNode#setVariable(java.lang.String, double)
     */
    public void setVariable(String name, double value) {
        left.setVariable(name, value);
        right.setVariable(name, value);
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
        ExpressionNode n_left = (ExpressionNode)left.clone();
        ExpressionNode n_right = (ExpressionNode)right.clone();
        return new OperatorNode(n_left, n_right, operation);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer str = new StringBuffer();
        if (needBrackets(left, true))
            str.append('(');
        str.append(left);
        if (needBrackets(left, true))
            str.append(')');
        str.append(getSubtype());
        if (needBrackets(right, false))
            str.append('(');
        str.append(right);
        if (needBrackets(right, false))
            str.append(')');
        return str.toString();
    }
    
    /**
     * Helper method for toString. Determines if brackets are needed.
     * @param child Child Node to analize
     * @param isleft true iff given node is ledt child
     * @return true iff we need to add brackets
     */
    private boolean needBrackets(ExpressionNode child, boolean isleft) {
        char childSubtype = child.getSubtype().charAt(0);
        if (child.getType() == ExpressionNode.CONSTANT_NODE || child.getType() == ExpressionNode.VARIABLE_NODE)
            return false;
        if (child.getType() == ExpressionNode.FUNCTION_NODE) 
            return childSubtype == '-';
        
        // At this point child is operational node. We must check precedences.
        switch (this.getSubtype().charAt(0)) {
        case '+':
            return false;
        case '-':
            return (childSubtype == '+' || childSubtype == '-') && !isleft;
        case '*':
            return childSubtype == '+' || childSubtype == '-' || childSubtype == '%';
        case '/':
            return !(childSubtype == '*' && isleft);
        case '%':
            return true;
        case '^':
            return true;
        }
        return true; // This statement will never be reached
    }
}
