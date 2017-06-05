/**
 * Copyright 2017 Remi Guillemette - n4dev.ca
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package ca.n4dev.aegaeon.api.exception;

/**
 * ServerException.java
 * 
 * Aegaeon server exception throwed by service during oauth operations.
 *
 * @author by rguillemette
 * @since May 17, 2017
 */
public class ServerException extends RuntimeException {

    private static final long serialVersionUID = 5417290794084341835L;

    private ServerExceptionCode code;
    
    protected ServerException() {}
    
    public ServerException(Throwable pThrowable) {
        super(pThrowable);
        this.code = ServerExceptionCode.UNEXPECTED_ERROR;
    }
    
    public ServerException(ServerExceptionCode pCode) {
        this.code = pCode;
    }
    
    public ServerException(ServerExceptionCode pCode, String pMessage) {
        super(pMessage);
        this.code = pCode;
    }

    /**
     * @return the code
     */
    public ServerExceptionCode getCode() {
        return code;
    }

    /**
     * @param pCode the code to set
     */
    public void setCode(ServerExceptionCode pCode) {
        code = pCode;
    }
    
    /**
     * Check if this ServerException is one of the code.
     * @param pCode The ServerExceptionCode to check.
     * @return true or false.
     */
    public boolean is(ServerExceptionCode... pCode) {
        if (pCode != null) {
            for (ServerExceptionCode c : pCode) {
                if (c == this.code) {
                    return true;
                }
            }
        }
        
        return false;
    }
}
