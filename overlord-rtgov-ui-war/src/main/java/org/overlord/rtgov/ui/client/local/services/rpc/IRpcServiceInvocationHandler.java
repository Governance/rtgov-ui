/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.rtgov.ui.client.local.services.rpc;

import javax.inject.Inject;

import org.overlord.rtgov.ui.client.local.services.NotificationService;


/**
 * An async handler interface for making service invocations.
 *
 * @author eric.wittmann@redhat.com
 */
public interface IRpcServiceInvocationHandler<T> {

    /**
     * Called when the RPC call successfully returns data.
     * @param data
     */
    public void onReturn(T data);

    /**
     * Called when the RPC call fails.
     * @param error
     */
    public void onError(Throwable error);
    
    /**
     * The result of IRpcServiceInvocationHandler call's.
     */
    interface RpcResult<T> {

        /**
         *
         * @return the Data of the Rpc Call or null in case of
         *         {@link #isError()}
         */
        T getData();

        /**
         *
         * @return the Error of the Rpc Call or null if successful
         */
        Throwable getError();

        /**
         *
         * @return true in case of any Error
         */
        boolean isError();

        class DefaultResult<T> implements RpcResult<T> {
            private T data;
            private Throwable error;

            public DefaultResult(T data, Throwable error) {
                super();
                this.data = data;
                this.error = error;
            }

            @Override
            public T getData() {
                return data;
            }

            @Override
            public Throwable getError() {
                return error;
            }

            @Override
            public boolean isError() {
                return getError() != null;
            }
        }
    }

    class RpcServiceInvocationHandlerAdapter<T> implements IRpcServiceInvocationHandler<T> {

        @Override
        public void onReturn(T data) {
            try {
                doOnReturn(data);
            } finally {
                doOnComplete(new RpcResult.DefaultResult<T>(data, null));
            }
        }

        public void doOnReturn(T data) {
        }

        @Override
        public void onError(Throwable error) {
            try {
                doOnError(error);
            } finally {
                doOnComplete(new RpcResult.DefaultResult<T>(null, error));
            }
        }

        public void doOnError(Throwable error) {
        }

        public void doOnComplete(RpcResult<T> result) {
        }

    }

	class VoidInvocationHandler implements IRpcServiceInvocationHandler<Void> {
		@Inject
		private NotificationService notificationService;
		private String title;

		public VoidInvocationHandler() {
			super();
		}

		public VoidInvocationHandler(String title) {
			super();
			this.title = title;
		}

		@Override
		public void onReturn(Void data) {
		}

		@Override
		public void onError(Throwable error) {
			notificationService.sendErrorNotification(title, error);
		}

	}



}
