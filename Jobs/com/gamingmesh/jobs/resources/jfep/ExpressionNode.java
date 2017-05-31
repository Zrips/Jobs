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
 * <p><b>Name:</b> ExpressionNode</p> 
 * <p><b>Description:</b> 
 * Common interface implemented by different nodes of an expression tree
 * </p>
 * <p><b>Date:</b> 08/dic/06
 * <b>Time:</b> 14:28:56</p>
 * @author Bertoli Marco
 * @version 1.0
 */
public interface ExpressionNode extends Cloneable {
    public static final int CONSTANT_NODE = 0;
    public static final int VARIABLE_NODE = 1;
    public static final int OPERATOR_NODE = 2;
    public static final int FUNCTION_NODE = 3;
    
    /**
     * Returns value of target node
     * @return value of node
     */
    public double getValue ();
    
    /**
     * Sets the value for a variable
     * @param name name of the variable to be set
     * @param value value for the variable
     */
    public void setVariable(String name, double value);
    
    /**
     * Returns type of node
     * @return CONSTANT_NODE, VARIABLE_NODE, OPERATOR_NODE, FUNCTION_NODE
     */
    public int getType();
    
    /**
     * Returns more information on node type
     * @return name of node function or operator symbol
     */
    public String getSubtype();

    /**
     * Returns depth of current subtree
     * @return depth of the tree
     */
    public int getDepth();

    /**
     * Counts number of nodes in current subtree
     * @return number of nodes (included root)
     */
    public int count();
    
    /**
     * Returns children nodes of this node, ordered from leftmost to rightmost.
     * @return an array of children nodes. If node has no child, destination array has zero size.
     */
    public ExpressionNode[] getChildrenNodes();

    /**
     * Returns a string describing the entire tree
     * @return string describing the entire tree
     */
    public String toString();

    /**
     * Clones current node
     * @return deep copy of current node
     */
    public Object clone();
}
