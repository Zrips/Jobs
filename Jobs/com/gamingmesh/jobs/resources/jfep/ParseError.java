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
 * <p><b>Name:</b> ParseError</p> 
 * <p><b>Description:</b> 
 * Thrown if the parser encountres an irrecoverable error.
 * </p>
 * <p><b>Date:</b> 08/dic/06
 * <b>Time:</b> 14:12:25</p>
 * @author Bertoli Marco
 * @version 1.0
 */
public class ParseError extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = -3688639666380211029L;
    private int position;
    /**
     * Basic constructor
     * @param str Error description
     * @param position Position that generated error in input string
     */
    public ParseError (String str, int position) {
      super(str);
      this.position = position;
    }

    /**
     * Get position that generated error in input string
     * @return position
     */
    public int getPosition() {
      return position;
    }

    public ParseError(String message) {
        super(message);
    }

    public ParseError(Throwable cause) {
        super(cause);
    }

    public ParseError(String message, Throwable cause) {
        super(message, cause);
    }

}
