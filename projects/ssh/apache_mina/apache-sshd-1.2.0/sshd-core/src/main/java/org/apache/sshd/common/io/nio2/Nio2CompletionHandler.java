/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sshd.common.io.nio2;

import java.nio.channels.CompletionHandler;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * @param <V> Result type
 * @param <A> Attachment type
 * @author <a href="mailto:dev@mina.apache.org">Apache MINA SSHD Project</a>
 */
public abstract class Nio2CompletionHandler<V, A> implements CompletionHandler<V, A> {

    @Override
    public void completed(final V result, final A attachment) {
        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                onCompleted(result, attachment);
                return null;
            }
        });
    }

    @Override
    public void failed(final Throwable exc, final A attachment) {
        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                onFailed(exc, attachment);
                return null;
            }
        });
    }

    protected abstract void onCompleted(V result, A attachment);

    protected abstract void onFailed(Throwable exc, A attachment);

}
