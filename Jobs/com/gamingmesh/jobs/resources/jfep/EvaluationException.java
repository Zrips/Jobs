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
 * <p><b>Name:</b> EvaluationException</p> 
 * <p><b>Description:</b> 
 * This exception is thrown when a variable was not initialized and
 * function was evaluated.
 * </p>
 * <p><b>Date:</b> 08/dic/06
 * <b>Time:</b> 19:45:36</p>
 * @author Bertoli Marco
 * @version 1.0
 */
public class EvaluationException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = -1297776708634902362L;

    public EvaluationException() {
    }

    public EvaluationException(String message) {
        super(message);
    }

    public EvaluationException(Throwable cause) {
        super(cause);
    }

    public EvaluationException(String message, Throwable cause) {
        super(message, cause);
    }

}
